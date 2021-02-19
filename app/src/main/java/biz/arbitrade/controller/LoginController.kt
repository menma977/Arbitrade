package biz.arbitrade.controller

import android.content.Context
import android.widget.EditText
import biz.arbitrade.model.User
import biz.arbitrade.network.ArbizAPI
import okhttp3.FormBody
import org.json.JSONObject

class LoginController {
  fun doLogin(username: String, password: String): JSONObject {
    val body = FormBody.Builder()
    body.add("username", username)
    body.add("password", password)
    val request = ArbizAPI("login", "post", "", body)
    return request.call()
  }

  fun validate(username: EditText, password: EditText): String {
    return when {
      username.text.isNullOrEmpty() -> "Username"
      password.text.isNullOrEmpty() -> "Password"
      else -> ""
    }
  }

  fun fillUser(context: Context, result: JSONObject, balance: Long, info: JSONObject): User {
    val json = if (result.has("user")) result.getJSONObject("user") else result
    val user = User(context)

    user.setString("username", json.getString("username"))
    user.setString("email", json.getString("email"))
    user.setString("hasTradedReal", json.getString("hasTradedReal"))
    user.setString("hasTradedFake", json.getString("hasTradedFake"))
    if (!user.has("token")) user.setString("token", json.getString("token"))
    user.setString("cookie", json.getString("cookie"))
    user.setString("wallet", json.getString("wallet"))
    user.setString("walletDax", json.getString("walletDax"))
    user.setLong("totalPin", json.getLong("totalPin"))
    user.setInteger("pinSpent", json.getInt("pinSpent"))
    user.setInteger("totalDownLine", json.getInt("totalDownLine"))
    user.setString("downLines", json.getJSONArray("downLines").toString())
    user.setString("sponsorId", json.getString("sponsorId"))
    user.setString("sponsor", json.getString("sponsor"))
    user.setLong("balance", balance)
    user.setLong("minBot", info.getLong("min_bot"))
    user.setLong("maxBot", info.getLong("max_bot"))
    user.setString("bankWallet", info.getString("wallet_bank"))
    user.setFloat("itShare", info.getDouble("it").toFloat())
    user.setFloat("buyWallShare", info.getDouble("buy_wall").toFloat())
    user.setFloat("sponsorShare", info.getDouble("sponsor_share").toFloat())

    return user
  }
}
