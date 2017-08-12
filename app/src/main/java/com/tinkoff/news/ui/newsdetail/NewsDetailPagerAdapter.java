package com.tinkoff.news.ui.newsdetail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import com.tinkoff.news.data.News;
import java.util.List;
import timber.log.Timber;

class NewsDetailPagerAdapter extends FragmentStatePagerAdapter {

  private SparseArray<Fragment> fragments = new SparseArray<>();
  private List<News> news;

  public NewsDetailPagerAdapter(FragmentManager fragmentManager, List<News> news) {
    super(fragmentManager);
    this.news = news;
  }

  // Register the fragment when the item is instantiated
  @Override public Object instantiateItem(ViewGroup container, int position) {
    Timber.i("instantiateItem: " + position);
    Fragment fragment = (Fragment) super.instantiateItem(container, position);
    fragments.put(position, fragment);
    return fragment;
  }

  // Unregister when the item is inactive
  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    Timber.i("destroyItem: " + position);
    fragments.remove(position);
    super.destroyItem(container, position, object);
  }

  @Override public Fragment getItem(int position) {
    Timber.i("getItem: " + position);
    Fragment item = fragments.get(position);
    if (item == null) {
      News currentNews = news.get(position);
      long newsId = currentNews.getNewsId();
      String title = currentNews.getText();
      item = NewsDetailFragment.Companion.newInstance(newsId, title);
    }
    return item;
  }

  @Override public int getCount() {
    return news.size();
  }
}