package biz.arbitrade.controller

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import biz.arbitrade.R

object Notification {

  object Id {
    const val announcement = 0x43
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createChannel(context: Context ,id: String, name: String, description: String) {
    val mNotificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val mChannel = NotificationChannel(id, name, importance)
    mChannel.description = description
    mChannel.setShowBadge(false)
    mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    mNotificationManager.createNotificationChannel(mChannel)
  }

  fun make(context: Context, channelId: String,title: String, message: String): Notification {
    createChannel(context, channelId, title, message)
    return Notification.Builder(context, channelId).setAutoCancel(true).setContentTitle(title).setSmallIcon(
      R.drawable.ic_arbitrade_square).setStyle(Notification.BigTextStyle().bigText(message))
      .setContentText(message).build()
  }
}