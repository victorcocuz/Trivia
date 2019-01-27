package com.example.victor.trivia.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/******
 * Created by Victor on 1/8/2019.
 ******/
public class TriviaContract {
    private static final String TRIVIA_SCHEME = "content://";
    public static final String TRIVIA_AUTHORITY = "com.example.victor.trivia";
    private static final Uri TRIVIA_BASE_URI = Uri.parse(TRIVIA_SCHEME + TRIVIA_AUTHORITY);

    public static final String TRIVIA_PATH_QUESTIONS = "questions";
    public static final String TRIVIA_PATH_SCORE = "games";
    public static final String TRIVIA_PATH_ANSWERS = "answers";

    public static final String QUESTIONS_CONTENT_DIR_BASE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_QUESTIONS;
    public static final String QUESTIONS_CONTENT_ITEM_BASE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_QUESTIONS;
    public static final String ANSWERS_CONTENT_DIR_BASE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_ANSWERS;
    public static final String ANSWERS_CONTENT_ITEM_BASE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_ANSWERS;
    public static final String SCORE_CONTENT_DIR_BASE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_SCORE;
    public static final String SCORE_CONTENT_ITEM_BASE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_SCORE;

    public static final class QuestionsEntry implements BaseColumns {
        public static final Uri QUESTIONS_URI = TRIVIA_BASE_URI.buildUpon()
                .appendPath(TRIVIA_PATH_QUESTIONS)
                .build();

        public static final String QUESTIONS_TABLE_NAME = "questionsTable";

        public static final String QUESTIONS_FIREBASE_ID = "questionsFirebaseId";
        public static final String QUESTIONS_CATEGORY = "questionsCategory";
        public static final String QUESTIONS_BODY = "questionsBody";
        public static final String QUESTIONS_CORRECT_ANSWER = "questionsCorrectAnswer";
        public static final String QUESTIONS_INCORRECT_ANSWER_01 = "questionsIncorrectAnswer01";
        public static final String QUESTIONS_INCORRECT_ANSWER_02 = "questionsIncorrectAnswer02";
        public static final String QUESTIONS_INCORRECT_ANSWER_03 = "questionsIncorrectAnswer03";
        public static final String QUESTIONS_ANSWER_DESCRIPTION = "questionsAnswerDescription";
        public static final String QUESTIONS_PHOTO_URL = "questionsPhotoUrl";
    }

    public static final class AnswersEntry implements BaseColumns {
        public static final Uri ANSWERS_URI = TRIVIA_BASE_URI.buildUpon()
                .appendEncodedPath(TRIVIA_PATH_ANSWERS)
                .build();

        public static final String ANSWERS_TABLE_NAME = "answersTable";
        public static final String ANSWERS_FIREBASE_ID = "answersFirebaseId";
        public static final String ANSWERS_FIREBASE_USER_ID = "answersFirebaseUserId";
        public static final String ANSWERS_FIREBASE_QUESTION_ID = "answersFirebaseQuestionId";
        public static final String ANSWERS_STATUS = "answersStatus";
        public static final String ANSWERS_ANSWER = "answersAnswer";
        public static final String ANSWERS_SCORE_QUESTION = "answersScoreQuestion";
        public static final String ANSWERS_SCORE_TIME = "answersScoreTime";
        public static final String ANSWERS_TIME = "answersTime";
        public static final String ANSWERS_CATEGORY = "answersCategory";
    }

    public static final class GamesEntry implements BaseColumns {

        public static final String SCORE_TABLE_NAME = "score";
    }
}
