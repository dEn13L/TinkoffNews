package com.tinkoff.news.data

class ApiException(
    val resultCode: String?
) : RuntimeException("Tinkoff Api exception. resultCode: $resultCode")