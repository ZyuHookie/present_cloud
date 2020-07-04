package me.zhengjie.modules.study.domain;

import me.zhengjie.modules.system.domain.User;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import me.zhengjie.modules.system.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Getter
@Setter
@Table(name="user_sys_val")
public class UserSysVal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;


    @OneToOne
    @JoinColumn(name = "val_id")
    private SysVal sysVal;

    @Column(name = "value")
    private Integer value;

    public void copy(UserSysVal source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}