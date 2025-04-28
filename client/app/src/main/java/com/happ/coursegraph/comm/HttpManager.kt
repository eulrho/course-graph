package com.happ.coursegraph.comm

import android.util.Log
import com.happ.coursegraph.BuildConfig

import com.happ.coursegraph.data.Course
import com.happ.coursegraph.ui.dashboard.SubjectStatus
import com.happ.coursegraph.ui.timetable.GeneralSchedule
import com.happ.coursegraph.ui.timetable.ScheduleSubject
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL
import java.sql.DriverManager.getConnection


object HttpManager {
    var BASE_URL = BuildConfig.BASE_URL
    val LOGIN_URL = BASE_URL + "login"
    val LOGOUT_URL = BASE_URL + "logout"
    val SIGNUP_URL = BASE_URL + "join"
    val SEND_CODE = BASE_URL + "send-mail"
    val DELETE_URL = BASE_URL + "delete"
    val VERIFY_CODE = BASE_URL + "verify-mail"

    val GET_COURSE_URL = BASE_URL + "course"
    val GET_GRADUATION = BASE_URL + "graduation"
    val GET_SUBJECTS_OF_GRADE = BASE_URL + "subjects"
    val GET_HISTORY_PAGE = BASE_URL + "history"
    val GET_TIME_TABLE_SCHEDULE = BASE_URL + "schedule"
    val GET_TIME_TABLE_SCHEDULES = BASE_URL + "schedules"

    val POST_SUBJECTS_OF_GRADE = BASE_URL + "subjects"
    val POST_XLSX_UPLOAD = BASE_URL + "history-upload"
    val POST_TIMETABLE_GENERAL = BASE_URL + "schedule-general"
    val POST_TIMETABLE_SCHEDULE = BASE_URL + "schedule"
    val POST_TIMETABLE_RECOMMEND_SCHEDULE = BASE_URL + "recommend-schedules"

    val PATCH_HISTORY_UPLOAD = BASE_URL + "history"

    // region === GET ===
    /**
     * 코스조회
     */
    fun getCourse(
        token: String,
        onSuccessResult: (String) -> Unit,
        onFailure: (String?) -> Unit
    ) {
        getJsonRequest(
            urlString = GET_COURSE_URL,
            token = token,
            onSuccess = { res ->
                onSuccessResult(res)
            },
            onFailure = { error ->
                onFailure(error)

            },
            expectedCode = HTTP_OK
        )
    }

    fun getGraduation(
        token: String,
        onSuccessResult: (String) -> Unit,
        onFailure: (String?) -> Unit
    ) {
        Log.i("##INFO", "token = ${token} ")
        getJsonRequest(
            urlString = GET_GRADUATION,
            token = token,
            onSuccess = { res ->
                onSuccessResult(res)
            },
            onFailure = { error ->
                onFailure(error)

            },
            expectedCode = HTTP_OK
        )

    }

    /**
     * 전체 과목 조회
     */
    fun getSubjectsOfGrade(
        token: String,
        grade: String,
        onSuccessResult: (String) -> Unit,
        onFailure: (String?) -> Unit
    ) {
        getJsonRequest(
            urlString = GET_SUBJECTS_OF_GRADE + "?grade=$grade",
            token = token,
            onSuccess = { res ->
                onSuccessResult(res)
            },
            onFailure = { error ->
                onFailure(error)

            },
            expectedCode = HTTP_OK
        )
    }

    fun getHistorySubjects(
        token: String,
        courses: List<Course>,
        onSuccessResult: (String) -> Unit,
        onFailure: (String?) -> Unit
    ) {

    }

    fun getPagedHistoryCourses(
        token: String,
        page: Int,
        onSuccess: (List<Course>, Int) -> Unit,
        onFailure: (String?) -> Unit
    ) {
        val url = "$GET_HISTORY_PAGE?page=$page"

        getJsonRequest(
            urlString = url,
            token = token,
            onSuccess = { res ->
                try {
                    val json = JSONObject(res)
                    val dataArray = json.getJSONArray("data")
                    val totalPages = json.getJSONObject("pageInfo").getInt("totalPages")

                    val resultList = mutableListOf<Course>()
                    for (i in 0 until dataArray.length()) {
                        val item = dataArray.getJSONObject(i)
                        val name = item.getString("subjectName")
                        val grade = item.getString("score")
                        resultList.add(Course(name, grade))
                    }

                    onSuccess(resultList, totalPages)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onFailure(e.message)
                }
            },
            onFailure = {
                onFailure(it)
            }
        )
    }

