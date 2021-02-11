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
import okhttp3.FormBody
import java.util.*
import kotlin.concurrent.schedule

class RequestResetPasswordActivity : AppCompatActivity() {
  private lateinit var submit: Button
  private lateinit var email: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_forget_password)

    email = findViewById(R.id.editTextEmail)
    submit = findViewById(R.id.buttonSend)

    submit.setOnClickListener { run() }
  }

  private fun run() {
    Timer().schedule(100) {
      val body = FormBody.Builder()
      body.add("email", email.text.toString())
      val request = ArbizAPI("forgot.email", "POST", null, body).call()
      Log.d("MINE", request.toString())
      if (request.getInt("code") < 400) {
        val code = request.getInt("uniqueCode")
        runOnUiThread {
          Toast.makeText(
            this@RequestResetPasswordActivity,
            request.getString("message"),
            Toast.LENGTH_SHORT
          )
            .show()
          Log.d("PASSWORD", code.toString())
          val intent = Intent(this@RequestResetPasswordActivity, ResetPasswordActivity::class.java)
          intent.putExtra("code", code)
          intent.putExtra("email", email.text.toString())
          startActivity(intent)
          finishAfterTransition()
        }
      } else {
        runOnUiThread {
          Log.e("Login", request.toString())
          Toast.makeText(
            this@RequestResetPasswordActivity,
            request.getString("message") ?: "Cannot connect to server",
            Toast.LENGTH_SHORT
          )
            .show()
        }
      }
    }
  }
}