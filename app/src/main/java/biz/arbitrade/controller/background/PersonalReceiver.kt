package biz.arbitrade.controller.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import biz.arbitrade.controller.events.OnTicket
import biz.arbitrade.controller.events.OnTrade
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
    header["X-Requested-With"] = "XMLHttpRequest"
    header["Accept"] = "application/json"
    Log.d("pusher", "TOKEN : ${user.getString("token")}")
    val authorize = HttpAuthorizer(Url.Pusher.auth())
    authorize.setHeaders(header)
    val options = PusherOptions().setHost(Url.Pusher.url).setWsPort(Url.Pusher.port).setWssPort(Url.Pusher.port).setUseTLS(Url.Pusher.secured).setAuthorizer(authorize)
    val privatePusher = Pusher("arbi.biz.key", options)
    val personalChannel = privatePusher.subscribePrivate("private-arbi.biz.${user.getString("username")}", object : PrivateChannelEventListener {
      override fun onEvent(event: PusherEvent) {}

      override fun onSubscriptionSucceeded(channelName: String?) {
        Log.d("pusher", "PersonalReceiver subscribe to $channelName success")
      }

      override fun onAuthenticationFailure(message: String?, e: Exception?) {
        Log.e("pusher", "PersonalReceiver subscribe to $message fail $e")
      }
    })

    OnTicket(application, personalChannel).bind()
    OnTrade(application, personalChannel).bind()

    privatePusher.connect(object : ConnectionEventListener {
      override fun onConnectionStateChange(change: ConnectionStateChange) {
        if (change.currentState == ConnectionState.CONNECTED) {
          Log.d("pusher", "PersonalReceiver Connected")
        }
      }

      override fun onError(message: String?, code: String?, e: Exception?) {
        Log.e("pusher", "PersonalReceiver connection fail: $message ; $e code $code")
      }
    })
  }

}