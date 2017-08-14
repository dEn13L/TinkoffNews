package com.tinkoff.news.ui.newsdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.tinkoff.news.R;
import com.tinkoff.news.ui.base.view.BaseFragment;
import com.tinkoff.news.utils.ExtensionsKt;

public class NewsDetailFragment extends BaseFragment implements NewsDetailPresenter.View {

  public static String EXTRA_NEWS_ID = NewsDetailFragment.class.getName() + "." + "newsId";
  public static String EXTRA_NEWS_TITLE = NewsDetailFragment.class.getName() + "." + "newsTitle";
  @InjectPresenter NewsDetailPresenter presenter;

  private View loadingView;
  private TextView titleTextView;
  private TextView messageTextView;
  private TextView contentTextView;

  public static NewsDetailFragment newInstance(Long newsId, String title) {
    NewsDetailFragment fragment = new NewsDetailFragment();
    Bundle bundle = new Bundle();
    bundle.putLong(EXTRA_NEWS_ID, newsId);
    bundle.putString(EXTRA_NEWS_TITLE, title);
    fragment.setArguments(bundle);
    return fragment;
  }

  @ProvidePresenter NewsDetailPresenter providePresenter() {
    Bundle arguments = getArguments();
    long newsId = 0;
    String title = null;
    if (arguments != null) {
      newsId = arguments.getLong(EXTRA_NEWS_ID);
      title = arguments.getString(EXTRA_NEWS_TITLE);
    }
    return new NewsDetailPresenter(newsId, title);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_news_detail, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loadingView = view.findViewById(R.id.loadingView);
    titleTextView = view.findViewById(R.id.titleTextView);
    messageTextView = view.findViewById(R.id.messageTextView);
    contentTextView = view.findViewById(R.id.contentTextView);
    if (savedInstanceState == null) {
      presenter.loadNewsDetail();
    }
  }

  /** MVP methods */

  @Override public void showLoading() {
    loadingView.setVisibility(View.VISIBLE);
    messageTextView.setVisibility(View.GONE);
    contentTextView.setVisibility(View.GONE);
  }

  @Override public void showError() {
    loadingView.setVisibility(View.GONE);
    messageTextView.setVisibility(View.VISIBLE);
    contentTextView.setVisibility(View.GONE);
    messageTextView.setText(R.string.news_detail_error);
  }

  @Override public void showSelectNews() {
    loadingView.setVisibility(View.GONE);
    messageTextView.setVisibility(View.VISIBLE);
    contentTextView.setVisibility(View.GONE);
    messageTextView.setText(R.string.news_detail_select);
  }

  @Override public void showNewsDetail(String title, String content) {
    ExtensionsKt.loadHtml(titleTextView, title);
    ExtensionsKt.loadHtml(contentTextView, content);
    contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
  }

  @Override public void showContent() {
    loadingView.setVisibility(View.GONE);
    messageTextView.setVisibility(View.GONE);
    contentTextView.setVisibility(View.VISIBLE);
  }
}
