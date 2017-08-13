package com.tinkoff.news.ui.base.adapter

import android.support.v7.util.DiffUtil

class ViewTypesDiffCallback(
    private var oldItems: List<ViewType>,
    private var newItems: List<ViewType>
) : DiffUtil.Callback() {

  override fun getOldListSize(): Int {
    return oldItems.size
  }

  override fun getNewListSize(): Int {
    return newItems.size
  }

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldItems[oldItemPosition] == newItems[newItemPosition]
  }

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    return oldItems[oldItemPosition] == newItems[newItemPosition]
  }
}