package com.happ.coursegraph.ui.sign

import android.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.happ.coursegraph.CourseApplication
import com.happ.coursegraph.databinding.ActivitySingUpBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingUpBinding

    private lateinit var signVm: SignViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setEvent()

//        //region  ==Test Section==
//        binding.edEmail.setText("dlarp414@chungbuk.ac.kr")
//        binding.edPassword.setText("aaaaa")
//        binding.edPasswordCheck.setText("aaaaa")
//        //endregion
    }

    private fun init() {
        signVm = ViewModelProvider(CourseApplication.instance, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[SignViewModel::class.java]
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (2000..currentYear).map { it.toString() }

        val adapter = ArrayAdapter(
            this, // Activity일 경우, Fragment라면 requireContext()
            R.layout.simple_spinner_item,
            years
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spEnterYear.adapter = adapter
    }

    private fun setEvent() {
        itemViewClick()
        subscribeViewModel()
    }


    private fun itemViewClick() {
        binding.apply {
            btSendEmail.setOnClickListener {
                val email = binding.edEmail.text.toString()
                signVm.sendAuthCodeForEmail(email)
            }

            btCheckAuthNumber.setOnClickListener {
                val email = binding.edEmail.text.toString()
                val code = binding.edAuthNumber.text.toString()

                signVm.checkAuthCode(email, code)
            }

            spEnterYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedYear = parent.getItemAtPosition(position).toString()
                    Log.i("##INFO", "선택한 연도: $selectedYear")
                    signVm.setEnterYear(selectedYear.toInt())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }


        // 회원가입 버튼
        binding.btJoin.setOnClickListener {
            if (!binding.edEmail.text.toString().equals("")
                && !binding.edPassword.text.toString().equals("")
                && !binding.edPasswordCheck.text.toString().equals("")
                && signVm.enterYear.value != 0
            ) {
                val email = binding.edEmail.text.toString()
                val password = binding.edPassword.text.toString()
                val passwordCheck = binding.edPasswordCheck.text.toString()
                val year = signVm.enterYear.value!!

                signVm.signupUser(email, password, passwordCheck, year)
            } else {
                // 이메일과 비밀번호가 공백인 경우
                Toast.makeText(this@SignUpActivity, "모든 입력란을 작성해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun subscribeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signVm.isAuthCodeValid.observe(this@SignUpActivity) { isValid ->
                    if (isValid == null) {
                        return@observe
                    }

                    if (isValid) {
                        Log.i("##INFO", "인증번호 확인 성공")
                        Toast.makeText(this@SignUpActivity, "인증번호 확인 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.i("##INFO", "인증번호 확인 실패")
                        Toast.makeText(this@SignUpActivity, "인증번호 확인 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                signVm.result.collect { res ->
                    if (res == null) {
                        Toast.makeText(this@SignUpActivity, "error", Toast.LENGTH_SHORT).show()
                        return@collect
                    }

                    res.onSuccess {
                        Log.i("##INFO", "회원가입 성공")
                        Toast.makeText(this@SignUpActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        finish()
                    }.onFailure {
                        Log.i("##INFO", "회원가입 실패")
                        Toast.makeText(this@SignUpActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun authInputEmail() {

    }
}