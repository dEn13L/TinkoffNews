package com.tinkoff.news.ui.newsdetail

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.tinkoff.news.R
import com.tinkoff.news.ui.base.view.BaseActivity
import com.tinkoff.news.utils.getSimpleName
import com.tinkoff.news.utils.gone
import com.tinkoff.news.utils.visible
import kotlinx.android.synthetic.main.activity_news_detail.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.textResource
import timber.log.Timber

class NewsDetailActivity : BaseActivity(), NewsDetailPresenter.View {

  @InjectPresenter lateinit var presenter: NewsDetailPresenter

  companion object {

    val EXTRA_NEWS_ID = getSimpleName() + "." + "newsId"
    val EXTRA_TITLE = getSimpleName() + "." + "title"

    fun start(context: Context, newsId: Long, title: String) {
      val intent = context.intentFor<NewsDetailActivity>(
          EXTRA_NEWS_ID to newsId,
          EXTRA_TITLE to title
      )
      context.startActivity(intent)
    }
  }

  @ProvidePresenter
  fun providePresenter(): NewsDetailPresenter {
    val newsId = intent.getLongExtra(EXTRA_NEWS_ID, 0)
    val title = intent.getStringExtra(EXTRA_TITLE)
    return NewsDetailPresenter(newsId, title)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    Timber.i("onCreate: $")
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_news_detail)
    initToolbar()
    initViews()
    if (savedInstanceState == null) {
      presenter.loadNewsDetail()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    android.R.id.home -> consume { finish() }
    else -> super.onOptionsItemSelected(item)
  }

  /** MVP methods */

  override fun showLoading() {
    loadingView.visible()
    messageTextView.gone()
  }

  override fun showNewsDetail(title: String?, content: String?) {
    loadingView.gone()
    messageTextView.gone()
    titleTextView.text = title
    val htmlContent = if (content != null) Html.fromHtml(content) else null
    contentTextView.text = htmlContent
  }

  override fun showError() {
    loadingView.gone()
    messageTextView.visible()
    messageTextView.textResource = R.string.news_detail_error
  }

  /** Private methods */

  private fun initToolbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.let {
      it.setDisplayHomeAsUpEnabled(true)
      it.setDisplayShowTitleEnabled(false)
    }
  }

  private fun initViews() {
    contentTextView.movementMethod = LinkMovementMethod.getInstance()
  }
}
