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

  fun bet(satoshi: Long, low: Int, high: Int): JSONObject {
    val form = FormBody.Builder()
    form.add("a", "PlaceBet")
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
    data: JSONObject
  ): JSONObject {
    val body = FormBody.Builder()
    val res = data.getJSONObject("data") // TODO: change accordingly
    body.add("start_balance", res.getString("StartingBalance"))
    body.add("end_balance", res.getString("Balance"))
    body.add("target_balance", target.toString())
    body.add("pay_in", satoshi.toString())
    body.add("pay_out", "${res.getLong("Payout") - satoshi}")
    body.add("low", low.toString())
    body.add("high", high.toString())
    body.add(
      "status",
      if (satoshi < (res.getLong("Payout") - satoshi)) "lose" else "win"
    )
    body.add("is_finish", finish.toString())
    return ArbizAPI("marti.angel", "POST", token, body).call()
  }

  fun martingale(bet: Long, profit: Long): Long {
    if(bet <= 0 || initialBet <= 0){
      throw error("Bet cannot be 0 or less")
    }
    totalProfit += profit
    return if (profit >= 0) initialBet//(bet * (1 + martingaleWinningIncrement)).toLong()
    else abs(profit) * 2
  }
}
