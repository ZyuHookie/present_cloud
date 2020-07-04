package me.zhengjie.modules.study.service.impl;

import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.study.domain.SysVal;
import me.zhengjie.modules.study.domain.UserSysVal;
import me.zhengjie.modules.study.repository.SysValRepository;
import me.zhengjie.utils.*;
import me.zhengjie.modules.study.repository.UserSysValRepository;
import me.zhengjie.modules.study.service.UserSysValService;
import me.zhengjie.modules.study.service.dto.UserSysValDto;
import me.zhengjie.modules.study.service.mapstruct.UserSysValMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
* @author zyuh
* @date 2020-05-25
*/
@Service
@RequiredArgsConstructor
//@CacheConfig(cacheNames = "userSysVal")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserSysValServiceImpl implements UserSysValService {

    private final UserSysValRepository userSysValRepository;
    private final SysValRepository sysValRepository;
    private final UserSysValMapper userSysValMapper;

    /*public UserSysValServiceImpl(UserSysValRepository userSysValRepository, SysValRepository sysValRepository, UserSysValMapper userSysValMapper) {
        this.userSysValRepository = userSysValRepository;
        this.sysValRepository = sysValRepository;
        this.userSysValMapper = userSysValMapper;
    }*/

//    @Override
//    //@Cacheable
//    public Map<String,Object> queryAll(UserSysValQueryCriteria criteria){
////        List<UserSysVal> re =
//        return userSysValMapper.toDto(userSysValRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
//    }

    @Override
    //@Cacheable
    public Map<String, Object> findByUser(Long userId){
        List<UserSysVal> vals = userSysValRepository.findByUserId(userId);
        List<SysVal> temps = vals.stream().map(UserSysVal::getSysVal).collect(Collectors.toList());
        List<Long> ids = temps.stream().map(SysVal::getId).collect(Collectors.toList());
        List<SysVal> sysVals = sysValRepository.findAll();
        sysVals.forEach(sysVal -> {
            /*用户变量表不存在对应的系统变量值，则新增一个*/
            if (!ids.contains(sysVal.getId())) {
                UserSysVal newVal = new UserSysVal();
                newVal.setUserId(userId);
                newVal.setSysVal(new SysVal(sysVal.getId()));
                newVal.setValue(sysVal.getDefaultValue());
                vals.add(userSysValRepository.save(newVal));
            }
        });
        List<UserSysValDto> dtos = userSysValMapper.toDto(vals);
        Map<String,Object> map = new LinkedHashMap<>(2);
        map.put("content",dtos);
        map.put("totalElements",dtos.size());
        return map;
    }

    @Override
    //@Cacheable(key = "#p0")
    public UserSysValDto findById(Long id) {
        UserSysVal userSysVal = userSysValRepository.findById(id).orElseGet(UserSysVal::new);
        ValidationUtil.isNull(userSysVal.getId(),"UserSysVal","id",id);
        return userSysValMapper.toDto(userSysVal);
    }

    @Override
    //@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public UserSysValDto create(UserSysVal resources) {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        resources.setId(snowflake.nextId()); 
        return userSysValMapper.toDto(userSysValRepository.save(resources));
    }

    @Override
    //@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(UserSysVal resources) {
        UserSysVal userSysVal = userSysValRepository.findById(resources.getId()).orElseGet(UserSysVal::new);
        ValidationUtil.isNull( userSysVal.getId(),"UserSysVal","id",resources.getId());
        userSysVal.copy(resources);
        userSysValRepository.save(userSysVal);
    }

    @Override
    //@CacheEvict(allEntries = true)
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            userSysValRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<UserSysValDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserSysValDto userSysVal : all) {
            Map<String,Object> map = new LinkedHashMap<>();
//            map.put(" userId",  userSysVal.());
//            map.put(" valId",  userSysVal.getValId());
            map.put(" value",  userSysVal.getValue());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}