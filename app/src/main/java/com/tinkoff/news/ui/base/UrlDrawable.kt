package com.tinkoff.news.ui.base

import android.graphics.Canvas
import android.graphics.ColorFilter
import com.bumptech.glide.load.resource.drawable.GlideDrawable

class UrlDrawable : GlideDrawable() {

  var drawable: GlideDrawable? = null

  override fun setLoopCount(loopCount: Int) {
    drawable?.setLoopCount(loopCount)
  }

  override fun start() {
    drawable?.start()
  }

  override fun setAlpha(p0: Int) {
    drawable?.alpha = p0
  }

  override fun getOpacity(): Int {
    return drawable?.opacity ?: 0
  }

  override fun stop() {
    drawable?.stop()
  }

  override fun isRunning(): Boolean {
    return drawable?.isRunning ?: false
  }

  override fun isAnimated(): Boolean {
    return drawable?.isAnimated ?: false
  }

  override fun setColorFilter(p0: ColorFilter?) {
    drawable?.colorFilter = p0
  }

  override fun draw(canvas: Canvas) {
    drawable?.draw(canvas)
    drawable?.start()
  }
}