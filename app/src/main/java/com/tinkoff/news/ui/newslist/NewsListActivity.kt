package com.tinkoff.news.ui.newslist

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.tinkoff.news.R
import com.tinkoff.news.data.News
import com.tinkoff.news.ui.base.adapter.deletages.NewsDelegateAdapter
import com.tinkoff.news.ui.base.view.BaseActivity
import com.tinkoff.news.ui.newsdetail.NewsDetailActivity
import com.tinkoff.news.utils.gone
import com.tinkoff.news.utils.visible
import kotlinx.android.synthetic.main.activity_news_list.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import org.jetbrains.anko.textResource
import timber.log.Timber

class NewsListActivity : BaseActivity(), NewsListPresenter.View,
    NewsDelegateAdapter.Listener {

  @InjectPresenter lateinit var presenter: NewsListPresenter
  private val adapter = NewsListAdapter(this)
  // Flag to prevent multiple news detail opening
  private var newsClicked = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_news_list)
    initToolbar()
    initViews()
    presenter.loadNews(false)
  }

  override fun onResume() {
    super.onResume()
    newsClicked = false
  }

  /** MVP methods */

  override fun showLoading() {
    swipeRefreshLayout.isEnabled = false
    swipeRefreshLayout.isRefreshing = false
    loadingView.visible()
    newsRecyclerView.gone()
    messageTextView.gone()
  }

  override fun showContent() {
    swipeRefreshLayout.isEnabled = true
    swipeRefreshLayout.isRefreshing = false
    loadingView.gone()
    newsRecyclerView.visible()
    messageTextView.gone()
  }

  override fun showError() {
    swipeRefreshLayout.isEnabled = true
    swipeRefreshLayout.isRefreshing = false
    loadingView.gone()
    newsRecyclerView.gone()
    messageTextView.visible()
    messageTextView.textResource = R.string.news_error
  }

  override fun showEmpty() {
    swipeRefreshLayout.isEnabled = true
    swipeRefreshLayout.isRefreshing = false
    loadingView.gone()
    newsRecyclerView.gone()
    messageTextView.visible()
    messageTextView.textResource = R.string.news_empty
  }

  override fun showNews(news: List<News>) {
    adapter.showItems(news)
  }

  /** Adapter items methods */

  override fun onNewsClicked(newsId: Long, title: String) {
    if (!newsClicked) {
      newsClicked = true
      NewsDetailActivity.start(this, newsId, title)
    }
  }

  /** Private methods */

  private fun initToolbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.let {
      it.setDisplayHomeAsUpEnabled(false)
      it.setDisplayShowTitleEnabled(true)
    }
  }

  private fun initViews() {
    swipeRefreshLayout.setOnRefreshListener {
      Timber.i("OnRefresh")
      presenter.loadNews(true)
    }

    newsRecyclerView.setHasFixedSize(true)
    newsRecyclerView.adapter = adapter
    newsRecyclerView.layoutManager = LinearLayoutManager(this)
  }
}
