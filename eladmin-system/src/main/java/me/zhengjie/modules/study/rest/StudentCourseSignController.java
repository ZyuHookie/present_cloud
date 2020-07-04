package me.zhengjie.modules.study.rest;

import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.study.domain.StudentCourseSign;
import me.zhengjie.modules.study.service.StudentCourseSignService;
import me.zhengjie.modules.study.service.dto.StudentCourseSignQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author zyuh
* @date 2020-05-15
*/
@Api(tags = "系统：签到记录管理")
@RestController
@RequestMapping("/api/studentCourseSign")
@RequiredArgsConstructor
public class StudentCourseSignController {

    private final StudentCourseSignService studentCourseSignService;

    /*public StudentCourseSignController(StudentCourseSignService studentCourseSignService) {
        this.studentCourseSignService = studentCourseSignService;
    }*/

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('sign:list')")
    public void download(HttpServletResponse response, StudentCourseSignQueryCriteria criteria) throws IOException {
        studentCourseSignService.download(studentCourseSignService.queryAll(criteria), response);
    }

    @Log("查询签到记录")
    @ApiOperation("查询签到记录")
    @GetMapping
    @PreAuthorize("@el.check('sign:list')")
    public ResponseEntity<Object> getStudentCourseSigns(StudentCourseSignQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(studentCourseSignService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("查询课程某次签到记录")
    @ApiOperation("查询课程某次签到记录")
    @GetMapping(value = "/sign")
    @PreAuthorize("@el.check('sign:list')")
    public ResponseEntity<Object> getSignsByHistoryId(Long id){
        return new ResponseEntity<>(studentCourseSignService.findByHistoryId(id),HttpStatus.OK);
    }

    @Log("新增签到记录")
    @ApiOperation("新增签到记录")
    @PostMapping
    @PreAuthorize("@el.check('sign:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody StudentCourseSign resources){
        return new ResponseEntity<>(studentCourseSignService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改签到记录")
    @ApiOperation("修改签到记录")
    @PutMapping
    @PreAuthorize("@el.check('sign:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody StudentCourseSign resources){
        studentCourseSignService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("修改详细签到记录")
    @ApiOperation("修改详细签到记录")
    @PutMapping(value = "/sign")
    public ResponseEntity<Object> updateSign(@RequestBody Long[] ids){
        studentCourseSignService.updateSignByIds(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除签到记录")
    @ApiOperation("删除签到记录")
    @DeleteMapping
    @PreAuthorize("@el.check('sign:del')")
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        studentCourseSignService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}