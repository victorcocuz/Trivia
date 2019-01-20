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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.victor.trivia.R;
import com.example.victor.trivia.adapters.ResultAdapter;
import com.example.victor.trivia.data.TriviaContract.AnswersEntry;
import com.example.victor.trivia.databinding.ActivityResultBinding;
import com.example.victor.trivia.objects.Score;
import com.example.victor.trivia.utilities.Constants;
import com.example.victor.trivia.objects.Answer;
import com.example.victor.trivia.objects.Question;
import com.example.victor.trivia.utilities.TriviaUtilities;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    //Main
    public ActivityResultBinding binding;
    private String userId;

    //Database
    FirebaseDatabase firebaseDatabase;
    DatabaseReference answersDatabaseReference;
    DatabaseReference scoreDatabaseReference;

    //Intent information
    ArrayList<Answer> answers;
    ArrayList<Question> questions;
    ArrayList<String> correctAnswers;
    Score score;

    //Loaders
    private static final int LOADER_ID_CURSOR_ANSWERED = 1;
    ResultAdapter resultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME_USER, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_USER_ID, Constants.CONSTANT_ANONYMUOS);

        //Get Intent
        Intent intent = getIntent();
        Bundle bundle = null;
        if (intent != null) {
            bundle = intent.getExtras();
        }

        //Set up Recycler View
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.activityResultRvAnswers.setLayoutManager(layoutManager);
        binding.activityResultRvAnswers.setHasFixedSize(true);
        resultAdapter = new ResultAdapter(this);
        binding.activityResultRvAnswers.setAdapter(resultAdapter);

        //If there is a saved instance state get parcelables for questions, answers and score from saved instance state
        if (savedInstanceState == null) {
            if (bundle != null) {
                //If there is no saved instance state get parcelables for questions, answers and score from intent
                questions = bundle.getParcelableArrayList(Constants.INTENT_ACTIVITY_RESULT_QUESTIONS_ARRAY);
                answers = bundle.getParcelableArrayList(Constants.INTENT_ACTIVITY_RESULT_ANSWERS_ARRAY);
                correctAnswers = bundle.getStringArrayList(Constants.INTENT_ACTIVITY_RESULT_CORRECT_ANSWERS);
                score = bundle.getParcelable(Constants.INTENT_ACTIVITY_RESULT_SCORE);
            } else {
                Timber.e("Could not receive information from intent");
            }

            if (score != null) {
                //Update score using a method from utilities and update result to Firebase
                Score updatedScore = TriviaUtilities.updateScore(this,
                        score.getUserQuestionPoints(),
                        score.getUserTimePoints(),
                        score.getUserNumberOfQuestionsAnswered(),
                        score.getUserNumberOfQuestionsAnsweredCorrect(),
                        score.getUserAverageAnswerTime());

                firebaseDatabase = FirebaseDatabase.getInstance();
                scoreDatabaseReference = firebaseDatabase.getReference().child(userId).child(Constants.PATH_FIREBASE_SCORE_TABLE);

                //If there is no preexisting score, push current score to Firebase. Else, get the score key and updated
                if (score.getUserNumberOfQuestionsAnswered() == updatedScore.getUserNumberOfQuestionsAnswered()) {
                    scoreDatabaseReference.push().setValue(updatedScore);
                } else {
                    String key = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SCORE_KEY, Constants.CONSTANT_NULL);
                    if (key != null) {
                        scoreDatabaseReference.child(key).setValue(updatedScore);
                    } else {
                        Timber.e("Could not find key to update score");
                    }
                }

                //Update results in Shared Preference;
                final SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME_USER, MODE_PRIVATE).edit();
                editor.putInt(Constants.SHARED_PREFERENCES_USER_TIME_POINTS, updatedScore.getUserTimePoints());
                editor.putInt(Constants.SHARED_PREFERENCES_USER_QUESTION_POINTS, updatedScore.getUserQuestionPoints());
                editor.putInt(Constants.SHARED_PREFERENCES_USER_LEVEL, updatedScore.getUserLevel());
                editor.putInt(Constants.SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED, updatedScore.getUserNumberOfQuestionsAnswered());
                editor.putInt(Constants.SHARED_PREFERENCES_USER_NUMBER_OF_QUESTIONS_ANSWERED_CORRECT, updatedScore.getUserNumberOfQuestionsAnsweredCorrect());
                editor.putFloat(Constants.SHARED_PREFERENCES_USER_PERCENTAGE_CORRECT, updatedScore.getUserPercentageCorrect());
                editor.putFloat(Constants.SHARED_PREFERENCES_USER_AVERAGE_ANSWER_TIME, updatedScore.getUserAverageAnswerTime());
                editor.apply();
            }
        }

        //Set score text
        binding.activityResultTvScoreQuestions.setText(String.valueOf(score.getUserQuestionPoints()));
        binding.activityResultTvScoreTime.setText(String.valueOf(score.getUserTimePoints()));
        binding.activityResultTvAnswersTotal.setText(String.valueOf(score.getUserNumberOfQuestionsAnswered()));
        binding.activityResultTvAnswersCorrect.setText(String.valueOf(score.getUserNumberOfQuestionsAnsweredCorrect()));

        //If there are any correct answers, populate recycler view from adapter
        if (score.getUserNumberOfQuestionsAnswered() != 0) {
            binding.activityResultTvPercentageCorrect.setText(String.valueOf(Math.round(score.getUserNumberOfQuestionsAnsweredCorrect() / score.getUserNumberOfQuestionsAnswered())));
            if (answers != null && answers.size() != 0) {
                if (questions != null && questions.size() != 0) {
                    resultAdapter.updateResults(questions, answers, correctAnswers);
                } else {
                    Timber.e("Could not retrieve results");
                }
                //Call answers loader if it hasn't already been called
                if (savedInstanceState == null) {
                    if (getSupportLoaderManager().getLoader(LOADER_ID_CURSOR_ANSWERED) == null) {
                        getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_ANSWERED, null, ResultActivity.this).forceLoad();
                    }
                }
            }
        } else {
            binding.activityResultTvPercentageCorrect.setText(String.valueOf(0));
            Toast.makeText(this, getString(R.string.toast_no_question_answered), Toast.LENGTH_SHORT).show();
        }

        //Button to go back
        binding.activityResultFinishActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.SAVED_INSTANCE_RESULT_QUESTIONS, questions);
        outState.putParcelableArrayList(Constants.SAVED_INSTANCE_RESULT_ANSWERS, answers);
        outState.putStringArrayList(Constants.SAVED_INSTANCE_RESULT_CORRECT_ANSWERS, correctAnswers);
        outState.putParcelable(Constants.SAVED_INSTANCE_RESULT_SCORE, score);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        questions = savedInstanceState.getParcelableArrayList(Constants.SAVED_INSTANCE_RESULT_QUESTIONS);
        answers = savedInstanceState.getParcelableArrayList(Constants.SAVED_INSTANCE_RESULT_ANSWERS);
        correctAnswers = savedInstanceState.getStringArrayList(Constants.SAVED_INSTANCE_RESULT_CORRECT_ANSWERS);
        score = savedInstanceState.getParcelable(Constants.SAVED_INSTANCE_RESULT_SCORE);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int i, @Nullable Bundle bundle) {
        switch (i) {
            case LOADER_ID_CURSOR_ANSWERED:
                String[] projection = {AnswersEntry.ANSWERS_FIREBASE_ID, AnswersEntry.ANSWERS_FIREBASE_QUESTION_ID};
                return new CursorLoader(this,
                        AnswersEntry.ANSWERS_URI,
                        projection,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object o) {
        final Cursor cursorAnswers = (Cursor) o;

        switch (loader.getId()) {
            case LOADER_ID_CURSOR_ANSWERED:
                //Get all answers from database, unique by FirebaseQuestionId
                final List<String> questionFirebaseIds = new ArrayList<>();
                final List<String> answersFirebaseIds = new ArrayList<>();
                if (cursorAnswers != null) {
                    if (cursorAnswers.getCount() > 0) {
                        for (int i = 0; i < cursorAnswers.getCount(); i++) {
                            cursorAnswers.moveToPosition(i);
                            answersFirebaseIds.add(cursorAnswers.getString(cursorAnswers.getColumnIndex(AnswersEntry.ANSWERS_FIREBASE_ID)));
                            questionFirebaseIds.add(cursorAnswers.getString(cursorAnswers.getColumnIndex(AnswersEntry.ANSWERS_FIREBASE_QUESTION_ID)));
                        }
                    }
                    getSupportLoaderManager().destroyLoader(LOADER_ID_CURSOR_ANSWERED);
                }

                //Setup Firebase and push answers, only if not already added
                answersDatabaseReference = firebaseDatabase.getReference().child(userId).child(AnswersEntry.ANSWERS_TABLE_NAME);

                for (int i = 0; i < answers.size(); i++) {
                    Boolean answerIsAlreadyAdded = false;
                    for (int j = 0; j < questionFirebaseIds.size(); j++) {
                        if (answers.get(i).getAnswerFirebaseQuestionId().equals(questionFirebaseIds.get(j))) {
                            answerIsAlreadyAdded = true;
                            Timber.e("answer already added");

                            //Update correct answers
                            if (answers.get(i).getAnswerStatus() == 1) {
                                answersDatabaseReference.child(answersFirebaseIds.get(j)).setValue(answers.get(i));

                                String selection = AnswersEntry.ANSWERS_FIREBASE_ID + "=?";
                                String[] selectionArgs = {answersFirebaseIds.get(j)};
                                ContentValues answerValues = new ContentValues();
                                answerValues.put(AnswersEntry.ANSWERS_FIREBASE_USER_ID, userId);
                                answerValues.put(AnswersEntry.ANSWERS_FIREBASE_QUESTION_ID, answers.get(i).getAnswerFirebaseQuestionId());
                                answerValues.put(AnswersEntry.ANSWERS_STATUS, answers.get(i).getAnswerStatus());
                                answerValues.put(AnswersEntry.ANSWERS_ANSWER, answers.get(i).getAnswerAnswer());
                                answerValues.put(AnswersEntry.ANSWERS_SCORE_QUESTION, answers.get(i).getAnswerScoreQuestion());
                                answerValues.put(AnswersEntry.ANSWERS_SCORE_TIME, answers.get(i).getAnswerScoreTime());
                                answerValues.put(AnswersEntry.ANSWERS_TIME, answers.get(i).getAnswerTime());
                                answerValues.put(AnswersEntry.ANSWERS_CATEGORY, answers.get(i).getAnswerCategory());
                                getContentResolver().update(AnswersEntry.ANSWERS_URI, answerValues, selection, selectionArgs);
                                Timber.d("Answer was updated in db %s", answersFirebaseIds.get(j));
                            }
                        }
                    }
                    if (!answerIsAlreadyAdded) {
                        answersDatabaseReference.push().setValue(answers.get(i));
                    }
                }
                break;
            default:
                Timber.e("Could not return cursor");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        getSupportLoaderManager().destroyLoader(LOADER_ID_CURSOR_ANSWERED);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
