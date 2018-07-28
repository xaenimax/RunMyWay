package com.udacity.xaenimax.runmyway.ui.configurationlist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.entity.Configuration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfigurationListAdapter extends RecyclerView.Adapter<ConfigurationListAdapter.ViewHolder>{
    private List<Configuration> mConfigurationList;
    private ConfigurationListAdapterListener mListener;

    public ConfigurationListAdapter(List<Configuration> configurations, ConfigurationListAdapterListener listener){
        mConfigurationList = configurations;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.configuration_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onViewClicked(view);
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Configuration configuration = mConfigurationList.get(position);
        holder.bindData(configuration);
    }

    @Override
    public int getItemCount() {
        return mConfigurationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.configuration_title_tv)
        TextView configurationTitleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(Configuration configuration) {
            configurationTitleTextView.setText(configuration.name);
        }
    }

    public interface ConfigurationListAdapterListener{
        void onViewClicked(View view);
    }
}
