package com.tinkoff.news.data.repository.news;

import com.tinkoff.news.data.News;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import java.util.List;

public interface INewsRepository {

  Flowable<List<News>> getNews();

  Maybe<List<News>> refreshNews();

  void clearNews();
}