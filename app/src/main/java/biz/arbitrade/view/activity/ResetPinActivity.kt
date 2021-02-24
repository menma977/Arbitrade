package biz.arbitrade.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import biz.arbitrade.R
import biz.arbitrade.controller.Helper
import biz.arbitrade.model.User
import biz.arbitrade.network.ArbizAPI
import biz.arbitrade.view.dialog.Loading
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class ResetPinActivity : AppCompatActivity() {
  private var code = -1
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var submit: Button
  private lateinit var otp: EditText
  private lateinit var pin: EditText
  private lateinit var pinConfirm: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_reset_pin)
    loading = Loading(this)
    user = User(this)
    code = intent.getIntExtra("code", -1)
    if (code < 0) {
      finish()
    } else {
      submit = findViewById(R.id.buttonSend)
      otp = findViewById(R.id.editTextOTP)
      pin = findViewById(R.id.editTextPin)
      pinConfirm = findViewById(R.id.editTextConfirmPin)
      submit.setOnClickListener { run() }
    }
  }

  private fun run() {
    if (code == otp.text.toString().toInt()) {
      loading.openDialog()
      Timer().schedule(100) {
        val body = FormBody.Builder()
        body.add("pin", pin.text.toString())
        body.add("confirmation_pin", pinConfirm.text.toString())
        val request = ArbizAPI("user.update.pin", "POST", user.getString("token"), body).call()
        if (request.getInt("code") < 400) {
          runOnUiThread {
            Toast.makeText(
              this@ResetPinActivity, request.getString(
                if (request.optString("data").isNotBlank()) "data"
                else "message"
              ), Toast.LENGTH_SHORT
            ).show()
            user.setString("pin", pin.text.toString())
            loading.closeDialog()
            finish()
          }
        } else {
          runOnUiThread {
            Log.e("Login", request.toString())
            val msg = if(request.optString("data").isNotBlank()) request.optString("data") else request.optString("message")
            Toast.makeText(
              this@ResetPinActivity, if(msg.isNotBlank()) msg else "Cannot connect to server", Toast.LENGTH_SHORT
            ).show()
            loading.closeDialog()
            if(msg == "Unauthenticated"){
              Helper.logoutAll(this@ResetPinActivity)
            }
          }
        }
      }
    } else {
      Toast.makeText(
        this@ResetPinActivity, "Confirmation code didn't match", Toast.LENGTH_SHORT
      ).show()
    }
  }
}