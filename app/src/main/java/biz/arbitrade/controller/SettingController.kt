package biz.arbitrade.controller

import biz.arbitrade.model.User
import biz.arbitrade.network.ArbizAPI
import okhttp3.FormBody

class SettingController(private val user: User) {

  fun changeName(name: String): Boolean {
    val body = FormBody.Builder()
    body.add("name", name)
    val result = ArbizAPI("user/update/name", "POST", user.getString("token"), body).call()
      .getInt("code") < 400
    if(result){
      user.setString("name", name)
    }
    return result
  }

  fun changeDaxWallet(wallet: String): Boolean {
    val body = FormBody.Builder()
    body.add("wallet", wallet)
    val result = ArbizAPI("user/update/wallet", "POST", user.getString("token"), body).call()
      .getInt("code") < 400
    if(result){
      user.setString("walletDax", wallet)
    }
    return result
  }

  fun changePassword(password: String, passwordConfirm: String): Boolean {
    val body = FormBody.Builder()
    body.add("password", password)
    body.add("confirmation_password", passwordConfirm)
    return ArbizAPI("user/update/wallet", "POST", user.getString("token"), body).call()
      .getInt("code") < 400
  }
}