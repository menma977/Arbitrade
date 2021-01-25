package biz.arbitrade.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import biz.arbitrade.R
import biz.arbitrade.controller.LoginController
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity : AppCompatActivity() {
    private val controller = LoginController()
    private lateinit var btnLogin: Button
    private lateinit var textUsername: EditText
    private lateinit var textPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.buttonLogin)
        textUsername = findViewById(R.id.editTextUsername)
        textPassword = findViewById(R.id.editTextPassword)

        btnLogin.setOnClickListener {
            Timer().schedule(1000) {
                val result =
                    controller.doLogin(textUsername.text.toString(), textPassword.text.toString())
                Log.d("MINE", result.toString())
                runOnUiThread {
                    if (result.getInt("code") >= 300) {
                        val msg = if (result.getString("data")
                                .contains("failed to connect")
                        )
                            "Cannot Connect to Server please check your connection"
                        else result.getString("data")
                        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(
                            applicationContext,
                            result.getString("data"),
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }
        }
    }
}