package com.udacity.xaenimax.runmyway.ui.addconfiguration;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.xaenimax.runmyway.R;

import com.udacity.xaenimax.runmyway.model.entity.ConfigurationStep;

import java.util.ArrayList;

import butterknife.BindView;
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
        ConfigurationStep step = mConfigurationSteps.get(position);
        holder.bindData(step);
    }

    @Override
    public int getItemCount() {
        return mConfigurationSteps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.activity_iv)
        ImageView activityImageView;

        @BindView(R.id.step_tv)
        TextView stepTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(ConfigurationStep step) {
            activityImageView.setImageResource(step.stepType.equals(ConfigurationStep.StepType.Walk) ? R.drawable.walker : R.drawable.runner);
            stepTextView.setText(String.format(activityImageView.getContext().getString(R.string.action_imageview)));
        }
    }
}
