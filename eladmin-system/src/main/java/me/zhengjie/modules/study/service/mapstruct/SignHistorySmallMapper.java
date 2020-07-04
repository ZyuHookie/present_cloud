package me.zhengjie.modules.study.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.study.domain.SignHistory;
import me.zhengjie.modules.study.service.dto.SignHistorySmallDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SignHistorySmallMapper extends BaseMapper<SignHistorySmallDto, SignHistory> {
}
