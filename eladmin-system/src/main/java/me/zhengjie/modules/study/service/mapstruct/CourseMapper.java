package me.zhengjie.modules.study.service.mapstruct;

import me.zhengjie.modules.study.domain.Course;
import me.zhengjie.modules.study.service.dto.CourseDto;
import me.zhengjie.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper extends BaseMapper<CourseDto, Course> {
    @Override
    @Mapping(source = "course.createUser.nickName",target = "userName")
    CourseDto toDto(Course course);
}