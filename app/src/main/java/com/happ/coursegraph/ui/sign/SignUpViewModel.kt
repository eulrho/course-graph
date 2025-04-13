package com.happ.coursegraph.ui.sign

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happ.coursegraph.ui.BottomSheetFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class SignUpViewModel : ViewModel() {
    val baseServerUrl = ""

    private val _result = MutableStateFlow<Result<Boolean>?>(null)
    val result = _result.asStateFlow()


    fun checkEmailAvailability(email: String) {
        if (email.isEmpty() == true) {
            _result.value = Result.failure(IllegalArgumentException("email is empty"))
            return
        }

        viewModelScope.launch {
            try {
                val conn = URL(baseServerUrl).openConnection() as HttpURLConnection

                conn.requestMethod = "POST"
                conn.doOutput = true


                val responseCode = conn.responseCode
                val responseMessage = conn.responseMessage

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    _result.value = Result.success(true)
                } else {
                    _result.value = Result.success(false)
                }
            } catch (e: IOException) {
                _result.value = Result.failure(e)
            } catch (e: Exception) {
                _result.value = Result.failure(e)
            }
        }
    }
}