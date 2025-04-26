package com.course_graph.service;
import com.course_graph.entity.UserGeneralScheduleEntity;
import com.course_graph.entity.UserScheduleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ValidateScheduleService {
    public boolean isTimeConflictInSchedules(List<UserScheduleEntity> originList, List<UserScheduleEntity> newScheduleList) {
        return originList.stream()
                .anyMatch(origin -> newScheduleList.stream()
                        .anyMatch(newSchedule -> isContainedTime(origin.getScheduleEntity().getTime(), newSchedule.getScheduleEntity().getTime())));
    }

    public boolean isTimeConflictInGeneralSchedules(List<UserGeneralScheduleEntity> originList, List<UserScheduleEntity> newScheduleList) {
        return originList.stream()
                .anyMatch(origin -> newScheduleList.stream()
                        .anyMatch(newSchedule -> isContainedTime(origin.getTime(), newSchedule.getScheduleEntity().getTime())));
    }

    public boolean isGeneralTimeConflictInSchedules(List<UserScheduleEntity> originList, List<UserGeneralScheduleEntity> newScheduleList) {
        return originList.stream()
                .anyMatch(origin -> newScheduleList.stream()
                        .anyMatch(newSchedule -> isContainedTime(origin.getScheduleEntity().getTime(), newSchedule.getTime())));
    }

    public boolean isGeneralTimeConflictInGeneralSchedules(List<UserGeneralScheduleEntity> originList, List<UserGeneralScheduleEntity> newScheduleList) {
        return originList.stream()
                .anyMatch(origin -> newScheduleList.stream()
                        .anyMatch(newSchedule -> isContainedTime(origin.getTime(), newSchedule.getTime())));
    }

    private boolean isContainedTime(String time, String newTime) {
        if (time.charAt(0) == newTime.charAt(0)) {
            Set<String> timeSet = new HashSet<>(List.of(time.split(" ,")));
            Set<String> newTimeSet = new HashSet<>(List.of(newTime.split(" ,")));

            if (timeSet.size() < newTimeSet.size()) {
                return timeSet.stream().anyMatch(newTimeSet::contains);
            } else {
                return newTimeSet.stream().anyMatch(timeSet::contains);
            }
        }
        return false;
    }
}
