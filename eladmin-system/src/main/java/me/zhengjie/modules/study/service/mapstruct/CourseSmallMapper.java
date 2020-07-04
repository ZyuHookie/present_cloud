package me.zhengjie.modules.study.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.study.domain.Course;
import me.zhengjie.modules.study.service.dto.CouserSmallDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseSmallMapper extends BaseMapper<CouserSmallDto, Course> {
}
