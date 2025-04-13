package com.happ.coursegraph.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.happ.coursegraph.MainActivity
import com.happ.coursegraph.data.UserModel
import com.happ.coursegraph.databinding.ActivityLoginBinding
import com.happ.coursegraph.ui.sign.SignUpActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
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

        itemClick()
        pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)


//        binding.edEmail.setText("tester@test.com")
//        binding.edPassword.setText("aaaaaa")
//        binding.btLogin.performClick()
    }


    private fun itemClick() {
        // 로그인 버튼 클릭시 파이어베이스 auth로 유저의 정보를 전송한다.
        binding.btLogin.setOnClickListener { view ->
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val email: String = binding.edEmail.getText().toString()
            val passward: String = binding.edPassword.getText().toString()

            if (email.isEmpty() || passward.isEmpty()) {
                Toast.makeText(this, "모든 입력란을 작성해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, passward)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                    } else {
                        Log.i(
                            "##INFO",
                            "onComplete(): failure",
                            task.exception
                        )
                    }
                    task.addOnFailureListener { e: Exception ->
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                        Log.i("##INFO", "onComplete(): e = " + e.message)
                    }
                }

                .addOnFailureListener {
                    Log.e("##ERROR", "it = ${it.message} ");
                }
                .addOnSuccessListener {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    getUserInfoToDataBase()


                    //userData save
                    val pref: SharedPreferences = getSharedPreferences("userData", MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = pref.edit()
                    editor.putString("email", email)
                    editor.commit()


                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
        }

        //회원가입 페이지로 이동하는 클릭 이벤트 리스너
        binding.btSignup.setOnClickListener { view ->
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    //유저가 로그인 했을때 유저의 데이터를 가져오는 부분
    private fun getUserInfoToDataBase() {
        val db = FirebaseFirestore.getInstance()
        val id = FirebaseAuth.getInstance().currentUser?.uid
        if (id != null) {
            db.collection("Users").document(id).get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    val userInfo = queryDocumentSnapshots.toObject(UserModel::class.java)
                    if (userInfo != null) {
                    }
                }.addOnFailureListener { e ->
                    Log.d(TAG, "onFailure: " + e.message)
                }
        } else {
            Log.i("##INFO", "uid is null");
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001
    }
}