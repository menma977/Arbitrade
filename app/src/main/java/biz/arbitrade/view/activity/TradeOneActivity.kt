package biz.arbitrade.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import biz.arbitrade.R
import biz.arbitrade.controller.Helper
import biz.arbitrade.controller.TradeOneController
import biz.arbitrade.model.Bet
import biz.arbitrade.model.User
import kotlinx.android.synthetic.main.activity_trade_one.*
import kotlinx.android.synthetic.main.activity_trade_one.txtWarning
import kotlinx.android.synthetic.main.activity_trade_two.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.abs
import kotlin.random.Random

class TradeOneActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var bet: Bet
  private lateinit var controller: TradeOneController
  private lateinit var spinner: ProgressBar
  private lateinit var loading: ProgressBar
  private lateinit var status: TextView
  private lateinit var minTrading: TextView
  private lateinit var maxTrading: TextView
  private var onTrading = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_trade_one)

    user = User(this)
    bet = Bet(this)
    controller = TradeOneController()

    spinner = findViewById(R.id.spinner)
    loading = findViewById(R.id.progressBar)
    status = findViewById(R.id.txtStatus)
    minTrading = findViewById(R.id.min_trading)
    maxTrading = findViewById(R.id.max_trading)
    txtWarning.isSelected = true

    minTrading.text = Helper.toDogeString(user.getLong("minBot"))
    maxTrading.text = Helper.toDogeString(user.getLong("maxBot"))

    Log.d("MINE", user.getString("hasTradedFake"))
    if (user.getString("hasTradedFake") == "true") {
      Toast.makeText(this@TradeOneActivity, "You have traded today", Toast.LENGTH_SHORT).show()
      finish()
      return
    } else {
      val lastBet = Bet.getCalendar(bet.getLong("last_f_trade"))
      val now = Calendar.getInstance()
      if (bet.has("last_f_trade") && lastBet.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) && lastBet.get(
          Calendar.YEAR
        ) == now.get(Calendar.YEAR)
      ) {
        if (bet.has("last_f_trade_result")) {
          statusChange(
            if (bet.getBoolean("last_f_trade_result")) R.string.win else R.string.lose,
            if (bet.getBoolean("last_f_trade_result")) R.color.Info else R.color.Danger
          )
        } else statusChange(R.string.already_play, R.color.Danger)
        spinner.visibility = View.GONE
        status.visibility = View.VISIBLE
        progressBar.progress = progressBar.max
      } else {
        spinner.visibility = View.VISIBLE
        status.visibility = View.GONE
        progressBar.progress = 0
        Timer().schedule(100) {
          Log.d("MINE", user.getLong("balance").toString())
          val response = controller.trade(user)
          Log.d("MINE", response.toString())
          if (response.getInt("code") < 400) {
            onTrading = true
            Timer().scheduleAtFixedRate(
              abs(Random.nextLong() % 750), abs((Random.nextLong() % 50) + 25)
            ) {
              runOnUiThread {
                progressBar.progress++
                if (progressBar.progress == progressBar.max) {
                  spinner.visibility = View.GONE
                  status.visibility = View.VISIBLE
                  Log.d("MINE", response.toString())
                  statusChange(
                    if (response.getString("message") == "WIN") R.string.win else R.string.lose,
                    if (response.getString("message") == "WIN") R.color.Info else R.color.Danger
                  )
                  onTrading = false
                  cancel()
                }
              }
            }
          } else {
            val message = response.optString("data")
            println("==============================")
            println(message)
            println("==============================")
            Log.e("TradeOne.ArbiAPI", message)
            runOnUiThread {
              spinner.visibility = View.GONE
              status.visibility = View.VISIBLE
              statusChange(
                R.string.cannot_start_trading,
                R.color.Danger,
                response.optString("message")
              )
              onTrading = false
              Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
            }
          }
        }
      }
    }
  }

  override fun onBackPressed() {
    if (!onTrading) super.onBackPressed()
  }

  private fun statusChange(text: Int, color: Int, msg: String = "") {
    if (msg.isEmpty())
      status.setText(text)
    else
      status.text = msg
    status.setTextColor(ContextCompat.getColor(this, color))
  }

  private var broadcastReceiverTrade: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (user.getString("hasTradedFake") == "true") {
        Toast.makeText(this@TradeOneActivity, "You have traded today-", Toast.LENGTH_SHORT).show()
        finish()
      }
    }
  }

  override fun onStart() {
    super.onStart()
    LocalBroadcastManager.getInstance(this)
      .registerReceiver(broadcastReceiverTrade, IntentFilter("trade"))
  }

  override fun onPause() {
    super.onPause()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverTrade)
  }

  override fun onStop() {
    super.onStop()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverTrade)
  }

  override fun onDestroy() {
    super.onDestroy()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverTrade)
  }
}
