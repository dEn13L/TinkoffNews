package com.tinkoff.news.ui.newsdetail

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpView
import com.tinkoff.news.TinkoffNewsApplication
import com.tinkoff.news.data.interactors.NewsDetailInteractor
import com.tinkoff.news.data.repository.newsdetail.INewsDetailRepository
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
class NewsDetailPresenter(
    private val newsId: Long,
    private var title: String?
) : BasePresenter<NewsDetailPresenter.View>() {

  interface View : MvpView {

    fun showLoading()

    fun showError()

    fun showSelectNews()

    fun showNewsDetail(title: String?, content: String?)

    fun showContent()
  }

  @PresenterScope
  @Subcomponent(modules = arrayOf(Component.InnerModule::class))
  interface Component : PresenterComponent<NewsDetailPresenter> {

    @Subcomponent.Builder
    interface Builder : PresenterComponentBuilder<InnerModule, Component>

    @Module
    class InnerModule : PresenterModule {

      @Provides @PresenterScope fun provideNewsDetailInteractor(
          newsDetailRepository: INewsDetailRepository
      ): NewsDetailInteractor {
        return NewsDetailInteractor(newsDetailRepository)
      }
    }
  }

  @Inject lateinit var newsDetailInteractor: NewsDetailInteractor
  private var content: String? = null

  init {
    TinkoffNewsApplication.appComponent
        .newsDetailPresenterBuilder()
        .build()
        .injectMembers(this)
  }

  fun loadNewsDetail() {
    if (newsId == 0L) {
      viewState.showSelectNews()
    } else {
      showNewsDetail()
      val d = newsDetailInteractor.getNewsDetail(newsId)
          .compose(setFlowableSchedulers())
          .doOnSubscribe { viewState.showLoading() }
          .subscribe({ (news, _, _, content) ->
            this.title = news.text
            this.content = content
            showNewsDetail()
            viewState.showContent()
          }, {
            Timber.e(it, "Load news detail error")
            viewState.showError()
          })
      addDisposable(d)
    }
  }

  private fun showNewsDetail() {
    viewState.showNewsDetail(title, content)
  }
}