package biz.arbitrade.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import biz.arbitrade.MainActivity
import biz.arbitrade.R
import biz.arbitrade.controller.Helper
import biz.arbitrade.controller.SettingController
import biz.arbitrade.model.Bet
import biz.arbitrade.model.User
import biz.arbitrade.view.activity.HomeActivity
import biz.arbitrade.view.dialog.Loading
import java.util.*
import kotlin.concurrent.schedule

class SettingFragment : Fragment() {
  private lateinit var user: User
  private lateinit var bets: Bet
  private lateinit var loading: Loading
  private lateinit var controller: SettingController
  private lateinit var parentActivity: HomeActivity
  private lateinit var textName: EditText
  private lateinit var textPassword: EditText
  private lateinit var textPasswordConfirm: EditText
  private lateinit var buttonLogout: Button
  private lateinit var buttonChangeName: Button
  private lateinit var buttonChangePassword: Button

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_setting, container, false)

    parentActivity = activity as HomeActivity

    user = User(parentActivity.applicationContext)
    bets = Bet(parentActivity.applicationContext)
    loading = Loading(parentActivity)
    controller = SettingController(user)

    textName = view.findViewById(R.id.editTextName)
    textPassword = view.findViewById(R.id.editTextPassword)
    textPasswordConfirm = view.findViewById(R.id.editTextConfirmPassword)
    buttonChangeName = view.findViewById(R.id.buttonChangeName)
    buttonChangePassword = view.findViewById(R.id.buttonChangePassword)
    buttonLogout = view.findViewById(R.id.buttonLogout)

    buttonChangeName.setOnClickListener {
      loading.openDialog()
      Timer().schedule(100) {
        val result = controller.changeName(textName.text.toString())
        if (result == "Unauthenticated.") {
          Helper.logoutAll(this@SettingFragment.parentActivity)
          loading.closeDialog()
        } else
            parentActivity.runOnUiThread {
              Toast.makeText(this@SettingFragment.context, result, Toast.LENGTH_SHORT).show()
              loading.closeDialog()
            }
      }
    }

    buttonChangePassword.setOnClickListener {
      loading.openDialog()
      Timer().schedule(100) {
        val result =
            controller.changePassword(
                textPassword.text.toString(), textPasswordConfirm.text.toString())
        if (result == "Unauthenticated.") {
          loading.closeDialog()
          Helper.logoutAll(this@SettingFragment.parentActivity)
        } else
            parentActivity.runOnUiThread {
              Toast.makeText(this@SettingFragment.context, result, Toast.LENGTH_SHORT).show()
              loading.closeDialog()
            }
      }
    }

    buttonLogout.setOnClickListener {
      val intent = Intent(parentActivity, MainActivity::class.java)
      startActivity(intent)
      user.clear()
      bets.clear()
      parentActivity.finishAffinity()
    }
    return view
  }
}
