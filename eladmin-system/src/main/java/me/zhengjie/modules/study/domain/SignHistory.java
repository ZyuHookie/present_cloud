package me.zhengjie.modules.study.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;
import me.zhengjie.modules.system.domain.Dict;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.io.Serializable;


@Entity
@Getter
@Setter
@Table(name="sign_history")
@NoArgsConstructor
public class SignHistory implements Serializable {

    public SignHistory(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty(value = "课程id")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    /** 方便查询，没有实际显示 **/
    @Column(name = "course_id", insertable=false, updatable=false)
    private Long courseId;


    @ApiModelProperty(value = "出勤人数")
    @Column(name = "attendance")
    private Integer attendance = 0;


    @ApiModelProperty(value = "缺勤人数")
    @Column(name = "absence")
    private Integer absence = 0;

    @ApiModelProperty(value = "状态")
    @Column(name = "status")
    private Boolean status = true;

    @ApiModelProperty(value = "班课编码")
    @Column(name = "code")
    private String code;

    @ApiModelProperty(value = "发起时间")
    @Column(name = "create_time")
    @CreationTimestamp
    private Timestamp createTime;

    public void copy(SignHistory source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    public String toString() {
        return "SignHistory{" +
                "id=" + id +
                ", course=" + course.getCourseName() +
                ", code=" + code +
                ", attendance=" + code +
                ", absence=" + absence +
                ", createTime=" + createTime +
                '}';
    }
}