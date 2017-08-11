package com.tinkoff.news.api

import com.google.gson.annotations.SerializedName

data class ApiNewsResponse(
    @SerializedName("resultCode") val resultCode: String?,
    @SerializedName("payload") val news: List<ApiNews>?
)

data class ApiNews(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("text") val text: String?,
    @SerializedName("publicationDate") val publicationDate: ApiDate?,
    @SerializedName("bankInfoTypeId") val bankInfoTypeId: Int?
)

data class ApiNewsDetailResponse(
    @SerializedName("resultCode") val resultCode: String?,
    @SerializedName("payload") val news: ApiNewsDetail?,
    @SerializedName("trackingId") val trackingId: String
)

data class ApiNewsDetail(
    @SerializedName("title") val news: ApiNews?,
    @SerializedName("creationDate") val creationDate: ApiDate?,
    @SerializedName("lastModificationDate") val lastModificationDate: ApiDate?,
    @SerializedName("content") val content: String?,
    @SerializedName("bankInfoTypeId") val bankInfoTypeId: Int?,
    @SerializedName("typeId") val typeId: String?
)

data class ApiDate(
    @SerializedName("milliseconds") val milliseconds: Long?
)
