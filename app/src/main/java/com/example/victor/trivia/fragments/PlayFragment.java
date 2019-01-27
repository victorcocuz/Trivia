package com.example.victor.trivia.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.databinding.DataBindingUtil;

import com.example.victor.trivia.databinding.FragmentPlayBinding;
import com.example.victor.trivia.R;
import com.example.victor.trivia.activities.GameActivity;

//Helpers
import com.example.victor.trivia.utilities.Constants;
import com.example.victor.trivia.utilities.TriviaUtilities;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends Fragment {

    //Main
    FragmentPlayBinding binding;
    private static String userId;
    private Context context;

    //Populate fragment
    private String userName;
    private int level, pointsTime, pointsQuestions, pointsRemaining;

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialize main components
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false);
        View rootView = binding.getRoot();

        //Get information from shared preferences and populate text views
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME_USER, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_USER_ID, Constants.CONSTANT_ANONYMUOS);

        //Set title
        userName = sharedPreferences.getString(Constants.SHARED_PREFERENCES_USER_DISPLAY_NAME, Constants.CONSTANT_ANONYMUOS);
        String title = getString(R.string.fragment_play_title) + " " + userName + "!";
        binding.fragmentPlayTitle.setText(title);

        //Set intro
        level = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_USER_LEVEL, Constants.CONSTANT_ONE);
        pointsTime = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_USER_TIME_POINTS, Constants.CONSTANT_NONE);
        pointsQuestions = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_USER_QUESTION_POINTS, Constants.QUESTION_CATEGORY_NONE);
        String intro01 = getString(R.string.fragment_play_intro_01) + " " + String.valueOf(level) + ".";
        binding.fragmentPlayIntro01.setText(intro01);
        String intro02 = getString(R.string.fragment_play_intro_02a)
                + " "
                + TriviaUtilities.getRemainingPoints(pointsTime + pointsQuestions, context)
                + " "
                + getString(R.string.fragment_play_intro_02b)
                + " "
                + String.valueOf(level + 1)
                + "!";
        binding.fragmentPlayIntro02.setText(intro02);

        //Set progress
        binding.fragmentPlayTvProgressQuestionsAnsweredValue.setText(String.valueOf(sharedPreferences.getInt(Constants.SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED, Constants.CONSTANT_NONE)));
        binding.fragmentPlayTvProgressPointsAccumulatedValue.setText(String.valueOf(pointsQuestions + pointsTime));
        binding.fragmentPlayTvProgressPercentageCorrectValue.setText(String.format("%s%%", String.valueOf(Math.round(100 * sharedPreferences.getFloat(Constants.SHARED_PREFERENCES_USER_PERCENTAGE_CORRECT, Constants.CONSTANT_NONE)))));
        binding.fragmentPlayTvProgressAverageAnsweringSpeedValue.setText(String.format(Locale.UK, "%.2f",sharedPreferences.getFloat(Constants.SHARED_PREFERENCES_USER_AVERAGE_ANSWER_TIME, Constants.CONSTANT_NONE)));

        binding.fragmentPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startGameIntent = new Intent(context, GameActivity.class);
                startGameIntent.putExtra(Constants.INTENT_ACTIVITY_GAME_USER_ID, userId);
                startActivity(startGameIntent);
            }
        });
        return rootView;
    }

    public static void setPlayFragmentUserId(String userId) {
        PlayFragment.userId = userId;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}

