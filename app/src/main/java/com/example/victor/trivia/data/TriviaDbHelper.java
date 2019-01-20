package com.example.victor.trivia.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Contract imports
import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;
import com.example.victor.trivia.data.TriviaContract.AnswersEntry;

/******
 * Created by Victor on 1/8/2019.
 ******/
public class TriviaDbHelper extends SQLiteOpenHelper {

    private static final String TRIVIA_DB_NAME = "trivia.db";
    private static final int TRIVIA_DB_VERSION = 1;

    public TriviaDbHelper(Context context) {
        super(context, TRIVIA_DB_NAME, null, TRIVIA_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE_QUESTIONS = "CREATE TABLE " + QuestionsEntry.QUESTIONS_TABLE_NAME + " ("
                + QuestionsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + QuestionsEntry.QUESTIONS_FIREBASE_ID + " TEXT UNIQUE, "
                + QuestionsEntry.QUESTIONS_CATEGORY + " INTEGER, "
                + QuestionsEntry.QUESTIONS_BODY + " TEXT, "
                + QuestionsEntry.QUESTIONS_CORRECT_ANSWER + " TEXT, "
                + QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_01 + " TEXT, "
                + QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_02 + " TEXT, "
                + QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_03 + " TEXT, "
                + QuestionsEntry.QUESTIONS_ANSWER_DESCRIPTION + " TEXT, "
                + QuestionsEntry.QUESTIONS_PHOTO_URL + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_QUESTIONS);

        final String SQL_CREATE_TABLE_QUESTIONS_ANSWERED = "CREATE TABLE " + AnswersEntry.ANSWERS_TABLE_NAME + " ("
                + AnswersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AnswersEntry.ANSWERS_FIREBASE_ID + " TEXT UNIQUE, "
                + AnswersEntry.ANSWERS_FIREBASE_USER_ID + " TEXT, "
                + AnswersEntry.ANSWERS_FIREBASE_QUESTION_ID + " TEXT, "
                + AnswersEntry.ANSWERS_STATUS + " TEXT, "
                + AnswersEntry.ANSWERS_ANSWER + " TEXT, "
                + AnswersEntry.ANSWERS_SCORE_QUESTION + " INTEGER, "
                + AnswersEntry.ANSWERS_SCORE_TIME + " INTEGER, "
                + AnswersEntry.ANSWERS_TIME + " TEXT, "
                + AnswersEntry.ANSWERS_CATEGORY + " INTEGER);";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_QUESTIONS_ANSWERED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuestionsEntry.QUESTIONS_TABLE_NAME);
    }
}
