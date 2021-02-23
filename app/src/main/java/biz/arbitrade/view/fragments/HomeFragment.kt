package biz.arbitrade.view.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import biz.arbitrade.R
import biz.arbitrade.controller.Helper
import biz.arbitrade.model.User
import biz.arbitrade.view.activity.*
import biz.arbitrade.view.dialog.DepositDialog
import biz.arbitrade.view.dialog.WithdrawDialog
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.part_announcement.view.*
import org.json.JSONObject

class HomeFragment : Fragment() {
  private lateinit var username: TextView
  private lateinit var balance: TextView
  private lateinit var txtTotalPin: TextView
  private lateinit var txtWallet: TextView
  private lateinit var imgQr: ImageView
  private lateinit var register: LinearLayout
  private lateinit var withdraw: LinearLayout
  private lateinit var sendTicket: LinearLayout
  private lateinit var tradeOne: LinearLayout
  private lateinit var tradeTwo: LinearLayout
  private lateinit var history: LinearLayout
  private lateinit var announcementGroup: LinearLayout
  private lateinit var user: User

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_home, container, false)

    user = User(activity!!.applicationContext)
    username = view.findViewById(R.id.textViewUsername)
    balance = view.findViewById(R.id.textViewBalance)
    txtTotalPin = view.findViewById(R.id.txtTotalPin)
    txtWallet = view.findViewById(R.id.txtWallet)
    imgQr = view.findViewById(R.id.imgQr)
    register = view.findViewById(R.id.lnrLayoutRegister)
    withdraw = view.findViewById(R.id.lnrLayoutWithdraw)
    sendTicket = view.findViewById(R.id.lnrLayoutSendTicket)
    tradeOne = view.findViewById(R.id.lnrLayoutTradeOne)
    tradeTwo = view.findViewById(R.id.lnrLayoutTradeTwo)
    history = view.findViewById(R.id.lnrLayoutHistory)
    announcementGroup = view.findViewById(R.id.lnrLayoutAnnouncements)

    txtTotalPin.text = Helper.toDogeString(user.getLong("totalPin"))

    username.text = user.getString("username")
    balance.text = Helper.toDogeString(user.getLong("balance"))
    val barcodeEncoder = BarcodeEncoder()
    val wallet = if (user.getString("wallet").isBlank()) "placeholder" else user.getString("wallet")
    val bitmap = barcodeEncoder.encodeBitmap(wallet, BarcodeFormat.QR_CODE, 500, 500)
    imgQr.setImageBitmap(bitmap)

    imgQr.setOnClickListener {
      DepositDialog(this@HomeFragment.requireActivity(), user.getString("wallet"))
    }

    register.setOnClickListener { move("register") }
    sendTicket.setOnClickListener { move("send_ticket") }
    tradeOne.setOnClickListener { move("trade_one") }
    tradeTwo.setOnClickListener { move("trade_two") }
    history.setOnClickListener { move("history") }
    withdraw.setOnClickListener {
      WithdrawDialog(this@HomeFragment.requireActivity(), user.getString("cookie"))
    }

    if (user.getString("announcement").isNotBlank()) {
      val announce = JSONObject(user.getString("announcement"))
      createAnnouncement(announce.getString("message"))
    }

    return view
  }

  private fun createAnnouncement(message: String) {
    val v = this.layoutInflater.inflate(
      R.layout.part_announcement, null
    ) // v.imageAnnouncementIcon.setImageResource()
    v.textAnnouncementMessage.text = message
    announcementGroup.addView(v)
  }

  private fun move(to: String, finish: Boolean = false, finishAffinity: Boolean = false) {
    val intent = Intent(
      activity, when (to) {
        "register" -> RegisterActivity::class.java
        "send_ticket" -> SendTicketActivity::class.java
        "trade_one" -> TradeOneActivity::class.java
        "trade_two" -> TradeTwoActivity::class.java
        "history" -> HistoryActivity::class.java
        else -> null
      }
    )
    startActivity(intent)
    if (finish) {
      if (finishAffinity) activity?.finishAffinity() else activity?.finish()
    }
  }

  override fun onStart() {
    super.onStart()
    LocalBroadcastManager.getInstance(activity!!).registerReceiver(broadcastReceiverAnnouncement, IntentFilter("announcement"))
    LocalBroadcastManager.getInstance(activity!!).registerReceiver(broadcastReceiverTicket, IntentFilter("ticket"))
    LocalBroadcastManager.getInstance(activity!!).registerReceiver(broadcastReceiverDoge, IntentFilter("web.doge"))
  }

  override fun onDestroy() {
    super.onDestroy()
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverAnnouncement)
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverTicket)
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverDoge)
  }

  override fun onStop() {
    super.onStop()
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverAnnouncement)
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverTicket)
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverDoge)
  }

  override fun onPause() {
    super.onPause()
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverAnnouncement)
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverTicket)
    LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiverDoge)
  }

  private var broadcastReceiverDoge: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      balance.text = Helper.toDogeString(user.getLong("balance"))
    }
  }
  private var broadcastReceiverTicket: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      txtTotalPin.text = Helper.toDogeString(user.getLong("totalPin"))
    }
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
