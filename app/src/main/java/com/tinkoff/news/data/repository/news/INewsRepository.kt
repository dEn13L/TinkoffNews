package com.tinkoff.news.data.repository.news

import com.tinkoff.news.data.News
import io.reactivex.Single

interface INewsRepository {

  fun getNews(): Single<List<News>>

  fun refreshNews(): Single<List<News>>
}