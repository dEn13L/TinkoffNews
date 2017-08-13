package com.tinkoff.news.ui.newsdetail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import com.tinkoff.news.data.News;
import java.util.List;

class NewsDetailPagerAdapter extends FragmentStatePagerAdapter {

  private List<News> news;
  private SparseArray<Fragment> fragments = new SparseArray<>();

  public NewsDetailPagerAdapter(FragmentManager fragmentManager, List<News> news) {
    super(fragmentManager);
    this.news = news;
  }

  @Override public Fragment getItem(int position) {
    News currentNews = news.get(position);
    long newsId = currentNews.getNewsId();
    String title = currentNews.getText();
    return NewsDetailFragment.newInstance(newsId, title);
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {
    Fragment fragment = (Fragment) super.instantiateItem(container, position);
    fragments.put(position, fragment);
    return fragment;
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    fragments.remove(position);
    super.destroyItem(container, position, object);
  }

  @Override public int getCount() {
    return news != null ? news.size() : 0;
  }
}