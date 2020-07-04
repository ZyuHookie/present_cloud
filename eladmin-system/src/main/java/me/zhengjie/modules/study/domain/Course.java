package me.zhengjie.modules.study.domain;

import me.zhengjie.base.BaseEntity;
import me.zhengjie.modules.system.domain.Dept;
import me.zhengjie.modules.system.domain.DictDetail;
import me.zhengjie.modules.system.domain.User;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Getter;
import lombok.Setter;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name="course")
public class Course extends BaseEntity implements Serializable {
    // 课程id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty(value = "专业班级")
    @Column(name = "class_name")
    private String className = "19计算机技术";

    @ApiModelProperty(value = "课程名")
    @Column(name = "course_name")
    private String courseName;

    @ApiModelProperty(value = "课程编码")
    @Column(name = "course_code",nullable = false)
    private String courseCode;

    @ApiModelProperty(value = "课程状态")
    private Boolean enabled;

    @ApiModelProperty(value = "是否开放")
    @Column(name = "join_permission")
    private Boolean joinPermission;

    @ApiModelProperty(value = "学期")
    @Column
    private String semester;


    @ApiModelProperty(value = "选课人数")
    @Column(name = "student_count")
    private Integer studentCount = 0;


    @ApiModelProperty(value = "授课老师姓名")
    @Column(name = "teacher_name")
    private String teacherName;

    @ApiModelProperty(value = "所属院校")
    @OneToOne
    @JoinColumn(name = "college_id")
    private Dept college;

    @ApiModelProperty(value = "课程创建者id")
    @OneToOne
    @JoinColumn(name = "create_uid")
    private User createUser;

    //  方便查询，没有实际显示
    @Column(name = "create_uid", insertable=false, updatable=false)
    private Long userId;


    @ApiModelProperty(value = "发起签到次数")
    @Column(name = "sign_count")
    private Integer signCount = 0;

    @ManyToMany
    @JoinTable(name = "course_student", joinColumns = {@JoinColumn(name = "course_id",referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "student_id",referencedColumnName = "id")})
    private Set<Student> students;

    @OneToMany(mappedBy = "course",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
    private List<SignHistory> signHistory;

    public void copy(Course source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", joinPermission=" + joinPermission +
                ", semester='" + semester + '\'' +
                ", studentCount=" + studentCount +
                ", teacherName='" + teacherName + '\'' +
                ", college=" + college.getName() +
                ", createUser=" + createUser +
                ", signCount=" + signCount +
                ", signHistory=" + signHistory +
                '}';
    }
}