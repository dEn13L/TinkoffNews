package com.tinkoff.news.data.repository

import com.tinkoff.news.api.TinkoffNewsApi
import com.tinkoff.news.data.ApiException
import com.tinkoff.news.data.NewsDetail
import com.tinkoff.news.data.mapper.NewsDetailMapper
import com.tinkoff.news.data.mapper.NewsMapper
import com.tinkoff.news.db.DbNewsDetail
import com.tinkoff.news.db.IDbNewsDetail
import com.tinkoff.news.di.ApplicationScope
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import timber.log.Timber
import javax.inject.Inject

@ApplicationScope
class NewsDetailRepository @Inject constructor(
    private val api: TinkoffNewsApi,
    private val store: KotlinReactiveEntityStore<Persistable>,
    private val newsMapper: NewsMapper,
    private val newsDetailMapper: NewsDetailMapper
) {

  fun getNewsDetail(newsId: Long): Flowable<NewsDetail> {
    Timber.d("Get new detail (newsId: $newsId)")
    val local = getLocalNewsDetail(newsId)
    val cloud = getCloudNewsDetail(newsId)
        .doOnSuccess(this::saveNewsDetail)
        .flatMap { local }

    return Maybe.mergeArray(local, cloud)
  }

  private fun getLocalNewsDetail(newsId: Long): Maybe<NewsDetail> {
    return store
        .select(IDbNewsDetail::class)
        .where(DbNewsDetail.NEWS_ID.eq(newsId))
        .get()
        .maybe()
        .map { newsDetailMapper.map(it) }
        .doOnSubscribe { Timber.d("Get db news detail (newsId: $newsId)") }
        .doOnSuccess { Timber.d("Got db news detail $it") }
  }

  private fun getCloudNewsDetail(newsId: Long): Maybe<NewsDetail> {
    return api.getNewsDetail(newsId)
        .flatMapMaybe { (resultCode, newsDetail) ->
          if (resultCode == null || resultCode != "OK") {
            throw ApiException(resultCode)
          } else {
            newsDetailMapper.mapMaybe(newsDetail)
          }
        }
        .doOnSubscribe { Timber.d("Get cloud news detail (newsId: $newsId)") }
        .doOnSuccess { Timber.d("Got cloud news detail $it") }
  }

  fun saveNewsDetail(newsDetail: NewsDetail) {
    val dbNewsDetail = newsDetailMapper.map(newsDetail)
    val dbNews = newsMapper.map(newsDetail.news, dbNewsDetail)
    store
        .upsert(dbNews)
        .toCompletable()
        .doOnSubscribe { Timber.d("Save news detail ($newsDetail) into DB") }
        .subscribe({
          Timber.d("News detail saved")
        }, {
          Timber.e(it, "Save news detail error")
        })
  }
}