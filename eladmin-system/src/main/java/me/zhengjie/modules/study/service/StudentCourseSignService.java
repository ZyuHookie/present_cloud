package me.zhengjie.modules.study.service;

import me.zhengjie.modules.study.domain.StudentCourseSign;
import me.zhengjie.modules.study.service.dto.StudentCourseSignDto;
import me.zhengjie.modules.study.service.dto.StudentCourseSignQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;


public interface StudentCourseSignService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(StudentCourseSignQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<StudentCourseSignDto>
    */
    List<StudentCourseSignDto> queryAll(StudentCourseSignQueryCriteria criteria);

    /**
     * 通过某次课程的签到历史id获得所有学生那次的签到情况
     * @param id
     * @return
     */
    Map<String, Object> findByHistoryId(Long id);

    void updateSignByIds(Long[] ids);

    /**
     * 根据ID查询
     * @param id ID
     * @return StudentCourseSignDto
     */
    StudentCourseSignDto findById(Long id);

    /**
    * 创建学生班课签到--返回签到Dto
    * @param resources /
    * @return StudentCourseSignDto
    */
    StudentCourseSignDto create(StudentCourseSign resources);

    /**
     * 创建学生班课签到--无返回值
     * @param resources /
     */
    void create(List<StudentCourseSign> resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(StudentCourseSign resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(Long[] ids);

    /**
     * 通过签到记录id查找该条记录的学生用户
     * @param id /
     */
    Set<Long> findSignedStudentsById(Long id);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<StudentCourseSignDto> all, HttpServletResponse response) throws IOException;
}