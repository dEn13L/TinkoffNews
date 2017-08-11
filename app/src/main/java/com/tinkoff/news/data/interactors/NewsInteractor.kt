package com.tinkoff.news.data.interactors

import com.tinkoff.news.data.News
import com.tinkoff.news.data.repository.NewsRepository
import io.reactivex.Single

class NewsInteractor constructor(
    private val newsRepository: NewsRepository
) {

  fun getNews(): Single<List<News>> {
    return newsRepository.getNews()
  }

  fun refreshNews(): Single<List<News>> {
    return newsRepository.refreshNews()
  }
}