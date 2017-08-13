package com.tinkoff.news.ui.base.view

import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import com.arellomobile.mvp.MvpAppCompatActivity

abstract class BaseActivity : MvpAppCompatActivity() {

  inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
  }

  protected fun navigateToUp() {
    val upIntent = NavUtils.getParentActivityIntent(this)
    if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot) {
      TaskStackBuilder.create(this)
          .addNextIntentWithParentStack(upIntent)
          .startActivities()
    } else {
      NavUtils.navigateUpTo(this, upIntent)
    }
  }
}