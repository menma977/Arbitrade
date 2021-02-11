package biz.arbitrade.network

object Url {
  fun web(target: String?): String {
    return "http://192.168.77.223/api/${
      if (!target.isNullOrEmpty()) target.replace(
        ".", "/"
      ) else ""
    }"
  }

  fun doge(): String {
    return "https://www.999doge.com/api/web.aspx"
  }

  object Pusher {
    const val url = "192.168.77.223"
    private const val authEndpoint = "broadcasting.auth"
    const val port = 6001
    const val secured = false

    fun auth(): String {
      return web(authEndpoint)
    }
  }
}