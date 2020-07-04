package me.zhengjie.modules.system.rest;

import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.modules.security.service.dto.MobileAuthVo;
import me.zhengjie.modules.study.domain.*;
import me.zhengjie.modules.study.repository.CourseRepository;
import me.zhengjie.modules.study.repository.CourseStudentRepository;
import me.zhengjie.modules.study.repository.SignHistoryRepository;
import me.zhengjie.modules.study.repository.StudentCourseSignRepository;
import me.zhengjie.modules.study.service.CourseService;
import me.zhengjie.modules.study.service.SignHistoryService;
import me.zhengjie.modules.study.service.StudentCourseSignService;
import me.zhengjie.modules.study.service.StudentService;
import me.zhengjie.modules.study.service.dto.StudentDto;
import me.zhengjie.modules.study.service.mapstruct.CourseMapper;
import me.zhengjie.modules.study.service.mapstruct.StudentMapper;
import me.zhengjie.modules.system.domain.Role;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.domain.vo.MobilePassVo;
import me.zhengjie.modules.system.domain.vo.MobileUserVo;
import me.zhengjie.modules.system.service.DeptService;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.DeptDto;
import me.zhengjie.modules.system.service.dto.DeptSmallDto;
import me.zhengjie.modules.system.service.dto.UserDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/mobileApp")
@RequiredArgsConstructor
public class MobileAppController {
    private final StudentService studentService;
    private final UserService userService;
    private final CourseService courseService;
    private final PasswordEncoder passwordEncoder;
    private final CourseStudentRepository courseStudentRepository;
    private final StudentMapper studentMapper;
    private final DeptService deptService;
    private final SignHistoryService signHistoryService;
    private final SignHistoryRepository signHistoryRepository;
    private final StudentCourseSignService studentCourseSignService;
    private final StudentCourseSignRepository studentCourseSignRepository;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;


