package com.happ.coursegraph.ui.dashboard

import android.util.Log
import android.widget.Toast
import androidx.browser.trusted.Token
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happ.coursegraph.CourseApplication.Companion.getUserToken
import com.happ.coursegraph.comm.HttpManager
import com.happ.coursegraph.data.Course
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class MajorStatusViewModel : ViewModel() {
    private val _majorStatus = MutableLiveData<MajorStatusData>()
    val majorStatus: LiveData<MajorStatusData> get() = _majorStatus

    private val _subjectStatusList = MutableLiveData<List<SubjectStatus>>()
    val subjectStatusList: LiveData<List<SubjectStatus>> get() = _subjectStatusList

    private val _isChanged = MutableLiveData<Boolean>()
    val isChanged: LiveData<Boolean> get() = _isChanged

    private val _courseList = MutableLiveData<List<Course>>()
    val courseList: LiveData<List<Course>> get() = _courseList

    private val _historySubjects = MutableLiveData<List<Course>>()
    val historySubjects: LiveData<List<Course>> get() = _historySubjects

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> get() = _totalPages

    fun getGraduation() {
        viewModelScope.launch(Dispatchers.IO) {

            val token = getUserToken()
            if (token != null) {
                HttpManager.getGraduation(
                    token,
                    onSuccessResult = { res ->
                        try {
                            val json = JSONObject(res)

                            val requiredMinCredit = json.getInt("requiredMinCredit")
                            val electiveMinCredit = json.getInt("electiveMinCredit")
                            val totalRequiredCredit = json.getInt("totalRequiredCredit")
                            val totalElectiveCredit = json.getInt("totalElectiveCredit")

                            val notTakenArray = json.getJSONArray("notTakenRequiredSubjects")
                            val notTakenRequiredSubjects = mutableListOf<String>()
                            for (i in 0 until notTakenArray.length()) {
                                notTakenRequiredSubjects.add(notTakenArray.getString(i))
                            }

                            val data = MajorStatusData(
                                requiredMinCredit,
                                electiveMinCredit,
                                totalRequiredCredit,
                                totalElectiveCredit,
                                notTakenRequiredSubjects
                            )

                            _majorStatus.postValue(data)

                        } catch (e: Exception) {
                            e.printStackTrace()
                            // 예외 처리 로직 필요 시 여기에 작성
                        }
                    },
                    onFailure = {
                        // 실패 처리
                    }
                )
            }
        }
    }

    fun getSubjectOfGrade(passedGrade: String) {
        viewModelScope.launch(Dispatchers.IO) {

            val token = getUserToken()
            if (token != null) {
                HttpManager.getSubjectsOfGrade(
                    token,
                    passedGrade,
                    onSuccessResult = { res ->
                        try {
                            val jsonArray = org.json.JSONArray(res)
                            val tempList = mutableListOf<SubjectStatus>()

                            for (i in 0 until jsonArray.length()) {
                                val item = jsonArray.getJSONObject(i)
                                val data = item.getJSONObject("data")
                                val name = data.optString("name", "이름 없음")
                                val status = item.optString("status", "상태 없음")

                                // 서버 응답에 grade 없음 → 파라미터로 받은 grade 사용
                                tempList.add(SubjectStatus(name, passedGrade, status))
                            }

                            _subjectStatusList.postValue(tempList)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    onFailure = {
                        // 네트워크 실패 처리
                    }
                )
            }
        }
    }

    fun getPagedHistoryCourses(page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val token = getUserToken()
            if (token != null) {
                HttpManager.getPagedHistoryCourses(
                    token = token,
                    page = page,
                    onSuccess = { courseList, totalPage ->
                        _courseList.postValue(courseList)
                        _totalPages.postValue(totalPage)
                    },
                    onFailure = {
                        Log.e("##ERROR", "페이징된 과목 목록 가져오기 실패: $it")
                    }
                )
            }
        }
    }

    fun postSubjects(subjectList: List<SubjectStatus>) {
        viewModelScope.launch(Dispatchers.IO) {
            val token = getUserToken()
            if (token != null) {
                HttpManager.postSubjects(
                    token = token,
                    subjectList = subjectList,
                    onSuccessResult = {
                        _isChanged.postValue(true)
                    },
                    onFailure = {
                        _isChanged.postValue(false)
                    })
            }
        }
    }

    fun updateCourses(newList: List<Course>) {
        _courseList.value = newList
    }

    fun patchHistorySubjects() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = getUserToken()
            val currentCourses = _courseList.value

            if (token != null && currentCourses != null) {
                HttpManager.patchHistoryUpload(
                    token = token,
                    courseList = currentCourses,
                    onSuccessResult = {
                        // 성공 처리 (예: 로그 or UI 알림용 LiveData 추가 가능)
                        Log.i("##INFO", "성공 업로드 ")
                    },
                    onFailure = {
                        // 실패 처리
                        Log.i("##INFO", "실패 업로드 ")
                    }
                )
            }
        }
    }

    fun setTotalPages(pageCount: Int) {
        _totalPages.postValue(pageCount)
    }

    fun postXlsxUpload(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            val token = getUserToken() ?: return@launch
            val fileBytes = file.readBytes()

            HttpManager.postXlsxUpload(
                token = token,
                fileName = file.name,
                fileBytes = fileBytes,
                onSuccess = { Log.d("Upload", "성공") },
                onFailure = { error -> Log.e("Upload", "실패: $error") }
            )
        }
    }

    fun addCourses(newCourses: List<Course>) {
        val currentList = _courseList.value ?: emptyList()
        val currentNames = currentList.map { it.name }.toSet() // 이미 있는 과목 이름들

        val filteredNewCourses = newCourses.filter { it.name !in currentNames } // 중복 제거
        _courseList.postValue(currentList + filteredNewCourses)
    }
}