package me.zhengjie.modules.study.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.study.domain.UserSysVal;
import me.zhengjie.modules.study.service.UserSysValService;
import me.zhengjie.modules.study.service.dto.UserSysValQueryCriteria;
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
* @date 2020-05-25
*/
@Api(tags = "系统：学习行为变量")
@RestController
@RequestMapping("/api/userSysVal")
public class UserSysValController {

    private final UserSysValService userSysValService;

    public UserSysValController(UserSysValService userSysValService) {
        this.userSysValService = userSysValService;
    }


    @Log("查询行为变量")
    @ApiOperation("查询行为变量")
    @GetMapping
    @PreAuthorize("@el.check('userSysVal:list')")
    public ResponseEntity<Object> getUserSysVals(Long userId){
        return new ResponseEntity<>(userSysValService.findByUser(userId),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增行为变量")
    @ApiOperation("新增行为变量")
    @PreAuthorize("@el.check('userSysVal:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody UserSysVal resources){
        return new ResponseEntity<>(userSysValService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改行为变量")
    @ApiOperation("修改行为变量")
    @PreAuthorize("@el.check('userSysVal:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody UserSysVal resources){
        userSysValService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除行为变量")
    @ApiOperation("删除行为变量")
    @PreAuthorize("@el.check('userSysVal:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        userSysValService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}