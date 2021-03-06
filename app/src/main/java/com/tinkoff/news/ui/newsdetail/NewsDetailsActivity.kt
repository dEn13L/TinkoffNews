package com.tinkoff.news.ui.newsdetail

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.tinkoff.news.R
import com.tinkoff.news.data.News
import com.tinkoff.news.ui.base.view.BaseActivity
import com.tinkoff.news.ui.newslist.NewsListActivity
import com.tinkoff.news.utils.ZoomOutPageTransformer
import com.tinkoff.news.utils.getSimpleName
import com.tinkoff.news.utils.gone
import com.tinkoff.news.utils.visible
import kotlinx.android.synthetic.main.activity_news_detail.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.textResource

class NewsDetailsActivity : BaseActivity(), NewsDetailsPresenter.View {

  companion object {

    val EXTRA_NEWS_ID = getSimpleName() + "." + "newsId"

    fun start(context: Context, newsId: Long) {
      val intent = context.intentFor<NewsDetailsActivity>(
          EXTRA_NEWS_ID to newsId
      )

      context.startActivity(intent)
    }
  }

  @InjectPresenter lateinit var presenter: NewsDetailsPresenter

  @ProvidePresenter
  fun providePresenter(): NewsDetailsPresenter {
    val newsId = intent?.getLongExtra(EXTRA_NEWS_ID, 0) ?: 0
    return NewsDetailsPresenter(newsId)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (resources.getBoolean(R.bool.has_two_panes)) {
      presenter.enterTwoPaneMode()
    }
    setContentView(R.layout.activity_news_detail)
    initToolbar()
    if (savedInstanceState == null) {
      presenter.loadNews()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    android.R.id.home -> consume { navigateToUp() }
    else -> super.onOptionsItemSelected(item)
  }

  /** MVP methods */

  override fun showError() {
    viewPager.gone()
    messageTextView.visible()
    messageTextView.textResource = R.string.news_detail_error
  }

  override fun showNews(news: List<News>, startingPosition: Int) {
    val pagerAdapter = NewsDetailPagerAdapter(supportFragmentManager, news)
    viewPager.adapter = pagerAdapter
    viewPager.currentItem = startingPosition
    viewPager.setPageTransformer(true, ZoomOutPageTransformer())
    viewPager.onPageChangeListener {
      onPageSelected { position ->
        presenter.pageChanged(position)
      }
    }
  }

  override fun showNews(newsId: Long, title: String) {
    NewsListActivity.start(this, newsId, title)
    finish()
  }

  override fun showContent() {
    messageTextView.gone()
    viewPager.visible()
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
