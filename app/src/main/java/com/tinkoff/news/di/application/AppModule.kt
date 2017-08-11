package com.tinkoff.news.di.application

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tinkoff.news.di.ApplicationScope
import dagger.Module
import dagger.Provides

@Module class AppModule(private val application: Application) {

  @Provides @ApplicationScope fun provideContext(): Context {
    return application.applicationContext
  }

  @Provides @ApplicationScope fun provideGson(): Gson {
    return GsonBuilder()
        .create()
  }
}