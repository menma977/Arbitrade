package biz.arbitrade.network

object Url {
  fun web(url: String?): String {
    var target = if (!url.isNullOrEmpty()) url.replace(
      Regex("(?<=[a-z])\\."),
      "/"
    ) else ""
    if(target.endsWith(".0") || target.endsWith(".1")){
      val r = target[target.length-1]
      target = target.substring(0, target.length-2)
      target = "$target/$r"
    }
    return "https://tradearbi.com/api/$target"
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