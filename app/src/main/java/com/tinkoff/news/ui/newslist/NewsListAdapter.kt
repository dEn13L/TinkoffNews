package com.tinkoff.news.ui.newslist

import com.tinkoff.news.ui.base.adapter.AdapterConstants
import com.tinkoff.news.ui.base.adapter.BaseAdapter
import com.tinkoff.news.ui.base.adapter.deletages.NewsDelegateAdapter

class NewsListAdapter(
    newsListener: NewsDelegateAdapter.Listener
) : BaseAdapter() {

  init {
    delegateAdapters.put(AdapterConstants.NEWS, NewsDelegateAdapter(this, newsListener))
  }
}