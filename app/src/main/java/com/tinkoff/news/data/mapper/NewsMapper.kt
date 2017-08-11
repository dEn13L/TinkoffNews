package com.tinkoff.news.data.mapper

import com.tinkoff.news.api.ApiNews
import com.tinkoff.news.data.News
import com.tinkoff.news.db.DbNews
import com.tinkoff.news.db.IDbNews
import com.tinkoff.news.di.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class NewsMapper @Inject constructor() {

  fun map(newsList: List<ApiNews?>?): List<News> {
    val news = mutableListOf<News?>()
    newsList?.mapTo(news) { map(it) }
    return news.filterNotNull()
  }

  fun map(news: ApiNews?): News? {
    news?.let {
      val newsId = news.id?.toLong()
      val publicationDate = news.publicationDate?.milliseconds
      if (newsId != null &&
          news.name != null && news.name.isNotBlank() &&
          news.text != null && news.text.isNotBlank() &&
          publicationDate != null &&
          news.bankInfoTypeId != null) {
        return News(
            newsId,
            news.name,
            news.text,
            publicationDate,
            news.bankInfoTypeId
        )
      }
    }
    return null
  }

  fun map(news: IDbNews): News = with(news) {
    News(
        newsId,
        name,
        text,
        publicationDate,
        bankInfoTypeId
    )
  }

  fun map(news: News): IDbNews = with(news) {
    val dbNews = DbNews()
    dbNews.setNewsId(newsId)
    dbNews.setName(name)
    dbNews.setText(text)
    dbNews.setPublicationDate(publicationDate)
    dbNews.setBankInfoTypeId(bankInfoTypeId)
    dbNews
  }
}