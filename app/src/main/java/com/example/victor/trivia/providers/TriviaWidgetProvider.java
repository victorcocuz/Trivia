package com.example.victor.trivia.providers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.victor.trivia.R;
import com.example.victor.trivia.activities.GameActivity;
import com.example.victor.trivia.activities.MainActivity;
import com.example.victor.trivia.services.TriviaWidgetService;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class TriviaWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String description,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.trivia_widget_provider);
        views.setTextViewText(R.id.widget_tv_title, context.getString(R.string.widget_did_you_know));
        views.setTextViewText(R.id.widget_tv_show_more, context.getString(R.string.widget_show_me_more));
        views.setTextViewText(R.id.widget_tv_ask_more, context.getString(R.string.widget_ask_me_something));

        //Create intent to launch Game Activity when clicked
        Intent activityIntent = new Intent (context, MainActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_tv_ask_more, activityPendingIntent);

        //Create intent to update description when clicked
        Intent descriptionIntent = new Intent(context, TriviaWidgetService.class);
        descriptionIntent.setAction(TriviaWidgetService.ACTION_UPDATE_DESCRIPTION);
        PendingIntent descriptionUpdatePendingIntent = PendingIntent.getService(context, 0, descriptionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_tv_show_more, descriptionUpdatePendingIntent);

        //Set description
        views.setTextViewText(R.id.widget_tv_description, description);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        TriviaWidgetService.updateDescription(context);
    }

    public static void updateTriviaWidgets(Context context, AppWidgetManager appWidgetManager, String description, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Timber.e("SHOW ME MORE %s", description);
            updateAppWidget(context, appWidgetManager, description, appWidgetId);
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

