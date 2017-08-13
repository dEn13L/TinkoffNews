package com.tinkoff.news.ui.newslist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.tinkoff.news.R
import com.tinkoff.news.ui.base.OnRefreshListener
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
    val SELECTED_NEWS_POSITION = "newsPosition"

    fun start(context: Context, newsId: Long, title: String, position: Int) {
      val intent = context.intentFor<NewsListActivity>(
          SELECTED_NEWS_ID to newsId,
          SELECTED_NEWS_TITLE to title,
          SELECTED_NEWS_POSITION to position
      )
      context.startActivity(intent)
    }
  }

  private var isTwoPanes = false
  private var currentNewsId = 0L
  private var currentNewsTitle: String? = null
  private var currentPosition = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    Timber.i("onCreate")
    super.onCreate(savedInstanceState)
    setContentView(R.layout.news_list_layout)
    initToolbar()
    isTwoPanes = isTwoPanesLayout()
    restoreSelectedNews(savedInstanceState)
  }

  override fun onNewIntent(intent: Intent?) {
    Timber.i("onNewIntent")
    super.onNewIntent(intent)
    val bundle = intent?.extras
    restoreSelectedNews(bundle)
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    Timber.i("onSaveInstanceState")
    saveInstanceState(outState)
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

  /** @see NewsDelegateAdapter.Listener */

  override fun onNewsSelected(newsId: Long, title: String?, position: Int) {
    Timber.i("onNewsSelected newsId: $newsId, title: $title, position: $position")
    currentNewsId = newsId
    currentNewsTitle = title
    currentPosition = position
    if (isTwoPanes) {
      showNewsDetailFragment(newsId, title)
    } else {
      NewsDetailsActivity.start(this, position)
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
    supportFragmentManager.fragments?.filterIsInstance<OnRefreshListener>()?.forEach {
      it.onRefresh()
    }
  }

  private fun saveInstanceState(outState: Bundle?) {
    outState?.putLong(SELECTED_NEWS_ID, currentNewsId)
    outState?.putString(SELECTED_NEWS_TITLE, currentNewsTitle)
    outState?.putInt(SELECTED_NEWS_POSITION, currentPosition)
  }

  private fun restoreSelectedNews(savedInstanceState: Bundle?) {
    currentNewsId = savedInstanceState?.getLong(SELECTED_NEWS_ID) ?: 0L
    currentNewsTitle = savedInstanceState?.getString(SELECTED_NEWS_TITLE)
    currentPosition = savedInstanceState?.getInt(SELECTED_NEWS_POSITION) ?: 0
    Timber.i("restoreSelectedNews, $currentNewsId, $currentNewsTitle, $currentPosition")
    if (isTwoPanes) {
      onNewsSelected(currentNewsId, currentNewsTitle, currentPosition)
    }
  }

  private fun showNewsDetailFragment(newsId: Long, title: String?) {
    val fragment = NewsDetailFragment.newInstance(newsId, title)
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.newsDetailFragment, fragment)
    transaction.commit()
  }
}
