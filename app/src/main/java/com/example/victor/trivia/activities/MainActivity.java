package com.example.victor.trivia.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.victor.trivia.R;
import com.example.victor.trivia.adapters.FragmentAdapter;
import com.example.victor.trivia.databinding.ActivityMainBinding;
import com.example.victor.trivia.objects.Answer;
import com.example.victor.trivia.objects.Question;
import com.example.victor.trivia.objects.Score;
import com.example.victor.trivia.utilities.Constants;
import com.example.victor.trivia.utilities.NetworkDetection;
import com.facebook.stetho.Stetho;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Helpers
import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;
import com.example.victor.trivia.data.TriviaContract.AnswersEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    // ++TODO: 1/19/2019 check for useless implementations
    //Main
    ActivityMainBinding binding;
    private Bundle mSavedInstanceState;
    private String userId;

    //Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference scoreDatabaseReference, questionsDatabaseReference, answersDatabaseReference;
    private ChildEventListener scoreEventListener, questionsEventListener, answersEventListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    public static final int RC_SIGN_IN = 1;

    //Loaders
    private static final int LOADER_ID_CURSOR_QUESTIONS_ADDED = 1;
    private static final int LOADER_ID_CURSOR_ANSWERS_ADDED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize main components
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        Stetho.initializeWithDefaults(this);
        mSavedInstanceState = savedInstanceState;

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //Check network connection. If connected, either sign in or load questions, if not, show toast
                NetworkDetection networkDetection = new NetworkDetection(MainActivity.this);
                if (networkDetection.isConnected()) {
                    if (user != null) {
                        userId = user.getUid();
                        onSignedInInitialize(user);

                        //Set up fragments and tab layout
                        FragmentAdapter generalFragmentAdapter = new FragmentAdapter(MainActivity.this, getSupportFragmentManager(), user.getUid());
                        binding.mainActivityViewPager.setAdapter(generalFragmentAdapter);
                        binding.mainActivityTabLayout.setupWithViewPager(binding.mainActivityViewPager);
                    } else {
                        onSignedOutCleanUp();
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setAvailableProviders(Arrays.asList(
                                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                                        .build(),
                                RC_SIGN_IN);
                    }
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_no_network_connection), Toast.LENGTH_SHORT).show();
                }
            }
        };
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
                String[] answersProjection = {AnswersEntry.ANSWERS_FIREBASE_QUESTION_ID};
                return new CursorLoader(this,
                        AnswersEntry.ANSWERS_URI,
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

        switch (loader.getId()) {
            //Add questions
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
                    getSupportLoaderManager().destroyLoader(LOADER_ID_CURSOR_QUESTIONS_ADDED);
                }

                questionsDatabaseReference = firebaseDatabase.getReference().child(QuestionsEntry.QUESTIONS_TABLE_NAME);

                //Synchronize Firebase with local database
                questionsEventListener = new ChildEventListener() {
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

            //Add answers
            case LOADER_ID_CURSOR_ANSWERS_ADDED:
                //Get all answers from database, unique by FirebaseQuestionId
                final List<String> answersQuestionFirebaseIds = new ArrayList<>();
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToPosition(i);
                            answersQuestionFirebaseIds.add(cursor.getString(cursor.getColumnIndex(AnswersEntry.ANSWERS_FIREBASE_QUESTION_ID)));
                        }
                    }
                    getSupportLoaderManager().destroyLoader(LOADER_ID_CURSOR_ANSWERS_ADDED);
                }

                answersDatabaseReference = firebaseDatabase.getReference().child(userId).child(AnswersEntry.ANSWERS_TABLE_NAME);
                //Synchronize with local database only new answers
                answersEventListener = new ChildEventListener() {
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
                                answerValues.put(AnswersEntry.ANSWERS_FIREBASE_ID, key);
                                answerValues.put(AnswersEntry.ANSWERS_FIREBASE_USER_ID, userId);
                                answerValues.put(AnswersEntry.ANSWERS_FIREBASE_QUESTION_ID, answer.getAnswerFirebaseQuestionId());
                                answerValues.put(AnswersEntry.ANSWERS_STATUS, answer.getAnswerStatus());
                                answerValues.put(AnswersEntry.ANSWERS_ANSWER, answer.getAnswerAnswer());
                                answerValues.put(AnswersEntry.ANSWERS_SCORE_QUESTION, answer.getAnswerScoreQuestion());
                                answerValues.put(AnswersEntry.ANSWERS_SCORE_TIME, answer.getAnswerScoreTime());
                                answerValues.put(AnswersEntry.ANSWERS_TIME, answer.getAnswerTime());
                                answerValues.put(AnswersEntry.ANSWERS_CATEGORY, answer.getAnswerCategory());
                                getContentResolver().insert(AnswersEntry.ANSWERS_URI, answerValues);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    private void onSignedInInitialize(FirebaseUser user) {
        //Get user information from Firebase and add it to SharedPreferences
        final SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME_USER, MODE_PRIVATE).edit();
        editor.putString(Constants.SHARED_PREFERENCES_USER_ID, user.getUid());
        editor.putString(Constants.SHARED_PREFERENCES_USER_DISPLAY_NAME, user.getDisplayName());
        editor.putString(Constants.SHARED_PREFERENCES_USER_EMAIL, user.getEmail());
        editor.apply();

        scoreDatabaseReference = firebaseDatabase.getReference().child(user.getUid()).child(Constants.PATH_FIREBASE_SCORE_TABLE);
        scoreEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Score score = dataSnapshot.getValue(Score.class);
                if (score != null) {
                    editor.putString(Constants.SHARED_PREFERENCES_SCORE_KEY, dataSnapshot.getKey());
                    editor.putInt(Constants.SHARED_PREFERENCES_USER_TIME_POINTS, score.getUserTimePoints());
                    editor.putInt(Constants.SHARED_PREFERENCES_USER_QUESTION_POINTS, score.getUserQuestionPoints());
                    editor.putInt(Constants.SHARED_PREFERENCES_USER_LEVEL, score.getUserLevel());
                    editor.putInt(Constants.SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED, score.getUserNumberOfQuestionsAnswered());
                    editor.putInt(Constants.SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED_CORRECT, score.getUserNumberOfQuestionsAnsweredCorrect());
                    editor.putFloat(Constants.SHARED_PREFERENCES_USER_PERCENTAGE_CORRECT, score.getUserPercentageCorrect());
                    editor.putFloat(Constants.SHARED_PREFERENCES_USER_AVERAGE_ANSWER_TIME, score.getUserAverageAnswerTime());
                } else {
                    editor.putString(Constants.SHARED_PREFERENCES_SCORE_KEY, Constants.CONSTANT_NULL);
                    editor.putInt(Constants.SHARED_PREFERENCES_USER_TIME_POINTS, Constants.CONSTANT_NONE);
                    editor.putInt(Constants.SHARED_PREFERENCES_USER_QUESTION_POINTS, Constants.CONSTANT_NONE);
                    editor.putInt(Constants.SHARED_PREFERENCES_USER_LEVEL, Constants.CONSTANT_NONE);
                    editor.putInt(Constants.SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED, Constants.CONSTANT_NONE);
                    editor.putInt(Constants.SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED_CORRECT, Constants.CONSTANT_NONE);
                    editor.putFloat(Constants.SHARED_PREFERENCES_USER_PERCENTAGE_CORRECT, Constants.CONSTANT_NONE);
                    editor.putFloat(Constants.SHARED_PREFERENCES_USER_AVERAGE_ANSWER_TIME, Constants.CONSTANT_NONE);
                }
                editor.apply();
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
        scoreDatabaseReference.addChildEventListener(scoreEventListener);

        //Get questions and answers from Firebase
        if (mSavedInstanceState != null) {
            if (questionsEventListener == null) {
                getSupportLoaderManager().restartLoader(LOADER_ID_CURSOR_QUESTIONS_ADDED, null, MainActivity.this).forceLoad();
            }
            if (answersEventListener == null) {
                getSupportLoaderManager().restartLoader(LOADER_ID_CURSOR_ANSWERS_ADDED, null, MainActivity.this).forceLoad();
            }
        } else {
            if (questionsEventListener == null) {
                getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_QUESTIONS_ADDED, null, MainActivity.this).forceLoad();
            }
            if (answersEventListener == null) {
                getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_ANSWERS_ADDED, null, MainActivity.this).forceLoad();
            }
        }
    }

    private void onSignedOutCleanUp() {
        //Set user to anonymous
        userId = Constants.CONSTANT_ANONYMUOS;

        //Remove shared preferences
        SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME_USER, MODE_PRIVATE).edit();
        editor.putString(Constants.SHARED_PREFERENCES_USER_ID, Constants.CONSTANT_ANONYMUOS);
        editor.putString(Constants.SHARED_PREFERENCES_USER_DISPLAY_NAME, Constants.CONSTANT_ANONYMUOS);
        editor.putString(Constants.SHARED_PREFERENCES_USER_EMAIL, Constants.CONSTANT_ANONYMUOS);
        editor.putInt(Constants.SHARED_PREFERENCES_USER_TIME_POINTS, Constants.CONSTANT_NONE);
        editor.putInt(Constants.SHARED_PREFERENCES_USER_QUESTION_POINTS, Constants.CONSTANT_NONE);
        editor.putInt(Constants.SHARED_PREFERENCES_USER_LEVEL, Constants.CONSTANT_NONE);
        editor.putInt(Constants.SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED, Constants.CONSTANT_NONE);
        editor.putInt(Constants.SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED_CORRECT, Constants.CONSTANT_NONE);
        editor.putFloat(Constants.SHARED_PREFERENCES_USER_PERCENTAGE_CORRECT, Constants.CONSTANT_NONE);
        editor.putFloat(Constants.SHARED_PREFERENCES_USER_AVERAGE_ANSWER_TIME, Constants.CONSTANT_NONE);
        editor.apply();

        //Delete answers from local database
        getContentResolver().delete(AnswersEntry.ANSWERS_URI, null, null);

        //Detach Firebase read listener
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (questionsEventListener != null) {
            questionsDatabaseReference.removeEventListener(questionsEventListener);
            questionsEventListener = null;
        }
        if (answersEventListener != null) {
            answersDatabaseReference.removeEventListener(answersEventListener);
            answersEventListener = null;
        }

        if (scoreEventListener != null) {
            scoreDatabaseReference.removeEventListener(scoreEventListener);
            scoreEventListener = null;
        }
    }
}
