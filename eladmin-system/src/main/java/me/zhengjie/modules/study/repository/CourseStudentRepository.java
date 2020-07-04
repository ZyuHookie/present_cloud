package me.zhengjie.modules.study.repository;

import me.zhengjie.modules.study.domain.CourseStudent;
import me.zhengjie.modules.study.domain.SignHistoryPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@SuppressWarnings("all")
public interface CourseStudentRepository extends JpaRepository<CourseStudent, SignHistoryPrimaryKey> {

    List<CourseStudent> findByIdCourseId(Long courseId);

    Integer countByIdCourseId(Long courseId);

    @Modifying
    @Query(value = "update course_student set experience = ?3 where course_id = ?1 and student_id = ?2",nativeQuery = true)
    void updateExperience(Long courseId, Long studentId, int experience);

}
