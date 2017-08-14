package com.tinkoff.news.ui.newslist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.tinkoff.news.R
import com.tinkoff.news.ui.base.OnRefreshListener
import com.tinkoff.news.ui.base.OnSearchListener
import com.tinkoff.news.ui.base.OnToolbarClickListener
import com.tinkoff.news.ui.base.adapter.deletages.NewsDelegateAdapter
import com.tinkoff.news.ui.base.view.BaseActivity
import com.tinkoff.news.ui.newsdetail.NewsDetailFragment
import com.tinkoff.news.ui.newsdetail.NewsDetailsActivity
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.intentFor
import timber.log.Timber

class NewsListActivity : BaseActivity(), NewsDelegateAdapter.Listener {

  companion object {

    val SELECTED_NEWS_ID = "newsId"
    val SELECTED_NEWS_TITLE = "newsTitle"
    val SEARCH_QUERY = "searchQuery"

    fun start(context: Context, newsId: Long, title: String) {
      val intent = context.intentFor<NewsListActivity>(
          SELECTED_NEWS_ID to newsId,
          SELECTED_NEWS_TITLE to title
      )
      context.startActivity(intent)
    }
  }

  private var isTwoPanes = false
  private var currentNewsId = 0L
  private var currentNewsTitle: String? = null
  private var searchQuery: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.news_list_layout)
    initToolbar()
    isTwoPanes = isTwoPanesLayout()
    restoreSelectedNews(savedInstanceState)
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    val bundle = intent?.extras
    restoreSelectedNews(bundle)
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    saveInstanceState(outState)
    super.onSaveInstanceState(outState)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_news_list, menu)
    val searchMenuItem = menu.findItem(R.id.action_search)
    val searchView = searchMenuItem.actionView as SearchView
    searchQuery?.let {
      if (it.isNotEmpty()) {
        searchMenuItem.expandActionView()
        searchView.setQuery(it, true)
        searchView.clearFocus()
      }
    }
    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        return true
      }

      override fun onQueryTextChange(query: String?): Boolean {
        searchNews(query)
        return true
      }
    })
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.action_refresh -> consume { refresh() }
    else -> super.onOptionsItemSelected(item)
  }

  /** @see NewsDelegateAdapter.Listener */

  override fun onNewsSelected(newsId: Long, title: String?) {
    Timber.i("onNewsSelected newsId: $newsId, title: $title")
    currentNewsId = newsId
    currentNewsTitle = title
    if (isTwoPanes) {
      showNewsDetailFragment(newsId, title)
    } else {
      NewsDetailsActivity.start(this, newsId)
    }
  }

  /** Private methods */

  private fun initToolbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.let {
      it.setDisplayHomeAsUpEnabled(false)
      it.setDisplayShowTitleEnabled(true)
    }
    toolbar.setOnClickListener {
      supportFragmentManager.fragments?.filterIsInstance<OnToolbarClickListener>()?.forEach {
        it.onToolbarClicked()
      }
    }
  }

  private fun isTwoPanesLayout(): Boolean {
    return findViewById<ViewGroup>(R.id.newsDetailFragment) != null
  }

  private fun refresh() {
    supportFragmentManager.fragments?.filterIsInstance<OnRefreshListener>()?.forEach {
      it.onRefresh()
    }
  }

  private fun searchNews(query: String?) {
    searchQuery = query
    supportFragmentManager.fragments?.filterIsInstance<OnSearchListener>()?.forEach {
      it.onSearch(query)
    }
  }

  private fun saveInstanceState(outState: Bundle?) {
    outState?.putLong(SELECTED_NEWS_ID, currentNewsId)
    outState?.putString(SELECTED_NEWS_TITLE, currentNewsTitle)
    outState?.putString(SEARCH_QUERY, searchQuery)
  }

  private fun restoreSelectedNews(savedInstanceState: Bundle?) {
    currentNewsId = savedInstanceState?.getLong(SELECTED_NEWS_ID) ?: 0L
    currentNewsTitle = savedInstanceState?.getString(SELECTED_NEWS_TITLE)
    searchQuery = savedInstanceState?.getString(SEARCH_QUERY) ?: searchQuery
    if (isTwoPanes) {
      onNewsSelected(currentNewsId, currentNewsTitle)
    }
  }

  private fun showNewsDetailFragment(newsId: Long, title: String?) {
    val fragment = NewsDetailFragment.newInstance(newsId, title)
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.newsDetailFragment, fragment)
    transaction.commit()
  }
}
