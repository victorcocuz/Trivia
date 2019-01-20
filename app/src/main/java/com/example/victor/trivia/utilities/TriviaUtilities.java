package com.example.victor.trivia.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.victor.trivia.R;
import com.example.victor.trivia.objects.Score;

/******
 * Created by Victor on 1/19/2019.
 ******/
public class TriviaUtilities {

    private TriviaUtilities() {
        //empty constructor
    }

    public static int getLevel(int score, Context context) {
        int[] arrayLevels = context.getResources().getIntArray(R.array.array_levels);
        for (int i = 0; i < arrayLevels.length; i++) {
            if (isBetween(score, arrayLevels[i], arrayLevels[i + 1])) return i + 1;
        }
        return 0;
    }

    private static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x < upper;
    }

    public static Score updateScore(Context context,
                                    int userQuestionPoints,
                                    int userTimePoints,
                                    int userNumberOfQuestionsAnswered,
                                    int userNumberOfQuestionsAnsweredCorrect,
                                    float userAverageAnswerTime) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME_USER, Context.MODE_PRIVATE);

        int sharedQuestionPoints = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_USER_QUESTION_POINTS, Constants.CONSTANT_NONE);
        int sharedTimePoints = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_USER_TIME_POINTS, Constants.CONSTANT_NONE);
        int sharedNumberOfQuestionsAnswered = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED, Constants.CONSTANT_NONE);
        int sharedNumberOfQuestionsAnsweredCorrect = sharedPreferences.getInt(Constants.SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED_CORRECT, Constants.CONSTANT_NONE);
        float sharedAverageAnswerTime = sharedPreferences.getFloat(Constants.SHARED_PREFERENCES_USER_AVERAGE_ANSWER_TIME, Constants.CONSTANT_NONE);

        return new Score(
                sharedQuestionPoints + userQuestionPoints,
                sharedTimePoints + userTimePoints,
                getLevel(sharedQuestionPoints + userQuestionPoints + sharedTimePoints + userTimePoints, context),
                sharedNumberOfQuestionsAnswered + userNumberOfQuestionsAnswered,
                sharedNumberOfQuestionsAnsweredCorrect + userNumberOfQuestionsAnsweredCorrect,
                (float) (sharedNumberOfQuestionsAnsweredCorrect + userNumberOfQuestionsAnsweredCorrect) / (float) (sharedNumberOfQuestionsAnswered + userNumberOfQuestionsAnswered),
                (sharedAverageAnswerTime * (float) sharedNumberOfQuestionsAnswered + userAverageAnswerTime * (float) userNumberOfQuestionsAnswered) / (float) (sharedNumberOfQuestionsAnswered + userNumberOfQuestionsAnswered));
    }
}

