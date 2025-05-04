package com.happ.coursegraph.ui.timetable


data class Subject(
    val name: String,
    val timeSlots: List<TimeSlot>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Subject) return false
        return name == other.name && timeSlots == other.timeSlots
    }

    override fun hashCode(): Int {
        return name.hashCode() * 31 + timeSlots.hashCode()
    }
}

data class TimeSlot(
    val day: String,
    val period: Int
)

data class GeneralSchedule(
    val name: String,
    val timeList: List<String>,
    val status: String // "ADD" 또는 "DELETE"
)

data class ScheduleSubject(
    val code: String,
    val name: String,
    val credit: Int,
    val type: String,
    val professor: String,
    val classNumber: Int,
    val classroomList: List<Classroom>,
    var status : String = "ADD"
) {
    fun formattedTime(): String {
        return classroomList.joinToString(", ") {
            it.time.replace(",", "교시").replace(" ", "") + "교시"
        }
    }

    fun toSubject(): Subject {
        val timeSlots = mutableListOf<TimeSlot>()
        val regex = Regex("""([월화수목금])\s+([\d\s,]+)""")

        for (classroom in classroomList) {
            val match = regex.find(classroom.time)
            if (match != null) {
                val dayKor = match.groupValues[1]
                val dayEng = when (dayKor) {
                    "월" -> "mon"
                    "화" -> "tue"
                    "수" -> "wed"
                    "목" -> "thu"
                    "금" -> "fri"
                    else -> ""
                }

                val periods = match.groupValues[2]
                    .replace(" ", "")
                    .split(",")
                    .mapNotNull { it.toIntOrNull() }

                periods.forEach { timeSlots.add(TimeSlot(dayEng, it)) }
            }
        }

        return Subject(this.name, timeSlots)
    }


}

data class Classroom(
    val time: String,
    val room: String
)

data class RecommendResult(
    val schedule: List<RecommendSubject>,
    val generalSubjects: List<RecommendSubject>,
    val totalCredit: Int
)


data class RecommendSubject(
    val name: String,
    val timeList: List<String>
)