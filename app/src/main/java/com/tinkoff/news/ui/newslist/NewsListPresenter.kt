package com.tinkoff.news.ui.newslist

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
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

    @StateStrategyType(AddToEndSingleStrategy::class)
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
  private var news: List<News>? = null
  private var loadingState = false

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
    if (!loadingState) {
      val d = newsInteractor.refreshNews()
          .compose(setMaybeSchedulers())
          .doOnSubscribe {
            loadingState = true
            viewState.showLoading()
          }
          .doFinally {
            loadingState = false
            viewState.showContent()
          }
          .subscribe({ news ->
            this.news = news
            viewState.showNews(news)
          }, {
            Timber.e(it, "Refresh news error")
          })
      addDisposable(d)
    }
  }

  private fun loadNews() {
    if (!loadingState) {
      val d = newsInteractor.getNews()
          .compose(setFlowableSchedulers())
          .doOnSubscribe {
            loadingState = true
            viewState.showLoading()
          }
          .doFinally {
            loadingState = false
          }
          .doOnCancel {
            if (news?.isNotEmpty() ?: false) {
              viewState.showContent()
            } else {
              viewState.showEmpty()
            }
          }
          .subscribe({ news ->
            this.news = news
            viewState.showNews(news)
          }, {
            Timber.e(it, "Load news error")
            viewState.showError()
          }, {
            if (news?.isNotEmpty() ?: false) {
              viewState.showContent()
            } else {
              viewState.showEmpty()
            }
          })
      addDisposable(d)
    }
  }
}