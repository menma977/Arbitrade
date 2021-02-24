package biz.arbitrade.controller

import biz.arbitrade.model.RegisterForm
import biz.arbitrade.model.User
import biz.arbitrade.network.ArbizAPI
import okhttp3.FormBody
import org.json.JSONObject

class RegisterController {
  fun doRegister(user: User, newUser: RegisterForm): JSONObject {
    val validation = newUser.validate()
    if (validation.isNotBlank()) return JSONObject("{code: 400, message: '$validation'}")
    val body = FormBody.Builder()
    body.add("name", newUser.name)
    body.add("username", newUser.username)
    body.add("email", newUser.email)
    body.add("password", newUser.password)
    body.add("confirmation_password", newUser.passwordConfirm)
    body.add("pin", newUser.pin)
    body.add("confirmation_pin", newUser.pinConfirm)
    return ArbizAPI("register", "post", user.getString("token"), body).call()
  }
}