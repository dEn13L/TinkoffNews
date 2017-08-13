package com.tinkoff.news.data.repository.news

import com.tinkoff.news.data.News
import io.reactivex.Flowable
import io.reactivex.Maybe

interface INewsRepository {

  fun getNews(): Flowable<List<News>>

  fun refreshNews(): Maybe<List<News>>

  fun clearNews()
}