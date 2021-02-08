package biz.arbitrade.view.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import biz.arbitrade.R
import biz.arbitrade.controller.Helper
import biz.arbitrade.model.User
import biz.arbitrade.view.activity.RegisterActivity
import biz.arbitrade.view.activity.TradeOneActivity
import biz.arbitrade.view.activity.TradeTwoActivity
import kotlinx.android.synthetic.main.part_announcement.view.*
import org.json.JSONObject

class HomeFragment : Fragment() {
  private lateinit var username: TextView
  private lateinit var balance: TextView
  private lateinit var register: LinearLayout
  private lateinit var tradeOne: LinearLayout
  private lateinit var tradeTwo: LinearLayout
  private lateinit var announcementGroup: LinearLayout
  private lateinit var user: User

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_home, container, false)

    user = User(activity!!.applicationContext)

    Log.d("MINE", R.id.btnRegister.toString())

    username = view.findViewById(R.id.textViewUsername)
    balance = view.findViewById(R.id.textViewBalance)
    register = view.findViewById(R.id.lnrLayoutRegister)
    tradeOne = view.findViewById(R.id.lnrLayoutTradeOne)
    tradeTwo = view.findViewById(R.id.lnrLayoutTradeTwo)
    announcementGroup = view.findViewById(R.id.lnrLayoutAnnouncements)

    username.text = user.getString("username")
    balance.text = Helper.toDogeString(user.getLong("balance"))

    register.setOnClickListener {
      move("register")
    }
    tradeOne.setOnClickListener {
      move("trade_one")
    }
    tradeTwo.setOnClickListener {
      move("trade_two")
    }

    if (user.getString("announcement").isNotBlank()) {
      val announce = JSONObject(user.getString("announcement"))
      createAnnouncement(announce.getString("message"))
    }

    return view
  }

  private fun createAnnouncement(message: String) {
    val v = this.layoutInflater.inflate(R.layout.part_announcement, null) //v.imageAnnouncementIcon.setImageResource()
    v.textAnnouncementMessage.text = message
    announcementGroup.addView(v)
  }

  private fun move(
    to: String, finish: Boolean = false, finishAffinity: Boolean = false
  ) {
    val intent = Intent(
      activity, when (to) {
        "register" -> RegisterActivity::class.java
        "trade_one" -> TradeOneActivity::class.java
        "trade_two" -> TradeTwoActivity::class.java
        else -> null
      }
    )
    startActivity(intent)
    if (finish) {
      if (finishAffinity) activity?.finishAffinity()
      else activity?.finish()
    }
  }

  override fun onStart() {
    super.onStart()
    LocalBroadcastManager.getInstance(activity!!).registerReceiver(broadcastReceiverAnnouncement, IntentFilter("announcement"))
  }

  override fun onDestroy() {
    super.onDestroy()
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverAnnouncement)
  }

  override fun onStop() {
    super.onStop()
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverAnnouncement)
  }

  override fun onPause() {
    super.onPause()
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverAnnouncement)
  }

  private var broadcastReceiverAnnouncement: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      announcementGroup.removeAllViewsInLayout()
      if (user.getString("announcement").isNotBlank()) {
        val announce = JSONObject(user.getString("announcement"))
        createAnnouncement(announce.getString("message"))
      }
      announcementGroup.invalidate()
      announcementGroup.requestLayout()
    }
  }

}