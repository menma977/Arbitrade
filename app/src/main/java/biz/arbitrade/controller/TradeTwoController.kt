package biz.arbitrade.controller

import biz.arbitrade.network.ArbizAPI
import biz.arbitrade.network.DogeAPI
import okhttp3.FormBody
import org.json.JSONObject
import kotlin.math.abs
import kotlin.random.Random

/**
 * 999doge bet controller
 * @param winTarget target win from initial balance in percent
 */
class TradeTwoController() {
  private val martingaleWinningIncrement = .1
  private var totalProfit: Long = 0
  var initialBet:Long = 0

  fun getTotalProfit(): Long {
    return totalProfit
  }

  fun bet(satoshi: Long, low: Int, high: Int, cookie: String): JSONObject {
    val form = FormBody.Builder()
    form.add("a", "PlaceBet")
    form.add("s", cookie)
    form.add("PayIn", "$satoshi")
    form.add("Low", "$low")
    form.add("High", "$high")
    form.add("ClientSeed", "${Random.nextInt()}")
    form.add("Currency", "doge")
    form.add("ProtocolVersion", "2")
    return DogeAPI(form).call()
  }

  fun store(
    token: String,
    satoshi: Long,
    target: Long,
    low: Int,
    high: Int,
    finish: Boolean,
    profit: Long,
    data: JSONObject
  ): JSONObject {
    val body = FormBody.Builder()
    body.add("start_balance", data.getString("StartingBalance"))
    body.add("end_balance", (data.getLong("StartingBalance") + profit).toString())
    body.add("target_balance", target.toString())
    body.add("pay_in", satoshi.toString())
    body.add("pay_out", "${data.getLong("PayOut")}")
    body.add("low", low.toString())
    body.add("high", high.toString())
    body.add(
      "status",
      if ((data.getLong("PayOut") - satoshi) <= 0) "lose" else "win"
    )
    body.add("is_finish", finish.toString())
    return ArbizAPI("bot.marti.angel", "POST", token, body).call()
  }

  fun martingale(bet: Long, profit: Long): Long {
    if(bet <= 0 || initialBet <= 0){
      throw error("Bet cannot be 0 or less")
    }
    totalProfit += profit
    return if (profit >= 0) initialBet//(bet * (1 + martingaleWinningIncrement)).toLong()
    else abs(bet) * 2
  }
}
