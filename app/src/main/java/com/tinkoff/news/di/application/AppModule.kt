package com.tinkoff.news.di.application

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.tinkoff.news.api.TinkoffNewsApi
import com.tinkoff.news.data.mapper.NewsDetailMapper
import com.tinkoff.news.data.mapper.NewsMapper
import com.tinkoff.news.data.repository.news.INewsRepository
import com.tinkoff.news.data.repository.news.NewsRepository
import com.tinkoff.news.data.repository.newsdetail.INewsDetailRepository
import com.tinkoff.news.data.repository.newsdetail.NewsDetailRepository
import com.tinkoff.news.di.ApplicationScope
import dagger.Module
import dagger.Provides
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore

@Module class AppModule(private val application: Application) {

  @Provides @ApplicationScope fun provideContext(): Context {
    return application.applicationContext
  }

  @Provides @ApplicationScope fun provideNewsRepository(
      api: TinkoffNewsApi,
      store: KotlinReactiveEntityStore<Persistable>,
      newsMapper: NewsMapper,
      gson: Gson
  ): INewsRepository {
    return NewsRepository(api, store, newsMapper, gson)
  }

  @Provides @ApplicationScope fun provideNewsDetailRepository(
      api: TinkoffNewsApi,
      store: KotlinReactiveEntityStore<Persistable>,
      newsDetailMapper: NewsDetailMapper,
      gson: Gson
  ): INewsDetailRepository {
    return NewsDetailRepository(api, store, newsDetailMapper, gson)
  }
}