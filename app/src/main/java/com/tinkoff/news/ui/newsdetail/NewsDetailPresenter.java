package com.tinkoff.news.ui.newsdetail;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpView;
import com.tinkoff.news.TinkoffNewsApplication;
import com.tinkoff.news.data.NewsDetail;
import com.tinkoff.news.data.interactors.NewsDetailInteractor;
import com.tinkoff.news.data.repository.newsdetail.INewsDetailRepository;
import com.tinkoff.news.di.PresenterComponent;
import com.tinkoff.news.di.PresenterComponentBuilder;
import com.tinkoff.news.di.PresenterModule;
import com.tinkoff.news.di.PresenterScope;
import com.tinkoff.news.ui.base.presenter.BasePresenter;
import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import org.reactivestreams.Subscription;
import timber.log.Timber;
import javax.inject.Inject;

@InjectViewState
public class NewsDetailPresenter extends BasePresenter<NewsDetailPresenter.View> {

  interface View extends MvpView {

    void showLoading();

    void showError();

    void showSelectNews();

    void showNewsDetail(String title, String content);

    void showContent();
  }

  @PresenterScope
  @Subcomponent(modules = Component.InnerModule.class)
  public interface Component extends PresenterComponent<NewsDetailPresenter> {

    @Subcomponent.Builder
    interface Builder extends PresenterComponentBuilder<InnerModule, Component> {}

    @Module
    class InnerModule implements PresenterModule {

      @Provides @PresenterScope NewsDetailInteractor provideNewsDetailInteractor(
          INewsDetailRepository newsDetailRepository
      ) {
        return new NewsDetailInteractor(newsDetailRepository);
      }
    }
  }

  @Inject NewsDetailInteractor newsDetailInteractor;
  private Long newsId;
  private String title;
  private String content;

  NewsDetailPresenter(Long newsId, String title) {
    this.newsId = newsId;
    this.title = title;

    TinkoffNewsApplication.appComponent
        .newsDetailPresenterBuilder()
        .build()
        .injectMembers(this);
  }

  void loadNewsDetail() {
    if (newsId == 0L) {
      getViewState().showSelectNews();
    } else {
      showNewsDetail();
      Disposable d = newsDetailInteractor.getNewsDetail(newsId)
          .compose(setFlowableSchedulers())
          .doOnSubscribe(new Consumer<Subscription>() {
            @Override public void accept(Subscription disposable) throws Exception {
              getViewState().showLoading();
            }
          })
          .subscribe(new Consumer<Object>() {
            @Override public void accept(Object newsDetail) throws Exception {
              onNewsDetail((NewsDetail) newsDetail);
            }
          }, new Consumer<Throwable>() {
            @Override public void accept(Throwable throwable) throws Exception {
              Timber.e(throwable, "Load news detail error");
              getViewState().showError();
            }
          });
      addDisposable(d);
    }
  }

  private void onNewsDetail(NewsDetail newsDetail) {
    this.title = newsDetail.getNews().getText();
    this.content = newsDetail.getContent();
    showNewsDetail();
    getViewState().showContent();
  }

  private void showNewsDetail() {
    getViewState().showNewsDetail(title, content);
  }
}