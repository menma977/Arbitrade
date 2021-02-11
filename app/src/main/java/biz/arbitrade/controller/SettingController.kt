package biz.arbitrade.controller

import biz.arbitrade.model.User
import biz.arbitrade.network.ArbizAPI
import okhttp3.FormBody

class SettingController(private val user: User) {

  fun changeName(name: String): String {
    val body = FormBody.Builder()
    body.add("name", name)
    val result = ArbizAPI("user/update/name", "POST", user.getString("token"), body).call()
    if(result.getInt("code") < 400){
      user.setString("name", name)
    }
    return when{
      result.optString("message").isNotBlank() -> result.optString("message")
      result.optString("data").isNotBlank() -> result.optString("data")
      else -> "Changing name failed"
    }
  }

  fun changeDaxWallet(wallet: String): String {
    val body = FormBody.Builder()
    body.add("wallet", wallet)
    val result = ArbizAPI("user/update/wallet", "POST", user.getString("token"), body).call()
    if(result.getInt("code") < 400){
      user.setString("walletDax", wallet)
    }
    return when{
      result.optString("message").isNotBlank() -> result.optString("message")
      result.optString("data").isNotBlank() -> result.optString("data")
      else -> "Changing wallet dax failed"
    }
  }

  fun changePassword(password: String, passwordConfirm: String): String {
    val body = FormBody.Builder()
    body.add("password", password)
    body.add("confirmation_password", passwordConfirm)
    val result = ArbizAPI("user/update/password", "POST", user.getString("token"), body).call()
    return when{
      result.optString("message").isNotBlank() -> result.optString("message")
      result.optString("data").isNotBlank() -> result.optString("data")
      else -> "Changing password failed"
    }
  }
}