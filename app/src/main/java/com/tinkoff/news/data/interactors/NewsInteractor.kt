package com.tinkoff.news.data.interactors

import com.tinkoff.news.data.News
import com.tinkoff.news.data.repository.news.INewsRepository
import io.reactivex.Single

class NewsInteractor constructor(
    private val newsRepository: INewsRepository
) {

  fun getNews(): Single<List<News>> {
    return newsRepository.getNews()
  }

  fun refreshNews(): Single<List<News>> {
    return newsRepository.refreshNews()
  }
}