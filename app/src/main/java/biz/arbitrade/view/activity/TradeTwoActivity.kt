package biz.arbitrade.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import biz.arbitrade.R
import biz.arbitrade.controller.DogeHelper
import biz.arbitrade.controller.Helper
import biz.arbitrade.controller.TradeTwoController
import biz.arbitrade.model.Bet
import biz.arbitrade.model.BetHistory
import biz.arbitrade.model.User
import biz.arbitrade.network.ArbizAPI
import biz.arbitrade.view.adapter.BetHistoryAdapter
import kotlinx.android.synthetic.main.activity_trade_one.txtWarning
import kotlinx.android.synthetic.main.activity_trade_two.*
import org.eazegraph.lib.charts.ValueLineChart
import org.eazegraph.lib.models.ValueLinePoint
import org.eazegraph.lib.models.ValueLineSeries
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.pow

class TradeTwoActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var bets: Bet
  private lateinit var chart: ValueLineChart
  private lateinit var history: RecyclerView
  private lateinit var controller: TradeTwoController
  private lateinit var layoutMain: LinearLayout
  private lateinit var layoutBtn: LinearLayout
  private lateinit var btnContinue: Button
  private lateinit var btnStop: Button
  private lateinit var spinner: ProgressBar
  private lateinit var status: TextView
  private lateinit var startingBalance: TextView
  private lateinit var remainingBalance: TextView
  private lateinit var txtTarget: TextView
  private lateinit var txtLose: TextView
  private var bet: Long = 0
  private var betCounter: Int = 0
  private var betHistory = ArrayList<BetHistory>()
  private var counter = 1
  private val betDelay: Long = 250
  private val betPeriod: Long = 2500
  private val chartFreq: Int = 30
  private val dogeWinChance = .5f
  private val betLow = 0
  private val betHigh = (999999 * dogeWinChance).toInt()
  private val loseTarget = 1f
  private var winTarget = .02f
  private val maxBetCount = 5
  private lateinit var initialTask: TimerTask

  private var timerRunning = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_trade_two)

    controller = TradeTwoController()
    user = User(this)
    bets = Bet(this)

    layoutBtn = findViewById(R.id.lnrLayoutBtnTrade)
    layoutMain = findViewById(R.id.lnrLayoutMain)

    spinner = findViewById(R.id.spinner)
    status = findViewById(R.id.txtStatus)
    startingBalance = findViewById(R.id.starting_balance)
    remainingBalance = findViewById(R.id.remaining_balance)
    txtTarget = findViewById(R.id.target_win)
    txtLose = findViewById(R.id.target_lose)

    btnStop = findViewById(R.id.stopBtn)
    btnContinue = findViewById(R.id.continueBtn)

    layoutBtn.visibility = View.GONE
    layoutMain.invalidate()
    layoutMain.requestLayout()

    spinner.visibility = View.VISIBLE
    status.visibility = View.GONE

    startingBalance.text = Helper.toDogeString(user.getLong("balance"))
    remainingBalance.text = Helper.toDogeString(user.getLong("balance"))

    chart = findViewById(R.id.cubicLineChart)
    history = findViewById(R.id.listHistory)
    val series = ValueLineSeries()
    series.color = getColor(R.color.colorPrimary)
    series.addPoint(ValueLinePoint("0", (user.getLong("balance") / 10.0.pow(4)).toFloat()))
    txtWarning.isSelected = true
    val adapter = BetHistoryAdapter(betHistory)
    history.layoutManager = LinearLayoutManager(this)
    history.adapter = adapter

    chart.axisTextColor = getColor(R.color.textSecondary)
    chart.containsPoints()
    chart.isUseDynamicScaling = true
    chart.addSeries(series)
    chart.startAnimation()
    val target = user.getLong("balance") + (user.getLong("balance") * winTarget).toLong()
    val lose = if(loseTarget == 1f) 0 else user.getLong("balance") - (user.getLong("balance") * loseTarget).toLong()

    btnStop.setOnClickListener {
      Timer().schedule(100) {
        initialTask.cancel()
        endTrading(user.getLong("profit"))
      }
    }

    btnContinue.setOnClickListener {
      val t = user.getLong("balance") + (user.getLong("balance") * winTarget).toLong()
      val l = if(loseTarget == 1f) 0 else user.getLong("balance") - (user.getLong("balance") * loseTarget).toLong()
      startTask(t, l, series)
    }

    bet = (user.getLong("balance") * 0.001).toLong()
    if (bet > 0) {
      controller.initialBet = bet

      if (bets.has("last_bet")) {
        val lastBet = Bet.getCalendar(bets.getLong("last_bet"))
        val now = Calendar.getInstance()
        if (lastBet.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
          lastBet.get(Calendar.YEAR) == now.get(Calendar.YEAR)
        ) {
          if (!bets.getBoolean("isWithdrawn")) {
            Timer().schedule(100) {
              endTrading(user.getLong("profit"))
            }
          }
        }
      } else {
        if (user.getString("hasTradedReal") == "true") {
          Toast.makeText(this@TradeTwoActivity, "You have traded today", Toast.LENGTH_SHORT).show()
          finish()
        } else {
          startTask(target, lose, series)
          bets.setBoolean("isWithdrawn", false)
        }
      }
    } else {
      Toast.makeText(this, "Cannot trade with empty balance", Toast.LENGTH_SHORT).show()
      finish()
    }

    if(user.getLong("balance") < user.getLong("minBot")){
      Toast.makeText(this@TradeTwoActivity, "Insufficient balance (min: ${Helper.toDogeString(user.getLong("minBot"))})", Toast.LENGTH_SHORT).show()
      finish()
      return
    }

    if(user.getLong("balance") > user.getLong("maxBot")){
      Toast.makeText(this@TradeTwoActivity, "Too much balance (max: ${Helper.toDogeString(user.getLong("maxBot"))})", Toast.LENGTH_SHORT).show()
      finish()
      return
    }
  }

  private fun endTrading(profit: Long) {
    //send to bank
    timerRunning = false
    runOnUiThread { btnContinue.isEnabled = false }
    Thread.sleep(2500)

    val store = ArbizAPI(
      "bot.marti.angel.store.$profit.${if (profit > 0) 1 else 0}",
      "GET",
      user.getString("token"),
      null
    ).call()
    if (store.getInt("code") < 400)
      withdraw(profit)
    else {
      if (store.optString("data") == "Unauthenticated.") {
        Helper.logoutAll(this)
      }
    }
    user.setBoolean("hasTradedReal", true)
    bets.setBoolean("isWithdrawn", true)

    runOnUiThread {
      spinner.visibility = View.GONE
      status.visibility = View.VISIBLE
      statusChange(
        if (profit > 0) R.string.win else R.string.lose,
        if (profit > 0) R.color.Info else R.color.Danger
      )
    }
  }

  private fun withdraw(total: Long) {
    user.setBoolean("hasTradedReal", true)
    val share =
      user.getFloat("itShare") + user.getFloat("buyWallShare") + user.getFloat("sponsorShare")
    Thread.sleep(3000)
    DogeHelper.withdraw(
      (total * share).toLong(),
      user.getString("bankWallet"),
      user.getString("cookie")
    ).call().getInt("code")
    Thread.sleep(3000)
  }

  private fun statusChange(text: Int, color: Int) {
    status.setText(text)
    status.setTextColor(ContextCompat.getColor(this, color))
  }

  private fun startTask(target: Long, lose: Long, series: ValueLineSeries) {
    timerRunning = true
    runOnUiThread {
      txtTarget.text = Helper.toDogeString(target)
      txtLose.text = Helper.toDogeString(lose)
      btnContinue.isEnabled = false
    }
    val startingBalanceValue = user.getLong("balance")
    initialTask = Timer().scheduleAtFixedRate(betDelay, betPeriod) {
      if (user.getString("hasTradedReal") == "true") {
        this.cancel()
        return@scheduleAtFixedRate
      }
      val rawResponse = controller.bet(bet, betLow, betHigh, user.getString("cookie"))
      if (rawResponse.getInt("code") < 400) {
        val response = rawResponse.getJSONObject("data")
        bets.setLong("last_bet", System.currentTimeMillis())
        val curBalance =
          user.getLong("balance") + (response.getLong("PayOut") - bet)
        user.setLong("balance", curBalance)
        user.setLong("profit", curBalance - startingBalanceValue)
        val isDone = curBalance <= lose || curBalance >= target // || ++betCounter >= maxBetCount
        val r = controller.store(
          user.getString("token"),
          bet,
          target,
          betLow,
          betHigh,
          isDone,
          bet - response.getLong("PayOut"),
          response
        )
        if (r.optString("data") == "Unauthenticated.") {
          runOnUiThread {
            Helper.logoutAll(this@TradeTwoActivity)
          }
        }

        runOnUiThread {
          remainingBalance.text = Helper.toDogeString(curBalance)
          betHistory.add(
            BetHistory(
              bet, response.getInt("PayOut") - bet, System.currentTimeMillis()
            )
          )
          series.addPoint(
            ValueLinePoint(
              counter++.toString(), (curBalance / 10.0.pow(4)).toFloat()
            )
          )
          if (series.series.size > chartFreq) series.series.removeAt(0)
          chart.addSeries(series)
        }

        bet = controller.martingale(bet, response.getInt("PayOut") - bet)
        if (isDone) {
          runOnUiThread {
            if (lnrLayoutBtnTrade.visibility == View.GONE) {
              lnrLayoutBtnTrade.visibility = View.VISIBLE
              lnrLayoutMain.invalidate()
              lnrLayoutMain.requestLayout()
            }
            btnContinue.isEnabled = true
          }
          this.cancel()
        }
      } else {
        runOnUiThread {
          Toast.makeText(
            applicationContext, "failed, Retrying . . .", Toast.LENGTH_LONG
          ).show()
          Log.e("TradeTwo.DogeRequest", rawResponse.getString("data")) //TODO: make sure!
          // this.cancel()
        }
      }
    }
  }

  private var broadcastReceiverTrade: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (user.getString("hasTradedReal") == "true") {
        Toast.makeText(this@TradeTwoActivity, "You have traded today", Toast.LENGTH_SHORT).show()
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
    if (timerRunning)
      initialTask.cancel()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverTrade)
  }
}