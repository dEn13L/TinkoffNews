package com.tinkoff.news.utils

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan

object SpannableUtils {

  fun highlightBackground(context: Context, text: CharSequence, target: String?,
      highlightColorRes: Int): Spannable {
    val highlightColor = ContextCompat.getColor(context, highlightColorRes)
    val backgroundSpan = BackgroundColorSpan(highlightColor)
    return applySpans(text, target, false, backgroundSpan)
  }

  fun applySpans(text: CharSequence, target: String?, vararg spans: Any): Spannable {
    val textSpannable = SpannableStringBuilder(text)

    if (target != null && target.isNotEmpty() && spans.isNotEmpty()) {
      val textLength = textSpannable.length
      val targetLength = target.length
      var lastIndex = textSpannable.indexOf(target, 0, true)

      while (lastIndex >= 0) {
        val start = lastIndex
        val end = start + targetLength
        spans.forEach { span ->
          textSpannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        if (lastIndex < textLength - targetLength) {
          lastIndex = textSpannable.indexOf(target, lastIndex + targetLength, true)
        } else {
          lastIndex = -1
        }
      }
    }

    return textSpannable
  }
}