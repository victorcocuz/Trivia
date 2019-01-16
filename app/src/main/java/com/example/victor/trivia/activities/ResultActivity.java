package com.example.victor.trivia.activities;

import android.content.ContentValues;
import android.content.Intent;
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

import com.example.victor.trivia.R;
import com.example.victor.trivia.adapters.ResultAdapter;
import com.example.victor.trivia.data.TriviaContract;
import com.example.victor.trivia.data.TriviaContract.AnsweredEntry;
import com.example.victor.trivia.databinding.ActivityResultBinding;
import com.example.victor.trivia.helpers.Constants;
import com.example.victor.trivia.objects.Answer;
import com.example.victor.trivia.objects.Question;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    public ActivityResultBinding binding;

    //Intent information
    ArrayList<Answer> answers;
    ArrayList<Question> questions;
    int scoreTotalQuestions;
    int scoreTotalTime;
    int numberOfQuestionsAnswered;
    int numberOfQuestionsAnsweredCorrect;

    //Loaders
    private static final int LOADER_ID_CURSOR_ANSWERED = 1;
    ResultAdapter resultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result);

        //Get information from intent
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                answers = bundle.getParcelableArrayList(Constants.INTENT_ACTIVITY_RESULT_ANSWERS_ARRAY);
                questions = bundle.getParcelableArrayList(Constants.INTENT_ACTIVITY_RESULT_QUESTIONS_ARRAY);
                scoreTotalQuestions = bundle.getInt(Constants.INTENT_ACTIVITY_RESULT_SCORE_TOTAL_QUESTIONS);
                scoreTotalTime = bundle.getInt(Constants.INTENT_ACTIVITY_RESULT_SCORE_TOTAL_TIME);
                numberOfQuestionsAnswered = bundle.getInt(Constants.INTENT_ACTIVITY_RESULT_NUMBER_QUESTIONS);
                numberOfQuestionsAnsweredCorrect = bundle.getInt(Constants.INTENT_ACTIVITY_RESULT_NUMBER_QUESTIONS_CORRECT);
            }
        }

        //Set score text
        binding.activityResultTvAnswersCorrect.setText(String.valueOf(numberOfQuestionsAnsweredCorrect));
        binding.activityResultTvAnswersTotal.setText(String.valueOf(numberOfQuestionsAnswered));
        binding.activityResultTvPercentageCorrect.setText(String.valueOf(Math.round(numberOfQuestionsAnsweredCorrect / numberOfQuestionsAnswered)));
        binding.activityResultTvScoreQuestions.setText(String.valueOf(scoreTotalQuestions));
        binding.activityResultTvScoreTime.setText(String.valueOf(scoreTotalTime));

        if (answers != null && answers.size() != 0) {
            //Setup Recycler View
            if (questions != null && questions.size() != 0) {
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                binding.activityResultRvAnswers.setLayoutManager(layoutManager);
                binding.activityResultRvAnswers.setHasFixedSize(true);
                resultAdapter = new ResultAdapter(this, questions, answers);
                binding.activityResultRvAnswers.setAdapter(resultAdapter);
            } else {
                Timber.e("Could not retrieve results");
            }

            getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_ANSWERED, null, ResultActivity.this).forceLoad();
        }

        //Call answers loader
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int i, @Nullable Bundle bundle) {
        switch (i) {
            case LOADER_ID_CURSOR_ANSWERED:
                String[] projection = {AnsweredEntry.ANSWERED_FIREBASE_ID, AnsweredEntry.ANSWERED_FIREBASE_QUESTION_ID};
                return new CursorLoader(this,
                        AnsweredEntry.ANSWERED_URI,
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
                            answersFirebaseIds.add(cursorAnswers.getString(cursorAnswers.getColumnIndex(AnsweredEntry.ANSWERED_FIREBASE_ID)));
                            questionFirebaseIds.add(cursorAnswers.getString(cursorAnswers.getColumnIndex(AnsweredEntry.ANSWERED_FIREBASE_QUESTION_ID)));
                        }
                    }
                    cursorAnswers.close();
                }

                //Setup Firebase and push answers, only if not already added
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference answersDatabaseReference = firebaseDatabase.getReference().child(TriviaContract.AnsweredEntry.ANSWERED_TABLE_NAME);

                for (int i = 0; i < answers.size(); i++) {
                    Boolean answerIsAlreadyAdded = false;
                    for (int j = 0; j < questionFirebaseIds.size(); j++) {
                        if (answers.get(i).getAnswerFirebaseQuestionId().equals(questionFirebaseIds.get(j))) {
                            answerIsAlreadyAdded = true;

                            //Update correct answers
                            if (answers.get(i).getAnswerStatus() == 1) {
                                answersDatabaseReference.child(answersFirebaseIds.get(j)).setValue(answers.get(i));

                                String selection = AnsweredEntry.ANSWERED_FIREBASE_ID + "=?";
                                String[] selectionArgs = {answersFirebaseIds.get(j)};
                                ContentValues answerValues = new ContentValues();
                                answerValues.put(AnsweredEntry.ANSWERED_FIREBASE_USER_ID, "");
                                answerValues.put(AnsweredEntry.ANSWERED_FIREBASE_QUESTION_ID, answers.get(i).getAnswerFirebaseQuestionId());
                                answerValues.put(AnsweredEntry.ANSWERED_STATUS, answers.get(i).getAnswerStatus());
                                answerValues.put(AnsweredEntry.ANSWERED_ANSWER, answers.get(i).getAnswerAnswer());
                                answerValues.put(AnsweredEntry.ANSWERED_SCORE_QUESTION, answers.get(i).getAnswerScoreQuestion());
                                answerValues.put(AnsweredEntry.ANSWERED_SCORE_TIME, answers.get(i).getAnswerScoreTime());
                                answerValues.put(AnsweredEntry.ANSWERED_TIME, answers.get(i).getAnswerTime());
                                getContentResolver().update(AnsweredEntry.ANSWERED_URI, answerValues, selection, selectionArgs);
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

    }
}
