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
import com.example.victor.trivia.databinding.ActivityMainBinding;
import com.example.victor.trivia.helpers.Constants;
import com.example.victor.trivia.objects.Question;
import com.facebook.stetho.Stetho;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Helpers
import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    //Loaders
    private static final int LOADER_ID_CURSOR_ADDED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        Stetho.initializeWithDefaults(this);
        // TODO: 1/13/2019 check internet connection

        //Set up fragments and tab layout
        ViewPager generalViewPager = findViewById(R.id.main_activity_view_pager);
        FragmentAdapter generalFragmentAdapter = new FragmentAdapter(this, getSupportFragmentManager());
        generalViewPager.setAdapter(generalFragmentAdapter);
        TabLayout tableLayout = findViewById(R.id.main_activity_tab_layout);
        tableLayout.setupWithViewPager(generalViewPager);

        getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_ADDED, null, MainActivity.this).forceLoad();
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {QuestionsEntry.QUESTIONS_FIREBASE_ID};
        return new CursorLoader(this,
                QuestionsEntry.QUESTIONS_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        final Cursor cursorQuestions = (Cursor) data;

        //Setup Firebase and synchronize with local database;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference questionsDatabaseReference = firebaseDatabase.getReference().child(Constants.DATABASE_TABLE_QUESTIONS);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key;
                Question question = dataSnapshot.getValue(Question.class);

                if (question != null) {
                    Boolean questionIsAlreadyAdded = false;
                    key = dataSnapshot.getKey();

                    if (cursorQuestions != null && key != null) {
                        if (cursorQuestions.getCount() > 0) {
                            for (int i = 0; i < cursorQuestions.getCount(); i++) {
                                cursorQuestions.moveToPosition(i);
                                if (key.equals(cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_FIREBASE_ID)))) {
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
                        }
                    } else {
                        Timber.e("Could not download questions from FireBase");
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

        questionsDatabaseReference.addChildEventListener(childEventListener);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
