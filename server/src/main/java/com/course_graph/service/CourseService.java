package com.course_graph.service;

import com.course_graph.dto.*;
import com.course_graph.entity.CurriculumEntity;
import com.course_graph.entity.HistoryEntity;
import com.course_graph.entity.SubjectEntity;
import com.course_graph.entity.UserEntity;
import com.course_graph.enums.SubjectStatus;
import com.course_graph.repository.HistoryRepository;
import com.course_graph.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final UserService userService;
    private final SubjectRepository subjectRepository;
    private final HistoryRepository historyRepository;
    private final int currentYear = 2025;

    public Page<HistoryDTO> getUserHistory(String email, int page) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        return findHistories(userEntity, page, 12)
                .map(HistoryDTO::toHistoryDTO);
    }

    public List<CourseStatusDTO> getUserCourse(String email) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        List<CourseStatusDTO> courseStatusDTOList = new ArrayList<>();
        List<SubjectEntity> subjectEntityList = subjectRepository.findAllByDeletedAtGreaterThan(currentYear);
        for (SubjectEntity subjectEntity : subjectEntityList) {
            CourseDTO courseDTO = CourseDTO.toCourseDTO(subjectEntity.getName(), extractSubjectTracks(subjectEntity), subjectEntity.getGrade());
            CourseStatusDTO courseStatusDTO = CourseStatusDTO.toCourseStatusDTO(courseDTO);
            if (isTakenSubject(subjectEntity, userEntity))
                courseStatusDTO.setStatus(SubjectStatus.TAKEN.toString());
            courseStatusDTOList.add(courseStatusDTO);
        }

        List<HistoryEntity> historyEntityList = userEntity.getHistoryEntityList();
        for (HistoryEntity data : historyEntityList) {
            SubjectEntity takenSubjectEntity = data.getSubjectEntity();
            if (takenSubjectEntity.getDeletedAt() <= currentYear) {
                CourseDTO courseDTO = CourseDTO.toCourseDTO(takenSubjectEntity.getName(), extractSubjectTracks(takenSubjectEntity), takenSubjectEntity.getGrade());
                CourseStatusDTO courseStatusDTO = CourseStatusDTO.toCourseStatusDTO(courseDTO);
                courseStatusDTO.setStatus(SubjectStatus.TAKEN.toString());
                courseStatusDTOList.add(courseStatusDTO);
            }
        }
        return courseStatusDTOList;
    }

    public List<String> extractSubjectTracks(SubjectEntity subjectEntity) {
        List<String> tracks = new ArrayList<>();
        List<CurriculumEntity> curriculumEntityList = subjectEntity.getCurriculumEntityList();
        for (CurriculumEntity curriculum : curriculumEntityList)
            tracks.add(curriculum.getTrack().getMessage());
        return tracks;
    }

    public boolean isTakenSubject(SubjectEntity subjectEntity, UserEntity userEntity) {
        Optional<HistoryEntity> optionalHistoryEntity = historyRepository.findByUserEntityAndSubjectEntity(userEntity, subjectEntity);
        return optionalHistoryEntity.isPresent();
    }

    public Page<HistoryEntity> findHistories(UserEntity userEntity, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);

        return historyRepository.findAllByUserEntityOrderByIdAsc(userEntity, pageRequest);
    }
}