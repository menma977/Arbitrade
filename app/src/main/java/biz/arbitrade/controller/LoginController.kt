package biz.arbitrade.controller

import android.content.Context
import android.util.Log
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
    Log.e("MINE", result.toString())
    val result = if (result.has("user")) result.getJSONObject("user") else result
    val user = User(context)

    user.setString("username", result.getString("username"))
    user.setString("email", result.getString("email"))
    user.setString("hasTradedReal", result.getString("hasTradedReal"))
    user.setString("hasTradedFake", result.getString("hasTradedFake"))
    if (!user.has("token")) user.setString("token", result.getString("token"))
    user.setString("cookie", result.getString("cookie"))
    user.setString("walletDax", result.getString("walletDax"))
    user.setLong("totalPin", result.getLong("totalPin"))
    user.setInteger("pinSpent", result.getInt("pinSpent"))
    user.setInteger("totalDownLine", result.getInt("totalDownLine"))
    user.setString("downLines", result.getJSONArray("downLines").toString())
    user.setString("sponsorId", result.getString("sponsorId"))
    user.setString("sponsor", result.getString("sponsor"))
    user.setLong("balance", balance)
    user.setString("bankWallet", info.getString("wallet_bank"))
    user.setFloat("itShare", info.getDouble("it").toFloat())
    user.setFloat("buyWallShare", info.getDouble("buy_wall").toFloat())
    user.setFloat("sponsorShare", info.getDouble("sponsor").toFloat())

    return user
  }
}
