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

class NewsListActivity : BaseActivity(), OnNewsSelectedListener {

  private var isDualPane = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_news_list)
    initToolbar()
    isDualPane = determinePaneLayout()
    if (isDualPane) {
      // Show empty new detail fragment
      showNewsDetailFragment(0L, null)
    }
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

  override fun onNewsSelected(newsId: Long, title: String) {
    if (isDualPane) {
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

  private fun refresh() {
    supportFragmentManager.fragments.filterIsInstance<OnRefreshListener>().forEach {
      it.onRefresh()
    }
  }

  private fun determinePaneLayout(): Boolean {
    return findViewById<ViewGroup>(R.id.newsDetailFragment) != null
  }

  private fun showNewsDetailFragment(newsId: Long, title: String?) {
    val fragment = NewsDetailFragment.newInstance(newsId, title)
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.newsDetailFragment, fragment)
    transaction.commit()
  }
}
