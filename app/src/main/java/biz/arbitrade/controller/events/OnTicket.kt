package biz.arbitrade.controller.events

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import biz.arbitrade.MainActivity
import biz.arbitrade.model.User
import com.pusher.client.channel.Channel
import org.json.JSONObject

class OnTicket(context: Context, channel: Channel) : PusherEvent(context, channel, true) {
  override val eventName: String = "App\\Events\\TicketEvent"

  override fun handle(context: Context, result: JSONObject) {
    val user = User(context)
    val intent = Intent(context, MainActivity::class.java)
    user.setLong("totalPin", result.getLong("ticket"))
    intent.action = "ticket"
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
  }
}