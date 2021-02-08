package biz.arbitrade

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import biz.arbitrade.controller.background.PusherReceiver
import biz.arbitrade.model.User
import biz.arbitrade.network.ArbizAPI
import biz.arbitrade.network.JWTUtils
import biz.arbitrade.view.activity.HomeActivity
import biz.arbitrade.view.activity.InfoOnlyActivity
import biz.arbitrade.view.activity.LoginActivity
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
  private val version = 1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val user = User(this)
    Timer().schedule(100) {
      val info = ArbizAPI("info", "GET", null, null).call()
      if(info.getInt("maintenance") != 0){
        moveError(InfoOnlyActivity.MAINTENANCE)
        return@schedule
      }else if(info.getInt("version") != version){
        moveError(InfoOnlyActivity.MISMATCH_VERSION)
        return@schedule
      }
      if (user.has("token")) {
        val expiredAt =
          JWTUtils.decode(user.getString("token")).getJSONObject("body").getDouble("exp")
        if (System.currentTimeMillis() / 1000f - expiredAt < 0) {
          val response = ArbizAPI("my", "GET", user.getString("token"), null).call()
          runOnUiThread {
            val intent = Intent(applicationContext, PusherReceiver::class.java)
            if (applicationContext != null) {
              startService(intent)
            }
            move(if (response.getInt("code") > 300) "login" else "main")
          }
        } else runOnUiThread { move("login") }
      } else runOnUiThread { move("login") }
    }
  }

  fun move(to: String) {
    val cls = if (to == "login") LoginActivity::class.java else HomeActivity::class.java
    val intent = Intent(applicationContext, cls)
    startActivity(intent)
    finishAffinity()
  }

  private fun moveError(type: String) {
    val intent = Intent(applicationContext, InfoOnlyActivity::class.java)
    intent.putExtra("type", type)
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