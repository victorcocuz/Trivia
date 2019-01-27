package com.example.victor.trivia.utilities;

//Contract imports

import com.example.victor.trivia.data.TriviaContract.AnswersEntry;
import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;

/******
 * Created by Victor on 12/4/2018.
 ******/
public class Constants {

    //Firebase paths
    public static final String PATH_FIRE_BASE_SCORE_TABLE = "scoreTable";

    //Intents
    public static final String INTENT_ACTIVITY_GAME_USER_ID = "intentActivityGameUserId";
    public static final String INTENT_ACTIVITY_RESULT_QUESTIONS_ARRAY = "intentActivityResultAnswersArray";
    public static final String INTENT_ACTIVITY_RESULT_ANSWERS_ARRAY = "intentActivityResultQuestionsArray";
    public static final String INTENT_ACTIVITY_RESULT_SCORE = "intentActivityResultScoreArray";
    public static final String INTENT_ACTIVITY_RESULT_CORRECT_ANSWERS = "intentActivityResultCorrectAnswers";

    //Shared preferences values
    public static final String SHARED_PREFERENCES_NAME_USER = "sharedPreferencesNameUser";
    public static final String SHARED_PREFERENCES_SCORE_KEY = "sharedPreferencesScoreKey";
    public static final String SHARED_PREFERENCES_USER_ID = "sharedPreferencesUserId";
    public static final String SHARED_PREFERENCES_USER_DISPLAY_NAME = "sharedPreferencesUserDisplayName";
    public static final String SHARED_PREFERENCES_USER_EMAIL = "sharedPreferencesUserEmail";
    public static final String SHARED_PREFERENCES_USER_TIME_POINTS = "sharedPreferencesUserTimePoints";
    public static final String SHARED_PREFERENCES_USER_QUESTION_POINTS = "sharedPreferencesUserQuestionPoints";
    public static final String SHARED_PREFERENCES_USER_LEVEL = "sharedPreferencesUserLevel";
    public static final String SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED = "sharedPreferencesUserNumberOfQuestionsAnswered";
    public static final String SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED_CORRECT = "sharedPreferencesUserNumberOfQuestionsAnsweredCorrect";
    public static final String SHARED_PREFERENCES_USER_PERCENTAGE_CORRECT = "sharedPreferencesUserPercentageCorrect";
    public static final String SHARED_PREFERENCES_USER_AVERAGE_ANSWER_TIME = "sharePreferencesUserAverageAnswerTime";

    //Shared preferences fallback values
    public static final String CONSTANT_ANONYMOUS = "anonymous";
    public static final int CONSTANT_NONE = 0;
    public static final int CONSTANT_ONE = 1;
    public static final String CONSTANT_NULL = "";

    //Saved instance state constants
    public static final String SAVED_INSTANCE_RESULT_QUESTIONS = "savedInstanceResultQuestions";
    public static final String SAVED_INSTANCE_RESULT_ANSWERS = "savedInstanceResultAnswers";
    public static final String SAVED_INSTANCE_RESULT_SCORE = "savedInstanceResultScore";
    public static final String SAVED_INSTANCE_RESULT_CORRECT_ANSWERS = "savedInstanceResultCorrectAnswers";
    public static final String SAVED_INSTANCE_VIEW_PAGER_POSITION = "savedInstanceViewPagerPosition";

    //Question categories
    public static final int QUESTION_CATEGORY_NONE = -1;
    public static final int QUESTION_CATEGORY_ARTS_AND_CULTURE = 1;
    public static final int QUESTION_CATEGORY_GEOGRAPHY = 2;
    public static final int QUESTION_CATEGORY_HISTORY = 3;
    public static final int QUESTION_CATEGORY_MOVIES = 4;
    public static final int QUESTION_CATEGORY_MUSIC = 5;
    public static final int QUESTION_CATEGORY_SCIENCE = 6;
    public static final int QUESTION_CATEGORY_SOCIAL_SCIENCES = 7;
    public static final int QUESTION_CATEGORY_SPORT = 8;

    //Answer status
    public static final int ANSWER_STATUS_CORRECT = 1;
    public static final int ANSWER_STATUS_INCORRECT = 2;

    //SqLite Database projections
    public static final String[] PROJECTION_QUESTIONS = new String[]{
            QuestionsEntry.QUESTIONS_FIREBASE_ID,
            QuestionsEntry.QUESTIONS_CATEGORY,
            QuestionsEntry.QUESTIONS_BODY,
            QuestionsEntry.QUESTIONS_CORRECT_ANSWER,
            QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_01,
            QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_02,
            QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_03,
            QuestionsEntry.QUESTIONS_ANSWER_DESCRIPTION,
            QuestionsEntry.QUESTIONS_PHOTO_URL
    };
    public static final String[] PROJECTION_ANSWERS = new String[]{
            AnswersEntry.ANSWERS_FIREBASE_QUESTION_ID,
            AnswersEntry.ANSWERS_STATUS,
            AnswersEntry.ANSWERS_ANSWER
    };
}
