package biz.arbitrade.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import biz.arbitrade.MainActivity
import biz.arbitrade.R
import biz.arbitrade.model.User
import biz.arbitrade.view.activity.HomeActivity

class SettingFragment : Fragment() {
  private lateinit var user: User
  private lateinit var parentActivity: HomeActivity
  private lateinit var buttonLogout: Button

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_setting, container, false)

    parentActivity = activity as HomeActivity

    user = User(parentActivity.applicationContext)

    buttonLogout = view.findViewById(R.id.buttonLogout)

    buttonLogout.setOnClickListener {
      val intent = Intent(parentActivity, MainActivity::class.java)
      startActivity(intent)
      user.clear()
      parentActivity.finishAffinity()
    }
    return view
  }
}