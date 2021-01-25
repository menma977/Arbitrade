package biz.arbitrade.controller

import biz.arbitrade.network.ArbizAPI
import okhttp3.FormBody
import org.json.JSONObject

class LoginController {
    public fun doLogin(username: String, password: String): JSONObject{
        val body = FormBody.Builder()
        val request = ArbizAPI("login", "post", "", body)
        return request.call()
    }
}