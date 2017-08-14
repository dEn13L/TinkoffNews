package com.tinkoff.news.ui.base

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.ViewTarget
import com.tinkoff.news.R

class GlideImageGetter(
    private val context: Context,
    private val textView: TextView
) : Html.ImageGetter, Drawable.Callback {

  private val targets = mutableSetOf<ImageGetterViewTarget>()

  fun clear() {
    val prev = get(textView) ?: return
    for (target in prev.targets) {
      Glide.clear(target)
    }
  }

  init {
    clear()
    textView.setTag(R.id.drawable_callback_tag, this)
  }

  override fun getDrawable(url: String): Drawable {
    val urlDrawable = UrlDrawable()
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(ImageGetterViewTarget(textView, urlDrawable))
    return urlDrawable
  }

  override fun invalidateDrawable(who: Drawable) {
    textView.invalidate()
  }

  override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
  }

  override fun unscheduleDrawable(who: Drawable, what: Runnable) {
  }

  private inner class ImageGetterViewTarget constructor(
      view: TextView,
      private val drawable: UrlDrawable
  ) : ViewTarget<TextView, GlideDrawable>(view) {

    init {
      targets.add(this)
    }

    override fun onResourceReady(resource: GlideDrawable?,
        glideAnimation: GlideAnimation<in GlideDrawable>) {
      val view = getView()
      if (resource != null && view != null) {
        val rect: Rect
        if (resource.intrinsicWidth > 100) {
          val width: Float
          val height: Float
          if (resource.intrinsicWidth >= view.width) {
            val downScale = resource.intrinsicWidth.toFloat() / view.width
            width = resource.intrinsicWidth.toFloat() / downScale
            height = resource.intrinsicHeight.toFloat() / downScale
          } else {
            val multiplier = view.width.toFloat() / resource.intrinsicWidth
            width = resource.intrinsicWidth.toFloat() * multiplier
            height = resource.intrinsicHeight.toFloat() * multiplier
          }
          rect = Rect(0, 0, Math.round(width), Math.round(height))
        } else {
          rect = Rect(0, 0, resource.intrinsicWidth * 2, resource.intrinsicHeight * 2)
        }
        resource.bounds = rect
        drawable.bounds = rect
        drawable.drawable = resource

        if (resource.isAnimated) {
          drawable.callback = get(view)
          resource.setLoopCount(GlideDrawable.LOOP_FOREVER)
          resource.start()
        }
        view.text = view.text
        view.invalidate()
      }
    }

    private var request: Request? = null

    override fun getRequest(): Request? {
      return request
    }

    override fun setRequest(request: Request?) {
      this.request = request
    }
  }

  companion object {
    operator fun get(view: View): GlideImageGetter? {
      return view.getTag(R.id.drawable_callback_tag) as? GlideImageGetter
    }
  }
}