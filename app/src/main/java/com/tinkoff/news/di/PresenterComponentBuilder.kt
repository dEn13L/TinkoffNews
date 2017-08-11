package com.tinkoff.news.di

import dagger.MembersInjector

interface PresenterComponentBuilder<M : PresenterModule, C : PresenterComponent<out PresenterInjector>> {
  fun presenterModule(presenterModule: M): PresenterComponentBuilder<M, C>
  fun build(): C
}

interface PresenterComponent<P : PresenterInjector> : MembersInjector<P>

interface PresenterModule

interface PresenterInjector