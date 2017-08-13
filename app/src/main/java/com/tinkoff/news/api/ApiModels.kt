package com.tinkoff.news.api

import com.google.gson.annotations.SerializedName

abstract class ApiResponse(
    open val resultCode: String?
)

data class ApiNewsResponse(
    override val resultCode: String?,
    val news: List<ApiNews>
) : ApiResponse(resultCode)

data class ApiNews(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("text") val text: String?,
    @SerializedName("publicationDate") val publicationDate: ApiDate?,
    @SerializedName("bankInfoTypeId") val bankInfoTypeId: Int?
)

data class ApiNewsDetailResponse(
    override val resultCode: String?,
    val newsDetail: ApiNewsDetail?,
    val trackingId: String?
) : ApiResponse(resultCode)

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
