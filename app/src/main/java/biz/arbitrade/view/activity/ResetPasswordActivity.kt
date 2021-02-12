package biz.arbitrade.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import biz.arbitrade.R
import biz.arbitrade.network.ArbizAPI
import biz.arbitrade.view.dialog.Loading
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class ResetPasswordActivity : AppCompatActivity() {
  private var code = -1
  private var mEmail = ""
  private lateinit var loading: Loading
  private lateinit var submit: Button
  private lateinit var otp: EditText
  private lateinit var password: EditText
  private lateinit var passwordConfirm: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_reset_password)
    loading = Loading(this)
    code = intent.getIntExtra("code", -1)
    if (code < 0) {
      finish()
    } else {
      mEmail = intent.getStringExtra("email")!!
      submit = findViewById(R.id.buttonSend)
      otp = findViewById(R.id.editTextOTP)
      password = findViewById(R.id.editTextPassword)
      passwordConfirm = findViewById(R.id.editTextConfirmPassword)
      submit.setOnClickListener { run() }
    }
  }

  private fun run() {
    if (code == otp.text.toString().toInt()) {
      loading.openDialog()
      Timer().schedule(100) {
        val body = FormBody.Builder()
        body.add("email", mEmail)
        body.add("password", password.text.toString())
        body.add("confirmation_password", passwordConfirm.text.toString())
        val request = ArbizAPI("forgot.update", "POST", null, body).call()
        if (request.getInt("code") < 400) {
          runOnUiThread {
            Toast.makeText(
              this@ResetPasswordActivity, request.getString(
                if (request.optString("data").isNotBlank()) "data"
                else "message"
              ), Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
            loading.closeDialog()
            finish()
          }
        } else {
          runOnUiThread {
            Log.e("Login", request.toString())
            Toast.makeText(
              this@ResetPasswordActivity, request.getString("data") ?: "Cannot connect to server", Toast.LENGTH_SHORT
            ).show()
            loading.closeDialog()
          }
        }
      }
    } else {
      Toast.makeText(
        this@ResetPasswordActivity, "Confirmation code didn't match", Toast.LENGTH_SHORT
      )
    }
  }
}