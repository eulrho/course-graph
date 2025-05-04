package com.happ.coursegraph.ui.timetable

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happ.coursegraph.CourseApplication.Companion.getUserToken
import com.happ.coursegraph.comm.HttpManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class TimeTableViewModel : ViewModel() {

    private val _subjects = MutableLiveData<List<Subject>>()
    val subjects: LiveData<List<Subject>> get() = _subjects

    //학년별 시간표 담는 변수
    private val _scheduleGradeSubjects = MutableLiveData<List<ScheduleSubject>>()
    val scheduleGradeSubjects: LiveData<List<ScheduleSubject>> = _scheduleGradeSubjects

    private val _postScheduleResult = MutableLiveData<Boolean>()
    val postScheduleResult: LiveData<Boolean> get() = _postScheduleResult

    private val _userTimeTableUploadResult = MutableLiveData<Boolean>()
    val userTimeTableUploadResult: LiveData<Boolean> get() = _userTimeTableUploadResult

    val recommendSchedule = MutableLiveData<List<RecommendResult>>()

    fun postScheduleGeneral(schedules: List<GeneralSchedule>) {

        viewModelScope.launch(Dispatchers.IO) {
            val token = getUserToken()
            if (token != null) {
                HttpManager.postScheduleGeneral(
                    token = token,
                    scheduleList = schedules,
                    onSuccessResult = {
                        _postScheduleResult.postValue(true)
                    },
                    onFailure = {
                        _postScheduleResult.postValue(false)
                    }
                )
            } else {
                _postScheduleResult.postValue(false)
            }
        }
    }

    fun postSchedule() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = getUserToken()
            if (token != null) {
                _scheduleGradeSubjects.value?.let {
                    HttpManager.postSchedule(
                        token = token,
                        scheduleList = it,
                        onSuccessResult = { jsonString ->
                            Log.i("##INFO", "postSchedule res json = ${jsonString} ")
                            _userTimeTableUploadResult.postValue(true)
                        },
                        onFailure = {
                            Log.i("##INFO", "fail postSchedule ")
                            _userTimeTableUploadResult.postValue(false)
                        }
                    )
                }
            }
        }
    }

    fun getTimeTableSchedule() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = getUserToken()
            if (token != null) {
                HttpManager.getTimeTableSchedule(
                    token = token,
                    onSuccessResult = { jsonString ->
                        val parsed = parseScheduleJson(jsonString)
                        _subjects.postValue(parsed)
                    },
                    onFailure = {
                        _subjects.postValue(emptyList())
                    }
                )
            }
        }
    }

    fun getScheduleGrade(grade: String) {
        val token = getUserToken()
        viewModelScope.launch(Dispatchers.IO) {
            if (token != null) {
                HttpManager.getScheduleGrade(
                    token = token,
                    grade = grade,
                    onSuccessResult = { jsonString ->
                        try {
                            val list = JSONArray(jsonString)
                            val parsedList = mutableListOf<ScheduleSubject>()

                            for (i in 0 until list.length()) {
                                val item = list.getJSONObject(i)

                                val classroomArray = item.getJSONArray("classroomList")
                                val classroomList = mutableListOf<Classroom>()

                                for (j in 0 until classroomArray.length()) {
                                    val roomItem = classroomArray.getJSONObject(j)
                                    classroomList.add(
                                        Classroom(
                                            time = roomItem.getString("time"),
                                            room = roomItem.getString("room")
                                        )
                                    )
                                }

                                parsedList.add(
                                    ScheduleSubject(
                                        code = item.getString("code"),
                                        name = item.getString("name"),
                                        credit = item.getInt("credit"),
                                        type = item.getString("type"),
                                        professor = item.getString("professor"),
                                        classNumber = item.getInt("classNumber"),
                                        classroomList = classroomList
                                    )
                                )
                            }

                            _scheduleGradeSubjects.postValue(parsedList)
                        } catch (e: Exception) {
                            Log.e("##ERROR", "getScheduleGrade: error =${e.stackTraceToString()}")
                            e.printStackTrace()
                        }
                    },
                    onFailure = {
                        // 실패 처리 필요시 여기에 작성
                        Log.e("##ERROR", "getScheduleGrade: ")
                    }
                )
            }
        }
    }

    private fun parseScheduleJson(json: String): List<Subject> {
        val result = mutableListOf<Subject>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val name = obj.getString("name")
                val timeList = obj.getJSONArray("timeList")

                val timeSlots = mutableListOf<TimeSlot>()
                for (j in 0 until timeList.length()) {
                    val timeStr = timeList.getString(j).trim()
                    val parts = timeStr.split(" ", limit = 2)
                    if (parts.size >= 2) {
                        val dayKor = parts[0]
                        val dayEng = convertDayToEng(dayKor)
                        val periods = parts[1].split(",").mapNotNull { it.trim().toIntOrNull() }
                        periods.forEach { period ->
                            timeSlots.add(TimeSlot(dayEng, period))
                        }
                    }
                }

                result.add(Subject(name, timeSlots))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }


    private fun convertDayToEng(koreanDay: String): String {
        return when (koreanDay.trim()) {
            "월" -> "mon"
            "화" -> "tue"
            "수" -> "wed"
            "목" -> "thu"
            "금" -> "fri"
            else -> ""
        }
    }

    fun postRecommendSchedule(grade: String, targetCredit: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val token = getUserToken()
            if (token != null) {
                HttpManager.postRecommendSchedule(
                    token = token,
                    grade = grade,
                    targetCredit = targetCredit,
                    onSuccessResult = { jsonString ->
                        try {
                            val jsonArray = JSONArray(jsonString)
                            val resultList = mutableListOf<RecommendResult>()

                            for (i in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(i)

                                // schedules 파싱
                                val scheduleArray = obj.getJSONArray("schedules")
                                val scheduleList = mutableListOf<RecommendSubject>()

                                for (j in 0 until scheduleArray.length()) {
                                    val item = scheduleArray.getJSONObject(j)
                                    val name = item.getString("name")
                                    val timeListJson = item.getJSONArray("timeList")

                                    val timeList = mutableListOf<String>()
                                    for (k in 0 until timeListJson.length()) {
                                        timeList.add(timeListJson.getString(k))
                                    }

                                    scheduleList.add(RecommendSubject(name, timeList))
                                }

                                // generalSubjects 파싱
                                val generalArray = obj.getJSONArray("generalSubjects")
                                val generalList = mutableListOf<RecommendSubject>()

                                for (j in 0 until generalArray.length()) {
                                    val item = generalArray.getJSONObject(j)
                                    val name = item.getString("name")
                                    val timeListJson = item.getJSONArray("timeList")

                                    val timeList = mutableListOf<String>()
                                    for (k in 0 until timeListJson.length()) {
                                        timeList.add(timeListJson.getString(k))
                                    }

                                    generalList.add(RecommendSubject(name, timeList))
                                }

                                val totalCredit = obj.getInt("totalCredit")

                                // RecommendResult에 generalSubjects 포함
                                resultList.add(RecommendResult(scheduleList, generalList, totalCredit))
                            }

                            recommendSchedule.postValue(resultList)

                        } catch (e: Exception) {
                            Log.e("##ERROR", "파싱 오류: ${e.stackTraceToString()}")
                        }
                    },
                    onFailure = {
                        Log.e("##ERROR", "추천 시간표 서버 요청 실패")
                    }
                )
            }
        }
    }


    fun applyRecommendedSubjects(subjects: List<Subject>) {
        _subjects.postValue(subjects)
    }


}