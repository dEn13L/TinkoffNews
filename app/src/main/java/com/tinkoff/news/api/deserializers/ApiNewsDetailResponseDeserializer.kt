package com.tinkoff.news.api.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tinkoff.news.api.ApiNewsDetail
import com.tinkoff.news.api.ApiNewsDetailResponse
import java.lang.reflect.Type

class ApiNewsDetailResponseDeserializer : JsonDeserializer<ApiNewsDetailResponse> {

  override fun deserialize(json: JsonElement?, typeOfT: Type?,
      context: JsonDeserializationContext?): ApiNewsDetailResponse? {
    json?.let {
      val jsonObject = it.asJsonObject
      val resultCode = jsonObject.get("resultCode")?.asString
      val newsDetail = jsonObject.get("payload")?.asJsonObject
      val apiNewsDetail = context?.deserialize<ApiNewsDetail>(newsDetail, ApiNewsDetail::class.java)
      val trackingId = jsonObject.get("trackingId")?.asString
      return ApiNewsDetailResponse(resultCode, apiNewsDetail, trackingId)
    }
    return null
  }
}