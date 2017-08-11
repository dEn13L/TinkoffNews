package com.tinkoff.news

import android.app.Application
import com.facebook.stetho.Stetho

object StethoInjector {

  fun injectStetho(application: Application) {
    Stetho.initializeWithDefaults(application)
  }
}