    /**
     * 유저 타임테이블 get
     */
    fun getTimeTableSchedule(
        token: String,
        onSuccessResult: (String) -> Unit,
        onFailure: (String?) -> Unit
    ) {
        getJsonRequest(
            urlString = GET_TIME_TABLE_SCHEDULE,
            token = token,
            onSuccess = { res ->
                onSuccessResult(res)
            },
            onFailure = { error ->
                onFailure(error)

            },
            expectedCode = HTTP_OK
        )
    }

    /**
     * 학년별 모든 과목 get
     */
    fun getScheduleGrade(
        token: String,
        grade: String,
        onSuccessResult: (String) -> Unit,
        onFailure: (String?) -> Unit
    ) {
        getJsonRequest(
            urlString = GET_TIME_TABLE_SCHEDULES + "?grade=$grade",
            token = token,
            onSuccess = { res ->
                onSuccessResult(res)
            },
            onFailure = { error ->
                onFailure(error)

            },
            expectedCode = HTTP_OK
        )
    }

    //endregion


    // region === POST ===
    /**
     * 로그인 요청
     */
    fun loginUser(email: String, password: String, res: (Boolean, String?) -> Unit) {
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        postJsonRequest(LOGIN_URL, json,
            onSuccess = { response ->
                val token = JSONObject(response).getString("message")
                res(true, token)
            },
            onFailure = { res(false, null) }
        )
    }

    /**
     * 로그아웃 요청
     */
    fun logoutUser(token: String, res: (Boolean) -> Unit) {
        val json = JSONObject().apply {
            put("token", token)
        }

        postJsonRequest(
            LOGOUT_URL, json,
            onSuccess = { res(true) },
            onFailure = { res(false) },
            header = mapOf("Authorization" to "Bearer $token")
        )
    }

    fun deleteUser(password: String, token: String, res: (Boolean) -> Unit) {
        val json = JSONObject().apply {
            put("password", password)
        }

        postJsonRequest(
            DELETE_URL, json,
            onSuccess = { res(true) },
            onFailure = { res(false) },
            header = mapOf("Authorization" to "Bearer $token"),
            method = "DELETE"
        )
    }

    /**
     * 이메일 인증코드 요청
     */
    fun sendCodeForEmail(email: String, isSuccess: (Boolean) -> Unit) {
        val json = JSONObject().apply {
            put("email", email)
        }

        postJsonRequest(SEND_CODE, json,
            onSuccess = { isSuccess(true) },
            onFailure = { isSuccess(false) }
        )
    }


    /**
     * 인증코드 확인
     */
    fun validateAuthCode(email: String, code: String, isSuccess: (Boolean) -> Unit) {
        val json = JSONObject().apply {
            put("email", email)
            put("code", code)
        }

        postJsonRequest(VERIFY_CODE, json,
            onSuccess = { isSuccess(true) },
            onFailure = { isSuccess(false) }
        )
    }

    /**
     * 회원가입 요청
     */
    fun signupUserData(
        email: String,
        year: Int,
        password: String,
        passwordCheck: String,
        isSuccess: (Boolean) -> Unit
    ) {
        val json = JSONObject().apply {
            put("email", email)
            put("year", year)
            put("password", password)
            put("passwordCheck", passwordCheck)
        }

        postJsonRequest(
            SIGNUP_URL, json,
            onSuccess = { isSuccess(true) },
            onFailure = { isSuccess(false) },
            expectedCode = HttpURLConnection.HTTP_CREATED
        )
    }

    /**
     * 과목 추가
     */
    fun postSubjects(
        token: String,
        subjectList: List<SubjectStatus>,
        onSuccessResult: (Boolean) -> Unit,
        onFailure: (Boolean?) -> Unit
    ) {
        val subjectArray = JSONArray()

        for (subject in subjectList) {
            val obj = JSONObject()
            obj.put("subjectName", subject.name)
            obj.put("status", subject.status)
            subjectArray.put(obj)
        }

        postJsonRequest(
            POST_SUBJECTS_OF_GRADE,
            subjectArray,
            onSuccess = { res ->
                onSuccessResult(true)
            },
            onFailure = { error ->
                onFailure(false)

            },
            expectedCode = HTTP_OK,
            header = mapOf("Authorization" to "Bearer $token")
        )
    }

