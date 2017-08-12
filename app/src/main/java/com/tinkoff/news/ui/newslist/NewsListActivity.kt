package com.tinkoff.news.ui.newslist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.tinkoff.news.R
import com.tinkoff.news.ui.base.view.BaseActivity
import kotlinx.android.synthetic.main.activity_news_list.*
import kotlinx.android.synthetic.main.toolbar.toolbar

class NewsListActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_news_list)
    initToolbar()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_news_list, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.action_refresh -> consume { refresh() }
    else -> super.onOptionsItemSelected(item)
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
    (newsListFragment as NewsListFragment).refreshNews()
  }
}
