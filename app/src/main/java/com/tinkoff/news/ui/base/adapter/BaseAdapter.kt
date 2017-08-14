package com.tinkoff.news.ui.base.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

abstract class BaseAdapter(
    var items: List<ViewType>? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), QueryProvider {

  var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
  private var filterQuery: String? = null

  override fun getItemCount(): Int {
    return items?.size ?: 0
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return delegateAdapters.get(viewType).onCreateViewHolder(parent)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, items!![position])
  }

  override fun getItemViewType(position: Int): Int {
    return items!![position].getViewType()
  }

  override fun getQuery(): String? {
    return filterQuery
  }

  fun showItems(items: List<ViewType>, query: String? = null) {
    this.filterQuery = query
    this.items = items
    notifyDataSetChanged()
  }
}