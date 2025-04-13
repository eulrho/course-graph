package com.happ.coursegraph.ui.sign

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.happ.coursegraph.databinding.ActivitySingUpBinding
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingUpBinding

    //    var firebaseAuth: FirebaseAuth? = null
    private val signVm: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        firebaseAuth = FirebaseAuth.getInstance()

        init()
        setEvent()
    }

    private fun init() {

    }

    private fun setEvent() {
        itemViewClick()
        subscribeViewModel()
    }

    private fun subscribeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signVm.result.collect { res ->
                    if(res == null) {
                        Toast.makeText(this@SignUpActivity, "error", Toast.LENGTH_SHORT).show()
                        return@collect
                    }

                    res.onSuccess {


                    }.onFailure {

                    }
                }
            }
        }
    }

    private fun itemViewClick() {
        binding.apply {
            btSendEmail.setOnClickListener {
//                signVm.
            }

            btCheckAuthNumber.setOnClickListener {

            }
        }


//        // 회원가입 버튼
//        binding.btJoin.setOnClickListener {
//            if (!binding.edEmail.text.toString()
//                    .equals("") && !binding.edPassword.text.toString()
//                    .equals("")
//            ) {
//                // 이메일과 비밀번호가 공백이 아닌 경우
//                createUser(binding.edEmail.text.toString(), binding.edPassword.text.toString())
//            } else {
//                // 이메일과 비밀번호가 공백인 경우
//                Toast.makeText(this@SignUpActivity, "모든 입력란을 작성해주세요.", Toast.LENGTH_LONG).show()
//            }
//        }
    }

    //유저의 데이터를 전송하여 회원가입을 진행한다.
    private fun createUser(email: String, password: String) {
        // region === old code ===
//        firebaseAuth!!.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task: Task<AuthResult?> ->
//                if (task.isSuccessful) {
//                    val userModel = UserModel()
//                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
//                    userModel.email = binding.edEmail.text.toString()
//                    userModel.password = binding.edPassword.text.toString()
//
//
//                    val db = FirebaseFirestore.getInstance()
//                    db.collection("Users").document(uid).set(userModel)
//                        .addOnSuccessListener { aVoid: Void? ->
//                            Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
//                            finish()
//                        }.addOnFailureListener { e: Exception ->
//                            Log.e(
//                                "SignUpActivity",
//                                "onFailure: " + e.message
//                            )
//                        }
//
//
//                } else {
//                    task.addOnFailureListener {
//                        Log.e("##ERROR", ": error = task erro = ${it.message}");
//                    }
//                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
//                }
//            }

        //endregion
    }

    private fun authInputEmail() {

    }
}