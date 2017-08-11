package com.tinkoff.news

import android.app.Application
import android.content.Context
import com.squareup.leakcanary.LeakCanary
import com.tinkoff.news.di.application.AppComponent
import com.tinkoff.news.di.application.AppModule
import com.tinkoff.news.di.application.DaggerAppComponent
import timber.log.Timber

class TinkoffNewsApplication : Application() {

  companion object {

    lateinit var appComponent: AppComponent
    fun get(context: Context): TinkoffNewsApplication {
      return context.applicationContext as TinkoffNewsApplication
    }
  }

  override fun onCreate() {
    super.onCreate()
    initAppComponent()
    initTimber()
    initLeakCanary()
    initStetho()
  }

  private fun initAppComponent() {
    appComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()
  }

  private fun initTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  private fun initLeakCanary() {
    if (BuildConfig.DEBUG) {
      LeakCanary.install(this)
    }
  }

  private fun initStetho() {
    StethoInjector.injectStetho(this)
  }
}
