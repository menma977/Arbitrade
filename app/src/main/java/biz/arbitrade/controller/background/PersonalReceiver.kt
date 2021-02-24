package biz.arbitrade.controller.background

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import biz.arbitrade.controller.events.OnLogout
import biz.arbitrade.controller.events.OnTicket
import biz.arbitrade.controller.events.OnTrade
import biz.arbitrade.model.User
import biz.arbitrade.network.Url
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer

class PersonalReceiver : Service() {
  private val binder = LocalBinder()
  private lateinit var token: String
  private lateinit var username: String
  private lateinit var personalChannel: PrivateChannel
  private lateinit var privatePusher: Pusher

  override fun onBind(intent: Intent): IBinder {
    token = intent.getStringExtra("token") ?: ""
    username = intent.getStringExtra("username") ?: ""
    val header = HashMap<String, String>()
    header["Authorization"] = "Bearer $token}"
    header["X-Requested-With"] = "XMLHttpRequest"
    header["Accept"] = "application/json"
    val authorize = HttpAuthorizer(Url.Pusher.auth())
    authorize.setHeaders(header)
    val options =
      PusherOptions()
        .setHost(Url.Pusher.url)
        .setWsPort(Url.Pusher.port)
        .setWssPort(Url.Pusher.port)
        .setUseTLS(Url.Pusher.secured)
        .setAuthorizer(authorize)
    privatePusher = Pusher("arbi.biz.key", options)
    personalChannel =
      privatePusher.subscribePrivate(
        "private-arbi.biz." + username,
        object : PrivateChannelEventListener {
          override fun onEvent(event: PusherEvent) {}

          override fun onSubscriptionSucceeded(channelName: String?) {
            Log.d("pusher", "PersonalReceiver subscribe to $channelName success")
          }

          override fun onAuthenticationFailure(message: String?, e: Exception?) {
            Log.e("pusher", "PersonalReceiver subscribe to $message fail $e")
            e?.printStackTrace()
          }
        })

    OnTicket(application, personalChannel).bind()
    OnTrade(application, personalChannel).bind()
    OnLogout(application, personalChannel).bind()

    privatePusher.connect(
      object : ConnectionEventListener {
        override fun onConnectionStateChange(change: ConnectionStateChange) {
          if (change.currentState == ConnectionState.CONNECTED) {
            Log.d("pusher", "PersonalReceiver Connected")
          }
        }

        override fun onError(message: String?, code: String?, e: Exception?) {
          Log.e("pusher", "PersonalReceiver connection fail: $message ; $e code $code", e)
          e?.printStackTrace()
        }
      })
    return binder
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d("MINE", "destroy")
    privatePusher.disconnect()
  }

  override fun onUnbind(intent: Intent?): Boolean {
    Log.d("MINE", "onUnbind")
    privatePusher.disconnect()
    return super.onUnbind(intent)
  }

  inner class LocalBinder : Binder() {
    // Return this instance of LocalService so clients can call public methods
    fun getService(): PersonalReceiver = this@PersonalReceiver
  }

}
