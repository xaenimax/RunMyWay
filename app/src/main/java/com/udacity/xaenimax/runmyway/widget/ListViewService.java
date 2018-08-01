package com.udacity.xaenimax.runmyway.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.entity.RunSession;
import com.udacity.xaenimax.runmyway.utils.InjectorUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ListViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewsRemoteFactory(this.getApplicationContext());
    }

    class ListViewsRemoteFactory implements RemoteViewsService.RemoteViewsFactory {
        private final String LOG_TAG = ListViewsRemoteFactory.class.getSimpleName();
        Context mContext;
        List<RunSession> myRunSessions = new ArrayList<>();

        ListViewsRemoteFactory(Context applicationContext) {
            mContext = applicationContext;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            RunMyWayRepository repository = InjectorUtils.provideRepository(mContext);
            myRunSessions = repository.getRunSessions();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return myRunSessions.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            Log.d(LOG_TAG, "Assigning values at index " + i);
            RunSession session = myRunSessions.get(i);

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.configuration_step_item);

            DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();// new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            String strDate = dateFormat.format(session.sessionDate);
            String minutes =
                    String.format(Locale.getDefault(),"%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes(session.duration),
                            TimeUnit.MILLISECONDS.toSeconds(session.duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(session.duration))
                    );
            views.setTextViewText(R.id.step_tv,
                    String.format(getString(R.string.home_session_resume),
                            strDate,
                            minutes,
                            session.calories,
                            session.distance)
            );

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
