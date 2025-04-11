package com.course_graph.service;

import com.course_graph.dto.ClassroomDTO;
import com.course_graph.dto.ScheduleDTO;
import com.course_graph.entity.ScheduleEntity;
import com.course_graph.entity.SubjectEntity;
import com.course_graph.entity.SubjectTypeEntity;
import com.course_graph.enums.Type;
import com.course_graph.repository.ScheduleRepository;
import com.course_graph.repository.SubjectRepository;
import com.course_graph.repository.SubjectTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final SubjectRepository subjectRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubjectTypeRepository subjectTypeRepository;
    private final UserService userService;
    private final int currentYear = 2025;

    public List<ScheduleDTO> getAllSchedules() {
        List<SubjectEntity> subjectEntityList = subjectRepository.findAllByDeletedAt(currentYear + 1);
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();

        for (SubjectEntity subjectEntity : subjectEntityList) {
            for (int i=1; i<=4; i++) {
                List<ScheduleEntity> scheduleEntityList = scheduleRepository.findAllBySubjectEntityAndClassNumber(subjectEntity, i);
                if (scheduleEntityList.isEmpty()) break ;

                List<ClassroomDTO> classroomDTOList = new ArrayList<>();
                for (ScheduleEntity scheduleEntity : scheduleEntityList) {
                    ClassroomDTO classroomDTO = ClassroomDTO.toClassroomDTO(scheduleEntity.getTime(), scheduleEntity.getRoom());
                    classroomDTOList.add(classroomDTO);
                }

                ScheduleDTO scheduleDTO = ScheduleDTO.toScheduleDTO(subjectEntity, getSubjectType(subjectEntity), scheduleEntityList.get(0), classroomDTOList);
                scheduleDTOList.add(scheduleDTO);
            }
        }
        return scheduleDTOList;
    }

    public Type getSubjectType(SubjectEntity subjectEntity) {
        Optional<SubjectTypeEntity> optionalSubjectType = subjectTypeRepository
                .findBySubjectEntityAndEndedAtGreaterThan(subjectEntity, currentYear);
        return optionalSubjectType.get().getType();
    }
}
