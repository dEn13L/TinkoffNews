package com.tinkoff.news.ui.base.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

abstract class BaseAdapter(
    val items: MutableList<ViewType> = mutableListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return delegateAdapters.get(viewType).onCreateViewHolder(parent)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, items[position])
  }

  override fun getItemViewType(position: Int): Int {
    return items[position].getViewType()
  }

  fun showItems(items: List<ViewType>) {
    this.items.clear()
    this.items.addAll(items)
    notifyDataSetChanged()
  }
}