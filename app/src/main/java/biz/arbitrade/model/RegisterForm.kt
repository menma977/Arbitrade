package biz.arbitrade.model

import android.util.Patterns

class RegisterForm(
  val name: String,
  val username: String,
  val email: String,
  val password: String,
  val passwordConfirm: String,
  val walletDax: String,
) {
  fun validate(): String {
    return when {
      name.isEmpty() -> "Name field is empty"
      username.isEmpty() -> "Username field is empty"
      Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Not a valid Email"
      password.length < 6 -> "Password need at least 6 characters"
      password != passwordConfirm -> "Confirmation Password didn't match"
      !walletDax.startsWith("D") -> "Invalid Doge wallet"
      else -> ""
    }
  }
}