package me.zhengjie.modules.study.service.impl;

import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.study.domain.Course;
import me.zhengjie.modules.study.repository.CourseRepository;
import me.zhengjie.modules.study.repository.CourseStudentRepository;
import me.zhengjie.modules.study.service.dto.CouserSmallDto;
import me.zhengjie.modules.study.service.mapstruct.CourseSmallMapper;
import me.zhengjie.modules.system.service.RoleService;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.study.service.dto.CourseDto;
import me.zhengjie.modules.study.service.dto.CourseQueryCriteria;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.modules.system.service.mapstruct.UserMapper;
import me.zhengjie.utils.*;
import me.zhengjie.modules.study.service.CourseService;
import me.zhengjie.modules.study.service.mapstruct.CourseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * @author zyuh
 * @date 2020-05-15
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "course")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final RoleService roleService;
    private final CourseSmallMapper courseSmallMapper;
    private final CourseStudentRepository courseStudentRepository;


    @Override
    public Map<String,Object> queryAll(CourseQueryCriteria criteria, Pageable pageable) {
        Page<Course> page = courseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        List<Course> courses  = page.getContent();
        courses.forEach(course -> {
            Integer count = courseStudentRepository.countByIdCourseId(course.getId());
            System.out.println(count);
            course.setStudentCount(count);
        });
        return PageUtil.toPage(page.map(courseMapper::toDto));
    }

    @Override
    @Cacheable
    public List<CourseDto> queryAll(CourseQueryCriteria criteria){
        return courseMapper.toDto(courseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Cacheable(key = "#p0")
    public CourseDto findById(Long id) {
        Course course = courseRepository.findById(id).orElseGet(Course::new);
        ValidationUtil.isNull(course.getId(),"Course","id",id);
        return courseMapper.toDto(course);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public CourseDto create(Course resources) {
        /* 获得创建人信息 */
        UserDto user = userService.findByName(SecurityUtils.getCurrentUsername());
        if(user != null) {
            resources.setCreateUser(userMapper.toEntity(user));
        }
        /* 随机生成课程码 */
        String courseCode = StringUtils.randomNumStr(7);
        List<String> codes = courseRepository.findCourseCodes();
        while (codes.contains(courseCode)) {
            courseCode = StringUtils.randomNumStr(7);
        }
        resources.setCourseCode(courseCode);
        return courseMapper.toDto(courseRepository.save(resources));
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(Course resources) {
        Course course = courseRepository.findById(resources.getId()).orElseGet(Course::new);
        ValidationUtil.isNull( course.getId(),"Course","id",resources.getId());
        course.copy(resources);
        courseRepository.save(course);
    }


    @Override
    @CacheEvict(allEntries = true)
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            courseRepository.deleteById(id);
        }
    }

    @Override
//    @Cacheable(key = "'loadCourseById:'+#p0")
    public Course findCourseById(Long id) {
        Course course = courseRepository.findById(id).orElseGet(Course::new);
        return course;
    }

    @Override
    public void download(List<CourseDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CourseDto course : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("课程名", course.getCourseName());
            map.put("课程编码", course.getCourseCode());
            map.put("选课人数", course.getStudentCount());
            map.put("授课老师姓名", course.getTeacherName());
            map.put("所属院校", course.getCollege().getName());
            map.put("学期", course.getSemester());
            map.put("课程创建者", course.getUserName());
            map.put("发起签到次数", course.getSignCount());
            map.put("状态", course.getEnabled() ? "启用" : "禁用");
            map.put("是否允许加入", course.getJoinPermission() ? "是" : "否");
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public CourseDto createCourse(Course resources) {
        /*随机生成课程码*/
        String courseCode = StringUtils.randomNumStr(7);
        List<String> codes = courseRepository.findCourseCodes();
        while (codes.contains(courseCode)) {
            courseCode = StringUtils.randomNumStr(7);
        }
        resources.setCourseCode(courseCode);
        return courseMapper.toDto(courseRepository.save(resources));
    }

    @Override
    public CourseDto findByCode(String code) {
        Course course = courseRepository.findByCourseCode(code);
        ValidationUtil.isNull(course.getCourseCode(),"Course","courseCode",code);
        return courseMapper.toDto(course);
    }

    @Override
    public Boolean courseBelong(String code, String phone) {
        Course course = courseRepository.findByCourseCode(code);
        ValidationUtil.isNull(course.getCourseCode(),"Course","courseCode",code);
        if(course.getCreateUser().getPhone().equals(phone)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Course findEntityByCode(String code) {
        Course course = courseRepository.findByCourseCode(code);
        ValidationUtil.isNull(course,"course","courseCode",code);
        return course;
    }

    @Override
    public List<CouserSmallDto> findByUserId(Long id) {
        List<Course> course = courseRepository.findByUserId(id);
        return courseSmallMapper.toDto(course);
    }

    @Override
    public List<CourseDto> findAll(){
        List<Course> course = courseRepository.findAll();
        return courseMapper.toDto(course);
    }

}