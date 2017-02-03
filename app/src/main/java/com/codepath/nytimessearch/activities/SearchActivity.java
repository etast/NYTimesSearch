package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.nytimessearch.EndlessRecyclerViewScrollListener;
import com.codepath.nytimessearch.ItemClickSupport;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapter.ArticleAdapter;
import com.codepath.nytimessearch.fragments.AdvSrchOptsDialogFragment;
import com.codepath.nytimessearch.model.Article;
import com.codepath.nytimessearch.model.SearchSettings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

public class SearchActivity extends AppCompatActivity implements AdvSrchOptsDialogFragment.AdvSrchOptsDialogListener {
    RecyclerView rvResults;

    ArrayList<Article> articles;
    ArticleAdapter adapter;
    Handler handler;
    AsyncHttpClient client;

    final String URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    private EndlessRecyclerViewScrollListener scrollListener;
    private Integer page = 0;
    private Integer maxPages = 100;
    private String currentQuery;
    private SearchSettings searchSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handler = new Handler();
        client = new AsyncHttpClient();
        searchSettings = new SearchSettings();
        setupViews();
    }

    public void setupViews() {
        rvResults = (RecyclerView) findViewById(R.id.rvResults);
        articles = new ArrayList<>();
        adapter = new ArticleAdapter(this, articles);
        rvResults.setAdapter(adapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rvResults.setLayoutManager(gridLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("onLoadMore", String.format("%d", page));
                handler.postDelayed(loadDataFromApi, 1000);
            }
        };
        rvResults.addOnScrollListener(scrollListener);
        ItemClickSupport.addTo(rvResults).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);
                startActivity(i);
            }
        });
    }
    public Runnable loadDataFromApi = new Runnable() {
        @Override
        public void run() {
            Log.d("page: ", page.toString());
            Log.d("currentQuery: ", currentQuery);
            Log.d("maxPages: ", maxPages.toString());
            if (page < maxPages) {
                RequestParams params = new RequestParams();
                params.put("api-key", "ab9c232decde4a36a49426f2bdcd4c16");
                params.put("page", page.toString());
                params.put("q", currentQuery);

                if (!TextUtils.isEmpty(searchSettings.getmSortOrder())) {
                    params.put("sort", searchSettings.getmSortOrder().toLowerCase());
                }

                if (searchSettings.isCalendarSet()) {
                    params.put("begin_date", searchSettings.getSearchDate());
                }
                ArrayList newsDesk = new ArrayList();
                // StringBuilder newsDesk = new StringBuilder();

                if (searchSettings.isArtsFilterOn()) {
                    newsDesk.add("\"Arts\"");
                }

                if (searchSettings.isSportsFilterOn()) {
                    newsDesk.add("\"Sports\"");
                }

                if (searchSettings.isFashionFilterOn()) {
                    newsDesk.add("\"Fashion & Style\"");
                }

                if (newsDesk.size() > 0) {
                    params.put("fq", "news_desk:(" + android.text.TextUtils.join(" ", newsDesk) +")");
                    Log.d("fq=", "news_desk:(" + android.text.TextUtils.join(" ", newsDesk) +")");
                }
                Log.d("params", params.toString());

                client.get(URL, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        JSONArray articleJsonResults = null;
                        try {
                            int currentSize = adapter.getItemCount();
                            Log.d("CurrentSize", String.format("%d", currentSize));
                            if (page == 0) {
                                int hits = response.getJSONObject("response").getJSONObject("meta").getInt("hits") / 10;
                                if (hits < maxPages) {
                                    maxPages = hits;
                                    Log.d("hits", String.format("%d", hits));
                                    if (hits < 2) {
                                        Toast.makeText(SearchActivity.this, "No more results to display", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                            articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                            Log.d("added: ", String.format("%d", articleJsonResults.length()));
                            articles.addAll(Article.fromJSONArray(articleJsonResults));
                            adapter.notifyItemRangeInserted(currentSize, articles.size() - 1);
                            page++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("StatusCode: ", String.format("%d", statusCode));
                        Log.d("DEBUG: ", errorResponse.toString());
                    }
                });
            } else {
                Toast.makeText(SearchActivity.this, "No more results to display", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                articles.clear();
                adapter.notifyDataSetChanged();
                scrollListener.resetState();
                currentQuery = query;
                page = 0;
                handler.postDelayed(loadDataFromApi, 150);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FragmentManager fm = getSupportFragmentManager();
            AdvSrchOptsDialogFragment advSrchOptsDialogFragment = AdvSrchOptsDialogFragment.newInstance(searchSettings);
            advSrchOptsDialogFragment.show(fm, "frag_adv_srch_tops");
            return true;
        }
        return true;
    }

    @Override
    public void onFinishAdvSrchOptsDialog(String inputText) {
        Toast.makeText(this, inputText, Toast.LENGTH_SHORT).show();
    }
}
