package com.tinkoff.news.api

import android.content.Context
import com.tinkoff.news.BuildConfig
import com.tinkoff.news.R
import com.tinkoff.news.di.ApplicationScope
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

const val URL = "URL"

@Module class ApiModule {

  @Provides @Named(URL) @ApplicationScope fun provideApiUrl(context: Context): String {
    return context.getString(R.string.tinkoff_news_api_url)
  }

  @Provides @ApplicationScope fun provideHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = if (BuildConfig.DEBUG) BODY else NONE

    return OkHttpClient.Builder().addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
  }

  @Provides @ApplicationScope fun provideRetrofit(okHttpClient: OkHttpClient,
      @Named(URL) apiUrl: String): Retrofit {
    return Retrofit.Builder().baseUrl(apiUrl)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
  }

  @Provides @ApplicationScope fun provideApi(retrofit: Retrofit): ApiModule {
    return retrofit.create<ApiModule>(ApiModule::class.java)
  }
}