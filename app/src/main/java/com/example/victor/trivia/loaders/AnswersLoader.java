package com.example.victor.trivia.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.victor.trivia.objects.Question;

import java.util.List;

/******
 * Created by Victor on 1/12/2019.
 ******/
public class AnswersLoader extends AsyncTaskLoader<List<Question>> {

    public AnswersLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public List<Question> loadInBackground() {
        return null;
    }
}
