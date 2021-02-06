package biz.arbitrade.controller.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import biz.arbitrade.controller.events.OnTicket
import biz.arbitrade.model.User
import biz.arbitrade.network.Url
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer
import java.lang.Exception

class PersonalReceiver : Service() {
  override fun onBind(intent: Intent): IBinder {
    TODO("Return the communication channel to the service.")
  }

  override fun onCreate() {
    super.onCreate()
    Log.d("MIME", "PersonalReceiver onCreate")
    val user = User(this)
    val header = HashMap<String, String>()
    header["Authorization"] = "Bearer ${user.getString("token")}"
    Log.d("pusher", "TOKEN : ${user.getString("token")}")
    val authorize = HttpAuthorizer(Url.Pusher.auth())
    authorize.setHeaders(header)
    val options = PusherOptions().setHost(Url.Pusher.url).setWsPort(Url.Pusher.port).setAuthorizer(authorize).setUseTLS(Url.Pusher.secured) //.setCluster("mt1")
    val privatePusher = Pusher("arib.biz.key", options)

    privatePusher.connect(object : ConnectionEventListener {
      override fun onConnectionStateChange(change: ConnectionStateChange) {
        println(
          "pv State changed to " + change.currentState + " from " + change.previousState
        )
        if (change.currentState == ConnectionState.CONNECTED) {
          val personalChannel = privatePusher.subscribePrivate("private-arbi.biz.admin", object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent) {
              println("pusher")
              Log.d("pusher", event.data)
            }

            override fun onSubscriptionSucceeded(channelName: String?) {
              println("pusher2")
              Log.d("pusher", "private sub $channelName success")
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {
              println("pusher3")
              Log.e("pusher", "private sub $message fail $e")
              user.setString("username", message ?: "Error")
            }
          })

          OnTicket(application, personalChannel).bind()
        }
      }

      override fun onError(message: String?, code: String?, e: Exception?) {
        println("Personal Receiver There was a problem connecting!")
        Log.e("pusher", "private sub $message fail $e code $code")
      }
    })
  }

}