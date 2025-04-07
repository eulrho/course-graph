package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.dto.*;
import com.course_graph.entity.*;
import com.course_graph.enums.CustomErrorCode;
import com.course_graph.enums.SubjectStatus;
import com.course_graph.enums.Type;
import com.course_graph.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final UserService userService;
    private final SubjectRepository subjectRepository;
    private final HistoryRepository historyRepository;
    private final GraduationRepository graduationRepository;
    private final SubjectTypeRepository subjectTypeRepository;
    private final SubjectEquivalenceRepository subjectEquivalenceRepository;
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
            CourseStatusDTO courseStatusDTO = new CourseStatusDTO(courseDTO, SubjectStatus.NOT_TAKEN.toString());
            if (isTakenSubject(subjectEntity, userEntity))
                courseStatusDTO.setStatus(SubjectStatus.TAKEN.toString());
            courseStatusDTOList.add(courseStatusDTO);
        }

        List<HistoryEntity> historyEntityList = userEntity.getHistoryEntityList();
        for (HistoryEntity data : historyEntityList) {
            SubjectEntity takenSubjectEntity = data.getSubjectEntity();
            if (takenSubjectEntity.getDeletedAt() <= currentYear) {
                CourseDTO courseDTO = CourseDTO.toCourseDTO(takenSubjectEntity.getName(),
                        extractSubjectTracks(takenSubjectEntity), takenSubjectEntity.getGrade());
                CourseStatusDTO courseStatusDTO = new CourseStatusDTO(courseDTO, SubjectStatus.NOT_TAKEN.toString());
                courseStatusDTO.setStatus(SubjectStatus.TAKEN.toString());
                courseStatusDTOList.add(courseStatusDTO);
            }
        }
        return courseStatusDTOList;
    }

    public List<CourseStatusDTO> getUserAllSubjects(String grade, String email) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        List<CourseStatusDTO> courseStatusDTOList = new ArrayList<>();
        List<SubjectEntity> subjectEntityList = subjectRepository.findAllByGrade(grade);

        for (SubjectEntity subjectEntity : subjectEntityList) {
            SubjectDTO subjectDTO = SubjectDTO.toSubjectDTO(subjectEntity);
            CourseStatusDTO courseStatusDTO = new CourseStatusDTO(subjectDTO, SubjectStatus.NOT_TAKEN.toString());
            if (isTakenSubject(subjectEntity, userEntity))
                courseStatusDTO.setStatus(SubjectStatus.TAKEN.toString());
            courseStatusDTOList.add(courseStatusDTO);
        }
        return courseStatusDTOList;
    }

    public GraduationResponse getUserGraduationInfo(String email) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        Optional<GraduationEntity> optionalGraduation = graduationRepository.findByYear(userEntity.getYear());

        int totalRequiredCredit = 0, totalElectiveCredit = 0;
        List<String> notTakenRequiredSubjects = new ArrayList<>();
        List<SubjectEntity> subjectEntityList = subjectRepository.findAllByDeletedAtGreaterThan(currentYear);
        for (SubjectEntity subjectEntity : subjectEntityList) {
            Optional<SubjectTypeEntity> optionalSubjectType = subjectTypeRepository
                    .findBySubjectEntityAndEndedAtGreaterThan(subjectEntity, userEntity.getYear());
            SubjectTypeEntity subjectTypeEntity = optionalSubjectType.get();

            if (isTakenSubject(subjectEntity, userEntity)) {
                if (subjectTypeEntity.getType().equals(Type.MAJOR_REQUIRED.toString()))
                    totalRequiredCredit += subjectEntity.getCredit();
                else totalElectiveCredit += subjectEntity.getCredit();
            } else {
                if (subjectTypeEntity.getType().equals(Type.MAJOR_REQUIRED.toString())
                        && !isTakenSubject(getOriginalSubject(subjectEntity), userEntity))
                    notTakenRequiredSubjects.add(subjectEntity.getName());
            }
        }

        List<HistoryEntity> historyEntityList = userEntity.getHistoryEntityList();
        for (HistoryEntity data : historyEntityList) {
            SubjectEntity takenSubjectEntity = data.getSubjectEntity();
            Optional<SubjectTypeEntity> optionalSubjectType = subjectTypeRepository
                    .findBySubjectEntityAndEndedAtGreaterThan(takenSubjectEntity, userEntity.getYear());
            SubjectTypeEntity subjectTypeEntity = optionalSubjectType.get();
            if (takenSubjectEntity.getDeletedAt() <= currentYear) {
                if (subjectTypeEntity.getType().equals(Type.MAJOR_REQUIRED.toString()))
                    totalRequiredCredit += takenSubjectEntity.getCredit();
                else totalElectiveCredit += takenSubjectEntity.getCredit();
            }
        }
        GraduationResponse graduationResponse = GraduationResponse.toGraduationResponse(optionalGraduation.get(),
                totalRequiredCredit, totalElectiveCredit, notTakenRequiredSubjects);
        return graduationResponse;
    }

    public List<String> extractSubjectTracks(SubjectEntity subjectEntity) {
        List<String> tracks = new ArrayList<>();
        List<CurriculumEntity> curriculumEntityList = subjectEntity.getCurriculumEntityList();
        for (CurriculumEntity curriculum : curriculumEntityList)
            tracks.add(curriculum.getTrack().getMessage());
        return tracks;
    }

    public boolean isTakenSubject(SubjectEntity subjectEntity, UserEntity userEntity) {
        if (subjectEntity == null) return false;
        Optional<HistoryEntity> optionalHistoryEntity = historyRepository.findByUserEntityAndSubjectEntity(userEntity, subjectEntity);
        return optionalHistoryEntity.isPresent();
    }

    public Page<HistoryEntity> findHistories(UserEntity userEntity, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        return historyRepository.findAllByUserEntityOrderByIdAsc(userEntity, pageRequest);
    }

    public SubjectEntity getEquivalenceSubject(SubjectEntity subjectEntity) {
        Optional<SubjectEquivalenceEntity> optionalSubjectEquivalence = subjectEquivalenceRepository
                .findByOriginalSubjectEntity(subjectEntity);
        if (optionalSubjectEquivalence.isPresent()) {
            return optionalSubjectEquivalence.get().getEquivalenceSubjectEntity();
        }
        return null;
    }

    public SubjectEntity getOriginalSubject(SubjectEntity subjectEntity) {
        Optional<SubjectEquivalenceEntity> optionalSubjectEquivalence = subjectEquivalenceRepository
                .findByEquivalenceSubjectEntity(subjectEntity);
        if (optionalSubjectEquivalence.isPresent()) {
            return optionalSubjectEquivalence.get().getOriginalSubjectEntity();
        }
        return null;
    }

    @Transactional
    public void updateUserScores(List<ScoreUpdateRequest> updateRequests, String email) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        for (ScoreUpdateRequest updateRequest : updateRequests) {
            Optional<SubjectEntity> optionalSubject = subjectRepository.findByName(updateRequest.getSubjectName());
            Optional<HistoryEntity> optionalHistory = historyRepository.findByUserEntityAndSubjectEntity(userEntity, optionalSubject.get());
            HistoryEntity historyEntity = optionalHistory.get();
            historyEntity.edit(updateRequest.getScore());
        }
    }

    @Transactional
    public void updateUserTakenSubjectList(List<StatusUpdateRequest> updateRequests, String email) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        for (StatusUpdateRequest updateRequest : updateRequests) {
            Optional<SubjectEntity> optionalSubject = subjectRepository.findByName(updateRequest.getSubjectName());
            Optional<HistoryEntity> optionalHistory = historyRepository.findByUserEntityAndSubjectEntity(userEntity, optionalSubject.get());
            if (updateRequest.getStatus().equals(SubjectStatus.TAKEN.toString())) {
                if (optionalHistory.isPresent()) continue ;
                HistoryEntity historyEntity = HistoryEntity.toHistoryEntity(userEntity, optionalSubject.get(), "");
                historyRepository.save(historyEntity);
            }
            else if (updateRequest.getStatus().equals(SubjectStatus.NOT_TAKEN.toString()))
                optionalHistory.ifPresent(historyRepository::delete);
            else throw new RestApiException(CustomErrorCode.INVALID_PARAMETER);
        }
    }
}