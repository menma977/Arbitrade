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

class OnBot(context: Context, channel: Channel) : PusherEvent(context, channel, false) {
  override val eventName: String = "App\\Events\\Announcement"

  override fun handle(context: Context, result: JSONObject) {
    val user = User(context)
    user.setLong("minBot", result.getLong("min"))
    user.setLong("maxBot", result.getLong("max"))
    user.setFloat("itShare", result.getDouble("it").toFloat())
    user.setFloat("buyWallShare", result.getDouble("buy_wall").toFloat())
    user.setFloat("sponsorShare", result.getDouble("sponsor").toFloat())
  }
}