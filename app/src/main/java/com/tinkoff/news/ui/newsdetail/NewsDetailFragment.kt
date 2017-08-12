package com.tinkoff.news.ui.newsdetail

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.tinkoff.news.R
import com.tinkoff.news.ui.base.view.BaseFragment
import com.tinkoff.news.utils.getSimpleName
import com.tinkoff.news.utils.gone
import com.tinkoff.news.utils.visible
import kotlinx.android.synthetic.main.fragment_news_detail.*
import org.jetbrains.anko.textResource

class NewsDetailFragment : BaseFragment(), NewsDetailPresenter.View {

  companion object {

    val EXTRA_NEWS_ID = getSimpleName() + "." + "newsId"
    val EXTRA_TITLE = getSimpleName() + "." + "title"

    fun newInstance(newsId: Long, title: String?): NewsDetailFragment {
      val fragment = NewsDetailFragment()
      val bundle = Bundle()
      bundle.putLong(EXTRA_NEWS_ID, newsId)
      bundle.putString(EXTRA_TITLE, title)
      fragment.arguments = bundle
      return fragment
    }
  }

  @InjectPresenter lateinit var presenter: NewsDetailPresenter

  @ProvidePresenter
  fun providePresenter(): NewsDetailPresenter {
    val newsId = arguments?.getLong(EXTRA_NEWS_ID) ?: 0L
    val title = arguments?.getString(EXTRA_TITLE)
    return NewsDetailPresenter(newsId, title)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_news_detail, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initViews()
    presenter.loadNewsDetail()
  }

  /** MVP methods */

  override fun showLoading() {
    loadingView.visible()
    messageTextView.gone()
    contentTextView.gone()
  }

  override fun showContent() {
    loadingView.gone()
    messageTextView.gone()
    contentTextView.visible()
  }

  override fun showError() {
    loadingView.gone()
    messageTextView.visible()
    contentTextView.gone()
    messageTextView.textResource = R.string.news_detail_error
  }

  override fun showSelectNews() {
    loadingView.gone()
    messageTextView.visible()
    contentTextView.gone()
    messageTextView.textResource = R.string.news_detail_select
  }

  override fun showNewsDetail(title: String?, content: String?) {
    val htmlTitle = if (title != null) Html.fromHtml(title) else null
    titleTextView.text = htmlTitle
    val htmlContent = if (content != null) Html.fromHtml(content) else null
    contentTextView.text = htmlContent
  }

  /** Private methods */

  private fun initViews() {
    contentTextView.movementMethod = LinkMovementMethod.getInstance()
  }
}
