package biz.arbitrade.controller

import biz.arbitrade.network.DogeAPI
import okhttp3.FormBody

object DogeHelper {
  fun withdraw(amount: Long, to: String, cookie: String): DogeAPI{
    val body = FormBody.Builder()
    body.add("a", "Withdraw")
    body.add("s", cookie)
    body.add("Address", to)
    body.add("Amount", amount.toString())
    body.add("Currency", "doge")
    return DogeAPI(body)
  }
}