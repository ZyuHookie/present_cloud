package me.zhengjie.modules.study.service.dto;

import me.zhengjie.modules.system.service.dto.DeptSmallDto;
import lombok.Data;

@Data
public class CouserSmallDto {

    private Long id;

    private String className;

    private String courseName;

    private String teacherName;

    private String courseCode;

    private String semester;

    /** 所属院校 */
    private DeptSmallDto college;
}
