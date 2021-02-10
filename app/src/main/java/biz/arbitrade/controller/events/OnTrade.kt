package biz.arbitrade.controller.events

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import biz.arbitrade.MainActivity
import biz.arbitrade.model.User
import com.pusher.client.channel.Channel
import org.json.JSONObject

class OnTrade(context: Context, channel: Channel): PusherEvent(context, channel, true) {
  override val eventName: String = "App\\Events\\TradeEvent"

  override fun handle(context: Context, result: JSONObject) {
    val user = User(context)
    val intent = Intent(context, MainActivity::class.java)
    user.setString("hasTradedReal", result.getString("bot_two"))
    user.setString("hasTradedFake", result.getString("bot_one"))
    intent.action = "trade"
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
  }
}