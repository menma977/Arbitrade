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

  fun fillUser(context: Context, result: JSONObject, balance: Long): User {
    Log.e("MINE", result.toString())
    val data = result.getJSONObject("user")
    val user = User(context)

    user.setString("username", data.getString("username"))
    user.setString("email", data.getString("email"))
    user.setString("hasTradedReal", data.getString("hasTradedReal"))
    user.setString("hasTradedFake", data.getString("hasTradedFake"))
    user.setString("token", data.getString("token"))
    user.setString("cookie", data.getString("cookie"))
    user.setString("walletDax", data.getString("walletDax"))
    user.setLong("totalPin", data.getLong("totalPin"))
    user.setInteger("pinSpent", data.getInt("pinSpent"))
    user.setInteger("totalDownLine", data.getInt("totalDownLine"))
    user.setString("downLines", data.getJSONArray("downLines").toString())
    user.setString("sponsorId", data.getString("sponsorId"))
    user.setString("sponsor", data.getString("sponsor"))
    user.setLong("balance", balance)

    return user
  }
}
