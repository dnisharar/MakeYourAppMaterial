package com.sedsoftware.xyzreader.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sedsoftware.xyzreader.R;
import com.sedsoftware.xyzreader.data.DataManager;
import javax.inject.Inject;
import timber.log.Timber;

@SuppressWarnings("ConstantConditions")
public class ArticleListActivity extends BaseActivity {

  @Inject
  DataManager dataManager;

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.app_bar)
  AppBarLayout appbar;
  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  @BindView(R.id.swipe_refresh_layout)
  SwipeRefreshLayout swipeRefreshLayout;

  private ArticlesAdapter adapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getActivityComponent().inject(this);

    setContentView(R.layout.activity_article_list);
    ButterKnife.bind(this);

    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    int columnsCount = getResources().getInteger(R.integer.list_column_count);

    StaggeredGridLayoutManager sglm =
        new StaggeredGridLayoutManager(columnsCount, StaggeredGridLayoutManager.VERTICAL);

    adapter = new ArticlesAdapter();
    adapter.setHasStableIds(true);

    recyclerView.setLayoutManager(sglm);
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(adapter);

    if (savedInstanceState == null) {
      dataManager.syncArticles().subscribe();
    }

    dataManager.getArticlesObservableStream()
        .doOnSubscribe(disposable -> adapter.clearList())
        .doOnNext(article -> Timber.tag("myxyzreader").d("Fetch article from db: " + article.title()))
        .subscribe(article -> adapter.addArticle(article));
  }
}
