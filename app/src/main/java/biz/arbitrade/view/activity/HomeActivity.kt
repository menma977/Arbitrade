package biz.arbitrade.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import biz.arbitrade.R
import biz.arbitrade.view.fragments.HomeFragment

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fragment = HomeFragment()
        addFragment(fragment)
    }

    private fun addFragment(fragment:Fragment){
        val backStateName = fragment.javaClass.simpleName
        val fragmentManager = supportFragmentManager
        val fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0)

        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contentFragment, fragment, backStateName)
            fragmentTransaction.addToBackStack(backStateName)
            fragmentTransaction.commit()
        }
    }
}