package me.zhengjie.modules.study.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.study.domain.Student;
import me.zhengjie.modules.study.service.dto.StudentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper extends BaseMapper<StudentDto, Student> {

    @Mapping(source = "experience", target = "experience")
    StudentDto toDto(Student entity, Integer experience);
}