package biz.arbitrade.view.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import biz.arbitrade.MainActivity
import biz.arbitrade.R
import biz.arbitrade.controller.Helper
import biz.arbitrade.controller.background.DogeRefresher
import biz.arbitrade.controller.background.PersonalReceiver
import biz.arbitrade.model.Bet
import biz.arbitrade.model.User
import biz.arbitrade.network.ArbizAPI
import biz.arbitrade.view.fragments.HomeFragment
import biz.arbitrade.view.fragments.SettingFragment
import com.pusher.client.channel.PusherEvent
import java.util.*
import kotlin.concurrent.schedule

class HomeActivity : AppCompatActivity() {
  private lateinit var homeFragment: HomeFragment
  private lateinit var settingFragment: SettingFragment
  private lateinit var user: User
  private lateinit var bets: Bet
  private lateinit var intentPersonalReceiver: Intent
  private lateinit var intentMtReceiver: Intent
  private lateinit var intentDogeRefresher: Intent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)

    user = User(this)
    bets = Bet(this)
    homeFragment = HomeFragment()
    settingFragment = SettingFragment()

    findViewById<LinearLayout>(R.id.btnToHome).setOnClickListener { addFragment(homeFragment) }
    findViewById<LinearLayout>(R.id.btnToSetting).setOnClickListener {
      addFragment(settingFragment)
    }
    findViewById<LinearLayout>(R.id.btnToNetwork).setOnClickListener {
      Timer().schedule(100) {
        val response = ArbizAPI("test", "get", user.getString("token"), null).call()
        if(response.optString("data") == "Unauthenticated."){
          Helper.logoutAll(this@HomeActivity)
        }
      }
    }

    addFragment(homeFragment)
    checkNotification()

    intentPersonalReceiver = Intent(applicationContext, PersonalReceiver::class.java)
    intentDogeRefresher = Intent(applicationContext, DogeRefresher::class.java)
    intentMtReceiver = Intent(applicationContext, PusherEvent::class.java)
    if (applicationContext != null) {
      startService(intentPersonalReceiver)
      startService(intentDogeRefresher)
      startService(intentMtReceiver)
    }
  }

  private fun checkNotification() {
    Timer().schedule(100) {
      val res = ArbizAPI("announcement", "get", null, null).call()
      if (res.getInt("code") < 400) {
        if (!res.optString("message").isNullOrBlank()) {
          user.setString("announcement", "")
        } else {
          res.remove("code")
          user.setString("announcement", res.toString())
        }
      }
    }
  }

  private fun addFragment(fragment: Fragment) {
    val backStateName = fragment.javaClass.simpleName
    val fragmentManager = supportFragmentManager
    val fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0)

    if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) {
      fragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        .replace(R.id.contentFragment, fragment, backStateName) // .addToBackStack(backStateName)
        .commit()
    }
  }

  override fun onStart() {
    super.onStart()
    startService(intentPersonalReceiver)
    startService(intentDogeRefresher)
    startService(intentMtReceiver)
    LocalBroadcastManager.getInstance(this)
      .registerReceiver(broadcastReceiverMaintenance, IntentFilter("on_maintenance"))
  }

  override fun onPause() {
    super.onPause()
    stopService(intentPersonalReceiver)
    stopService(intentDogeRefresher)
    stopService(intentMtReceiver)
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverMaintenance)
  }

  override fun onDestroy() {
    super.onDestroy()
    stopService(intentPersonalReceiver)
    stopService(intentDogeRefresher)
    stopService(intentMtReceiver)
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverMaintenance)
  }

  private var broadcastReceiverMaintenance: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      val mIntent = Intent(applicationContext, MainActivity::class.java)
      user.clear()
      bets.clear()
      startActivity(mIntent)
      finishAffinity()
    }
  }
}
