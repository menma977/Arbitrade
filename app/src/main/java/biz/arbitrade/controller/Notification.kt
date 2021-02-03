package biz.arbitrade.controller

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import biz.arbitrade.R

object Notification {
  object Id {
    const val announcement = 0x43
  }

  private fun createChannel(context: Context, id: String, name: String, description: String) {
    val mNotificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val mChannel = NotificationChannel(id, name, importance)
    mChannel.description = description
    mChannel.setShowBadge(false)
    mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    mNotificationManager.createNotificationChannel(mChannel)
  }

  fun make(context: Context, channelId: String, title: String, message: String, isPermanent: Boolean = false): Notification {
    createChannel(context, channelId, title, message)
    val notification = Notification.Builder(context, channelId)
    notification.setAutoCancel(true)
    notification.setContentTitle(title)
    notification.setSmallIcon(R.drawable.ic_arbitrade_square)
    notification.style = Notification.BigTextStyle().bigText(message)
    notification.setContentText(message)

    return if (isPermanent) {
      notification.setOngoing(true).build()
    } else {
      notification.build()
    } //    return Notification.Builder(context, channelId).setAutoCancel(true).setContentTitle(title).setSmallIcon(R.drawable.ic_arbitrade_square).setStyle(Notification.BigTextStyle().bigText(message))
    //      .setContentText(message).setOngoing(true).build()
  }
}