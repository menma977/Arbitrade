package biz.arbitrade.controller.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import biz.arbitrade.model.User
import biz.arbitrade.network.DogeAPI
import okhttp3.FormBody
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class DogeRefresher : Service() {

  private lateinit var json: JSONObject
  private lateinit var user: User
  private var startBackgroundService: Boolean = false

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    return START_STICKY
  }

  private fun onHandleIntent() {
    user = User(this)
    var time = System.currentTimeMillis()

    Timer().schedule(100) {
      while (true) {
        val delta = System.currentTimeMillis() - time
        if (delta >= 10000) {
          time = System.currentTimeMillis()
          val privateIntent = Intent()
          if (startBackgroundService) {
            val body = FormBody.Builder()
            body.add("a", "GetBalance")
            body.add("s", user.getString("cookie"))
            body.add("Currency", "doge")
            json = DogeAPI(body).call()
            if (json.getInt("code") == 200)
              user.setLong("balance", json.getJSONObject("data").getLong("Balance"))
            privateIntent.action = "web.doge"
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(privateIntent)
          } else {
            stopSelf()
          }
        }
      }
    }
  }

  override fun onCreate() {
    super.onCreate()
    onHandleIntent()
    startBackgroundService = true
  }

  override fun onDestroy() {
    super.onDestroy()
    startBackgroundService = false
  }

  override fun onBind(intent: Intent?): IBinder? {
    TODO("Not yet implemented")
  }
}