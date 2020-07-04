package me.zhengjie.modules.study.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.study.domain.Student;
import me.zhengjie.modules.study.service.dto.StudentSmallDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentSmallMapper extends BaseMapper<StudentSmallDto, Student> {
}