    fun postXlsxUpload(
        token: String,
        fileName: String,
        fileBytes: ByteArray,
        onSuccess: (Boolean) -> Unit,
        onFailure: (String?) -> Unit
    ) {
        val boundary = "----CourseGraphBoundary${System.currentTimeMillis()}"
        val lineEnd = "\r\n"
        val twoHyphens = "--"

        try {
            val url = URL(HttpManager.POST_XLSX_UPLOAD)
            val conn = url.openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "POST"
                doInput = true
                doOutput = true
                useCaches = false
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                setRequestProperty("Authorization", "Bearer $token")
            }

            val outputStream = conn.outputStream
            val writer = outputStream.bufferedWriter(Charsets.UTF_8)

            writer.apply {
                write(twoHyphens + boundary + lineEnd)
                write("Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"$lineEnd")
                write("Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet$lineEnd")
                write(lineEnd)
                flush()
            }

            // 파일 바디 전송
            outputStream.write(fileBytes)
            outputStream.flush()

            // 끝나는 multipart 구문
            writer.apply {
                write(lineEnd)
                write(twoHyphens + boundary + twoHyphens + lineEnd)
                flush()
                close()
            }

            outputStream.close()

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                val response = readSuccessResponse(conn)
                Log.i("##INFO", "파일 업로드 성공: $response")
                onSuccess(true)
            } else {
                val error = readErrorResponse(conn)
                Log.e("##ERROR", "파일 업로드 실패 [$responseCode]: $error")
                onFailure(error)
            }

        } catch (e: Exception) {
            Log.e("##ERROR", "파일 업로드 중 예외 발생: ${e.stackTraceToString()}")
            onFailure(e.message)
        }
    }

    fun patchHistoryUpload(
        token: String,
        courseList: List<Course>,
        onSuccessResult: (Boolean) -> Unit,
        onFailure: (Boolean?) -> Unit
    ) {
        val jsonArray = JSONArray()

        for (course in courseList) {
            val obj = JSONObject().apply {
                put("subjectName", course.name.trim())
                put("score", course.grade.trim())
            }
            jsonArray.put(obj)
        }

        Log.i("##INFO", "body = ${jsonArray.toString()} ")

        patchJsonRequest(
            urlString = PATCH_HISTORY_UPLOAD,
            jsonBody = jsonArray,
            onSuccess = {
                Log.i("##INFO", "히스토리 업로드 성공")
                onSuccessResult(true)
            },
            onFailure = {
                Log.e("##ERROR", "히스토리 업로드 실패")
                onFailure(false)
            },
            expectedCode = HTTP_OK,
            header = mapOf("Authorization" to "Bearer $token")
        )
    }

    fun postScheduleGeneral(
        token: String,
        scheduleList: List<GeneralSchedule>,
        onSuccessResult: (Boolean) -> Unit,
        onFailure: (String?) -> Unit
    ) {
        val jsonArray = JSONArray()

        for (schedule in scheduleList) {
            val obj = JSONObject().apply {
                put("name", schedule.name)
                put("timeList", JSONArray(schedule.timeList))
                put("status", schedule.status)
            }
            jsonArray.put(obj)
        }

        postJsonRequest(
            urlString = POST_TIMETABLE_GENERAL,
            jsonBody = jsonArray,
            onSuccess = {
                Log.i("##INFO", "일반 시간표 업로드 성공")
                onSuccessResult(true)
            },
            onFailure = {
                Log.e("##ERROR", "일반 시간표 업로드 실패")
                onFailure(it)
            },
            expectedCode = HTTP_OK,
            header = mapOf("Authorization" to "Bearer $token")
        )
    }

    fun postSchedule(
        token: String,
        scheduleList: List<ScheduleSubject>,
        onSuccessResult: (Boolean) -> Unit,
        onFailure: (String?) -> Unit
    ) {
        val jsonArray = JSONArray()

        for (schedule in scheduleList) {
            val obj = JSONObject().apply {
                put("code", schedule.code)
                put("name", schedule.name)
                put("classNumber", schedule.classNumber)
                put("status", schedule.status)
            }
            jsonArray.put(obj)
        }

        Log.i("##INFO", "json =${jsonArray} ")

        postJsonRequest(
            urlString = POST_TIMETABLE_SCHEDULE,
            jsonBody = jsonArray,
            onSuccess = {
                Log.i("##INFO", "유저 시간표 업로드 성공")
                onSuccessResult(true)
            },
            onFailure = {
                Log.e("##ERROR", "유저 시간표 업로드 실패")
                onFailure(it)
            },
            expectedCode = HTTP_OK,
            header = mapOf("Authorization" to "Bearer $token")
        )
    }

    fun postRecommendSchedule(
        token: String,
        grade: String,
        targetCredit: Int,
        onSuccessResult: (String) -> Unit,  // ✅ 문자열 응답
        onFailure: (String?) -> Unit
    ) {
        val obj = JSONObject().apply {
            put("grade", grade)
            put("targetCredit", targetCredit)
        }

        Log.i("##INFO", "json = $obj")

        postJsonRequest(
            urlString = POST_TIMETABLE_RECOMMEND_SCHEDULE,
            jsonBody = obj,
            onSuccess = { response ->
                val body = response
                Log.i("##INFO", "추천 시간표 성공 응답: $body")
                if (body != null) {
                    onSuccessResult(body)
                } else {
                    onFailure("Empty response body")
                }
            },
            onFailure = {
                Log.e("##ERROR", "추천 시간표 실패: $it")
                onFailure(it)
            },
            expectedCode = HTTP_OK,
            header = mapOf("Authorization" to "Bearer $token")
        )
    }


    //endregion  =========================================================

    /**
     * 공통 POST 요청 로직
     */
    private fun postJsonRequest(
        urlString: String,
        jsonBody: Any,
        onSuccess: (String) -> Unit,
        onFailure: (String?) -> Unit,
        expectedCode: Int = HttpURLConnection.HTTP_OK,
        header: Map<String, String> = emptyMap(),
        method: String = "POST"
    ) {
        try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = method
            if (header.isNotEmpty()) {
                for ((key, value) in header) {
                    conn.setRequestProperty(key, value)
                }
            }
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.useCaches = false

            // JSON 전송
            BufferedWriter(OutputStreamWriter(conn.outputStream, "UTF-8")).use { writer ->
                writer.write(jsonBody.toString())
            }

            val responseCode = conn.responseCode
            if (responseCode == expectedCode) {
                val response = readSuccessResponse(conn)
                Log.i("##INFO", "응답 성공 [$urlString]: $response")
                onSuccess(response)
            } else {
                val error = readErrorResponse(conn)
                Log.e("##ERROR", "응답 실패 [$responseCode]: $error")
                onFailure(error)
            }

        } catch (e: Exception) {
            Log.e("##ERROR", "예외 발생 [$urlString]: ${e.stackTraceToString()}")
            onFailure(null)
        }
    }

    /**
     * 공통 PATCH 요청 로직
     */
    private fun patchJsonRequest(
        urlString: String,
        jsonBody: Any,
        onSuccess: (String) -> Unit,
        onFailure: (String?) -> Unit,
        expectedCode: Int = HttpURLConnection.HTTP_OK,
        header: Map<String, String> = emptyMap()
    ) {
        try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = "PATCH" // PATCH 메서드 지정
            if (header.isNotEmpty()) {
                for ((key, value) in header) {
                    conn.setRequestProperty(key, value)
                }
            }
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.useCaches = false

            // JSON 전송
            BufferedWriter(OutputStreamWriter(conn.outputStream, "UTF-8")).use { writer ->
                writer.write(jsonBody.toString())
            }

            val responseCode = conn.responseCode
            if (responseCode == expectedCode) {
                val response = readSuccessResponse(conn)
                Log.i("##INFO", "PATCH 응답 성공 [$urlString]: $response")
                onSuccess(response)
            } else {
                val error = readErrorResponse(conn)
                Log.e("##ERROR", "PATCH 응답 실패 [$responseCode]: $error")
                onFailure(error)
            }

        } catch (e: Exception) {
            Log.e("##ERROR", "PATCH 예외 발생 [$urlString]: ${e.stackTraceToString()}")
            onFailure(null)
        }
    }


    /**
     * 공통 GET 요청 로직
     */
    private fun getJsonRequest(
        urlString: String,
        token: String,
        onSuccess: (String) -> Unit,
        onFailure: (String?) -> Unit,
        expectedCode: Int = HttpURLConnection.HTTP_OK
    ) {
        try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doInput = true

            val responseCode = conn.responseCode
            if (responseCode == expectedCode) {
                val response = readSuccessResponse(conn)
                Log.i("##INFO", "응답 성공 [$urlString]: $response")
                onSuccess(response)
            } else {
                val error = readErrorResponse(conn)
                Log.e("##ERROR", "응답 실패 [$responseCode]: $error")
                onFailure(error)
            }

        } catch (e: Exception) {
            Log.e("##ERROR", "예외 발생 [$urlString]: ${e.stackTraceToString()}")
            onFailure(null)
        }
    }

    private fun readSuccessResponse(conn: HttpURLConnection): String {
        val reader = BufferedReader(InputStreamReader(conn.inputStream))
        val response = reader.readText()
        reader.close()

        return response
    }

    private fun readErrorResponse(conn: HttpURLConnection): String {
        val reader = BufferedReader(InputStreamReader(conn.errorStream))
        val error = reader.readText()
        reader.close()

        return error
    }
}
