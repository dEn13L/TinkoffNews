package com.tinkoff.news.api

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TinkoffNewsApi {

  @GET("/v1/news") fun getNews(): Single<JsonObject>

  @GET("/v1/news_content") fun getNewsDetail(
      @Query("id") newsId: Long): Single<JsonObject>

}