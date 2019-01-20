package com.example.victor.trivia.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.victor.trivia.R;
import com.example.victor.trivia.adapters.StatisticsAdapter;
import com.example.victor.trivia.data.TriviaContract.AnswersEntry;
import com.example.victor.trivia.databinding.FragmentStatisticsBinding;
import com.example.victor.trivia.objects.Answer;
import com.example.victor.trivia.utilities.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    //Main
    private String userId;
    private List<Answer> answers = new ArrayList<>();
    private Context context;
    private int numberOfCategories;

    //Loaders & adapters
    private static final int LOADER_ID_CURSOR_ANSWERED = 1;
    StatisticsAdapter statisticsAdapter;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentStatisticsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false);
        View rootView = binding.getRoot();

        //Shared Preferences
        if (getContext() != null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME_USER, Context.MODE_PRIVATE);
            userId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_USER_ID, Constants.CONSTANT_ANONYMUOS);
        }

        numberOfCategories = getResources().getStringArray(R.array.array_categories).length - 1;

        //Set up recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        binding.fragmentStatisticsRv.setLayoutManager(layoutManager);
        binding.fragmentStatisticsRv.setHasFixedSize(true);
        statisticsAdapter = new StatisticsAdapter(context);
        binding.fragmentStatisticsRv.setAdapter(statisticsAdapter);

        getLoaderManager().initLoader(LOADER_ID_CURSOR_ANSWERED, null, this).forceLoad();

        return rootView;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int i, @Nullable Bundle bundle) {
        switch (i) {
            case LOADER_ID_CURSOR_ANSWERED:
                String[] projection = {AnswersEntry.ANSWERS_STATUS, AnswersEntry.ANSWERS_CATEGORY};
                return new CursorLoader(context,
                        AnswersEntry.ANSWERS_URI,
                        projection,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_CURSOR_ANSWERED:
                Cursor cursor = (Cursor) data;
                int[] questionsPerCategory = new int[numberOfCategories];
                int[] questionsPerCategoryCorrect = new int[numberOfCategories];
                if (cursor != null && cursor.getCount() > 0) {
                    Timber.e("cursor count is %s", cursor.getCount());
                    //Add counters for each category for every answer and add counters for each correct answer
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        int category = cursor.getInt(cursor.getColumnIndex(AnswersEntry.ANSWERS_CATEGORY));
                        questionsPerCategory[category - 1]++;
                        if (cursor.getInt(cursor.getColumnIndex(AnswersEntry.ANSWERS_STATUS)) == 1) {
                            questionsPerCategoryCorrect[category - 1]++;
                        }
                    }

                    int[] questionsPositions = new int[numberOfCategories];
                    float[] questionsPerCategoryPercentage = new float[numberOfCategories];
                    DecimalFormat decimalFormat = new DecimalFormat("##.##");
                    for (int i = 0; i < numberOfCategories; i++) {
                        questionsPositions[i] = i;
                        if (questionsPerCategory[i] > 0) {
                            questionsPerCategoryPercentage[i] = Float.valueOf(decimalFormat.format((float) questionsPerCategoryCorrect[i] * 100 / (float) questionsPerCategory[i]));
                        } else {
                            questionsPerCategoryPercentage[i] = Float.valueOf(decimalFormat.format((float) 0));
                        }
                    }
                    for (int i = 0; i < numberOfCategories; i++) {
                        Timber.e("before categories is %s", getResources().getStringArray(R.array.array_categories)[i + 1]);
                        Timber.e("before position is %s", questionsPositions[i]);
                        Timber.e("before percentage is %s", questionsPerCategoryPercentage[i]);
                        Timber.e("before number answered is %s", questionsPerCategory[i]);
                    }


                    float temporaryQuestionPerCategoryPercentage;
                    int temporaryQuestionPerCategory, temporaryQuestionPosition;
                    for (int i = 0; i < numberOfCategories; i++) {
                        for (int j = i; j > 0; j--) {
                            if (questionsPerCategoryPercentage[j] > questionsPerCategoryPercentage[j - 1]) {
                                temporaryQuestionPerCategoryPercentage = questionsPerCategoryPercentage[j];
                                questionsPerCategoryPercentage[j] = questionsPerCategoryPercentage[j - 1];
                                questionsPerCategoryPercentage[j - 1] = temporaryQuestionPerCategoryPercentage;

                                temporaryQuestionPerCategory = questionsPerCategory[j];
                                questionsPerCategory[j] = questionsPerCategory[j - 1];
                                questionsPerCategory[j - 1] = temporaryQuestionPerCategory;

                                temporaryQuestionPosition = questionsPositions[j];
                                questionsPositions[j] = questionsPositions[j - 1];
                                questionsPositions[j - 1] = temporaryQuestionPosition;
                            } else if (questionsPerCategoryPercentage[j] == questionsPerCategoryPercentage[j - 1]) {
                                if (questionsPerCategory[j] > questionsPerCategory[j - 1]) {
                                    temporaryQuestionPerCategoryPercentage = questionsPerCategoryPercentage[j];
                                    questionsPerCategoryPercentage[j] = questionsPerCategoryPercentage[j - 1];
                                    questionsPerCategoryPercentage[j - 1] = temporaryQuestionPerCategoryPercentage;

                                    temporaryQuestionPerCategory = questionsPerCategory[j];
                                    questionsPerCategory[j] = questionsPerCategory[j - 1];
                                    questionsPerCategory[j - 1] = temporaryQuestionPerCategory;

                                    temporaryQuestionPosition = questionsPositions[j];
                                    questionsPositions[j] = questionsPositions[j - 1];
                                    questionsPositions[j - 1] = temporaryQuestionPosition;
                                }
                            }
                        }
                    }

                    statisticsAdapter.updateResults(questionsPositions, questionsPerCategory, questionsPerCategoryPercentage);

                    for (int i = 0; i < numberOfCategories; i++) {
                        Timber.e("category is %s", getResources().getStringArray(R.array.array_categories)[questionsPositions[i] + 1]);
                        Timber.e("position is %s", questionsPositions[i]);
                        Timber.e("percentage is %s", questionsPerCategoryPercentage[i]);
                        Timber.e("questions answered are %s", questionsPerCategory[i]);
                    }
                } else {
                    Timber.e("Cursor returned is null");
                }
                break;
            default:
                Timber.e("Could not return cursor");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
