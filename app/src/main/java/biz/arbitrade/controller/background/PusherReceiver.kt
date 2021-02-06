package biz.arbitrade.controller.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import biz.arbitrade.controller.events.OnAnnounce
import biz.arbitrade.controller.events.OnTicket
import biz.arbitrade.controller.events.PusherEvent
import biz.arbitrade.model.User
import biz.arbitrade.network.Url
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer
import org.json.JSONObject

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
    val options = PusherOptions().setHost(Url.Pusher.url).setWsPort(Url.Pusher.port).setUseTLS(Url.Pusher.secured).setAuthorizer(authorize) //.setCluster("mt1")
    val pusher = Pusher("arib.biz.key", options)
    pusher.connect(object : ConnectionEventListener {
      override fun onConnectionStateChange(change: ConnectionStateChange) {
        println(
          "State changed to " + change.currentState + " from " + change.previousState
        )
        if (change.currentState == ConnectionState.CONNECTED) {
          val announcementChannel = pusher.subscribe("arbi.biz.announcement")

          OnAnnounce(this@PusherReceiver, announcementChannel).bind()
        }
      }

      override fun onError(message: String?, code: String?, e: Exception?) {
        println("Pusher Receiver There was a problem connecting!")
      }
    })
  }
}