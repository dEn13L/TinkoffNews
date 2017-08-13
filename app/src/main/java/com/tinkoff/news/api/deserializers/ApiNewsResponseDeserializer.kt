package com.tinkoff.news.api.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tinkoff.news.api.ApiNews
import com.tinkoff.news.api.ApiNewsResponse
import java.lang.reflect.Type

class ApiNewsResponseDeserializer : JsonDeserializer<ApiNewsResponse> {

  override fun deserialize(json: JsonElement?, typeOfT: Type?,
      context: JsonDeserializationContext?): ApiNewsResponse? {
    json?.let {
      val jsonObject = it.asJsonObject
      val resultCode = jsonObject.get("resultCode")?.asString
      val newsList = mutableListOf<ApiNews>()
      jsonObject.get("payload")?.asJsonArray?.forEach { value ->
        context?.deserialize<ApiNews>(value, ApiNews::class.java)?.let {
          newsList.add(it)
        }
      }
      return ApiNewsResponse(resultCode, newsList)
    }
    return null
  }
}