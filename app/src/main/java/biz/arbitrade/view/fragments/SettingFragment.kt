package biz.arbitrade.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import biz.arbitrade.MainActivity
import biz.arbitrade.R
import biz.arbitrade.controller.SettingController
import biz.arbitrade.model.User
import biz.arbitrade.view.activity.HomeActivity

class SettingFragment : Fragment() {
  private lateinit var user: User
  private lateinit var controller: SettingController
  private lateinit var parentActivity: HomeActivity
  private lateinit var textName: EditText
  private lateinit var textWallet: EditText
  private lateinit var textPassword: EditText
  private lateinit var textPasswordConfirm: EditText
  private lateinit var buttonLogout: Button
  private lateinit var buttonChangeName: Button
  private lateinit var buttonChangeWallet: Button
  private lateinit var buttonChangePassword: Button

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_setting, container, false)

    parentActivity = activity as HomeActivity

    user = User(parentActivity.applicationContext)
    controller = SettingController(user)

    textName = view.findViewById(R.id.editTextName)
    textWallet = view.findViewById(R.id.editTextWalletDax)
    textPassword = view.findViewById(R.id.editTextPassword)
    textPasswordConfirm = view.findViewById(R.id.editTextConfirmPassword)
    buttonChangeName = view.findViewById(R.id.buttonChangeName)
    buttonChangeWallet = view.findViewById(R.id.buttonChangeWallet)
    buttonChangePassword = view.findViewById(R.id.buttonChangePassword)
    buttonLogout = view.findViewById(R.id.buttonLogout)

    buttonChangeName.setOnClickListener {
      Toast.makeText(this@SettingFragment.context,
        if(controller.changeName(textName.text.toString()))
          "Name changed to ${textName.text}"
        else
          "Changing name failed",
        Toast.LENGTH_SHORT
      ).show()
    }

    buttonChangeWallet.setOnClickListener {
      Toast.makeText(this@SettingFragment.context,
        if(controller.changeDaxWallet(textWallet.text.toString()))
          "Wallet changed"
        else
          "Changing name failed",
        Toast.LENGTH_SHORT
      ).show()
    }

    buttonChangePassword.setOnClickListener {
      Toast.makeText(this@SettingFragment.context,
        if(controller.changePassword(textPassword.text.toString(), textPasswordConfirm.text.toString()))
          "Password changed"
        else
          "Changing password failed",
        Toast.LENGTH_SHORT
      ).show()
    }

    buttonLogout.setOnClickListener {
      val intent = Intent(parentActivity, MainActivity::class.java)
      startActivity(intent)
      user.clear()
      parentActivity.finishAffinity()
    }
    return view
  }
}