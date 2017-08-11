package com.tinkoff.news.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TinkoffNewsApi {

  @GET("/v1/news") fun getNews(): Single<ApiNewsResponse>

  @GET("/v1/news_content") fun getNewsDetail(
      @Query("id") payload: String
  ): Single<ApiNewsDetailResponse>

}