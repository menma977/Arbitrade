package biz.arbitrade.controller.events

import android.content.Context
import com.pusher.client.channel.Channel
import org.json.JSONObject

abstract class PusherEvent(
  private val context: Context,
  private val channel: Channel
) {
  abstract val eventName: String

  abstract fun handle(context: Context, result: JSONObject)

  fun bind(){
    channel.bind(eventName) { handle(context, JSONObject(it.data)) }
  }
}