package com.happ.coursegraph.ui.timetable

data class Subject(
    val name: String,
    val professor: String,
    val timeSlots: List<TimeSlot>
)

data class TimeSlot(
    val day: String,   // 예: "mon", "tue", "wed"
    val period: Int    // 예: 1, 2, 3, ...
)