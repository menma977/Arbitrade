package biz.arbitrade.controller

import biz.arbitrade.network.DogeAPI
import okhttp3.FormBody
import org.json.JSONObject

class TradeTwoController {
  fun bet(satoshi: Int, low: Int, high: Int): JSONObject {
    val form = FormBody.Builder()
    form.add("a", "PlaceBet")
    form.add("PayIn", "$satoshi")
    form.add("Low", "$low")
    form.add("High", "$high")
    form.add("ClientSeed", "${(0..1000).random()}")
    form.add("Currency", "doge")
    form.add("ProtocolVersion", "2")
    return DogeAPI(form).call()
  }
}