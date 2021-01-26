package biz.arbitrade.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import biz.arbitrade.R
import biz.arbitrade.model.User

class HomeFragment : Fragment() {
    private lateinit var username: TextView
    private lateinit var balance: TextView
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val user = User(activity!!.applicationContext)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        username = view.findViewById(R.id.textViewUsername)
        balance = view.findViewById(R.id.textViewBalance)

        username.text = user.getString("username")
        balance.text = user.getInteger("balance").toString()
        return view
    }

}