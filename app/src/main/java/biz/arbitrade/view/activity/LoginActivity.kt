package biz.arbitrade.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import biz.arbitrade.R
import biz.arbitrade.controller.LoginController
import biz.arbitrade.controller.background.PusherReceiver
import biz.arbitrade.network.ArbizAPI
import biz.arbitrade.network.DogeAPI
import biz.arbitrade.view.dialog.Loading
import okhttp3.FormBody
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity : AppCompatActivity() {
  private val controller = LoginController()
  private lateinit var loading: Loading
  private lateinit var btnLogin: Button
  private lateinit var textUsername: EditText
  private lateinit var textPassword: EditText
  private lateinit var textForgetPassword: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    loading = Loading(this)

    btnLogin = findViewById(R.id.buttonLogin)
    textUsername = findViewById(R.id.editTextUsername)
    textPassword = findViewById(R.id.editTextPassword)
    textForgetPassword = findViewById(R.id.textViewForgotPassword)

    textUsername.setText("admin")
    textPassword.setText("admin")

    btnLogin.setOnClickListener {
      if (controller.validate(textUsername, textPassword).isNotBlank()) {
        Toast.makeText(applicationContext, "username and/or password cannot be empty", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }
      if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
        return@setOnClickListener
      }
      loading.openDialog()
      Timer().schedule(1000) {
        val result = controller.doLogin(textUsername.text.toString(), textPassword.text.toString())
        val resultDoge: JSONObject = when {
          result.has("cookie") -> {
            val body = FormBody.Builder()
            body.add("a", "GetBalance")
            body.add("s", result.getString("cookie"))
            body.add("Currency", "doge")
            DogeAPI(body).call()
          }
          else -> {
            JSONObject("{code: 400}")
          }
        }
        runOnUiThread {
          if (result.getInt("code") >= 400) {
            val msg = if (result.getString("message")
                .contains("failed to connect")
            ) "Cannot Connect to Server please check your connection"
            else result.getString("message")
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
          } else {
            if (resultDoge.getInt("code") >= 400) {
              Toast.makeText(
                applicationContext,
                "Cannot fetch current balance at the moment, please wait...",
                Toast.LENGTH_LONG
              ).show()
            }
            controller.fillUser(
              application,
              result,
              (resultDoge.optJSONObject("data")?.optLong("Balance") ?: 0),
              JSONObject(
                intent.getStringExtra("info") ?: "{wallet_bank: \"\"}"
              )
            )
            val mIntent = Intent(applicationContext, PusherReceiver::class.java)
            if (applicationContext != null) {
              applicationContext.startService(mIntent)
            }
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
          }
          loading.closeDialog()
        }
      }
    }

    textForgetPassword.setOnClickListener {
      intent = Intent(this@LoginActivity, RequestResetPasswordActivity::class.java)
      startActivity(intent)
    }
  }
}
