package biz.arbitrade.controller.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import biz.arbitrade.controller.events.OnAnnounce
import biz.arbitrade.network.Url
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions

class PusherReceiver : Service() {
  override fun onBind(intent: Intent): IBinder {
    TODO("Return the communication channel to the service.")
  }

  override fun onCreate() {
    super.onCreate()
    val options = PusherOptions().setHost(Url.pusher.url).setWsPort(Url.pusher.port)
      .setUseTLS(Url.pusher.secured) //.setCluster("mt1")
    val pusher = Pusher("arib.biz.key", options)
    val announcementChannel = pusher.subscribe("arbi.biz.announcement")

    OnAnnounce(this, announcementChannel).bind()

    pusher.connect()
  }

}