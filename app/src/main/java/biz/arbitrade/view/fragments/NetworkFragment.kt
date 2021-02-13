package biz.arbitrade.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import biz.arbitrade.R
import biz.arbitrade.model.User
import biz.arbitrade.network.Url
import biz.arbitrade.network.WebViewController
import biz.arbitrade.view.activity.HomeActivity
import biz.arbitrade.view.dialog.Loading
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

class NetworkFragment : Fragment() {
  private lateinit var user: User
  private lateinit var loading: Loading
  private lateinit var webContent: WebView
  private lateinit var result: JSONObject
  private lateinit var parentActivity: HomeActivity

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.fragment_network, container, false)

    parentActivity = activity as HomeActivity

    user = User(parentActivity.applicationContext)
    loading = Loading(parentActivity)

    webContent = view.findViewById(R.id.webViewContent)

    loading.openDialog()
    Timer().schedule(100) {
      result = WebViewController("binary.index", user.getString("token")).call()
      if (result.getInt("code") == 200) {
        parentActivity.runOnUiThread {
          webContent.removeAllViews()
          webContent.webViewClient = WebViewClient()
          webContent.webChromeClient = WebChromeClient()
          webContent.settings.javaScriptEnabled = true
          webContent.settings.domStorageEnabled = true
          webContent.settings.javaScriptCanOpenWindowsAutomatically = true
          webContent.loadData(result.getString("data"), "text/html", "UTF-8")
          webContent.loadDataWithBaseURL(
            Url.web("binary.index"), result.getString("data"), "text/html", "UTF-8", null
          )
          loading.closeDialog()
        }
      }
    }
    return view
  }
}