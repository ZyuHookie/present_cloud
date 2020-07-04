package me.zhengjie.modules.study.rest;

import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.study.domain.SignHistory;
import me.zhengjie.modules.study.service.SignHistoryService;
import me.zhengjie.modules.study.service.dto.SignHistoryQueryCriteria;
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
@Api(tags = "系统：历史签到记录管理")
@RestController
@RequestMapping("/api/signHistory")
@RequiredArgsConstructor
public class SignHistoryController {

    private final SignHistoryService signHistoryService;


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('sign:list')")
    public void download(HttpServletResponse response, SignHistoryQueryCriteria criteria) throws IOException {
        signHistoryService.download(signHistoryService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询历史签到记录")
    @ApiOperation("查询历史签到记录")
    @PreAuthorize("@el.check('sign:list')")
    public ResponseEntity<Object> getSignHistory(SignHistoryQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(signHistoryService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增历史签到记录")
    @ApiOperation("新增历史签到记录")
    @PostMapping
    @PreAuthorize("@el.check('sign:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SignHistory resources){
        return new ResponseEntity<>(signHistoryService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改历史签到记录")
    @ApiOperation("修改历史签到记录")
    @PutMapping
    @PreAuthorize("@el.check('sign:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody SignHistory resources){
        signHistoryService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除历史签到记录")
    @ApiOperation("删除历史签到记录")
    @DeleteMapping
    @PreAuthorize("@el.check('sign:del')")
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        signHistoryService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}