package com.tinkoff.news.ui.newslist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.tinkoff.news.R
import com.tinkoff.news.ui.base.OnNewsSelectedListener
import com.tinkoff.news.ui.base.OnRefreshListener
import com.tinkoff.news.ui.base.view.BaseActivity
import com.tinkoff.news.ui.newsdetail.NewsDetailActivity
import com.tinkoff.news.ui.newsdetail.NewsDetailFragment
import kotlinx.android.synthetic.main.toolbar.*

const val SELECTED_NEWS_ID = "newsId"
const val SELECTED_NEWS_TITLE = "newsTitle"

class NewsListActivity : BaseActivity(), OnNewsSelectedListener {

  private var isTwoPanes = false
  private var currentNewsId = 0L
  private var currentNewsTitle: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.news_list_layout)
    initToolbar()
    isTwoPanes = isTwoPanesLayout()
    restoreSelectedNews(savedInstanceState)
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    outState?.putLong(SELECTED_NEWS_ID, currentNewsId)
    outState?.putString(SELECTED_NEWS_TITLE, currentNewsTitle)
    super.onSaveInstanceState(outState)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_news_list, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.action_refresh -> consume { refresh() }
    else -> super.onOptionsItemSelected(item)
  }

  /** @see OnNewsSelectedListener */

  override fun onNewsSelected(newsId: Long, title: String?) {
    currentNewsId = newsId
    if (isTwoPanes) {
      showNewsDetailFragment(newsId, title)
    } else {
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

  private fun isTwoPanesLayout(): Boolean {
    return findViewById<ViewGroup>(R.id.newsDetailFragment) != null
  }

  private fun refresh() {
    supportFragmentManager.fragments.filterIsInstance<OnRefreshListener>().forEach {
      it.onRefresh()
    }
  }

  private fun restoreSelectedNews(savedInstanceState: Bundle?) {
    currentNewsId = savedInstanceState?.getLong(SELECTED_NEWS_ID) ?: 0L
    currentNewsTitle = savedInstanceState?.getString(SELECTED_NEWS_TITLE)
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
