package me.zhengjie.modules.study.repository;

import me.zhengjie.modules.study.domain.SysVal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SysValRepository  extends JpaRepository<SysVal, Long>, JpaSpecificationExecutor<SysVal> {
}
