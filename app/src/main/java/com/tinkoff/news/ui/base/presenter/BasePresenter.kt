package com.tinkoff.news.ui.base.presenter

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import com.tinkoff.news.di.PresenterInjector
import io.reactivex.FlowableTransformer
import io.reactivex.MaybeTransformer
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

open class BasePresenter<V : MvpView> : MvpPresenter<V>(), PresenterInjector {

  private val compositeDisposable = CompositeDisposable()

  protected fun addDisposable(disposable: Disposable) {
    compositeDisposable.add(disposable)
  }

  protected fun removeDisposable(disposable: Disposable) {
    compositeDisposable.remove(disposable)
  }

  override fun detachView(view: V) {
    super.detachView(view)
    compositeDisposable.clear()
  }

  fun <T> setMaybeSchedulers(): MaybeTransformer<T, T> = MaybeTransformer {
    it
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  fun <T> setSingleSchedulers(): SingleTransformer<T, T> = SingleTransformer {
    it
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  fun <T> setFlowableSchedulers(): FlowableTransformer<T, T> = FlowableTransformer {
    it
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }
}
