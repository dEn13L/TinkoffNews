package com.tinkoff.news.utils

import android.content.Context
import com.tinkoff.news.R
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

  fun getNewsDate(context: Context, timeInMillis: Long): String {
    val date = context.getString(R.string.news_date_format)
    return getSimpleDateFormat(date).format(timeInMillis)
  }

  private fun getSimpleDateFormat(format: String): SimpleDateFormat {
    return SimpleDateFormat(format, Locale.getDefault())
  }
}