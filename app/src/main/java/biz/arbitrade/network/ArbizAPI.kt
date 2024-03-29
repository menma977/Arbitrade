package biz.arbitrade.network

import android.annotation.SuppressLint
import okhttp3.FormBody
import okhttp3.Response
import org.json.JSONObject
import java.util.concurrent.Callable

class ArbizAPI(private var command: String, private var method: String, private var token: String?, private var bodyValue: FormBody.Builder?) : Callable<JSONObject> {
  @SuppressLint("DefaultLocale")
  override fun call(): JSONObject {
    val header = HashMap<String, String>()
    if (!token.isNullOrEmpty()) header["Authorization"] = "Bearer $token"
    return when {
      method.toLowerCase() == "post" -> {
        http.makePost(Url.web(command), bodyValue, header)
      }
      else -> http.makeGet(Url.web(command), null, header)
    }
  }

  private val http = object : Base() {
    override fun responseHandler(response: Response, json: JSONObject): JSONObject {
      if (response.isSuccessful) {
        if (!json.has("code")) json.put("code", response.code)
        return json
      } else {
        return when {
          json.toString().contains("Unauthenticated.") -> {
            JSONObject().put("code", 500).put("data", json.getString("message"))
          }
          json.toString().contains("errors") -> {
            JSONObject().put("code", 500).put(
              "data", json.getJSONObject("errors").getJSONArray(json.getJSONObject("errors").names()!![0].toString())[0]
            )
          }
          json.toString().contains("message") -> {
            JSONObject().put("code", 500).put("message", json.getString("message"))
          }
          else -> {
            JSONObject().put("code", 500).put("data", json)
          }
        }
      }
    }
  }
}