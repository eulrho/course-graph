package com.happ.coursegraph

import android.app.Application
//import androidx.browser.trusted.Token
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.happ.coursegraph.comm.HttpManager

class CourseApplication : Application(), ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()

    companion object {
        lateinit var instance: CourseApplication
            private set

        private var userToken: String? = null

        fun setUserToken(token : String) {
            userToken = token
        }

        fun getUserToken() : String? {
            return userToken
        }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // pc IP를 설정
        HttpManager.BASE_URL = "http://${getServerIpAddress()}:8080/api/"
    }

    private fun getServerIpAddress(): String {
        try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            for (intf in interfaces) {
                val addresses = intf.inetAddresses
                for (addr in addresses) {
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                        return addr.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "127.0.0.1" // 실패 시 localhost
    }
}
