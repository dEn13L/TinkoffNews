package com.tinkoff.news.ui.newsdetail

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import com.tinkoff.news.R
import com.tinkoff.news.ui.base.view.BaseActivity
import com.tinkoff.news.ui.newslist.NewsListActivity
import com.tinkoff.news.utils.getSimpleName
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.intentFor

class NewsDetailActivity : BaseActivity() {

  companion object {

    val EXTRA_NEWS_ID = getSimpleName() + "." + "newsId"
    val EXTRA_TITLE = getSimpleName() + "." + "title"

    fun start(context: Context, newsId: Long, title: String?) {
      val intent = context.intentFor<NewsDetailActivity>(
          EXTRA_NEWS_ID to newsId,
          EXTRA_TITLE to title
      )
      context.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (resources.getBoolean(R.bool.has_two_panes)) {
      finish()
      return
    }
    setContentView(R.layout.activity_news_detail)
    initToolbar()
    if (savedInstanceState == null) {
      val newsId = intent.getLongExtra(EXTRA_NEWS_ID, 0)
      val title = intent.getStringExtra(EXTRA_TITLE)
      val fragment = NewsDetailFragment.newInstance(newsId, title)
      val transaction = supportFragmentManager.beginTransaction()
      transaction.add(R.id.newsDetailFragment, fragment)
      transaction.commit()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    android.R.id.home -> consume {
      navigateUpTo(intentFor<NewsListActivity>())
    }
    else -> super.onOptionsItemSelected(item)
  }

  /** Private methods */

  private fun initToolbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.let {
      it.setDisplayHomeAsUpEnabled(true)
      it.setDisplayShowTitleEnabled(true)
    }
  }
}
