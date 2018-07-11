package com.udacity.xaenimax.runmyway.ui.addconfiguration;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.xaenimax.runmyway.R;

import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ConfigurationStepAdapter extends RecyclerView.Adapter<ConfigurationStepAdapter.ViewHolder> {
    ArrayList<ConfigurationStep> mConfigurationSteps;
    public ConfigurationStepAdapter(ArrayList<ConfigurationStep> configurationSteps){
        mConfigurationSteps = configurationSteps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.configuration_step_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mConfigurationSteps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
