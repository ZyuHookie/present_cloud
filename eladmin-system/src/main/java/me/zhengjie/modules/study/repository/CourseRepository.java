package me.zhengjie.modules.study.repository;

import me.zhengjie.modules.study.domain.Course;
import me.zhengjie.modules.study.domain.SchoolCourse;
import me.zhengjie.modules.study.service.dto.CourseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;


public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    Course findByCourseCode(String code);

    @Query(value = "select course_code from course" , nativeQuery = true)
    List<String> findCourseCodes();

    List<Course> findByUserId(Long id);

    @Modifying
    @Query(value = "update course set sign_count = ?2 where id = ?1",nativeQuery = true)
    void updateSignCountById(Long id, int account);

    /**
     * 根据院校查询
     * @param collegeIds /
     * @return /
     */
    @Query(value = "SELECT count(1) FROM course c WHERE c.college_id IN ?1", nativeQuery = true)
    int countByDepts(Set<Long> collegeIds);

    @Query(value = "select sc.course_name from school_course sc WHERE sc.college_id = ?1" , nativeQuery = true)
    List<String> getScoolCourseList(Long college_id);

    @Query(value = "SELECT sc.* FROM course sc order by id desc ", nativeQuery = true)
    List<Course> findAll();
    /* school_course Repository 方法 */

}