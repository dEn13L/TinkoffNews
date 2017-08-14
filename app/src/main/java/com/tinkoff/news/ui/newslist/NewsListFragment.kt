package com.tinkoff.news.ui.newslist

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.tinkoff.news.R
import com.tinkoff.news.data.News
import com.tinkoff.news.ui.base.OnRefreshListener
import com.tinkoff.news.ui.base.OnSearchListener
import com.tinkoff.news.ui.base.OnToolbarClickListener
import com.tinkoff.news.ui.base.adapter.deletages.NewsDelegateAdapter
import com.tinkoff.news.ui.base.view.BaseFragment
import com.tinkoff.news.utils.gone
import com.tinkoff.news.utils.visible
import kotlinx.android.synthetic.main.fragment_news_list.*
import org.jetbrains.anko.textResource


class NewsListFragment : BaseFragment(), NewsListPresenter.View,
    NewsDelegateAdapter.Listener,
    OnRefreshListener,
    OnToolbarClickListener,
    OnSearchListener {

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
    presenter.loadNews(true)
  }

  /** @see OnToolbarClickListener */

  override fun onToolbarClicked() {
    newsRecyclerView.scrollToPosition(0)
  }

  /** @see OnSearchListener */

  override fun onSearch(query: String?) {
    presenter.filter(query)
  }

  /** MVP methods */

  override fun showLoading() {
    swipeRefreshLayout.isRefreshing = true
    newsRecyclerView.gone()
    messageTextView.gone()
  }

  override fun showError() {
    swipeRefreshLayout.isRefreshing = false
    newsRecyclerView.gone()
    messageTextView.visible()
    messageTextView.textResource = R.string.news_error
  }

  override fun showEmpty() {
    swipeRefreshLayout.isRefreshing = false
    newsRecyclerView.gone()
    messageTextView.visible()
    messageTextView.textResource = R.string.news_empty
  }

  override fun showNews(news: List<News>, query: String?) {
    adapter.showItems(news, query)
    newsRecyclerView.visible()
    messageTextView.gone()
  }

  override fun showContent() {
    swipeRefreshLayout.isRefreshing = false
  }

  /** Adapter items methods */

  override fun onNewsSelected(newsId: Long, title: String?) {
    activity?.let { activity ->
      if (activity is NewsDelegateAdapter.Listener) {
        activity.onNewsSelected(newsId, title)
      }
    }
  }

  /** Private methods */

  private fun initViews() {
    swipeRefreshLayout.setColorSchemeResources(R.color.tangerine_yellow)
    swipeRefreshLayout.setOnRefreshListener {
      onRefresh()
    }

    val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    val divider = ContextCompat.getDrawable(context, R.drawable.divider)
    decoration.setDrawable(divider)
    newsRecyclerView.addItemDecoration(decoration)
    newsRecyclerView.setHasFixedSize(true)
    newsRecyclerView.adapter = adapter
    newsRecyclerView.layoutManager = LinearLayoutManager(context)
  }
}