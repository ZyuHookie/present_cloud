package me.zhengjie.modules.study.service.dto;

import me.zhengjie.modules.study.domain.SysVal;
import lombok.Data;
import java.io.Serializable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


@Data
public class UserSysValDto implements Serializable {

    /**
     * 防止精度丢失
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private Long userId;

    private SysValDto sysVal;

    private Integer value;
}