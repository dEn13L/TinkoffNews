package com.tinkoff.news.data.interactors

import com.tinkoff.news.data.News
import com.tinkoff.news.data.repository.news.INewsRepository
import io.reactivex.Flowable
import io.reactivex.Maybe

class NewsInteractor constructor(
    private val newsRepository: INewsRepository
) {

  fun getNews(): Flowable<List<News>> {
    return newsRepository.getNews()
  }

  fun refreshNews(): Maybe<List<News>> {
    return newsRepository.refreshNews()
  }

  fun clearNews() {
    newsRepository.clearNews()
  }
}