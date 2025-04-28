package com.happ.coursegraph.ui.dashboard

data class MajorStatusData(
    val requiredMinCredit: Int,
    val electiveMinCredit: Int,
    val totalRequiredCredit: Int,
    val totalElectiveCredit: Int,
    val notTakenRequiredSubjects: List<String>
)


data class SubjectStatus(
    val name: String,
    val grade: String,
    val status: String
)


