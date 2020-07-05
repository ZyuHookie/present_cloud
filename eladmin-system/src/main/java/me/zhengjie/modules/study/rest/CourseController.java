package me.zhengjie.modules.study.rest;

import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.study.domain.*;
import me.zhengjie.modules.study.repository.CourseStudentRepository;
import me.zhengjie.modules.study.service.dto.CourseDto;
import me.zhengjie.modules.study.service.dto.CourseQueryCriteria;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.study.service.CourseService;
import me.zhengjie.modules.study.service.dto.StudentDto;
import me.zhengjie.modules.study.service.mapstruct.StudentMapper;
import me.zhengjie.modules.system.service.DeptService;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.DeptDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;


/**
 * @author zyuh
 * @date 2020-05-15
 */
@Api(tags = "系统：班课管理")
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final StudentMapper studentMapper;
    private final CourseStudentRepository courseStudentRepository;
    private final DeptService deptService;


    @Log("查询课程部分信息")
    @ApiOperation("查询部分课程信息")
    @GetMapping(value = "/partInfo")
    @AnonymousAccess
//    @PreAuthorize("@el.check('course:list')")
    public ResponseEntity<Object> getSmallCourse(){
        String schoolName;
        List<CourseDto> courseDto = courseService.findAll();
        List<WebCourseVo> webCourseVos = new ArrayList<>();

        for (int i = 0;i<courseDto.size(); i++)
        {
            DeptDto deptDto = deptService.findById(courseDto.get(i).getCollege().getId());

            if (deptDto.getPid() != null)
            {
                DeptDto pDeptDto = deptService.findById(deptDto.getPid());
                schoolName = pDeptDto.getName();
            }
            else    schoolName = courseDto.get(i).getCollege().getName();

            WebCourseVo webCourseVo1 = new WebCourseVo(courseDto.get(i).getId(),courseDto.get(i).getCourseName(),schoolName,courseDto.get(i).getCollege());
            webCourseVos.add(webCourseVo1);
        }
        return new ResponseEntity<>(webCourseVos,HttpStatus.OK);
//        return new ResponseEntity<>(courseService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("查询课程信息")
    @ApiOperation("查询课程信息")
    @GetMapping
    @PreAuthorize("@el.check('course:list')")
    public ResponseEntity<Object> getCourses(CourseQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(courseService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增课程信息")
    @ApiOperation("新增课程信息")
    @PostMapping
    @PreAuthorize("@el.check('course:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Course resources){
        return new ResponseEntity<>(courseService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改课程信息")
    @ApiOperation("修改课程信息")
    @PutMapping
    @PreAuthorize("@el.check('course:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Course resources){
        courseService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除课程")
    @ApiOperation("删除课程")
    @DeleteMapping
    @PreAuthorize("@el.check('course:del')")
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        courseService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("查询选课学生")
    @ApiOperation("按课程编码查询选课学生")
    @GetMapping(value = "/student")
    @PreAuthorize("@el.check('course:list')")
    public ResponseEntity<Object> courseStudents(Long id) {
        Set<Student> students = courseService.findCourseById(id).getStudents();
        Set<StudentDto> studentDtos = new HashSet<>();
        if(students != null && students.size() != 0) {
            for(Student student: students) {
                SignHistoryPrimaryKey pk = new SignHistoryPrimaryKey(id, student.getId());
                CourseStudent cs = courseStudentRepository.findById(pk).orElseGet(CourseStudent::new);
                if(cs != null) {
                    Integer experience = cs.getExperience();
                    studentDtos.add(studentMapper.toDto(student,experience));
                } else {
                    throw new BadRequestException("获取学生经验值失败");
                }
            }
        }
        return new ResponseEntity<>(studentDtos,HttpStatus.OK);
//        return new ResponseEntity<>(cs.getExperience(),HttpStatus.OK);
    }


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('course:list')")
    public void download(HttpServletResponse response, CourseQueryCriteria criteria) throws IOException {
        courseService.download(courseService.queryAll(criteria), response);
    }

}