package biz.arbitrade.network

object Url {
  fun web(target: String?): String {
    return "https://tradearbi.com/api/${
      if (!target.isNullOrEmpty()) target.replace(
        ".", "/"
      ) else ""
    }"
  }

  fun doge(): String {
    return "https://www.999doge.com/api/web.aspx"
  }

  object Pusher {
    const val url = "tradearbi.com"
    private const val authEndpoint = "broadcasting.auth"
    const val port = 6001
    const val secured = true

    fun auth(): String {
      return web(authEndpoint)
    }
  }
}