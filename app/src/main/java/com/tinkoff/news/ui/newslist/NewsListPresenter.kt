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
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class NewsListPresenter : BasePresenter<NewsListPresenter.View>() {

  @StateStrategyType(AddToEndSingleStrategy::class)
  interface View : MvpView {

    fun showLoading()

    fun showError()

    fun showEmpty()

    fun showNews(news: List<News>, query: String?)

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
  private var loadingState = false
  private var query: String? = null
  private var querySubject = PublishSubject.create<String>()

  init {
    TinkoffNewsApplication.appComponent
        .newsListPresenterBuilder()
        .build()
        .injectMembers(this)
  }

  override fun attachView(view: View?) {
    super.attachView(view)
    querySubject
        .doOnSubscribe { addDisposable(it) }
        .debounce(100, TimeUnit.MILLISECONDS)
        .subscribe({ query ->
          newsInteractor.queryNews(query)
              .doOnSubscribe { addDisposable(it) }
              .compose(setSingleSchedulers())
              .subscribe({ news ->
                showNews(news)
              }, {
                Timber.e(it, "Filter news error")
              })
        })
  }

  fun loadNews(pullToRefresh: Boolean) {
    if (pullToRefresh) {
      refreshNews()
    } else {
      loadNews()
    }
  }

  fun filter(query: String?) {
    this.query = query
    val safeQuery = query ?: ""
    querySubject.onNext(safeQuery)
  }

  private fun refreshNews() {
    if (!loadingState) {
      val d = newsInteractor.refreshNews()
          .filter { it.isNotEmpty() }
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
            showNews(news)
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
          .doFinally { loadingState = false }
          .doOnCancel { viewState.showContent() }
          .subscribe({ news ->
            showNews(news)
          }, {
            Timber.e(it, "Load news error")
            viewState.showError()
          }, {
            viewState.showContent()
          })
      addDisposable(d)
    }
  }

  private fun showNews(news: List<News>) {
    val filteredNews = if (query.isNullOrBlank()) {
      news
    } else {
      news.filter { it.text.contains(query as CharSequence, true) }
    }
    if (filteredNews.isEmpty()) {
      viewState.showEmpty()
    } else {
      viewState.showNews(filteredNews, query)
    }
  }
}