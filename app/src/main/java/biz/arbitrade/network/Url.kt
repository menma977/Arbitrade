package biz.arbitrade.network

object Url {
  fun web(target: String?): String {
    return "http://10.0.2.2:8000/api/${if (!target.isNullOrEmpty()) target.replace(".", "/") else ""}"
  }

  fun doge(): String {
    return "https://www.999doge.com/api/web.aspx"
  }

  object pusher {
    const val url = "10.0.2.2"
    const val port = 6001
    const val secured = false
  }
}