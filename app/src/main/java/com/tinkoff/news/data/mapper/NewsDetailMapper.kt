package com.tinkoff.news.data.mapper

import com.tinkoff.news.api.ApiNewsDetail
import com.tinkoff.news.data.NewsDetail
import com.tinkoff.news.db.DbNewsDetail
import com.tinkoff.news.db.IDbNewsDetail
import com.tinkoff.news.di.ApplicationScope
import io.reactivex.Maybe
import javax.inject.Inject

@ApplicationScope
class NewsDetailMapper @Inject constructor(val newsMapper: NewsMapper) {

  fun mapMaybe(newsDetail: ApiNewsDetail?): Maybe<NewsDetail> {
    val news = map(newsDetail)
    if (news == null) {
      return Maybe.empty()
    } else {
      return Maybe.just(news)
    }
  }

  fun map(newsDetail: ApiNewsDetail?): NewsDetail? {
    newsDetail?.let {
      val news = newsMapper.map(newsDetail.news)
      val creationDate = newsDetail.creationDate?.milliseconds
      val lastModificationDate = newsDetail.lastModificationDate?.milliseconds
      if (news != null &&
          creationDate != null &&
          lastModificationDate != null &&
          newsDetail.content != null && newsDetail.content.isNotBlank() &&
          newsDetail.bankInfoTypeId != null &&
          newsDetail.typeId != null) {
        return NewsDetail(
            news,
            creationDate,
            lastModificationDate,
            newsDetail.content,
            newsDetail.bankInfoTypeId,
            newsDetail.typeId
        )
      }
    }
    return null
  }

  fun map(newsDetail: IDbNewsDetail): NewsDetail = with(newsDetail) {
    val news = newsMapper.map(newsDetail.news)
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