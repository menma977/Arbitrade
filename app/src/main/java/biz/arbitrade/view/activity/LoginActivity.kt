package biz.arbitrade.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import biz.arbitrade.R
import biz.arbitrade.controller.LoginController
import biz.arbitrade.controller.background.PusherReceiver
import biz.arbitrade.network.DogeAPI
import biz.arbitrade.view.dialog.Loading
import java.util.*
import kotlin.concurrent.schedule
import okhttp3.FormBody
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
  private val controller = LoginController()
  private lateinit var loading: Loading
  private lateinit var btnLogin: Button
  private lateinit var textUsername: EditText
  private lateinit var textPassword: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    loading = Loading(this)

    btnLogin = findViewById(R.id.buttonLogin)
    textUsername = findViewById(R.id.editTextUsername)
    textPassword = findViewById(R.id.editTextPassword)

    btnLogin.setOnClickListener {
      if (controller.validate(textUsername, textPassword).isNotBlank()) {
        Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }
      loading.openDialog()
      Timer().schedule(1000) {
        val result = controller.doLogin(textUsername.text.toString(), textPassword.text.toString())
        val resultDoge: JSONObject =
            when {
              result.has("user") -> {
                val body = FormBody.Builder()
                body.add("a", "GetBalance")
                body.add("s", result.getJSONObject("user").getString("cookie"))
                body.add("Currency", "doge")
                DogeAPI(body).call()
              }
              else -> {
                JSONObject("{code: 400}")
              }
            }
        runOnUiThread {
          if (result.getInt("code") >= 400) {
            val msg =
                if (result.getString("data").contains("failed to connect"))
                    "Cannot Connect to Server please check your connection"
                else result.getString("data")
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
          } else {
            if (resultDoge.getInt("code") >= 400) {
              Toast.makeText(
                      applicationContext,
                      "Cannot fetch current balance at the moment, please wait...",
                      Toast.LENGTH_LONG)
                  .show()
            }
            controller.fillUser(
                application, result, (resultDoge.optJSONObject("data")?.optLong("Balance") ?: 0))
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
  }
}
