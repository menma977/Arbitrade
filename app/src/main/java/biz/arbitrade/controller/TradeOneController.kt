package biz.arbitrade.controller

import biz.arbitrade.model.User
import biz.arbitrade.network.ArbizAPI
import okhttp3.FormBody
import org.json.JSONObject

class TradeOneController {
    fun trade(user: User): JSONObject {
        return if (user.getLong("balance") > 0) {
            val body = FormBody.Builder()
            body.add("balance", user.getLong("balance").toString())
            ArbizAPI("fake", "GET", user.getString("token"), body).call()
        } else JSONObject("{code:400,message:'Insufficient balance'}")
    }
}