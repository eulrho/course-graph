package com.happ.coursegraph.ui.sign

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happ.coursegraph.comm.HttpManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignViewModel(application: Application) : AndroidViewModel(application) {
    private val _userToken: MutableStateFlow<String?> = MutableStateFlow(null)
    val userToken get() = _userToken.asStateFlow()

    val baseServerUrl = ""
    private val _isAuthCodeValid = MutableLiveData<Boolean>(false)
    val isAuthCodeValid get() = _isAuthCodeValid


    val enterYear = MutableLiveData<Int>(0)

    private val _result = MutableStateFlow<Result<Boolean>?>(null)
    val result = _result.asStateFlow()

    fun setEnterYear(year: Int) {
        enterYear.postValue(year)
    }

    fun setUserToken(token: String) {
        _userToken.value = token
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("##INFO", "request Login")
            HttpManager.loginUser(email, password) { isSuccess, token ->
                if (isSuccess && token != null) {
                    Log.i("##INFO", "loginUser() : isSuccess = ${isSuccess}, token = ${token}")
                    _userToken.value = token

                } else {
                    Log.i("##INFO", "loginUser() : isSuccess = ${isSuccess}, token = ${token}")
                    _userToken.value = null
                }
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("##INFO", "logoutUser() : userToken = ${userToken.value}")
            userToken.value?.let { token ->
                HttpManager.logoutUser(token) { isSuccess ->
                    if (isSuccess) {
                        Log.i("##INFO", "logoutUser() : isSuccess = ${isSuccess}")
                        _userToken.value = null
                    } else {
                        Log.i("##INFO", "logoutUser() : isSuccess = ${isSuccess}")
                    }
                }
            }
        }
    }

    fun withdrawUser(pw : String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("##INFO", "logoutUser() : userToken = ${userToken.value}")
            userToken.value?.let { token ->
                HttpManager.deleteUser(pw,token) { isSuccess ->
                    if (isSuccess) {
                        Log.i("##INFO", "logoutUser() : isSuccess = ${isSuccess}")
                        _userToken.value = null
                        _result.value = Result.success(true)
                    } else {
                        Log.i("##INFO", "logoutUser() : isSuccess = ${isSuccess}")
                        _result.value = Result.failure(Exception("회원탈퇴 실패"))
                    }
                }
            }
        }
    }

    fun sendAuthCodeForEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            HttpManager.sendCodeForEmail(email) { isScuccess ->
//                Log.i("##INFO", "sendAuthCodeForEmail() : isScuccess = ${isScuccess}")
            }
        }
    }

    fun checkAuthCode(email: String, code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            HttpManager.validateAuthCode(email, code) { isSuccess ->
                Log.i("##INFO", "checkAuthCode() : isSuccess = ${isSuccess}")
                _isAuthCodeValid.postValue(isSuccess)
            }
        }
    }

    fun signupUser(email: String, password: String, passwordConfirm: String, year : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            HttpManager.signupUserData(email,year, password, passwordConfirm) { isSuccess ->
                Log.i("##INFO", "signupUser() : isSuccess = ${isSuccess}")
                if (isSuccess) {
                    _result.value = Result.success(true)
                } else {
                    _result.value = Result.failure(Exception("회원가입 실패"))
                }
            }
        }
    }
}

























