package com.tinkoff.news.data

import com.tinkoff.news.ui.base.adapter.AdapterConstants
import com.tinkoff.news.ui.base.adapter.ViewType


data class News(
    val newsId: Long,
    val name: String,
    val text: String,
    val publicationDate: Long,
    val bankInfoTypeId: Int
) : ViewType {
  override fun getViewType(): Int {
    return AdapterConstants.NEWS
  }
}

data class NewsDetail(
    val news: News,
    val creationDate: Long,
    val lastModificationDate: Long,
    val content: String,
    val bankInfoTypeId: Int,
    val typeId: String
)