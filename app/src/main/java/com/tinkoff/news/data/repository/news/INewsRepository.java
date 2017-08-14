package com.tinkoff.news.data.repository.news;

import com.tinkoff.news.data.News;
import io.reactivex.Flowable;
import io.reactivex.Single;
import java.util.List;

public interface INewsRepository {

  Flowable<List<News>> getNews();

  Single<List<News>> refreshNews();

  Single<List<News>> queryNews(String query);

  void clearNews();
}