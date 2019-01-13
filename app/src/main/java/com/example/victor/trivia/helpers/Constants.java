package com.example.victor.trivia.helpers;

//Contract imports

import com.example.victor.trivia.data.TriviaContract;
import com.example.victor.trivia.data.TriviaContract.UsersEntry;
import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;
import com.example.victor.trivia.data.TriviaContract.GamesEntry;
import com.example.victor.trivia.data.TriviaContract.AnsweredEntry;
import com.example.victor.trivia.objects.Answered;

/******
 * Created by Victor on 12/4/2018.
 ******/
public class Constants {
    public static final int INTENT_TO_GAME_REQUEST_CODE = 1;
    public static final String INTENT_TO_GAME_RETURN_KEY = "result";

    //    Questions
    public enum questionTypes {
        WRITE, TRUE_FALSE, MULTIPLE_CHOICE
    }

    public enum questionTopics {GEOGRAPHY,}

    //Database tables
    public static final String DATABASE_TABLE_QUESTIONS = "questions";

    //Question Categories
    public static final int QUESTION_CATEGORY_NONE = -1;
    public static final int QUESTION_CATEGORY_ARTS_AND_CULTURE = 1;
    public static final int QUESTION_CATEGORY_GEOGRAPHY = 2;
    public static final int QUESTION_CATEGORY_HISTORY = 3;
    public static final int QUESTION_CATEGORY_MOVIES = 4;
    public static final int QUESTION_CATEGORY_MUSIC = 5;
    public static final int QUESTION_CATEGORY_SCIENCE = 6;
    public static final int QUESTION_CATEGORY_SOCIAL_SCIENCES = 7;
    public static final int QUESTION_CATEGORY_SPORT = 8;

    //Question types
    public static final int QUESTION_TYPE_WRITE = 1;
    public static final int QUESTION_TYPE_MULTIPLE_CHOICE = 2;
    public static final int QUESTION_TYPE_TRUE_FALSE = 3;

    //Answer status
    public static final int ANSWER_STATUS_NO_ANSWER = 0;
    public static final int ANSWER_STATUS_CORRECT = 1;
    public static final int ANSWER_STATUS_INCORRECT = 2;

    //Sqlite Database projections
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
            AnsweredEntry.ANSWERED_FIREBASE_ID,
            AnsweredEntry.ANSWERD_STATUS,
            AnsweredEntry.ANSWERED_ANSWER
    };

    //Score
    public static final int TIMER_QUESTION_INTERVAL = 10000;
    public static final int TIMER_TICK_INTERVAL = 1000;
    public static final int SCORE_PER_QUESTION = 10;
}
