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
        sb.append("Your goal is to recommend up to two subject combinations whose total credits are as close as possible to the target.\n");
        sb.append("The selected classes must meet the following constraints:\n\n");
        sb.append("1. Classes must not overlap in time.\n");
        sb.append("2. If a subject with the same code, name, and classNumber is selected, all of its time slots must be included.\n");
        sb.append("3. You may return up to two subject combinations. Each combination may include multiple schedules.\n");
        sb.append("4. The total credit of each combination should be as close as possible to the target.\n");
        sb.append("5. Only select from the candidate list provided.\n");
        sb.append("6. The user has existing non-major schedules. You must avoid any time conflicts with these as well.\n\n");

        sb.append("Target credit: ").append(targetCredit).append("\n\n");

        sb.append("Here is the list of available subject schedules:\n\n");
        sb.append("[");

        for (int i = 0; i < schedules.size(); i++) {
            ScheduleDTO dto = schedules.get(i);
            sb.append("{\n");
            sb.append("  \"code\": \"").append(dto.getCode()).append("\",\n");
            sb.append("  \"name\": \"").append(dto.getName()).append("\",\n");
            sb.append("  \"classNumber\": ").append(dto.getClassNumber()).append(",\n");
            sb.append("  \"credit\": ").append(dto.getCredit()).append(",\n");
            sb.append("  \"times\": [");
            Map<String, List<String>> grouped = new LinkedHashMap<>();
            for (ClassroomDTO classroom : dto.getClassroomList()) {
                String[] parts = classroom.getTime().split(" ");
                String day = parts[0];
                String period = parts[1];
                grouped.computeIfAbsent(day, k -> new ArrayList<>()).add(period);
            }
            int k = 0;
            for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
                sb.append("\"").append(entry.getKey()).append(" ").append(String.join(" ,", entry.getValue())).append("\"");
                if (++k != grouped.size()) sb.append(", ");
            }
            sb.append("],\n");
            sb.append("  \"professor\": \"").append(dto.getProfessor()).append("\",\n");
            sb.append("  \"room\": \"").append(dto.getClassroomList().isEmpty() ? "" : dto.getClassroomList().get(0).getRoom()).append("\"\n");
            sb.append("}");
            if (i != schedules.size() - 1) sb.append(",\n");
        }

        sb.append("]\n\n");

        sb.append("Here is the user's current non-major schedule. Avoid time conflicts with these times:\n");
        sb.append("[");
        for (int i = 0; i < nonMajorSchedules.size(); i++) {
            ScheduleTimeDTO dto = nonMajorSchedules.get(i);
            sb.append("{\n");
            sb.append("  \"name\": \"").append(dto.getName()).append("\",\n");
            sb.append("  \"timeList\": [");
            Map<String, List<String>> grouped = new LinkedHashMap<>();
            for (String time : dto.getTimeList()) {
                String[] parts = time.split(" ");
                String day = parts[0];
                String period = parts[1];
                grouped.computeIfAbsent(day, k -> new ArrayList<>()).add(period);
            }
            int j = 0;
            for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
                sb.append("\"").append(entry.getKey()).append(" ").append(String.join(" ,", entry.getValue())).append("\"");
                if (++j != grouped.size()) sb.append(", ");
            }
            sb.append("]\n");
            sb.append("}");
            if (i != nonMajorSchedules.size() - 1) sb.append(",\n");
        }
        sb.append("]\n\n");

        sb.append("Return the result as a JSON array in the following format:\n\n");
        sb.append("[\n");
        sb.append("  {\n");
        sb.append("    \"schedule\": [\n");
        sb.append("      {\n");
        sb.append("        \"name\": \"...\",\n");
        sb.append("        \"timeList\": [\"...\", \"...\"]\n");
        sb.append("      },\n");
        sb.append("      ...\n");
        sb.append("    ],\n");
        sb.append("    \"totalCredit\": ...\n");
        sb.append("  },\n");
        sb.append("  ...\n");
        sb.append("]\n\n");

        sb.append("Respond ONLY with valid JSON. No explanation, no code block, no commentary.\n");

        return sb.toString();
    }
}
