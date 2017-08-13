package com.tinkoff.news.ui.newslist

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.tinkoff.news.R
import com.tinkoff.news.data.News
import com.tinkoff.news.ui.base.OnRefreshListener
import com.tinkoff.news.ui.base.adapter.deletages.NewsDelegateAdapter
import com.tinkoff.news.ui.base.view.BaseFragment
import com.tinkoff.news.utils.gone
import com.tinkoff.news.utils.visible
import kotlinx.android.synthetic.main.fragment_news_list.*
import org.jetbrains.anko.textResource


class NewsListFragment : BaseFragment(), NewsListPresenter.View,
    NewsDelegateAdapter.Listener,
    OnRefreshListener {

  @InjectPresenter lateinit var presenter: NewsListPresenter
  private val adapter = NewsListAdapter(this)

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_news_list, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initViews()
    if (savedInstanceState == null) {
      presenter.loadNews(false)
    }
  }

  /** @see OnRefreshListener */

  override fun onRefresh() {
    swipeRefreshLayout.isRefreshing = true
    presenter.loadNews(true)
  }

  /** MVP methods */

  override fun showLoading() {
    swipeRefreshLayout.isEnabled = false
    swipeRefreshLayout.isRefreshing = false
    loadingView.visible()
    swipeRefreshLayout.gone()
    messageTextView.gone()
  }

  override fun showError() {
    swipeRefreshLayout.isEnabled = true
    swipeRefreshLayout.isRefreshing = false
    loadingView.gone()
    swipeRefreshLayout.gone()
    messageTextView.visible()
    messageTextView.textResource = R.string.news_error
  }

  override fun showEmpty() {
    swipeRefreshLayout.isEnabled = true
    swipeRefreshLayout.isRefreshing = false
    loadingView.gone()
    swipeRefreshLayout.gone()
    messageTextView.visible()
    messageTextView.textResource = R.string.news_empty
  }

  override fun showNews(news: List<News>) {
    adapter.showItems(news)
  }

  override fun showContent() {
    swipeRefreshLayout.isEnabled = true
    swipeRefreshLayout.isRefreshing = false
    loadingView.gone()
    swipeRefreshLayout.visible()
    messageTextView.gone()
  }

  /** Adapter items methods */

  override fun onNewsSelected(
      newsId: Long, title: String?, position: Int) {
    activity?.let { activity ->
      if (activity is NewsDelegateAdapter.Listener) {
        activity.onNewsSelected(newsId, title, position)
      }
    }
  }

  /** Private methods */

  private fun initViews() {
    swipeRefreshLayout.setColorSchemeResources(R.color.tangerine_yellow)
    swipeRefreshLayout.setOnRefreshListener {
      onRefresh()
    }

    newsRecyclerView.setHasFixedSize(true)
    newsRecyclerView.adapter = adapter
    newsRecyclerView.layoutManager = LinearLayoutManager(context)
  }
}