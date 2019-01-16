package com.example.victor.trivia.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.victor.trivia.R;
import com.example.victor.trivia.adapters.FragmentAdapter;
import com.example.victor.trivia.data.TriviaContract;
import com.example.victor.trivia.databinding.ActivityMainBinding;
import com.example.victor.trivia.objects.Answer;
import com.example.victor.trivia.objects.Question;
import com.facebook.stetho.Stetho;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Helpers
import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;
import com.example.victor.trivia.data.TriviaContract.AnsweredEntry;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    //Loaders
    private static final int LOADER_ID_CURSOR_QUESTIONS_ADDED = 1;
    private static final int LOADER_ID_CURSOR_ANSWERS_ADDED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        Stetho.initializeWithDefaults(this);
        // TODO: 1/13/2019 check internet connection
        // TODO: 1/13/2019 lock app to portrait mode

        //Set up fragments and tab layout
        FragmentAdapter generalFragmentAdapter = new FragmentAdapter(this, getSupportFragmentManager());
        binding.mainActivityViewPager.setAdapter(generalFragmentAdapter);
        binding.mainActivityTabLayout.setupWithViewPager(binding.mainActivityViewPager);

        getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_QUESTIONS_ADDED, null, MainActivity.this).forceLoad();
        getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_ANSWERS_ADDED, null, MainActivity.this).forceLoad();
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int i, @Nullable Bundle bundle) {
        switch (i) {
            case LOADER_ID_CURSOR_QUESTIONS_ADDED:
                String[] questionsProjection = {QuestionsEntry.QUESTIONS_FIREBASE_ID};
                return new CursorLoader(this,
                        QuestionsEntry.QUESTIONS_URI,
                        questionsProjection,
                        null,
                        null,
                        null);
            case LOADER_ID_CURSOR_ANSWERS_ADDED:
                String[] answersProjection = {AnsweredEntry.ANSWERED_FIREBASE_QUESTION_ID};
                return new CursorLoader(this,
                        AnsweredEntry.ANSWERED_URI,
                        answersProjection,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        final Cursor cursor = (Cursor) data;

        //Setup Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference questionsDatabaseReference = firebaseDatabase.getReference().child(QuestionsEntry.QUESTIONS_TABLE_NAME);

        switch (loader.getId()){
            case LOADER_ID_CURSOR_QUESTIONS_ADDED:

                //Get all questions from database, unique by FirebaseQuestionId
                final List<String> questionFirebaseIds = new ArrayList<>();
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToPosition(i);
                            questionFirebaseIds.add(cursor.getString(cursor.getColumnIndex(QuestionsEntry.QUESTIONS_FIREBASE_ID)));
                        }
                    }
                    cursor.close();
                }

                //Synchronize Firebase with local database
                ChildEventListener questionsEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String key;
                        Question question = dataSnapshot.getValue(Question.class);

                        //Synchronize with local database only new answers
                        if (question != null) {
                            Boolean questionIsAlreadyAdded = false;
                            key = dataSnapshot.getKey();

                            if (questionFirebaseIds.size() > 0 && key != null) {
                                for (String questionFirebaseId : questionFirebaseIds) {
                                    if (key.equals(questionFirebaseId)) {
                                        questionIsAlreadyAdded = true;
                                    }
                                }
                            }

                            if (!questionIsAlreadyAdded) {
                                //Add question to Questions Table in Database
                                ContentValues questionValues = new ContentValues();
                                questionValues.put(QuestionsEntry.QUESTIONS_FIREBASE_ID, key);
                                questionValues.put(QuestionsEntry.QUESTIONS_CATEGORY, question.getQuestionCategory());
                                questionValues.put(QuestionsEntry.QUESTIONS_BODY, question.getQuestionBody());
                                questionValues.put(QuestionsEntry.QUESTIONS_CORRECT_ANSWER, question.getQuestionCorrectAnswer());
                                questionValues.put(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_01, question.getQuestionIncorrectAnswer01());
                                questionValues.put(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_02, question.getQuestionIncorrectAnswer02());
                                questionValues.put(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_03, question.getQuestionIncorrectAnswer03());
                                questionValues.put(QuestionsEntry.QUESTIONS_ANSWER_DESCRIPTION, question.getQuestionDescription());
                                questionValues.put(QuestionsEntry.QUESTIONS_PHOTO_URL, question.getQuestionPhotoUrl());
                                getApplicationContext().getContentResolver().insert(QuestionsEntry.QUESTIONS_URI, questionValues);

                            } else {
                                Timber.d("Question was already added in local db %s", key);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                questionsDatabaseReference.addChildEventListener(questionsEventListener);

                break;

            case LOADER_ID_CURSOR_ANSWERS_ADDED:

                //Get all answers from database, unique by FirebaseQuestionId
                final List<String> answersQuestionFirebaseIds = new ArrayList<>();
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToPosition(i);
                            answersQuestionFirebaseIds.add(cursor.getString(cursor.getColumnIndex(AnsweredEntry.ANSWERED_FIREBASE_QUESTION_ID)));
                        }
                    }
                    cursor.close();
                }

                final DatabaseReference answersDatabaseReference = firebaseDatabase.getReference().child(TriviaContract.AnsweredEntry.ANSWERED_TABLE_NAME);
                //Synchronize with local database only new answers
                final ChildEventListener answersEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String key;
                        Answer answer = dataSnapshot.getValue(Answer.class);

                        if (answer != null) {
                            Boolean questionIsAlreadyAdded = false;
                            key = dataSnapshot.getKey();

                            for (String questionFirebaseId : answersQuestionFirebaseIds) {
                                if (answer.getAnswerFirebaseQuestionId().equals(questionFirebaseId)) {
                                    questionIsAlreadyAdded = true;
                                }
                            }

                            if (!questionIsAlreadyAdded) {
                                //Add question to Questions Table in Database
                                ContentValues answerValues = new ContentValues();
                                answerValues.put(AnsweredEntry.ANSWERED_FIREBASE_ID, key);
                                answerValues.put(AnsweredEntry.ANSWERED_FIREBASE_USER_ID, "");
                                answerValues.put(AnsweredEntry.ANSWERED_FIREBASE_QUESTION_ID, answer.getAnswerFirebaseQuestionId());
                                answerValues.put(AnsweredEntry.ANSWERED_STATUS, answer.getAnswerStatus());
                                answerValues.put(AnsweredEntry.ANSWERED_ANSWER, answer.getAnswerAnswer());
                                answerValues.put(AnsweredEntry.ANSWERED_SCORE_QUESTION, answer.getAnswerScoreQuestion());
                                answerValues.put(AnsweredEntry.ANSWERED_SCORE_TIME, answer.getAnswerScoreTime());
                                answerValues.put(AnsweredEntry.ANSWERED_TIME, answer.getAnswerTime());
                                getContentResolver().insert(AnsweredEntry.ANSWERED_URI, answerValues);
                                Timber.d("Answer was added to db %s", key);
                            } else {
                                Timber.d("Answer was already added in local db %s", key);
                            }

                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };
                answersDatabaseReference.addChildEventListener(answersEventListener);
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
