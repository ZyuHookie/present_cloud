package me.zhengjie.modules.study.service.impl;

import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.study.domain.StudentCourseSign;
import me.zhengjie.modules.study.domain.SignHistory;
import me.zhengjie.modules.study.repository.SignHistoryRepository;
import me.zhengjie.modules.study.service.SignHistoryService;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.modules.study.repository.StudentCourseSignRepository;
import me.zhengjie.modules.study.service.StudentCourseSignService;
import me.zhengjie.modules.study.service.dto.StudentCourseSignDto;
import me.zhengjie.modules.study.service.dto.StudentCourseSignQueryCriteria;
import me.zhengjie.modules.study.service.mapstruct.StudentCourseSignMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @author zyuh
* @date 2020-05-15
*/
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "studentCourseSign")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StudentCourseSignServiceImpl implements StudentCourseSignService {

    private final StudentCourseSignRepository studentCourseSignRepository;
    private final StudentCourseSignMapper studentCourseSignMapper;
    private final SignHistoryRepository signHistoryRepository;
    private final SignHistoryService signHistoryService;


    @Override
    //@Cacheable
    public Map<String,Object> queryAll(StudentCourseSignQueryCriteria criteria, Pageable pageable){
        Page<StudentCourseSign> page = studentCourseSignRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(studentCourseSignMapper::toDto));
    }

    @Override
    //@Cacheable
    public List<StudentCourseSignDto> queryAll(StudentCourseSignQueryCriteria criteria){
        return studentCourseSignMapper.toDto(studentCourseSignRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    //@Cacheable(key = "'findByHistoryId'+#p0")
    public Map<String, Object> findByHistoryId(Long id) {
        List<StudentCourseSignDto> signs = studentCourseSignMapper.toDto(studentCourseSignRepository.findBySignHistory_Id(id));
        Set<StudentCourseSignDto> attendances = new HashSet<>();
        Set<StudentCourseSignDto> absences = new HashSet<>();
        // 分为签到的（包括补签）和未签到
        for (StudentCourseSignDto sign: signs) {
            if(sign.getAttendance() || sign.getReplenish()) {
                attendances.add(sign);
            } else {
                absences.add(sign);
            }
        }
        Map<String, Object> result = new HashMap<>(2);
        result.put("attendances", attendances);
        result.put("absences", absences);
        return result;
    }

    @Override
    //@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void updateSignByIds(Long[] ids) {
        // 修改补签记录
        Long sighHistoryId = null;
        for (Long id:ids) {
            StudentCourseSign sign = studentCourseSignRepository.findById(id).orElseGet(StudentCourseSign::new);
            if (sighHistoryId == null) {
                sighHistoryId = sign.getSignHistory().getId();
            }
            ValidationUtil.isNull( sign.getId(),"StudentCourseSign","id",id);
            sign.setReplenish(true);
            studentCourseSignRepository.save(sign);
        }
        // 修改该课程该次的签到历史里出勤人数
        SignHistory signHistory = signHistoryRepository.findById(sighHistoryId).orElseGet(SignHistory::new);
        ValidationUtil.isNull( signHistory.getId(),"SighHistory","id", sighHistoryId);
        signHistory.setAttendance(signHistory.getAttendance() + ids.length);
        signHistory.setAbsence(signHistory.getAbsence() - ids.length);
        signHistoryService.update(signHistory);

    }

    @Override
    //@Cacheable(key = "#p0")
    public StudentCourseSignDto findById(Long id) {
        StudentCourseSign studentCourseSign = studentCourseSignRepository.findById(id).orElseGet(StudentCourseSign::new);
        ValidationUtil.isNull(studentCourseSign.getId(),"StudentCourseSign","id",id);
        return studentCourseSignMapper.toDto(studentCourseSign);
    }

    @Override
    //@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public StudentCourseSignDto create(StudentCourseSign resources) {
        return studentCourseSignMapper.toDto(studentCourseSignRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(List<StudentCourseSign> resources) {
        studentCourseSignRepository.saveAll(resources);
    }

    @Override
    //@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(StudentCourseSign resources) {
        StudentCourseSign studentCourseSign = studentCourseSignRepository.findById(resources.getId()).orElseGet(StudentCourseSign::new);
        ValidationUtil.isNull( studentCourseSign.getId(),"StudentCourseSign","id",resources.getId());
        studentCourseSign.copy(resources);
        studentCourseSignRepository.save(studentCourseSign);
    }

    @Override
    //@CacheEvict(allEntries = true)
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            studentCourseSignRepository.deleteById(id);
        }
    }

    @Override
    public Set<Long> findSignedStudentsById(Long id) {
        return studentCourseSignRepository.findAllStudentIdById(id);
    }

    @Override
    public void download(List<StudentCourseSignDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StudentCourseSignDto studentCourseSign : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            /*map.put("签到历史id", studentCourseSign.getSignHistoryId());
            map.put("学生id", studentCourseSign.getStudentId());*/
            map.put("是否出勤", studentCourseSign.getAttendance());
            map.put("是否补签", studentCourseSign.getReplenish());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}