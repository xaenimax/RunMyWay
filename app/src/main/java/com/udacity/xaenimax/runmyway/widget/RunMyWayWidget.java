package com.udacity.xaenimax.runmyway.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.xaenimax.runmyway.R;
import com.udacity.xaenimax.runmyway.model.RunMyWayRepository;
import com.udacity.xaenimax.runmyway.model.entity.RunSession;
import com.udacity.xaenimax.runmyway.ui.home.MainActivity;
import com.udacity.xaenimax.runmyway.utils.InjectorUtils;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class RunMyWayWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.run_my_way_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        loadData(context, views);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void loadData(Context context, RemoteViews views) {
        //open main activity when click on widget
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        //RunMyWayRepository repository = InjectorUtils.provideRepository(context);
        //List<RunSession> runSessions = repository.getRunSessions();

        Intent serviceIntent = new Intent(context, ListViewService.class);
        views.setRemoteAdapter(R.id.sessions_lv, serviceIntent);
        views.setPendingIntentTemplate(R.id.sessions_lv, pendingIntent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

