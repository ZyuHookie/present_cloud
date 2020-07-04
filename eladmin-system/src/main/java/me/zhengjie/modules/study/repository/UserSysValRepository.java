package me.zhengjie.modules.study.repository;

import me.zhengjie.modules.study.domain.UserSysVal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserSysValRepository extends JpaRepository<UserSysVal, Long>, JpaSpecificationExecutor<UserSysVal> {

    List<UserSysVal> findByUserId(Long id);

    @Query(value = "select val_id from user_sys_val where user_id = ?1", nativeQuery = true)
    List<Long> findValIds(Long userId);
}