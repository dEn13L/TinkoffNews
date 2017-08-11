package com.tinkoff.news.data.mapper

import com.tinkoff.news.api.ApiNewsDetail
import com.tinkoff.news.data.News
import com.tinkoff.news.data.NewsDetail
import com.tinkoff.news.db.DbNewsDetail
import com.tinkoff.news.db.IDbNewsDetail
import com.tinkoff.news.di.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class NewsDetailMapper @Inject constructor(val newsMapper: NewsMapper) {

  fun map(newsDetail: ApiNewsDetail): NewsDetail? = with(newsDetail) {
    val news = newsMapper.map(news)
    val creationDate = creationDate?.milliseconds
    val lastModificationDate = lastModificationDate?.milliseconds

    if (news != null &&
        creationDate != null &&
        lastModificationDate != null &&
        content != null && content.isNotBlank() &&
        bankInfoTypeId != null &&
        typeId != null) {
      NewsDetail(
          news,
          creationDate,
          lastModificationDate,
          content,
          bankInfoTypeId,
          typeId
      )
    } else {
      null
    }
  }

  fun map(newsDetail: IDbNewsDetail, news: News): NewsDetail = with(newsDetail) {
    NewsDetail(
        news,
        creationDate,
        lastModificationDate,
        content,
        bankInfoTypeId,
        typeId
    )
  }

  fun map(newsDetail: NewsDetail): IDbNewsDetail = with(newsDetail) {
    val dbNewsDetail = DbNewsDetail()
    dbNewsDetail.setNewsId(news.newsId)
    dbNewsDetail.setCreationDate(creationDate)
    dbNewsDetail.setLastModificationDate(lastModificationDate)
    dbNewsDetail.setContent(content)
    dbNewsDetail.setBankInfoTypeId(bankInfoTypeId)
    dbNewsDetail.setTypeId(typeId)
    dbNewsDetail
  }
}