package com.udacity.xaenimax.runmyway.ui.news;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.dao.AppExecutor;
import com.udacity.xaenimax.runmyway.model.entity.Doc;
import com.udacity.xaenimax.runmyway.model.entity.NewsResponse;
import com.udacity.xaenimax.runmyway.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity {
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";

    @BindView(R.id.news_list_rv)
    RecyclerView newsList;
    @BindView(R.id.data_loader_pb)
    public ProgressBar dataLoaderProgressBar;

    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable mLayoutParcelable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        if(savedInstanceState != null && savedInstanceState.containsKey(RECYCLER_VIEW_STATE)){
            mLayoutParcelable = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mLayoutManager.onRestoreInstanceState(mLayoutParcelable);
        }

        newsList.setLayoutManager(mLayoutManager);

        loadData();

    }

    private void loadData() {
        new DownloadNewsAsyncTask().execute();
    }

    protected void onSaveInstanceState(Bundle outState) {
        mLayoutParcelable = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_STATE, mLayoutParcelable);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(RECYCLER_VIEW_STATE)) {
            mLayoutParcelable = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mLayoutManager.onRestoreInstanceState(mLayoutParcelable);
        }
    }

    class DownloadNewsAsyncTask extends AsyncTask<Void, Void, List<Doc>> {

        @Override
        protected List<Doc> doInBackground(Void... strings) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataLoaderProgressBar.setVisibility(View.VISIBLE);
                }
            });

            String response = null;
            NewsResponse newsResponse = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                newsResponse = new Gson().fromJson(response, NewsResponse.class);
                return newsResponse.response.docs;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Doc> docs) {
            super.onPostExecute(docs);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataLoaderProgressBar.setVisibility(View.GONE);
                }
            });
            if(docs != null) {
                NewsAdapter adapter = new NewsAdapter(docs);
                newsList.setAdapter(adapter);
            } else {
                Snackbar.make(newsList, getString(R.string.connection_error), Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
