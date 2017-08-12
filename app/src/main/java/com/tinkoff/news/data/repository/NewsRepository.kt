package com.tinkoff.news.data.repository

import com.tinkoff.news.api.TinkoffNewsApi
import com.tinkoff.news.data.ApiException
import com.tinkoff.news.data.News
import com.tinkoff.news.data.mapper.NewsMapper
import com.tinkoff.news.db.DbNews
import com.tinkoff.news.db.IDbNews
import com.tinkoff.news.di.ApplicationScope
import io.reactivex.Maybe
import io.reactivex.Single
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import timber.log.Timber
import javax.inject.Inject

@ApplicationScope
class NewsRepository @Inject constructor(
    private val api: TinkoffNewsApi,
    private val store: KotlinReactiveEntityStore<Persistable>,
    private val newsMapper: NewsMapper
) {

  fun getNews(): Single<List<News>> {
    Timber.d("Get news")
    val local = getLocalNews().filter { it.isNotEmpty() }
    val cloud = getCloudNews()
        .filter { it.isNotEmpty() }
        .doOnSuccess(this::saveNews)
        .flatMap { local }

    return Maybe.concatArray(local, cloud).firstOrError()
  }

  fun refreshNews(): Single<List<News>> {
    return getCloudNews()
        .doOnSuccess(this::saveNews)
  }

  private fun getLocalNews(): Single<List<News>> {
    return store
        .select(IDbNews::class)
        .orderBy(DbNews.PUBLICATION_DATE.desc())
        .get()
        .observable()
        .map { newsMapper.map(it) }
        .toList()
        .doOnSubscribe { Timber.d("Get db news") }
        .doOnSuccess { Timber.d("Got db news $it") }
  }

  private fun getCloudNews(): Single<List<News>> {
    return api.getNews()
        .map { (resultCode, news) ->
          if (resultCode == null || resultCode != "OK") {
            throw ApiException(resultCode)
          } else {
            newsMapper.map(news)
          }
        }
        .doOnSubscribe { Timber.d("Get cloud news") }
        .doOnSuccess { Timber.d("Got cloud news $it") }
  }

  fun saveNews(news: List<News>) {
    val dbNews = news.map { newsMapper.map(it) }
    store
        .upsert(dbNews)
        .toCompletable()
        .doOnSubscribe { Timber.d("Save news ($news) into DB") }
        .subscribe({
          Timber.d("News saved")
        }, {
          Timber.e(it, "Save news error")
        })
  }
}