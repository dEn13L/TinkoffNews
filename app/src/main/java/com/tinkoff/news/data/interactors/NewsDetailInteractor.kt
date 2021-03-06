package com.tinkoff.news.data.interactors

import com.tinkoff.news.data.NewsDetail
import com.tinkoff.news.data.repository.newsdetail.INewsDetailRepository
import io.reactivex.Flowable

class NewsDetailInteractor constructor(
    private val newsDetailRepository: INewsDetailRepository
) {

  fun getNewsDetail(newsId: Long): Flowable<NewsDetail> {
    return newsDetailRepository.getNewsDetail(newsId)
  }
}