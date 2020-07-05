package me.zhengjie.modules.study.repository;

import me.zhengjie.modules.study.domain.SignHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface SignHistoryRepository extends JpaRepository<SignHistory, Long>, JpaSpecificationExecutor<SignHistory> {
    List<SignHistory> findByCourseId(Long id);

    List<SignHistory> findByCourseIdOrderByCreateTimeDesc(Long id);

    @Query(value = "select count(*) where course_id = ?1",nativeQuery = true)
    int getSignCountByCourseId(Long id);

    int countByCourseId(Long id);
}