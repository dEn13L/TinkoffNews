package com.tinkoff.news.data


data class News(
    val newsId: Long,
    val name: String,
    val text: String,
    val publicationDate: Long,
    val bankInfoTypeId: Int
)

data class NewsDetail(
    val news: News,
    val creationDate: Long,
    val lastModificationDate: Long,
    val content: String,
    val bankInfoTypeId: Int,
    val typeId: String
)