package biz.arbitrade.controller.events

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import biz.arbitrade.MainActivity
import biz.arbitrade.controller.Helper
import biz.arbitrade.controller.Notification
import biz.arbitrade.model.User
import com.pusher.client.channel.Channel
import org.json.JSONObject

class OnTicket(context: Context, channel: Channel) : PusherEvent(context, channel, true) {
  override val eventName: String = "App\\Events\\TicketEvent"

  init {
    Log.d("MIME", "OnTicket Initialized")
  }

  override fun handle(context: Context, result: JSONObject) {
    Log.i("MIME $eventName", result.toString())

    val title = result.getString("username")
    val message = result.getString("ticket")
    val mNotification = Notification.make(context, "arbi.announce", title, message)
    val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(Notification.Id.announcement, mNotification)
  }
}