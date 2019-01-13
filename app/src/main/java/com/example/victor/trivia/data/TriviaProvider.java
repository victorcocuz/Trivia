package com.example.victor.trivia.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

//Contract imports
import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;
import com.example.victor.trivia.data.TriviaContract.GamesEntry;
import com.example.victor.trivia.data.TriviaContract.AnsweredEntry;
import com.example.victor.trivia.objects.Answered;

import timber.log.Timber;

/******
 * Created by Victor on 1/8/2019.
 ******/
public class TriviaProvider extends ContentProvider {

    private static final int CODE_USERS = 100;
    private static final int CODE_USERS_ITEM = 101;
    private static final int CODE_QUESTIONS = 200;
    private static final int CODE_QUESTIONS_ITEM = 201;
    private static final int CODE_GAMES = 300;
    private static final int CODE_GAMES_ITEM = 301;
    private static final int CODE_ANSWERS = 400;
    private static final int CODE_ANSWERS_ITEM = 401;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TriviaContract.TRIVIA_AUTHORITY, TriviaContract.TRIVIA_PATH_USERS, CODE_USERS);
        uriMatcher.addURI(TriviaContract.TRIVIA_AUTHORITY, TriviaContract.TRIVIA_PATH_USERS + "#", CODE_USERS_ITEM);
        uriMatcher.addURI(TriviaContract.TRIVIA_AUTHORITY, TriviaContract.TRIVIA_PATH_QUESTIONS, CODE_QUESTIONS);
        uriMatcher.addURI(TriviaContract.TRIVIA_AUTHORITY, TriviaContract.TRIVIA_PATH_QUESTIONS + "#", CODE_QUESTIONS_ITEM);
        uriMatcher.addURI(TriviaContract.TRIVIA_AUTHORITY, TriviaContract.TRIVIA_PATH_GAMES, CODE_GAMES);
        uriMatcher.addURI(TriviaContract.TRIVIA_AUTHORITY, TriviaContract.TRIVIA_PATH_GAMES + "#", CODE_GAMES_ITEM);
        uriMatcher.addURI(TriviaContract.TRIVIA_AUTHORITY, TriviaContract.TRIVIA_PATH_ANSWERS, CODE_ANSWERS);
        uriMatcher.addURI(TriviaContract.TRIVIA_AUTHORITY, TriviaContract.TRIVIA_PATH_ANSWERS + "#", CODE_ANSWERS_ITEM);
        return uriMatcher;
    }

    private TriviaDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new TriviaDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case CODE_USERS:
//                cursor = database.query(RecipesEntry.RECIPES_TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder);
                break;
            case CODE_USERS_ITEM:
//                selection = RecipesEntry._ID + "=?";
//                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
//                cursor = database.query(RecipesEntry.RECIPES_TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder);
                break;
            case CODE_QUESTIONS:
                cursor = database.query(QuestionsEntry.QUESTIONS_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_QUESTIONS_ITEM:
                selection = QuestionsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(QuestionsEntry.QUESTIONS_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_GAMES:
                cursor = database.query(GamesEntry.GAMES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_GAMES_ITEM:
                selection = GamesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(GamesEntry.GAMES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_ANSWERS:
                cursor = database.query(AnsweredEntry.ANSWERED_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_ANSWERS_ITEM:
                selection = QuestionsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(AnsweredEntry.ANSWERED_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = 0;

        if (values == null || values.size() == 0) {
            return null;
        }

        switch (uriMatcher.match(uri)) {
            case CODE_USERS:
//                id = database.insert(RecipesEntry.RECIPES_TABLE_NAME,
//                        null,
//                        values);
//                if (id == -1) {
//                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
//                }
                break;
            case CODE_QUESTIONS:
                id = database.insert(QuestionsEntry.QUESTIONS_TABLE_NAME,
                        null,
                        values);
                if (id == -1) {
                    Timber.e("Failed to insert row for %s", uri);
                }
                break;
            case CODE_GAMES:
                id = database.insert(GamesEntry.GAMES_TABLE_NAME,
                        null,
                        values);
                if (id == -1) {
                    Timber.e("Failed to insert row for %s", uri);
                }
                break;
            case CODE_ANSWERS:
                id = database.insert(AnsweredEntry.ANSWERED_TABLE_NAME,
                        null,
                        values);
                if (id == -1) {
                    Timber.e("Failed to insert row for %s", uri);
                }
                break;

            default:
                throw new IllegalArgumentException("Insert is not supported for " + uri);
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;

        if (values == null || values.size() == 0) {
            return 0;
        }

        switch (uriMatcher.match(uri)) {
            case CODE_USERS:
//                rowsUpdated = database.update(RecipesEntry.RECIPES_TABLE_NAME,
//                        values,
//                        selection,
//                        selectionArgs);
                break;
            case CODE_USERS_ITEM:
//                selection = RecipesEntry._ID + "=?";
//                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
//                rowsUpdated = database.update(RecipesEntry.RECIPES_TABLE_NAME,
//                        values,
//                        selection,
//                        selectionArgs);
                break;
            case CODE_QUESTIONS:
                rowsUpdated = database.update(QuestionsEntry.QUESTIONS_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_QUESTIONS_ITEM:
                selection = QuestionsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(QuestionsEntry.QUESTIONS_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_GAMES:
                rowsUpdated = database.update(GamesEntry.GAMES_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_GAMES_ITEM:
                selection = GamesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(GamesEntry.GAMES_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_ANSWERS:
                rowsUpdated = database.update(AnsweredEntry.ANSWERED_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_ANSWERS_ITEM:
                selection = QuestionsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(AnsweredEntry.ANSWERED_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriMatcher.match(uri)) {
            case CODE_USERS:
//                rowsDeleted = database.delete(RecipesEntry.RECIPES_TABLE_NAME,
//                        selection,
//                        selectionArgs);
                break;
            case CODE_USERS_ITEM:
//                selection = RecipesEntry._ID + "=?";
//                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
//                rowsDeleted = database.delete(RecipesEntry.RECIPES_TABLE_NAME,
//                        selection,
//                        selectionArgs);
                break;
            case CODE_QUESTIONS:
                rowsDeleted = database.delete(QuestionsEntry.QUESTIONS_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_QUESTIONS_ITEM:
                selection = QuestionsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(QuestionsEntry.QUESTIONS_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_GAMES:
                rowsDeleted = database.delete(GamesEntry.GAMES_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_GAMES_ITEM:
                selection = GamesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(GamesEntry.GAMES_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_ANSWERS:
                rowsDeleted = database.delete(AnsweredEntry.ANSWERED_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_ANSWERS_ITEM:
                selection = QuestionsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(AnsweredEntry.ANSWERED_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }

        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CODE_USERS:
                return TriviaContract.USERS_CONTENT_DIR_BASE;
            case CODE_USERS_ITEM:
                return TriviaContract.USERS_CONTENT_ITEM_BASE;
            case CODE_QUESTIONS:
                return TriviaContract.QUESTIONS_CONTENT_DIR_BASE;
            case CODE_QUESTIONS_ITEM:
                return TriviaContract.QUESTIONS_CONTENT_ITEM_BASE;
            case CODE_GAMES:
                return TriviaContract.GAMES_CONTENT_DIR_BASE;
            case CODE_GAMES_ITEM:
                return TriviaContract.GAMES_CONTENT_ITEM_BASE;
            case CODE_ANSWERS:
                return TriviaContract.ANSWERED_CONTENT_DIR_BASE;
            case CODE_ANSWERS_ITEM:
                return TriviaContract.ANSWERED_CONTENT_ITEM_BASE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + uri);
        }
    }

    @Override
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }
}


