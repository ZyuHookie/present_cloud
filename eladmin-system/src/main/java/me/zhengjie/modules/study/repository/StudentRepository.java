package me.zhengjie.modules.study.repository;

import me.zhengjie.modules.study.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;


@SuppressWarnings("all")
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    /**
     * 根据用户名查询
     * @param name 用户名
     * @return /
     */
    Student findByName(String name);

    /**
     * 根据邮箱查询
     * @param email 邮箱
     * @return /
     */
    Student findByEmail(String email);

    /**
     * 按手机号查找
     * @param phone
     * @return
     */
    Student findByPhone(String phone);

    /**
     * 修改密码
     * @param phone
     * @param pass
     * @param lastPasswordResetTime
     * @return
     */
    @Modifying
    @Query(value = "update student set password = ?2 , last_password_reset_time = ?3 where phone = ?1",nativeQuery = true)
    void updatePass(String phone, String pass, Date lastPasswordResetTime);
}