package me.zhengjie.modules.study.domain;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description="学生课程表")
@Table(name="course_student")
public class CourseStudent {
    @EmbeddedId
    private SignHistoryPrimaryKey id;

    Integer experience;  // 额外字段经验值存在联合主键，所以用@EmbeddedId 来标注


}
