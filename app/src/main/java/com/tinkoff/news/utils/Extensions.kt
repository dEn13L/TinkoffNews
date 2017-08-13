package com.tinkoff.news.utils

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
  return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun View.visible() {
  visibility = View.VISIBLE
}

fun View.invisible() {
  visibility = View.INVISIBLE
}

fun View.gone() {
  visibility = View.GONE
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.isGone() = visibility == View.GONE

fun View.isNotVisible() = visibility != View.VISIBLE

fun View.isNotInvisible() = visibility != View.INVISIBLE

fun View.isNotGone() = visibility != View.GONE

fun Any.getSimpleName(): String = this::class.java.simpleName

fun String?.fromHtml(): CharSequence {
  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
    return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
  } else {
    return Html.fromHtml(this)
  }

}
