package biz.arbitrade.controller

import biz.arbitrade.network.ArbizAPI
import biz.arbitrade.network.DogeAPI
import kotlin.math.abs
import kotlin.random.Random
import okhttp3.FormBody
import org.json.JSONObject

/**
 * 999doge bet controller
 * @param winTarget target win from initial balance in percent
 */
class TradeTwoController(private val winTarget: Float) {
    private val martiangleWinningIncrement = .1
    private var totalProfit: Long = 0

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
            data: JSONObject
    ): JSONObject {
        val body = FormBody.Builder()
        val res = data.getJSONObject("data")
        // TODO: change accordingly
        body.add("start_balance", res.getString("StartingBalance"))
        body.add("end_balance", res.getString("Balance"))
        body.add("target_balance", target.toString())
        body.add("pay_in", satoshi.toString())
        body.add("pay_out", "${res.getLong("Payout") - res.getLong("StartingBalance")}")
        body.add("low", low.toString())
        body.add("high", high.toString())
        body.add("status", "")
        body.add("is_finish", false.toString())
        return ArbizAPI("martiangle.store", "POST", token, body).call()
    }

    fun martiangle(bet: Long, profit: Long): Long {
        totalProfit += profit
        return if (profit >= 0) (bet * (1 + martiangleWinningIncrement)).toLong()
        else abs(profit) * 2
    }
}
