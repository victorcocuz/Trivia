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

    public static final String TRIVIA_PATH_USERS = "users";
    public static final String TRIVIA_PATH_QUESTIONS = "questions";
    public static final String TRIVIA_PATH_GAMES = "games";
    public static final String TRIVIA_PATH_ANSWERS = "answers";

    public static final String USERS_CONTENT_DIR_BASE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_USERS;
    public static final String USERS_CONTENT_ITEM_BASE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_USERS;
    public static final String QUESTIONS_CONTENT_DIR_BASE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_QUESTIONS;
    public static final String QUESTIONS_CONTENT_ITEM_BASE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_QUESTIONS;
    public static final String GAMES_CONTENT_DIR_BASE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_GAMES;
    public static final String GAMES_CONTENT_ITEM_BASE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_GAMES;
    public static final String ANSWERED_CONTENT_DIR_BASE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_ANSWERS;
    public static final String ANSWERED_CONTENT_ITEM_BASE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + TRIVIA_AUTHORITY + "/" + TRIVIA_PATH_ANSWERS;

    public static final class UsersEntry implements BaseColumns {
        public static final Uri USERS_URI = TRIVIA_BASE_URI.buildUpon()
                .appendPath(TRIVIA_PATH_USERS)
                .build();

        public static final String USERS_TABLE_NAME = "usersTable";
    }

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

    public static final class GamesEntry implements BaseColumns {
        public static final Uri GAMES_URI = TRIVIA_BASE_URI.buildUpon()
                .appendPath(TRIVIA_PATH_GAMES)
                .build();

        public static final String GAMES_TABLE_NAME = "questions";
    }

    public static final class AnsweredEntry implements BaseColumns {
        public static final Uri ANSWERED_URI = TRIVIA_BASE_URI.buildUpon()
                .appendEncodedPath(TRIVIA_PATH_ANSWERS)
                .build();

        public static final String ANSWERED_TABLE_NAME = "answeredTable";
        public static final String ANSWERED_FIREBASE_ID = "answeredFirebaseId";
        public static final String ANSWERED_FIREBASE_USER_ID = "answeredFirebaseUserId";
        public static final String ANSWERED_FIREBASE_QUESTION_ID = "answeredFirebaseQuestionId";
        public static final String ANSWERED_STATUS = "answeredStatus";
        public static final String ANSWERED_ANSWER = "answeredAnswer";
        public static final String ANSWERED_SCORE_QUESTION = "answeredScoreQuestion";
        public static final String ANSWERED_SCORE_TIME = "answeredScoreTime";
        public static final String ANSWERED_TIME = "answeredTime";
    }
}
