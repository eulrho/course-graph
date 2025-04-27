package com.happ.coursegraph.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happ.coursegraph.comm.HttpManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class HomeViewModel : ViewModel() {
    private val _courseData = MutableLiveData<List<RadialGraphView.Course>>()
    val courseData: LiveData<List<RadialGraphView.Course>> get() = _courseData


    fun getCourseData(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            HttpManager.getCourse(token = token,
                onSuccessResult = { res ->
                    Log.i("##INFO", "res = ${res} ")
                    val jsonArray = JSONArray(res)
                    parseCoursesFromServer(jsonArray)
                },
                onFailure = {

                })
        }
    }

    fun parseCoursesFromServer(jsonArray: JSONArray) {
        val courseList = mutableListOf<RadialGraphView.Course>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val data = obj.getJSONObject("data")
            val subjectName = data.getString("subjectName")
            val gradeStr = data.getString("grade")
            val grade = gradeStr[0].digitToInt()  // "2학년" → 2

            val tracks = data.optJSONArray("tracks")
            val trackList = mutableListOf<String>()
            if (tracks != null && tracks.length() > 0) {
                for (j in 0 until tracks.length()) {
                    trackList.add(tracks.getString(j))
                }
            } else {
                trackList.add("전공필수") // 기본값
            }

            trackList.forEach { track ->
                courseList.add(
                    RadialGraphView.Course(
                        name = subjectName,
                        category = track,
                        grade = grade
                    )
                )
            }
        }
        _courseData.postValue(courseList)
    }
}