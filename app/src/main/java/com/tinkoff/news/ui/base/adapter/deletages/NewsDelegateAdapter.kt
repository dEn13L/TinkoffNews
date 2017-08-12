package com.tinkoff.news.ui.base.adapter.deletages

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.tinkoff.news.R
import com.tinkoff.news.data.News
import com.tinkoff.news.ui.base.adapter.ViewType
import com.tinkoff.news.ui.base.adapter.ViewTypeDelegateAdapter
import com.tinkoff.news.utils.DateFormatter
import com.tinkoff.news.utils.fromHtml
import com.tinkoff.news.utils.inflate
import kotlinx.android.synthetic.main.list_item_news.view.*

class NewsDelegateAdapter(val listener: Listener) : ViewTypeDelegateAdapter {

  interface Listener {

    fun onNewsSelected(newsId: Long, title: String?, position: Int)
  }

  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    return ViewHolder(parent)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
    holder as ViewHolder
    holder.bind(item as News)
  }

  inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
      parent.inflate(R.layout.list_item_news)) {

    fun bind(news: News) = with(itemView) {
      titleTextView.text = news.text.fromHtml()
      dateTextView.text = DateFormatter.getNewsDate(context, news.publicationDate)

      setOnClickListener {
        listener.onNewsSelected(news.newsId, news.text, adapterPosition)
      }
    }
  }
}
