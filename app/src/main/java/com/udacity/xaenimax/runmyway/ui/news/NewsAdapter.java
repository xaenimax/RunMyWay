package com.udacity.xaenimax.runmyway.ui.news;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.entity.Doc;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{
    List<Doc> mNewsList;

    public NewsAdapter(List<Doc> news){
        mNewsList = news;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        Doc news = mNewsList.get(position);
        holder.bindData(news);
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.news_title_tv)
        public TextView newsTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(Doc news) {
            newsTextView.setText(news.snippet.length() > 0 ? news.snippet : newsTextView.getContext().getString(R.string.no_snippet));
            newsTextView.append("\n");
        }
    }
}
