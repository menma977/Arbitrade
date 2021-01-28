package biz.arbitrade.view.activity

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import biz.arbitrade.R
import biz.arbitrade.view.fragments.HomeFragment
import biz.arbitrade.view.fragments.SettingFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var homeFragment: HomeFragment
    private lateinit var settingFragment: SettingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        homeFragment = HomeFragment()
        settingFragment = SettingFragment()

        findViewById<LinearLayout>(R.id.btnToHome).setOnClickListener {
            addFragment(homeFragment)
        }
        findViewById<LinearLayout>(R.id.btnToSetting).setOnClickListener {
            addFragment(settingFragment)
        }

        addFragment(homeFragment)
    }

    private fun addFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.simpleName
        val fragmentManager = supportFragmentManager
        val fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0)

        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) {
            fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.contentFragment, fragment, backStateName)
                //.addToBackStack(backStateName)
                .commit()
        }
    }
}