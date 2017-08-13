package com.tinkoff.news.data.repository.news

import com.google.gson.Gson
import com.tinkoff.news.api.ApiNewsResponse
import com.tinkoff.news.api.TinkoffNewsApi
import com.tinkoff.news.data.News
import com.tinkoff.news.data.mapper.NewsMapper
import com.tinkoff.news.db.DbNews
import com.tinkoff.news.db.IDbNews
import com.tinkoff.news.utils.RxUtils
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import timber.log.Timber
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val api: TinkoffNewsApi,
    private val store: KotlinReactiveEntityStore<Persistable>,
    private val newsMapper: NewsMapper,
    private val gson: Gson
) : INewsRepository {

  private var news: List<News>? = null

  override fun getNews(): Flowable<List<News>> {
    val cachedNews = news
    if (cachedNews != null && cachedNews.isNotEmpty()) {
      return Flowable.just(cachedNews)
    } else {
      val local = getLocalNews()
          .filter { it.isNotEmpty() }
          .doOnSuccess { news = it }

      val cloud = getCloudNews()
          .filter { it.isNotEmpty() }
          .flatMap { news ->
            saveNews(news).andThen(local)
          }

      return Maybe.concatArray(local, cloud)
    }
  }

  override fun refreshNews(): Single<List<News>>? {
    val local = getLocalNews()
    return getCloudNews()
        .flatMap { news -> saveNews(news).andThen(local) }
        .doOnSuccess { news = it }
  }

  override fun clearNews() {
    news = null
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
        .map { gson.fromJson(it, ApiNewsResponse::class.java) }
        .compose<ApiNewsResponse>(RxUtils.checkResultCode())
        .map { (_, news) -> newsMapper.map(news) }
        .doOnSubscribe { Timber.d("Get cloud news") }
        .doOnSuccess { Timber.d("Got cloud news $it") }
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