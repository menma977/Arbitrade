package biz.arbitrade.network

import android.util.Base64
import org.json.JSONObject

object JWTUtils {
    fun decode(jwt: String): JSONObject{
        val section = jwt.split(".")
        val jsonObject = JSONObject()
        jsonObject.put("head", toJson(section[0]))
        jsonObject.put("body", toJson(section[1]))
        return jsonObject
    }

    private fun toJson(str: String): JSONObject{
        val rawJSON = Base64.decode(str, Base64.DEFAULT)
        return JSONObject(String(rawJSON))
    }
}