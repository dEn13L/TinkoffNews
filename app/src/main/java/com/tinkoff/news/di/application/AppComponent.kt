package com.tinkoff.news.di.application

import com.tinkoff.news.api.ApiModule
import com.tinkoff.news.db.DbModule
import com.tinkoff.news.di.ApplicationScope
import com.tinkoff.news.ui.newslist.NewsListPresenter
import dagger.Component

@ApplicationScope
@Component(modules = arrayOf(
    AppModule::class,
    ApiModule::class,
    DbModule::class
))
interface AppComponent {

  fun newsListPresenterBuilder(): NewsListPresenter.Component.Builder
}