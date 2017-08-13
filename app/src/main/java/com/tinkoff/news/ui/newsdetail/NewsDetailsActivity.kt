package com.tinkoff.news.ui.newsdetail

import android.content.Context
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.tinkoff.news.R
import com.tinkoff.news.data.News
import com.tinkoff.news.ui.base.view.BaseActivity
import com.tinkoff.news.utils.ZoomOutPageTransformer
import com.tinkoff.news.utils.getSimpleName
import com.tinkoff.news.utils.gone
import com.tinkoff.news.utils.visible
import kotlinx.android.synthetic.main.activity_news_detail.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textResource

class NewsDetailsActivity : BaseActivity(), NewsDetailsPresenter.View {

  companion object {

    val EXTRA_NEWS_POSITION = getSimpleName() + "." + "newsPosition"

    fun start(context: Context, position: Int) {
      val intent = context.intentFor<NewsDetailsActivity>(
          EXTRA_NEWS_POSITION to position
      )

      context.startActivity(intent)
    }
  }

  @InjectPresenter lateinit var presenter: NewsDetailsPresenter

  @ProvidePresenter
  fun providePresenter(): NewsDetailsPresenter {
    val startingPosition = intent?.getIntExtra(EXTRA_NEWS_POSITION, 0) ?: 0
    return NewsDetailsPresenter(startingPosition)
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
    messageTextView.gone()
    viewPager.visible()
    val pagerAdapter = NewsDetailPagerAdapter(supportFragmentManager, news)
    viewPager.adapter = pagerAdapter
    viewPager.currentItem = startingPosition
    viewPager.setPageTransformer(true, ZoomOutPageTransformer())
  }

  /** Private methods */

  private fun initToolbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.let {
      it.setDisplayHomeAsUpEnabled(true)
      it.setDisplayShowTitleEnabled(true)
    }
  }

  private fun navigateToUp() {
    val upIntent = NavUtils.getParentActivityIntent(this)
    if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot) {
      TaskStackBuilder.create(this)
          .addNextIntentWithParentStack(upIntent)
          .startActivities()
    } else {
      NavUtils.navigateUpTo(this, upIntent)
    }
  }
}
