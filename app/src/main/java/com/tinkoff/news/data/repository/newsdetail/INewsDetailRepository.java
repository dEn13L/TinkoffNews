package com.tinkoff.news.data.repository.newsdetail;

import com.tinkoff.news.data.NewsDetail;
import io.reactivex.Flowable;

public interface INewsDetailRepository {

  Flowable<NewsDetail> getNewsDetail(Long newsId);
}