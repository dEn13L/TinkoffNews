package com.tinkoff.news.data.repository.newsdetail

import com.google.gson.Gson
import com.tinkoff.news.api.ApiNewsDetailResponse
import com.tinkoff.news.api.TinkoffNewsApi
import com.tinkoff.news.data.NewsDetail
import com.tinkoff.news.data.mapper.NewsDetailMapper
import com.tinkoff.news.db.DbNewsDetail
import com.tinkoff.news.db.IDbNewsDetail
import com.tinkoff.news.utils.RxUtils
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import timber.log.Timber
import javax.inject.Inject

class NewsDetailRepository @Inject constructor(
    private val api: TinkoffNewsApi,
    private val store: KotlinReactiveEntityStore<Persistable>,
    private val newsDetailMapper: NewsDetailMapper,
    private val gson: Gson
) : INewsDetailRepository {

  override fun getNewsDetail(newsId: Long): Flowable<NewsDetail> {
    val local = getLocalNewsDetail(newsId)
    val cloud = getCloudNewsDetail(newsId)
        .flatMap { newsDetail ->
          saveNewsDetail(newsDetail).andThen(Maybe.just(newsDetail))
        }

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
        .map { gson.fromJson(it, ApiNewsDetailResponse::class.java) }
        .compose<ApiNewsDetailResponse>(RxUtils.checkResultCode())
        .flatMapMaybe { (_, newsDetail) -> newsDetailMapper.mapMaybe(newsDetail) }
        .doOnSubscribe { Timber.d("Get cloud news detail (newsId: $newsId)") }
        .doOnSuccess { Timber.d("Got cloud news detail $it") }
  }

  fun saveNewsDetail(newsDetail: NewsDetail): Completable {
    val dbNewsDetail = newsDetailMapper.map(newsDetail)
    return store
        .upsert(dbNewsDetail)
        .toCompletable()
        .doOnSubscribe { Timber.d("Save news detail ($newsDetail) into DB") }
        .doOnComplete { Timber.d("News detail saved") }
  }
}