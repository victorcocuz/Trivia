package com.example.victor.trivia.fragments;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("deprecation")
public class StatisticsFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    //Main
    private FragmentStatisticsBinding binding;
    private Context context;
    private int numberOfCategories;

    //Loaders & adapters
    private static final int LOADER_ID_CURSOR_ANSWERED = 1;
    private StatisticsAdapter statisticsAdapterHigh;
    private StatisticsAdapter statisticsAdapterMedium;
    private StatisticsAdapter statisticsAdapterLow;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialize main components
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false);
        View rootView = binding.getRoot();

        numberOfCategories = getResources().getStringArray(R.array.array_categories).length - 1;

        //Set up linear layout, adapters and recycler views
        RecyclerView.LayoutManager layoutManagerHigh = new LinearLayoutManager(context);
        RecyclerView.LayoutManager layoutManagerMedium = new LinearLayoutManager(context);
        RecyclerView.LayoutManager layoutManagerLow = new LinearLayoutManager(context);

        statisticsAdapterHigh = new StatisticsAdapter(context);
        statisticsAdapterMedium = new StatisticsAdapter(context);
        statisticsAdapterLow = new StatisticsAdapter(context);

        binding.fragmentStatisticsRvHighSkills.setLayoutManager(layoutManagerHigh);
        binding.fragmentStatisticsRvHighSkills.setHasFixedSize(true);
        binding.fragmentStatisticsRvHighSkills.setNestedScrollingEnabled(false);
        binding.fragmentStatisticsRvHighSkills.setAdapter(statisticsAdapterHigh);
        binding.fragmentStatisticsRvMediumSkills.setLayoutManager(layoutManagerMedium);
        binding.fragmentStatisticsRvMediumSkills.setHasFixedSize(true);
        binding.fragmentStatisticsRvMediumSkills.setAdapter(statisticsAdapterMedium);
        binding.fragmentStatisticsRvMediumSkills.setNestedScrollingEnabled(false);
        binding.fragmentStatisticsRvLowSkills.setLayoutManager(layoutManagerLow);
        binding.fragmentStatisticsRvLowSkills.setHasFixedSize(true);
        binding.fragmentStatisticsRvLowSkills.setNestedScrollingEnabled(false);
        binding.fragmentStatisticsRvLowSkills.setAdapter(statisticsAdapterLow);

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
                //noinspection ConstantConditions
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

                    //Add counters for each category for every answer and add counters for each correct answer
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        int category = cursor.getInt(cursor.getColumnIndex(AnswersEntry.ANSWERS_CATEGORY));
                        questionsPerCategory[category - 1]++;
                        if (cursor.getInt(cursor.getColumnIndex(AnswersEntry.ANSWERS_STATUS)) == 1) {
                            questionsPerCategoryCorrect[category - 1]++;
                        }
                    }

                    //Calculate percentage answered correct for each category
                    int[] questionsPositions = new int[numberOfCategories];
                    int[] questionsPerCategoryPercentage = new int[numberOfCategories];
                    for (int i = 0; i < numberOfCategories; i++) {
                        questionsPositions[i] = i;
                        if (questionsPerCategory[i] > 0) {
                            questionsPerCategoryPercentage[i] = Math.round((float) questionsPerCategoryCorrect[i] * 100 / (float) questionsPerCategory[i]);
                        } else {
                            questionsPerCategoryPercentage[i] = 0;
                        }
                    }

                    //Reorder categories by percentage and by number of questions answered
                    int temporaryQuestionPerCategoryPercentage;
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

                    //Filter the question categories by answering skills, by percentage
                    int lowSkillsCounter = 0, mediumSkillsCounter = 0, highSkillsCounter = 0;
                    for (int i = 0; i < numberOfCategories; i++) {
                        if (questionsPerCategoryPercentage[i] >= 75f) {
                            highSkillsCounter++;
                        } else if (questionsPerCategoryPercentage[i] >= 40f && (questionsPerCategory[i] < 75f)) {
                            mediumSkillsCounter++;
                        } else if (questionsPerCategoryPercentage[i] < 40f) {
                            lowSkillsCounter++;
                        }
                    }

                    //Update recycler views to show statistics, by high, medium and low skills
                    if (highSkillsCounter > 0) {
                        binding.fragmentStatisticsRvHighSkills.setVisibility(View.VISIBLE);
                        binding.fragmentStatisticsTvHighSkills.setVisibility(View.VISIBLE);
                        statisticsAdapterHigh.updateResults(getArray(questionsPositions, 0, highSkillsCounter),
                                getArray(questionsPerCategory, 0, highSkillsCounter),
                                getArray(questionsPerCategoryPercentage, 0, highSkillsCounter),
                                highSkillsCounter);
                    } else {
                        binding.fragmentStatisticsRvHighSkills.setVisibility(View.GONE);
                        binding.fragmentStatisticsTvHighSkills.setVisibility(View.GONE);
                    }
                    if (mediumSkillsCounter > 0) {
                        binding.fragmentStatisticsRvMediumSkills.setVisibility(View.VISIBLE);
                        binding.fragmentStatisticsTvMediumSkills.setVisibility(View.VISIBLE);
                        statisticsAdapterMedium.updateResults(getArray(questionsPositions, numberOfCategories - lowSkillsCounter - mediumSkillsCounter, mediumSkillsCounter),
                                getArray(questionsPerCategory, numberOfCategories - lowSkillsCounter - mediumSkillsCounter, mediumSkillsCounter),
                                getArray(questionsPerCategoryPercentage, numberOfCategories - lowSkillsCounter - mediumSkillsCounter, mediumSkillsCounter),
                                mediumSkillsCounter);
                    } else {
                        binding.fragmentStatisticsRvMediumSkills.setVisibility(View.GONE);
                        binding.fragmentStatisticsTvMediumSkills.setVisibility(View.GONE);
                    }
                    if (lowSkillsCounter > 0) {
                        binding.fragmentStatisticsRvLowSkills.setVisibility(View.VISIBLE);
                        binding.fragmentStatisticsTvLowSkills.setVisibility(View.VISIBLE);
                        statisticsAdapterLow.updateResults(getArray(questionsPositions, numberOfCategories - lowSkillsCounter, lowSkillsCounter),
                                getArray(questionsPerCategory, numberOfCategories - lowSkillsCounter, lowSkillsCounter),
                                getArray(questionsPerCategoryPercentage, numberOfCategories - lowSkillsCounter, lowSkillsCounter),
                                lowSkillsCounter);
                    } else {
                        binding.fragmentStatisticsRvLowSkills.setVisibility(View.GONE);
                        binding.fragmentStatisticsTvLowSkills.setVisibility(View.GONE);
                    }
                } else {
                    Timber.e("Cursor returned is null");
                }
                break;
            default:
                Timber.e("Could not return cursor");
        }
    }

    private int[] getArray(int[] questionArray, int startValue, int counter) {
        int[] updatedArray = new int[counter];
        List<Integer> temporaryArray = new ArrayList<>();
        for (int i = startValue; i < startValue + counter; i++) {
            temporaryArray.add(questionArray[i]);
        }
        for (int i = 0; i < counter; i++) {
            updatedArray[i] = temporaryArray.get(i);
        }
        return updatedArray;
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
