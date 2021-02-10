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

class OnAnnounce(context: Context, channel: Channel) : PusherEvent(context, channel, false) {
  override val eventName: String = "App\\Events\\Announcement"

  override fun handle(context: Context, result: JSONObject) {
    val user = User(context)
    val intent = Intent(context, MainActivity::class.java)
    if (result.has("title") && !result.optString("title").isNullOrBlank()) {
      user.setString("announcement", result.toString())
      if (Helper.isAppIsInBackground(context)) {
        val title = result.getString("title")
        val message = result.getString("message")
        val mNotification = Notification.make(context, "arbi.announce", title, message)
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Notification.Id.announcement, mNotification)
      } else {
        intent.action = "announcement"
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
      }
    } else {
      user.setString("announcement", "")
      intent.action = "announcement"
      LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
  }
}