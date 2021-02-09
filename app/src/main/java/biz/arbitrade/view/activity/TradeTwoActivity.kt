package biz.arbitrade.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import biz.arbitrade.R
import biz.arbitrade.controller.TradeTwoController
import biz.arbitrade.model.Bet
import biz.arbitrade.model.BetHistory
import biz.arbitrade.model.User
import biz.arbitrade.view.adapter.BetHistoryAdapter
import kotlinx.android.synthetic.main.activity_trade_one.txtWarning
import kotlinx.android.synthetic.main.activity_trade_two.*
import org.eazegraph.lib.charts.ValueLineChart
import org.eazegraph.lib.models.ValueLinePoint
import org.eazegraph.lib.models.ValueLineSeries
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.random.Random

class TradeTwoActivity : AppCompatActivity() {
  private lateinit var user: User
  private lateinit var bets: Bet
  private lateinit var chart: ValueLineChart
  private lateinit var history: RecyclerView
  private lateinit var controller: TradeTwoController
  private var bet: Long = 0
  private var betCounter: Int = 0
  private var betHistory = ArrayList<BetHistory>()
  private var counter = 1
  private val betDelay: Long = 250
  private val betPeriod: Long = 2500
  private val chartFreq: Int = 10
  private val dogeWinChance = .1f
  private val betLow = 0
  private val betHigh = (100000 * dogeWinChance).toInt()
  private val loseTarget = .1f
  private var winTarget = .1f
  private lateinit var initialTask: TimerTask

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_trade_two)

    controller = TradeTwoController()
    user = User(this)
    bets = Bet(this)

    chart = findViewById(R.id.cubiclinechart)
    history = findViewById(R.id.listHistory)
    val series = ValueLineSeries()
    series.color = -0x1212ff
    series.addPoint(ValueLinePoint(" ", user.getLong("balance") * 1f))
    findViewById<Button>(R.id.continueBtn).setOnClickListener {}
    txtWarning.isSelected = true
    val adapter = BetHistoryAdapter(betHistory)
    history.layoutManager = LinearLayoutManager(this)
    history.adapter = adapter

    chart.addSeries(series)
    chart.startAnimation()
    var target = user.getLong("balance") + (user.getLong("balance") * winTarget).toLong()
    var lose = user.getLong("balance") - (user.getLong("balance") * loseTarget).toLong()
    var betCounter = 0
    var bet = (target * .1).toLong()
    var doContinue = false

    controller.initialBet = bet

    if (bets.has("last_bet")) {
      val lastBet = Bet.getCalendar(bets.getLong("last_bet"))
      val now = Calendar.getInstance()
      if (lastBet.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) && lastBet.get(Calendar.MONTH) == now.get(
          Calendar.MONTH
        )
      ) {
        target = bets.getLong("target")
        lose = bets.getLong("lose")
        betCounter = bets.getInteger("bet_count")
        doContinue = true
      }
    }
    if (doContinue) {
      bets.setLong("target", target)
      bets.setLong("lose", lose)
      bets.setLong("profit", controller.getTotalProfit())
      bets.setInteger("bet_count", betCounter)
      bets.setLong("last_bet", System.currentTimeMillis())
    }

    startTask(bet, series)
    if(user.getString("hasTradedReal") == "true"){
      Toast.makeText(this@TradeTwoActivity, "You have traded today", Toast.LENGTH_SHORT).show()
      continueBtn.isEnabled = false
      stopBtn.isEnabled = false
    }else{
      continueBtn.setOnClickListener {
        startTask(bet, series)
      }
      stopBtn.setOnClickListener {
        initialTask.cancel()
      }
    }
  }

  private fun startTask(target: Long, series: ValueLineSeries){
    initialTask = Timer().scheduleAtFixedRate(betDelay, betPeriod) {
      if(user.getString("hasTradedReal") == "true") {
        this.cancel()
        return@scheduleAtFixedRate
      }
      val response = controller.bet(bet, betLow, betHigh)
      if (response.getInt("code") < 400) {
        betHistory.add(
          BetHistory(
            bet, response.getInt("PayOut") - bet, System.currentTimeMillis()
          )
        )
        bet = controller.martingale(bet, response.getInt("PayOut") - bet)

        bets.setLong("profit", controller.getTotalProfit())
        bets.setInteger("bet_count", betCounter + 1)
        bets.setLong("last_bet", System.currentTimeMillis())

        val curBalance =
          user.getLong("balance") + (response.getLong("PayIn") - response.getLong("PayOut"))
        user.setLong("balance", curBalance)

        runOnUiThread {
          series.addPoint(
            ValueLinePoint(
              counter++.toString(), Random(counter).nextFloat()
            )
          )
          if (series.series.size > chartFreq) series.series.removeAt(0)
          chart.addSeries(series)
        }
        val isDone = (++betCounter >= 30 || curBalance <= loseTarget || curBalance >= winTarget)
        controller.store(user.getString("t  oken"), bet, target, betLow, betHigh, isDone, response)
        if (isDone) this.cancel()
      } else {
        runOnUiThread {
          Toast.makeText(
            applicationContext, "Cannot start trading, please try again later", Toast.LENGTH_LONG
          ).show()
          Log.e("TradeTwo.DogeRequest", response.getString("data")) //TODO: make sure!
          this.cancel()
        }
      }
    }
  }

  private var broadcastReceiverTrade: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if(user.getString("hasTradedReal") == "true"){
        Toast.makeText(this@TradeTwoActivity, "You have traded today", Toast.LENGTH_SHORT).show()
        continueBtn.isEnabled = false
        stopBtn.isEnabled = false
      }
    }
  }

  override fun onStart() {
    super.onStart()
    LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverTrade, IntentFilter("trade"))
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
    initialTask.cancel()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverTrade)
  }
}