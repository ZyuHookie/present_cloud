package me.zhengjie.modules.study.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import me.zhengjie.base.BaseEntity;
import me.zhengjie.modules.system.domain.Dept;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import java.sql.Timestamp;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="student")
@NoArgsConstructor
public class Student extends BaseEntity implements Serializable {

    public Student(Long id) {
        this.id = id;
    }

    /** 学生id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty(value = "姓名")
    @Column(name = "name",nullable = false)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "学号")
    @Column(name = "student_number",nullable = false)
    @NotBlank
    private String studentNumber;

    @ApiModelProperty(value = "创建日期")
    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    private String password;

    @JsonIgnore
    @ApiModelProperty(value = "班课")
    @ManyToMany(mappedBy = "students")
    private Set<Course> courses;

    @ApiModelProperty(value = "院校")
    @OneToOne
    @JoinColumn(name = "college_id")
    private Dept college;

    @ApiModelProperty(value = "邮箱")
    @Column(name = "email",nullable = false)
    @NotBlank
    private String email;

    /** 状态：1启用、0禁用 */
    @Column(name = "enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "手机号")
    @Column(name = "phone",nullable = false)
    @NotBlank
    private String phone;

    @ApiModelProperty(value = "最后修改密码时间")
    @Column(name = "last_password_reset_time")
    private Timestamp lastPasswordResetTime;

    @ApiModelProperty(value = "性别")
    @Column(name = "sex")
    private String sex = "男";

    public void copy(Student source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
