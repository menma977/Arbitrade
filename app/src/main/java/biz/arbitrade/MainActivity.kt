package biz.arbitrade

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import biz.arbitrade.model.User
import biz.arbitrade.network.ArbizAPI
import biz.arbitrade.network.JWTUtils
import biz.arbitrade.view.activity.HomeActivity
import biz.arbitrade.view.activity.LoginActivity
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = User(this)

        if (user.has("token")) {
            val expiredAt = JWTUtils.decode(user.getString("token"))
                .getJSONObject("body").getDouble("exp")
            if(System.currentTimeMillis()/1000f - expiredAt < 0){
                Timer().schedule(100) {
                    val response = ArbizAPI("my", "GET", user.getString("token"), null).call()
                    runOnUiThread {
                        move(if(response.getInt("code")>300) "login" else "main")
                    }
                }
            }else move("login")
        }else move("login")
    }

    fun move(to: String){
        val cls = if (to == "login") LoginActivity::class.java else HomeActivity::class.java
        val intent = Intent(applicationContext, cls)
        startActivity(intent)
        finishAffinity()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            finishAffinity()
        }
    }
}