package com.tinkoff.news.ui.newsdetail

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
class NewsDetailsPresenter(
    val startingPosition: Int
) : BasePresenter<NewsDetailsPresenter.View>() {

  interface View : MvpView {

    fun showError()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showNews(news: List<News>, startingPosition: Int)

    fun showNews(newsId: Long, title: String, position: Int)

    fun showContent()
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
  private var currentPosition = 0
  private var news: List<News>? = null
  private var currentNews: News? = null

  init {
    currentPosition = startingPosition
    TinkoffNewsApplication.appComponent
        .newsDetailsPresenterBuilder()
        .build()
        .injectMembers(this)
  }

  fun enterTwoPaneMode() {
    currentNews?.let {
      viewState.showNews(it.newsId, it.text, currentPosition)
    }
  }

  fun loadNews() {
    val d = newsInteractor.getNews()
        .compose(setFlowableSchedulers())
        .subscribe({ news ->
          this.news = news
          pageChanged(startingPosition)
          viewState.showNews(news, startingPosition)
        }, {
          Timber.e(it, "Load news error")
          viewState.showError()
        }, {
          viewState.showContent()
        })
    addDisposable(d)
  }

  fun pageChanged(position: Int) {
    currentPosition = position
    currentNews = news?.get(position)
  }
}