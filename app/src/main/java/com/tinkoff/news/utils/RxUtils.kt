package com.tinkoff.news.utils

import com.tinkoff.news.api.ApiResponse
import com.tinkoff.news.data.ApiException
import io.reactivex.SingleTransformer

object RxUtils {

  fun <T : ApiResponse> checkResultCode(): SingleTransformer<T, T> = SingleTransformer {
    it.doOnSuccess {
      val resultCode = it.resultCode
      if (resultCode == null || resultCode != "OK") {
        throw ApiException(resultCode)
      }
    }
  }
}