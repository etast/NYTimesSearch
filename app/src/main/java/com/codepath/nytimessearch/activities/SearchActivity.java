package com.codepath.nytimessearch.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.nytimessearch.EndlessRecyclerViewScrollListener;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapter.ArticleAdapter;
import com.codepath.nytimessearch.model.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    RecyclerView rvResults;

    ArrayList<Article> articles;
    ArticleAdapter adapter;
    Handler handler;
    AsyncHttpClient client;

    final String URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    private EndlessRecyclerViewScrollListener scrollListener;
    private Integer page = 0;
    private String currentQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handler = new Handler();
        client = new AsyncHttpClient();
        setupViews();
    }

    public void setupViews() {
        rvResults = (RecyclerView) findViewById(R.id.rvResults);
        articles = new ArrayList<>();
        adapter = new ArticleAdapter(this, articles);
        rvResults.setAdapter(adapter);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvResults.setLayoutManager(gridLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("onLoadMore", String.format("%d", page));
                handler.postDelayed(loadDataFromApi, 1000);
            }
        };
        rvResults.addOnScrollListener(scrollListener);
        /*
        rvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);
                startActivity(i);
            }
        });
        */
    }
    public Runnable loadDataFromApi = new Runnable() {
        @Override
        public void run() {
            Log.d("page: ", page.toString());
            Log.d("currentQuery: ", currentQuery);
            RequestParams params = new RequestParams();
            params.put("api-key", "ab9c232decde4a36a49426f2bdcd4c16");
            params.put("page", page.toString());
            params.put("q", currentQuery);
            client.get(URL, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray articleJsonResults = null;
                    try {
                        int currentSize = adapter.getItemCount();
                        Log.d("CurrentSize", String.format("%d", currentSize));
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
            return true;
        }
        return true;
//        return super.onOptionsItemSelected(item);
    }
}
