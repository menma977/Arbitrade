package biz.arbitrade.controller.events

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pusher.client.channel.Channel
import org.json.JSONObject

class OnMaintenance(context: Context, channel: Channel) : PusherEvent(context, channel, true) {
  override val eventName: String = "App\\Events\\Maintenance"

  override fun handle(context: Context, result: JSONObject) {
    val intent = Intent("on_maintenance")
    intent.putExtra("status", result.optBoolean("status"))
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
  }
}