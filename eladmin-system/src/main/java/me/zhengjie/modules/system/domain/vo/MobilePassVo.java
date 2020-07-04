package me.zhengjie.modules.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MobilePassVo implements Serializable {

    private String role;

    private String newPassword;

    private String oldPassword;

    private String count;
}
