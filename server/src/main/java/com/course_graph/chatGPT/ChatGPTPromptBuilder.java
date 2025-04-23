package com.course_graph.chatGPT;

import com.course_graph.dto.ClassroomDTO;
import com.course_graph.dto.ScheduleDTO;
import com.course_graph.dto.ScheduleTimeDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChatGPTPromptBuilder {
    public static String buildSchedulePrompt(List<ScheduleDTO> schedules, List<ScheduleTimeDTO> nonMajorSchedules, int targetCredit) {
        StringBuilder sb = new StringBuilder();

        sb.append("You are a university course schedule assistant.\n\n");
        sb.append("Your goal is to recommend up to two combinations of subject schedules whose total credits are as close as possible to the target.\n");
        sb.append("Each combination must satisfy the following constraints:\n");
        sb.append("1. Classes must not overlap in time.\n");
        sb.append("2. The user's existing non-major schedule must not conflict with any new schedule.\n");
        sb.append("3. A subject (same name) should appear only once in a combination, even if it has multiple classNumbers.\n");
        sb.append("   For example, if a subject named '캡스톤디자인' exists with multiple classNumbers, select only one.\n");
        sb.append("4. Do not include more than one schedule with the same subject name in a single combination.\n");
        sb.append("5. Return at most two combinations.\n\n");

        sb.append("Target credit: ").append(targetCredit).append("\n\n");

        sb.append("Here is the list of available subject schedules:\n");
        sb.append("[");
        for (int i = 0; i < schedules.size(); i++) {
            ScheduleDTO dto = schedules.get(i);
            sb.append("{\n");
            sb.append("  \"name\": \"").append(dto.getName()).append("\",\n");
            sb.append("  \"credit\": \"").append(dto.getName()).append("\",\n");
            sb.append("  \"classNumber\": \"").append(dto.getClassNumber()).append("\"\n");
            sb.append("}");
            if (i != schedules.size() - 1) sb.append(",\n");
        }
        sb.append("]\n\n");

        sb.append("User's current non-major schedule (avoid time conflict with these):\n");
        sb.append("[");
        for (int i = 0; i < nonMajorSchedules.size(); i++) {
            ScheduleTimeDTO dto = nonMajorSchedules.get(i);
            sb.append("{\n");
            sb.append("  \"name\": \"").append(dto.getName()).append("\",\n");
            sb.append("  \"timeList\": [");
            List<String> times = dto.getTimeList();
            for (int j = 0; j < times.size(); j++) {
                sb.append("\"").append(times.get(j)).append("\"");
                if (j != times.size() - 1) sb.append(", ");
            }
            sb.append("]\n");
            sb.append("}");
            if (i != nonMajorSchedules.size() - 1) sb.append(",\n");
        }
        sb.append("]\n\n");

        sb.append("Return the result in the following JSON format:\n\n");
        sb.append("[\n");
        sb.append("  {\n");
        sb.append("    \"schedules\": [\n");
        sb.append("      {\n");
        sb.append("        \"name\": \"...\",\n");
        sb.append("        \"classNumber\": ...\n");
        sb.append("      },\n");
        sb.append("      ...\n");
        sb.append("    ],\n");
        sb.append("  },\n");
        sb.append("  ...\n");
        sb.append("]\n\n");

        sb.append("Respond only with valid JSON. No explanation, no code block, no commentary.");

        return sb.toString();
    }
}
