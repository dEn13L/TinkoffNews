package com.tinkoff.news.db

import android.content.Context
import com.tinkoff.news.di.ApplicationScope
import dagger.Module
import dagger.Provides
import io.requery.Persistable
import io.requery.android.sqlite.BuildConfig
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode

const val DB_NAME = "tinkoff_news.db"
const val DB_VERSION = 1

@Module
class DbModule {

  @Provides
  @ApplicationScope
  fun provideStore(context: Context): KotlinReactiveEntityStore<Persistable> {
    val source = DatabaseSource(context, Models.DEFAULT, DB_NAME, DB_VERSION)
    if (BuildConfig.DEBUG) {
      source.setLoggingEnabled(true)
      source.setTableCreationMode(TableCreationMode.DROP_CREATE)
    }
    return KotlinReactiveEntityStore(KotlinEntityDataStore(source.configuration))
  }
}