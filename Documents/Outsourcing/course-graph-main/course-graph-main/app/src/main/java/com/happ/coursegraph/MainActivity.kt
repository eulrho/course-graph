package com.happ.coursegraph

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.happ.coursegraph.databinding.ActivityMainBinding
import com.happ.coursegraph.ui.sign.LoginActivity
import com.happ.coursegraph.ui.sign.SignViewModel
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var signVm: SignViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setEvent()
    }

    private fun initView() {
        signVm = ViewModelProvider(
            CourseApplication.instance,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[SignViewModel::class.java]
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun setEvent() {
        requestPermission()
        observeViewModel()
        binding.apply {
            imbtSideOpen.setOnClickListener {
                drawerLayout.openDrawer(Gravity.RIGHT)
            }

            imbtSideClose.setOnClickListener {
                drawerLayout.closeDrawer(Gravity.RIGHT)
            }

            tvLogout.setOnClickListener {
                signVm.logoutUser()
            }

            tvWithdraw.setOnClickListener {
                val pw = getSharedPreferences("user_pw", MODE_PRIVATE)
                val userPw = pw.getString("user_pw", "")

                signVm.withdrawUser(userPw.toString())
            }
        }

        createFolder()
    }

    private fun createFolder() {
        val baseFile = File(
            Environment.getExternalStorageDirectory()
                .absolutePath + "/" + resources.getString(R.string.app_name)
        )

        // 파일 있을 시 리턴
        if (baseFile.exists() == true) return
        baseFile.mkdirs()
    }

    private val storageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                Log.i("##INFO", "is Ok")
            }
        }

    fun requestPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    requestStoragePermissions()
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.e("##ERROR", "requestPermission()   IllegalArgumentException : ${e} ")
        } catch (e: Exception) {
            Log.e("##ERROR", "requestPermission()   Exception : ${e} ")
        }
    }

    private fun requestStoragePermissions() {
        try {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(String.format("package:%s", packageName))
            storageActivityResultLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e("##ERROR", "requestStoragePermissions() : e  = ${e.message}")
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            storageActivityResultLauncher.launch(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            signVm.result.collect({ res ->
                if (res == null) {
                    Log.i("##INFO", "회원탈퇴 실패")
                    return@collect
                }

                res.onSuccess {
                    val shared = getSharedPreferences("user_info", MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = shared.edit()

                    editor.putBoolean("autoLogin", false)
                    editor.apply()

                    Log.i("##INFO", "회원탈퇴 성공")
                    signVm.setUserToken("")
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }.onFailure {
                    Log.i("##INFO", "회원탈퇴 실패")
                }
            })
        }

        lifecycleScope.launch {
            signVm.userToken.collect { token ->
                Log.i("##INFO", "token = ${token} ")
                if (token == null) {
                    val shared = getSharedPreferences("user_info", MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = shared.edit()

                    editor.putBoolean("autoLogin", false)
                    editor.apply()

                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    fun setVisibilityHideButton(isVisibility: Boolean) {
        if (isVisibility == true) {
            binding.imbtSideOpen.visibility = View.VISIBLE
        } else {
            binding.imbtSideOpen.visibility = View.GONE
        }
    }

    fun setVisibilityBottomNav(isVisibility: Boolean) {
        if (isVisibility == true) {
            binding.navView.visibility = View.VISIBLE
        } else {
            binding.navView.visibility = View.GONE
        }
    }
}


































































