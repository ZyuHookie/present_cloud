package me.zhengjie.modules.study.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.study.domain.SignHistory;
import me.zhengjie.modules.study.service.dto.SignHistoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SignHistoryMapper extends BaseMapper<SignHistoryDto, SignHistory> {

    @Override
    @Mappings({
            @Mapping(source = "entity.course.courseName", target = "courseName"),
            @Mapping(source = "entity.course.courseCode", target = "courseCode"),
            @Mapping(source = "entity.course.college.name", target = "collegeName"),
            @Mapping(source = "entity.course.teacherName", target = "teacherName")
    })
    SignHistoryDto toDto(SignHistory entity);
}