package com.tinkoff.news.ui.base.presenter

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import com.tinkoff.news.di.PresenterInjector
import io.reactivex.*
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

  fun setCompletableSchedulersAndDisposable(): CompletableTransformer = CompletableTransformer {
    it
        .doOnSubscribe { addDisposable(it) }
        .compose(setCompletableSchedulers())
  }

  fun setCompletableSchedulers(): CompletableTransformer = CompletableTransformer {
    it
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  fun setCompletableDisposable(): CompletableTransformer = CompletableTransformer {
    it.doOnSubscribe { addDisposable(it) }
  }

  fun <T> setObservableSchedulersAndDisposable(): ObservableTransformer<T, T> = ObservableTransformer {
    it
        .doOnSubscribe { addDisposable(it) }
        .compose(setObservableSchedulers())
  }

  fun <T> setObservableSchedulers(): ObservableTransformer<T, T> = ObservableTransformer {
    it
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  fun <T> setObservableDisposable(): ObservableTransformer<T, T> = ObservableTransformer {
    it.doOnSubscribe { addDisposable(it) }
  }

  fun <T> setSingleSchedulersAndDisposable(): SingleTransformer<T, T> = SingleTransformer {
    it
        .doOnSubscribe { addDisposable(it) }
        .compose(setSingleSchedulers())
  }

  fun <T> setSingleSchedulers(): SingleTransformer<T, T> = SingleTransformer {
    it
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  fun <T> setSingleDisposable(): SingleTransformer<T, T> = SingleTransformer {
    it.doOnSubscribe { addDisposable(it) }
  }

  fun <T> setMaybeSchedulersAndDisposable(): MaybeTransformer<T, T> = MaybeTransformer {
    it
        .doOnSubscribe { addDisposable(it) }
        .compose(setMaybeSchedulers())
  }

  fun <T> setMaybeSchedulers(): MaybeTransformer<T, T> = MaybeTransformer {
    it
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }

  fun <T> setMaybeDisposable(): MaybeTransformer<T, T> = MaybeTransformer {
    it.doOnSubscribe { addDisposable(it) }
  }

  fun <T> setFlowableSchedulers(): FlowableTransformer<T, T> = FlowableTransformer {
    it
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
  }
}
