package com.tinkoff.news.data.mapper;

import com.tinkoff.news.api.ApiNewsDetail;
import com.tinkoff.news.data.News;
import com.tinkoff.news.data.NewsDetail;
import com.tinkoff.news.db.DbNewsDetail;
import com.tinkoff.news.db.IDbNews;
import com.tinkoff.news.db.IDbNewsDetail;
import com.tinkoff.news.di.ApplicationScope;
import io.reactivex.Maybe;
import javax.inject.Inject;

@ApplicationScope public class NewsDetailMapper {

  private NewsMapper newsMapper;

  @Inject NewsDetailMapper(NewsMapper newsMapper) {
    this.newsMapper = newsMapper;
  }

  public Maybe<NewsDetail> mapMaybe(ApiNewsDetail newsDetail) {
    NewsDetail news = map(newsDetail);
    if (news == null) {
      return Maybe.empty();
    } else {
      return Maybe.just(news);
    }
  }

  private NewsDetail map(ApiNewsDetail newsDetail) {
    if (newsDetail != null) {
      News news = newsMapper.map(newsDetail.getNews());
      Long creationDate = null;
      if (newsDetail.getCreationDate() != null) {
        creationDate = newsDetail.getCreationDate().getMilliseconds();
      }
      Long lastModificationDate = null;
      if (newsDetail.getLastModificationDate() != null) {
        lastModificationDate = newsDetail.getLastModificationDate().getMilliseconds();
      }
      if (news != null
          && creationDate != null
          && lastModificationDate != null
          && newsDetail.getContent() != null
          && !newsDetail.getContent().isEmpty()
          && newsDetail.getBankInfoTypeId() != null
          && newsDetail.getTypeId() != null) {
        return new NewsDetail(news, creationDate, lastModificationDate, newsDetail.getContent(),
            newsDetail.getBankInfoTypeId(), newsDetail.getTypeId());
      }
    }
    return null;
  }

  public NewsDetail map(IDbNewsDetail newsDetail) {
    News news = newsMapper.map(newsDetail.getNews());
    return new NewsDetail(news, newsDetail.getCreationDate(), newsDetail.getLastModificationDate(),
        newsDetail.getContent(), newsDetail.getBankInfoTypeId(), newsDetail.getTypeId());
  }

  public IDbNewsDetail map(NewsDetail newsDetail) {
    IDbNews dbNews = newsMapper.map(newsDetail.getNews());
    DbNewsDetail dbNewsDetail = new DbNewsDetail();
    dbNewsDetail.setCreationDate(newsDetail.getCreationDate());
    dbNewsDetail.setLastModificationDate(newsDetail.getLastModificationDate());
    dbNewsDetail.setContent(newsDetail.getContent());
    dbNewsDetail.setBankInfoTypeId(newsDetail.getBankInfoTypeId());
    dbNewsDetail.setTypeId(newsDetail.getTypeId());
    dbNewsDetail.setNews(dbNews);
    return dbNewsDetail;
  }
}