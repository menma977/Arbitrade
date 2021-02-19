package biz.arbitrade.controller.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import biz.arbitrade.controller.events.OnAnnounce
import biz.arbitrade.controller.events.OnBot
import biz.arbitrade.controller.events.OnMaintenance
import biz.arbitrade.model.User
import biz.arbitrade.network.Url
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer

class PusherReceiver : Service() {
  override fun onBind(intent: Intent): IBinder {
    TODO("Return the communication channel to the service.")
  }

  override fun onCreate() {
    super.onCreate()
    val user = User(this)
    val header = HashMap<String, String>()
    header["Authorization"] = "Bearer ${user.getString("token")}"
    header["Accept"] = "application/json"
    header["Content-Type"] = "application/x-www-form-urlencoded"
    val authorize = HttpAuthorizer(Url.Pusher.auth())
    authorize.setHeaders(header)
    val options = PusherOptions().setHost(Url.Pusher.url).setWsPort(Url.Pusher.port).setWssPort(Url.Pusher.port).setUseTLS(Url.Pusher.secured).setAuthorizer(authorize)
    val pusher = Pusher("arbi.biz.key", options)
    val announcementChannel = pusher.subscribe("arbi.biz.announcement")
    val botChannel = pusher.subscribe("arbi.biz.bot")
    val maintenanceChannel = pusher.subscribe("arbi.biz.maintenance")
    OnAnnounce(this@PusherReceiver, announcementChannel).bind()
    OnMaintenance(this@PusherReceiver, maintenanceChannel).bind()
    OnBot(this@PusherReceiver, botChannel).bind()

    pusher.connect(object : ConnectionEventListener {
      override fun onConnectionStateChange(change: ConnectionStateChange) {
        if (change.currentState == ConnectionState.CONNECTED) {
          Log.d("pusher", "PersonalReceiver Connected")
        }
      }

      override fun onError(message: String?, code: String?, e: Exception?) {
        Log.e("json", e?.stackTraceToString() ?: "")
        Log.e("pusher", "PersonalReceiver connection fail: $message ; $e code $code")
      }
    })
  }
}