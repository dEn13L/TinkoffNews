package com.tinkoff.news.data.repository.newsdetail

import com.tinkoff.news.data.NewsDetail
import io.reactivex.Flowable

interface INewsDetailRepository {

  fun getNewsDetail(newsId: Long): Flowable<NewsDetail>
}