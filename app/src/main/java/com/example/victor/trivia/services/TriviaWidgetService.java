package com.example.victor.trivia.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.example.victor.trivia.data.TriviaContract;
import com.example.victor.trivia.providers.TriviaWidgetProvider;

import java.util.Random;

import timber.log.Timber;

/******
 * Created by Victor on 1/22/2019.
 ******/
public class TriviaWidgetService extends IntentService {
    public static final String ACTION_UPDATE_DESCRIPTION = "com.example.victor.trivia.services.action.update_description";

    public TriviaWidgetService() {
        super("TriviaWidgetService");
    }

    public static void updateDescription(Context context) {
        Intent intent = new Intent(context, TriviaWidgetService.class);
        intent.setAction(ACTION_UPDATE_DESCRIPTION);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_DESCRIPTION.equals(action)) {
                handleActionUpdateDescription();
            }
        }
    }

    private void handleActionUpdateDescription() {
        String description = null;
        String[] projection = {TriviaContract.QuestionsEntry.QUESTIONS_ANSWER_DESCRIPTION};
        Cursor cursor = getContentResolver().query(TriviaContract.QuestionsEntry.QUESTIONS_URI,
                projection,
                null,
                null,
                null);
        Random random = new Random();

        if (cursor != null) {
            cursor.moveToPosition(random.nextInt(cursor.getCount()));
            description = cursor.getString(cursor.getColumnIndex(TriviaContract.QuestionsEntry.QUESTIONS_ANSWER_DESCRIPTION));
            cursor.close();
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetids = appWidgetManager.getAppWidgetIds(new ComponentName(this, TriviaWidgetProvider.class));

        //Update all widgets
        TriviaWidgetProvider.updateTriviaWidgets(this, appWidgetManager, description, appWidgetids);
    }
}
