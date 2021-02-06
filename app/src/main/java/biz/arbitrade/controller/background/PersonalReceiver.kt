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
import com.pusher.client.util.HttpAuthorizer
import java.lang.Exception

class PersonalReceiver() : Service() {
  override fun onBind(intent: Intent): IBinder {
    TODO("Return the communication channel to the service.")
  }

  override fun onCreate() {
    super.onCreate()
    Log.d("MIME", "PersonalReceiver onCreate")
    val user = User(this)
    val header = HashMap<String, String>()
    header["Authorization"] = "Bearer ${user.getString("token")}"
    val authorize = HttpAuthorizer(Url.Pusher.auth())
    authorize.setHeaders(header)
    val options =
      PusherOptions().setHost(Url.Pusher.url).setWsPort(Url.Pusher.port).setAuthorizer(authorize)
        .setUseTLS(Url.Pusher.secured) //.setCluster("mt1")
    val pusher = Pusher("arib.biz.key", options)
    Log.d("pusher", options.buildUrl(Url.Pusher.auth()))
    Log.d("pusher", "-pusher.subscribePrivate \"private-arbi.biz.${user.getString("username")}\"")
    val personalChannel =
      pusher.subscribePrivate("private-arbi.biz.admin", object :
        PrivateChannelEventListener {
        override fun onEvent(event: PusherEvent?) {
          Log.d("pusher", "-onEvent")
          //TODO("Not yet implemented")
        }

        override fun onSubscriptionSucceeded(channelName: String?) {
          Log.d("pusher", "-onSubscriptionSucceeded")
          //TODO("Not yet implemented")
        }

        override fun onAuthenticationFailure(message: String?, e: Exception?) {
          Log.d("pusher", "-onAuthenticationFailure")
          //TODO("Not yet implemented")
        }

      })

    pusher.connect()

    OnTicket(this, personalChannel).bind()
  }

}