package com.happ.coursegraph.ui.sign

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.happ.coursegraph.CourseApplication
import com.happ.coursegraph.CourseApplication.Companion.setUserToken
import com.happ.coursegraph.MainActivity
import com.happ.coursegraph.comm.HttpManager
import com.happ.coursegraph.data.UserModel
import com.happ.coursegraph.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var signVm: SignViewModel
    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.i("##INFO", "onCreate(): granted = $granted")
        } else {
            Log.i("##INFO", "onCreate(): granted = $granted")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setEvent()
    }

    private fun init() {
        signVm = ViewModelProvider(
            CourseApplication.instance,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[SignViewModel::class.java]
        pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)


        val shared = getSharedPreferences("user_info", MODE_PRIVATE)
        val autoLogin = shared.getBoolean("autoLogin", false)
        Log.i("##INFO", "autoLogin = ${autoLogin}")

        if (autoLogin == true) {
            binding.rdAutoLogin.isChecked = true
            val email = shared.getString("user_email", "")
            val pw = shared.getString("user_pw", "")
            binding.edEmail.setText(email)
            binding.edPassword.setText(pw)
            lifecycleScope.launch {
                delay(500)
                binding.btLogin.performClick()
            }
        }
    }

    private fun setEvent() {
        observeViewModel()
        itemClick()
    }


    private fun observeViewModel() {
        lifecycleScope.launch {
            signVm.userToken.collect({ token ->
                Log.i("##INFO", "observeViewModel(): token = $token")

                if (token != null) {
                    val email = binding.edEmail.text.toString()
                    val pw = binding.edPassword.text.toString()
                    val shared = getSharedPreferences("user_info", MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = shared.edit()
                    // pw 저장
                    editor.putString("user_pw", pw)
                    editor.putString("user_email", email)

                    editor.putBoolean("autoLogin", true)
                    editor.apply()

                    setUserToken(token)

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            })
        }
    }


    private fun itemClick() {
        binding.btLogin.setOnClickListener { view ->
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()

            signVm.loginUser(email, password)
        }

        //회원가입 페이지로 이동하는 클릭 이벤트 리스너
        binding.btSignup.setOnClickListener { view ->
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.rdAutoLogin.setOnClickListener {
            val isChecked = binding.rdAutoLogin.isChecked

            if (isChecked == false) {
                val shared = getSharedPreferences("user_info", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = shared.edit()
                editor.putBoolean("autoLogin", false)
                editor.apply()
            }
        }
    }

}