package biz.arbitrade.controller.background

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import biz.arbitrade.MainActivity
import biz.arbitrade.R
import biz.arbitrade.model.User
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PusherEvent
import org.json.JSONObject

class PusherReceiver : Service() {
  private val AnnouncementNotification = 0x43

  override fun onBind(intent: Intent): IBinder {
    TODO("Return the communication channel to the service.")
  }

  override fun onCreate() {
    super.onCreate()
    val options = PusherOptions().setHost("10.0.2.2").setWsPort(6001) //.setCluster("mt1")
      .setUseTLS(false)
    val pusher = Pusher("arib.biz.key", options)
    val announcementChannel = pusher.subscribe("arbi.biz.announcement")

    announcementChannel.bind("App\\Events\\Announcement") { handleEvent(it) }

    pusher.connect()
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createChannel(id: String, name: String, description: String) {
    val mNotificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val mChannel = NotificationChannel(id, name, importance)
    mChannel.description = description
    mChannel.setShowBadge(false)
    mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    mNotificationManager.createNotificationChannel(mChannel)
  }

  private fun handleEvent(event: PusherEvent) {
    val user = User(applicationContext)
    val data = JSONObject(event.data)
    val intent = Intent(this, MainActivity::class.java)
    if (data.has("title") && !data.optString("title").isNullOrBlank()) {
      user.setString("announcement", event.data)
      if (isAppIsInBackground(this)) {
        val title = data.getString("title")
        val message = data.getString("message")
        val mNotification = makeNotification(title, message)
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(AnnouncementNotification, mNotification)
      } else {
        intent.action = "announcement"
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
      }
    } else {
      user.setString("announcement", "")
      intent.action = "announcement"
      LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }
  }

  private fun makeNotification(title: String, message: String): Notification {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createChannel("arbi.announce", title, message)
      Notification.Builder(this, "arbi.announce").setAutoCancel(true).setContentTitle(title).setSmallIcon(R.drawable.ic_arbitrade_square).setStyle(
          Notification.BigTextStyle().bigText(message)
        ).setContentText(message).build()
    } else {
      Notification.Builder(this).setAutoCancel(true).setPriority(Notification.PRIORITY_DEFAULT).setSmallIcon(R.drawable.ic_arbitrade_square).setContentTitle(title).setStyle(
          Notification.BigTextStyle().bigText(message)
        ).setContentText(message).build()
    }
  }

  private fun isAppIsInBackground(context: Context): Boolean {
    var isInBackground = true
    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningProcesses = am.runningAppProcesses
    for (processInfo in runningProcesses) {
      if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
        for (activeProcess in processInfo.pkgList) {
          if (activeProcess == context.getPackageName()) {
            isInBackground = false
          }
        }
      }
    }
    return isInBackground
  }

}