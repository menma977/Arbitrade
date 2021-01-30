package biz.arbitrade.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import biz.arbitrade.R
import biz.arbitrade.controller.Helper
import biz.arbitrade.model.User
import biz.arbitrade.view.activity.RegisterActivity
import biz.arbitrade.view.activity.TradeOneActivity
import biz.arbitrade.view.activity.TradeTwoActivity
import kotlinx.android.synthetic.main.part_announcement.view.*

class HomeFragment : Fragment() {
    private lateinit var username: TextView
    private lateinit var balance: TextView
    private lateinit var register: LinearLayout
    private lateinit var tradeOne: LinearLayout
    private lateinit var tradeTwo: LinearLayout
    private lateinit var announcementGroup: LinearLayout
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        user = User(activity!!.applicationContext)

        Log.d("MINE", R.id.btnRegister.toString())

        username = view.findViewById(R.id.textViewUsername)
        balance = view.findViewById(R.id.textViewBalance)
        register = view.findViewById(R.id.lnrLayoutRegister)
        tradeOne = view.findViewById(R.id.lnrLayoutTradeOne)
        tradeTwo = view.findViewById(R.id.lnrLayoutTradeTwo)
        announcementGroup = view.findViewById(R.id.lnrLayoutAnnouncements)

        username.text = user.getString("username")
        balance.text = Helper.toDogeString(user.getLong("balance"))

        register.setOnClickListener {
            move("register")
        }
        tradeOne.setOnClickListener {
            move("trade_one")
        }
        tradeTwo.setOnClickListener {
            move("trade_two")
        }

        return view
    }

    private fun createAnnouncement(type: Int, message: String) {
        val v = this.layoutInflater.inflate(R.layout.part_announcement, null)
        //v.imageAnnouncementIcon.setImageResource()
        v.textAnnouncementMessage.text = message
        announcementGroup.addView(v)
    }

    private fun move(
        to: String,
        finish: Boolean = false,
        finishAffinity: Boolean = false
    ) {
        val intent = Intent(
            activity, when (to) {
                "register" -> RegisterActivity::class.java
                "trade_one" -> TradeOneActivity::class.java
                "trade_two" -> TradeTwoActivity::class.java
                else -> null
            }
        )
        startActivity(intent)
        if (finish) {
            if (finishAffinity) activity?.finishAffinity()
            else activity?.finish()
        }
    }

}