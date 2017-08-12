package com.tinkoff.news.data.repository.news

import com.tinkoff.news.api.TinkoffNewsApi
import com.tinkoff.news.data.ApiException
import com.tinkoff.news.data.News
import com.tinkoff.news.data.mapper.NewsMapper
import com.tinkoff.news.db.DbNews
import com.tinkoff.news.db.IDbNews
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import timber.log.Timber
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val api: TinkoffNewsApi,
    private val store: KotlinReactiveEntityStore<Persistable>,
    private val newsMapper: NewsMapper
) : INewsRepository {

  override fun getNews(): Single<List<News>> {
    val local = getLocalNews().filter { it.isNotEmpty() }
    val cloud = getCloudNews()
        .filter { it.isNotEmpty() }
        .flatMap { news ->
          saveNews(news).andThen(local)
        }

    return Maybe.concatArray(local, cloud).firstOrError()
  }

  override fun refreshNews(): Maybe<List<News>> {
    val local = getLocalNews().filter { it.isNotEmpty() }
    return getCloudNews()
        .filter { it.isNotEmpty() }
        .flatMap { news ->
          saveNews(news).andThen(local)
        }
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
        .doOnSuccess { Timber.d("Got db news") }
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
        .doOnSuccess { Timber.d("Got cloud news") }
  }

  fun saveNews(news: List<News>): Completable {
    val dbNews = news.map { newsMapper.map(it) }
    return store
        .upsert(dbNews)
        .toCompletable()
        .doOnSubscribe { Timber.d("Save news ($news) into DB") }
        .doOnComplete { Timber.d("News saved") }
  }
}