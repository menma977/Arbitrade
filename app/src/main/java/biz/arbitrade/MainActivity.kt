package biz.arbitrade

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import biz.arbitrade.view.LoginActivity

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val intent = Intent(applicationContext, LoginActivity::class.java)
    startActivity(intent)
  }
}