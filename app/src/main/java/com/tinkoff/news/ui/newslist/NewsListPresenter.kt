package com.tinkoff.news.ui.newslist

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
class NewsListPresenter : BasePresenter<NewsListPresenter.View>() {

  interface View : MvpView {

    fun showLoading()

    fun showError()

    fun showEmpty()

    fun showNews(news: List<News>)

    fun showContent()
  }

  @PresenterScope
  @Subcomponent(modules = arrayOf(Component.InnerModule::class))
  interface Component : PresenterComponent<NewsListPresenter> {

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
        .newsListPresenterBuilder()
        .build()
        .injectMembers(this)
  }

  fun loadNews(pullToRefresh: Boolean) {
    if (pullToRefresh) {
      refreshNews()
    } else {
      loadNews()
    }
  }

  private fun refreshNews() {
    newsInteractor.refreshNews()
        .compose(setMaybeSchedulersAndDisposable())
        .subscribe({ news ->
          //          Timber.i("News are refreshed: $news")
          viewState.showNews(news)
        }, {
          Timber.e(it, "Refresh news error")
          viewState.showContent()
        }, {
          viewState.showContent()
        })
  }

  private fun loadNews() {
    Timber.i("Load news")
    newsInteractor.getNews()
        .compose(setSingleSchedulersAndDisposable())
        .doOnSubscribe { viewState.showLoading() }
        .subscribe({ news ->
          //          Timber.i("News are loaded: $news")
          if (news.isNotEmpty()) {
            viewState.showNews(news)
            viewState.showContent()
          } else {
            viewState.showEmpty()
          }
        }, {
          Timber.e(it, "Load news error")
          viewState.showError()
        })
  }
}