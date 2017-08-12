package com.tinkoff.news.ui.newsdetail

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpView
import com.tinkoff.news.TinkoffNewsApplication
import com.tinkoff.news.data.interactors.NewsDetailInteractor
import com.tinkoff.news.data.repository.newsdetail.INewsDetailRepository
import com.tinkoff.news.data.repository.newsdetail.NewsDetailRepository
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
    private var newsId: Long,
    private var title: String?
) : BasePresenter<NewsDetailPresenter.View>() {

  interface View : MvpView {

    fun showLoading()

    fun showError()

    fun showNewsDetail(title: String?, content: String?)
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

  override fun attachView(view: View?) {
    super.attachView(view)
    showNewsDetail()
  }

  fun loadNewsDetail() {
    val d = newsDetailInteractor.getNewsDetail(newsId)
        .compose(setFlowableSchedulers())
        .doOnSubscribe { viewState.showLoading() }
        .subscribe({ (news, _, _, content) ->
          Timber.i("News detail is loaded: $news, $content")
          this.title = news.text
          this.content = content
          showNewsDetail()
        }, {
          Timber.e(it, "Load news detail error")
          viewState.showError()
        })
    addDisposable(d)
  }

  fun showNewsDetail() {
    viewState.showNewsDetail(title, content)
  }
}