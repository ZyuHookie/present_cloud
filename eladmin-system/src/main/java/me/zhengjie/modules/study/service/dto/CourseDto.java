package me.zhengjie.modules.study.service.dto;

import me.zhengjie.modules.system.service.dto.DeptSmallDto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;


@Data
public class CourseDto implements Serializable {

    private Long id;

    private String className;

    private String courseName;

    private String courseCode;

    @NotNull
    private Boolean enabled;

    @NotNull
    private Boolean joinPermission;

    private String semester;

    /** 选课人数 */
    private Integer studentCount;

    private Integer count;

    /** 授课老师姓名 */
    private String teacherName;

    /** 所属院校 */
    private DeptSmallDto college;

    /** 课程创建者名字 */
    private String userName;

    /** 签到发起次数 */
    private Integer signCount;

    private List<SignHistorySmallDto> signHistory;


}