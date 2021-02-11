package biz.arbitrade.controller.events

import android.content.Context
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