package com.udacity.xaenimax.runmyway.ui.completedsession;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.entity.RunSession;
import com.udacity.xaenimax.runmyway.ui.home.MainActivity;
import com.udacity.xaenimax.runmyway.utils.InjectorUtils;
import com.udacity.xaenimax.runmyway.viewmodel.MainViewModel;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompletedSessionActivity extends AppCompatActivity {
    @BindView(R.id.goal_tv)
    public TextView goalTextView;
    @BindView(R.id.go_home_button)
    public Button goHomeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_session);
        ButterKnife.bind(this);
        setupViewModel();
        setupListeners();
    }

    private void setupListeners() {
        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompletedSessionActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void setupViewModel() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getRunSession().observe(this, new Observer<RunSession>() {
            @Override
            public void onChanged(@Nullable RunSession runSession) {
                if (runSession != null) {
                    String minutes =
                            String.format(Locale.getDefault(),"%d:%d",
                                    TimeUnit.MILLISECONDS.toMinutes(runSession.duration),
                                    TimeUnit.MILLISECONDS.toSeconds(runSession.duration) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runSession.duration))
                            );

                    goalTextView.setText(
                            String.format(getString(R.string.completed_session_resume),
                                    minutes,
                                    runSession.calories,
                                    runSession.distance)
                    );
                }
            }
        });
    }
}