    @ApiOperation("检查手机号是否注册")
    @GetMapping(value = "/check")
    @AnonymousAccess
    public ResponseEntity<Object> checkPhoneIsRegister(String phone) {
        if (userService.checkRegister(phone)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation("学生注册")
    @PostMapping(value = "/student/register")
    @AnonymousAccess
    public ResponseEntity<Object> studentRegister(@Validated @RequestBody Student resource) {
        resource.setPassword(passwordEncoder.encode(resource.getPassword()));
        resource.setEnabled(true);
        studentService.create(resource);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("教师注册")
    @PostMapping(value = "/teacher/register")
    @AnonymousAccess
    public ResponseEntity<Object> teacherRegister(@Validated @RequestBody User resource) {
        resource.setPassword(passwordEncoder.encode(resource.getPassword()));
        resource.setEnabled(true);
        Set<Role> roles = new HashSet<>();
        long id = 2;
        roles.add(new Role(id));
        resource.setRoles(roles);
        userService.create(resource);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("移动端登录")
    @AnonymousAccess
    @PostMapping(value = "/login")
    public ResponseEntity<Object> mobielApplogin(@Validated @RequestBody MobileAuthVo authUser){
        UserDto teacher = userService.findByPhoneOrEmail(authUser.getUsername());

        if (teacher != null) {
            if(passwordEncoder.matches(authUser.getPassword(), teacher.getPassword())) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                throw new BadRequestException("登录失败,密码错误");
            }
        } else {
            StudentDto student = studentService.findByName(authUser.getUsername());
            if (student != null && passwordEncoder.matches(authUser.getPassword(), student.getPassword())) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                throw new BadRequestException("登录失败,密码错误");
            }
        }
    }

    @ApiOperation("移动端密码修改")
    @AnonymousAccess
    @PostMapping(value = "/password/change")
    public ResponseEntity<Object> changePassword(@RequestBody MobilePassVo passVo){
        if("student".equals(passVo.getRole())) {
            Student user = studentService.findByCount(passVo.getCount());
            if (user != null && passwordEncoder.matches(passVo.getOldPassword(), user.getPassword())) {
                studentService.updatePass(passVo.getCount(), passwordEncoder.encode(passVo.getNewPassword()));
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                throw new BadRequestException("旧密码错误");
            }
        }

        if("teacher".equals(passVo.getRole())) {
            UserDto teacher = userService.findByPhoneOrEmail(passVo.getCount());
            if (passwordEncoder.matches(passVo.getOldPassword(), teacher.getPassword())) {
                userService.updatePassByPhone(passVo.getCount(), passwordEncoder.encode(passVo.getNewPassword()));
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                throw new BadRequestException("旧密码错误");
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  //HttpStatus.BAD_REQUEST=400 错误
    }

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/userInfo")
    @AnonymousAccess
    public ResponseEntity<Object> getTeacher(String phone) {
        UserDto teacher = userService.findByPhoneOrEmail(phone);
        MobileUserVo user;
        if (teacher != null) {
            DeptSmallDto tDto = teacher.getDept();
            Long  id = tDto.getId();
            DeptDto tDeptDto = deptService.findById(id);
            String schoolName;
            if (tDeptDto.getPid() != null) {
                Long tpid = tDeptDto.getPid();
                DeptDto tParentDeptDto = deptService.findById(tpid);
                schoolName = tParentDeptDto.getName();
                user = new MobileUserVo(teacher.getId(),teacher.getPhone(), teacher.getNickName(), teacher.getGender(), "教师",
                        schoolName, teacher.getDept().getName(), "",teacher.getEmail());
            }
            else {
                user = new MobileUserVo(teacher.getId(),teacher.getPhone(), teacher.getNickName(), teacher.getGender(), "教师",
                        tDeptDto.getName(), teacher.getDept().getName(), "",teacher.getEmail());
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        StudentDto student = studentService.findByName(phone);
        Long deptId = student.getCollege().getId();
        DeptDto deptDto = deptService.findById(deptId);
        String studentSchName;
        if (deptDto.getPid() != null) {
            DeptDto parentDeptDto = deptService.findById(deptDto.getPid());
            studentSchName = parentDeptDto.getName();
        }else {
            studentSchName =student.getCollege().getName();
        }
        user = new MobileUserVo(student.getId(),student.getPhone(), student.getName(), student.getSex(), "学生",
                studentSchName, student.getCollege().getName(), student.getStudentNumber(), student.getEmail());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ApiOperation("学生用户信息修改")
    @PostMapping(value = "/student/update")
    @AnonymousAccess
    public ResponseEntity<Object> updateStudent(@RequestBody Student resources) {
        studentService.update(resources);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ApiOperation("创建班课")
    @PostMapping(value = "/course")
    @AnonymousAccess
    public ResponseEntity<Object> createCourse(@RequestBody Course resources) {
        resources.setEnabled(true);
        resources.setJoinPermission(true);
        courseService.createCourse(resources);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("根据班课code获取班课信息")
    @GetMapping(value = "/course/info")
    @AnonymousAccess
    public ResponseEntity<Object> getCourse(String courseCode) {
        return new ResponseEntity<>(courseService.findByCode(courseCode), HttpStatus.OK);
    }

    @ApiOperation("检查是否已加入班课")
    @GetMapping(value = "/course/check")
    @AnonymousAccess
    public ResponseEntity<Object> checkCourseBelong(String courseCode, String phone) {
        Boolean flag = courseService.courseBelong(courseCode, phone);
        if(flag){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("根据教师用户id获取所创建的班课信息")
    @GetMapping(value = "/course/belong")
    @AnonymousAccess
    public ResponseEntity<Object> getCourseByTeacherId(Long id) {
        return new ResponseEntity<>(courseService.findByUserId(id), HttpStatus.OK);
    }

    @ApiOperation("根据学生用户id获取所加入的班课信息")
    @GetMapping(value = "/course/join")
    @AnonymousAccess
    public ResponseEntity<Object> getCourseByStudentId(Long id) {
        return new ResponseEntity<>(studentService.findJoinCourse(id), HttpStatus.OK);
    }

    @ApiOperation("修改班课信息")
    @PutMapping(value = "/course/update")
    @AnonymousAccess
    public ResponseEntity<Object> updateCourse(@RequestBody Course resources) {
        courseService.update(resources);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/course/student")
    @AnonymousAccess
    public ResponseEntity<Object> getCourseStudents(Long id) {
        Set<Student> students = courseService.findCourseById(id).getStudents();
        Set<StudentDto> studentDtos = new HashSet<>();
        if (students != null && students.size() != 0) {
            for (Student student : students) {
                SignHistoryPrimaryKey pk = new SignHistoryPrimaryKey(id, student.getId());
                CourseStudent cs = courseStudentRepository.findById(pk).orElseGet(CourseStudent::new);
                if (cs != null) {
                    Integer experience = cs.getExperience();
                    studentDtos.add(studentMapper.toDto(student, experience));
                } else {
                    throw new BadRequestException("获取学生经验值失败");
                }
            }
        }
        return new ResponseEntity<>(studentDtos,HttpStatus.OK);
    }


    @GetMapping(value = "/college")
    @AnonymousAccess
    public ResponseEntity<Object> getColleges(){
        // 数据权限
        List<DeptDto> deptDtos = deptService.findAll();
        Map<String,Object> map = (Map)deptService.buildTree(deptDtos);
        return new ResponseEntity<>(map.get("content"), HttpStatus.OK);
    }

    @ApiOperation("用户id根据班课code加入班课")
    @GetMapping(value = "/join/course")
    @AnonymousAccess
    public ResponseEntity<Object> studentJoinCourse(Long userId, String courseCode) {
        Course course = courseService.findEntityByCode(courseCode);
        SignHistoryPrimaryKey key = new SignHistoryPrimaryKey(course.getId(), userId);
        if((courseStudentRepository.findById(key)).orElse(null) != null) {
            throw new EntityExistException("该用户已加入该课程");
        }
        if(!course.getJoinPermission()) {
            throw new BadRequestException(HttpStatus.CONFLICT ,"该课程已停课不允许加入");
        }
        CourseStudent courseStudent = new CourseStudent(key, 0);

        courseStudentRepository.save(courseStudent);
        //  加入成功更新加入该课程学生人数
        course.setStudentCount(courseStudentRepository.countByIdCourseId(course.getId()));
        courseService.update(course);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("用户id退出某一班课")
    @GetMapping(value = "/quit/course")
    @AnonymousAccess
    public ResponseEntity<Object> studentQuitCourse(Long userId, String courseCode) {
        Course course = courseService.findEntityByCode(courseCode);
        SignHistoryPrimaryKey key = new SignHistoryPrimaryKey(course.getId(), userId);

        courseStudentRepository.deleteById(key);
        // 修改选课人数
        course.setStudentCount(course.getStudentCount()-1);
        courseService.update(course);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("发起签到")
    @PostMapping(value = "/release/sign")
    @AnonymousAccess
    public ResponseEntity<Object> releaseSign(@RequestBody SignHistory resource) {
        Set<Student> students = courseService.findCourseById((resource.getCourse()).getId()).getStudents();
        resource.setAbsence(students.size());
        // 创建完签到保存到数据库sign_history表，并且设置成3分钟后自动将签到状态结束
        SignHistory signHistory = signHistoryService.releaseSign(resource);
        // 更新课程的历史签到数
        Course course = courseService.findCourseById(resource.getCourse().getId());
        course.setSignCount(signHistoryRepository.countByCourseId(course.getId()));

        // 暂时先把该课程所有学生设为缺席添加到student_course_sign表中，等学生签到的时候一个一个改
        List<StudentCourseSign> absentList = new ArrayList<>();
        for(Student student: students){
            absentList.add(new StudentCourseSign(signHistory.getId(), student.getId(), false, false));
        }
        studentCourseSignService.create(absentList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("学生签到")
    @GetMapping(value = "/sign/student")
    @AnonymousAccess
    public ResponseEntity<Object> studentSignIn(Long courseId,String code, Long studentId) {
        List<SignHistory> signHistories = signHistoryRepository.findByCourseIdOrderByCreateTimeDesc(courseId);
        if (signHistories.size() == 0) {
            throw new BadRequestException("该课程未发布签到");
        }
        SignHistory sign = signHistories.get(0);
        Map<String,Object> map = new HashMap<>(1);
        if (sign.getStatus()) {
            if (code != null) { // // 不用签到代码 在签到期间直接签到就行sign.getCode() != null
                List<StudentCourseSign> signs = studentCourseSignRepository.findBySignHistory_IdAndStudent_Id(sign.getId(), studentId);
                if (signs.size() < 1) {
                    map.put("msg", "该学生未加入该课程");
                    return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
                }
                StudentCourseSign studentSign = signs.get(0);
                if (studentSign.getAttendance()) {
                    map.put("msg", "已经完成签到，无需重复签到");
                    return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
                }
                studentSign.setAttendance(true);
                SignHistoryPrimaryKey key = new SignHistoryPrimaryKey(courseId, studentId);
                CourseStudent courseStudent = courseStudentRepository.findById(key).orElse(new CourseStudent());
                if (courseStudent.getId() == null) {
                    throw new BadRequestException("该学生未加入该课程");
                }
                courseStudent.setExperience(courseStudent.getExperience() + 10);
                courseStudentRepository.save(courseStudent);
                studentCourseSignService.update(studentSign);
                // 签到成功 更新成功签到数和缺勤数
                sign.setAttendance(sign.getAttendance() + 1);
                sign.setAbsence(sign.getAbsence() - 1);
                signHistoryService.update(sign);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                map.put("msg", "验证码错误");
                return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
            }
        } else {
            map.put("msg", "该课程签到已结束");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
    }

    /*@ApiOperation("测试")
    @GetMapping(value = "/sign/test")
    @AnonymousAccess
    public ResponseEntity<Object> test(Long courseId,String code, Long studentId) {
        List<SignHistory> signHistories = signHistoryRepository.findByCourseIdOrderByCreateTimeDesc(courseId);

    }*/


    @ApiOperation("根据courseid查询是否签到ing")
    @GetMapping(value = "/sign/check")
    @AnonymousAccess
    public ResponseEntity<Object> checkSign(Long courseId) {
        List<SignHistory> signHistories = signHistoryRepository.findByCourseIdOrderByCreateTimeDesc(courseId);
        if (signHistories.size() != 0 && signHistories.get(0).getStatus()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation("关闭签到")
    @GetMapping(value = "/sign/close")
    @AnonymousAccess
    public ResponseEntity<Object> closeSign(Long courseId) {
        List<SignHistory> signHistories = signHistoryRepository.findByCourseIdOrderByCreateTimeDesc(courseId);
        if (signHistories.size() != 0 && signHistories.get(0).getStatus()) {
            SignHistory sign = signHistories.get(0);
            sign.setStatus(!sign.getStatus());
            signHistoryService.update(sign);
            return new ResponseEntity<>(signHistoryService.findSignHistoryStudentsById(sign.getId()),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping(value = "/sign/history")
    @AnonymousAccess
    public ResponseEntity<Object> checkSignHistory(Long courseId, Long studentId) {
        List<SignHistory> signHistories = signHistoryRepository.findByCourseIdOrderByCreateTimeDesc(courseId);
        List<Map> results = new ArrayList<>();
        for(SignHistory history: signHistories) {
            Map<String, Object> item = new HashMap<>(2);
            item.put("time", history.getCreateTime());
            List<StudentCourseSign> signs = studentCourseSignRepository.findBySignHistory_IdAndStudent_Id(history.getId(), studentId);
            if (signs.size() > 0 && (signs.get(0).getAttendance() || signs.get(0).getReplenish())) {
                item.put("attendance", true);
            } else {
                item.put("attendance", false);
            }
            results.add(item);
        }
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping(value = "/course/getCourseList")
    @AnonymousAccess
    public ResponseEntity<Object> getCourseList(Long college_id) {
        List<String> schoolCoursesDtoList = courseRepository.getScoolCourseList(college_id);
        List<Map> results = new ArrayList<>();


        for(int i=0 ; i< schoolCoursesDtoList.size(); i++) {
            Map<String, Object> item = new HashMap<>(2);
            item.put("course_name", schoolCoursesDtoList.get(i));
            results.add(item);
        }
//        results.set("content",results);
//        results.set("totalElement",results.size());
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping(value = "/course/getTopDeptName")
    @AnonymousAccess
    public ResponseEntity<DeptDto> getTopDeptName(Long college_id) {
        DeptDto deptDto1 = deptService.findById(college_id);
        Long pid1 = deptDto1.getPid();
//        return new ResponseEntity<>(deptDto1, HttpStatus.OK);
        if(pid1 != null)
        {
            DeptDto deptDto2 = deptService.findById(pid1);
            Long pid2 = deptDto2.getPid();
            if (pid2 != null)
            {
                DeptDto deptDto3 = deptService.findById(pid2);
                return new ResponseEntity<>(deptDto3, HttpStatus.OK);
            }
            else
                return new ResponseEntity<>(deptDto2, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(deptDto1, HttpStatus.OK);
    }
}
