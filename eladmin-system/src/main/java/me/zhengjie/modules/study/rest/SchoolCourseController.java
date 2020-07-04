package me.zhengjie.modules.study.rest;

import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.modules.study.domain.Course;
import me.zhengjie.modules.study.repository.CourseStudentRepository;
import me.zhengjie.modules.study.service.dto.CourseQueryCriteria;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.study.service.CourseService;
import me.zhengjie.modules.study.service.mapstruct.StudentMapper;
import me.zhengjie.modules.system.service.DeptService;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

@Api(tags = "系统：课程管理")
@RestController
@RequestMapping("/api/schoolCourse")
@RequiredArgsConstructor
public class SchoolCourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final StudentMapper studentMapper;
    private final CourseStudentRepository courseStudentRepository;
    private final DeptService deptService;


    @Log("查询课程信息")
    @ApiOperation("查询课程信息")
    @GetMapping
    @PreAuthorize("@el.check('course:list')")
    public ResponseEntity<Object> getCourses(CourseQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(courseService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("根据college_id查询课程信息")
    @ApiOperation("查询课程信息")
    @GetMapping(value = "/course/getCourseList")
    @AnonymousAccess
    public ResponseEntity<Object> getCoursesByCollege_id(UserQueryCriteria criteria, Pageable pageable){
        if (!ObjectUtils.isEmpty(criteria)) {
            criteria.getDeptIds().add(criteria.getDeptId());
            criteria.getDeptIds().addAll(deptService.getDeptChildren(criteria.getDeptId(),
                    deptService.findByPid(criteria.getDeptId())));
        }
        if (!CollectionUtils.isEmpty(criteria.getDeptIds())) {
            Object user = userService.queryAll(criteria, pageable);
            return new ResponseEntity<>(criteria.getDeptIds(), HttpStatus.OK);
        }
//        CourseQueryCriteria courseQueryCriteria =new CourseQueryCriteria();
//        courseQueryCriteria.setBlurry();
        return new ResponseEntity<>(criteria.getDeptIds(),HttpStatus.OK);
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
}
