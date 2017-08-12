package com.tinkoff.news.ui.newsdetail

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpView
import com.tinkoff.news.TinkoffNewsApplication
import com.tinkoff.news.data.News
import com.tinkoff.news.data.interactors.NewsInteractor
import com.tinkoff.news.data.repository.news.INewsRepository
import com.tinkoff.news.di.PresenterComponent
import com.tinkoff.news.di.PresenterComponentBuilder
import com.tinkoff.news.di.PresenterModule
import com.tinkoff.news.di.PresenterScope
import com.tinkoff.news.ui.base.presenter.BasePresenter
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class NewsDetailsPresenter(
    val startingPosition: Int
) : BasePresenter<NewsDetailsPresenter.View>() {

  interface View : MvpView {

    fun showError()

    fun showNews(news: List<News>, startingPosition: Int)
  }

  @PresenterScope
  @Subcomponent(modules = arrayOf(Component.InnerModule::class))
  interface Component : PresenterComponent<NewsDetailsPresenter> {

    @Subcomponent.Builder
    interface Builder : PresenterComponentBuilder<InnerModule, Component>

    @Module
    class InnerModule : PresenterModule {

      @Provides @PresenterScope fun provideNewsInteractor(
          newsRepository: INewsRepository
      ): NewsInteractor {
        return NewsInteractor(newsRepository)
      }
    }
  }

  @Inject lateinit var newsInteractor: NewsInteractor

  init {
    TinkoffNewsApplication.appComponent
        .newsDetailsPresenterBuilder()
        .build()
        .injectMembers(this)
  }

  fun loadNews() {
    Timber.i("Load news")
    newsInteractor.getNews()
        .compose(setSingleSchedulersAndDisposable())
        .subscribe({ news ->
          //          Timber.i("News are loaded: $news")
          viewState.showNews(news, startingPosition)
        }, {
          Timber.e(it, "Load news error")
          viewState.showError()
        })
  }
}