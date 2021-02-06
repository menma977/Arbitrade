package biz.arbitrade.controller.events

import android.content.Context
import android.util.Log
import com.pusher.client.channel.Channel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import org.json.JSONObject

abstract class PusherEvent(
  private val context: Context,
  private val channel: Channel,
  private val isPrivateChannel: Boolean = true
) {
  abstract val eventName: String

  abstract fun handle(context: Context, result: JSONObject)

  fun bind() {
    return if (isPrivateChannel) {
      Log.d("pusher", "$eventName $isPrivateChannel")
      Log.d("pusher", channel.name)
      channel.bind(eventName, object : PrivateChannelEventListener {
        override fun onEvent(event: PusherEvent) {
          Log.d("pusher", "Event Appear")
          handle(context, JSONObject(event.data))
        }

        override fun onSubscriptionSucceeded(channelName: String?) {
          Log.d("pusher", "private sub $eventName success")
        }

        override fun onAuthenticationFailure(message: String?, e: Exception?) {
          Log.d("pusher", "private sub $eventName fail")
        }
      })
    }else
      channel.bind(eventName) { handle(context, JSONObject(it.data)) }
  }
}