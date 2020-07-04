package me.zhengjie.modules.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


@Data
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MobileUserVo implements Serializable {

    private Long id;

    private String phone;

    private String name;

    private String sex;

    private String status;

    private String school;

    private String college;

    private String number;

    private String email;
}
