package com.example.victor.trivia.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.victor.trivia.R;

import com.example.victor.trivia.adapters.AnswersAdapter;
import com.example.victor.trivia.databinding.ActivityGameBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Helpers
import com.example.victor.trivia.helpers.Constants;
import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;
import com.example.victor.trivia.data.TriviaContract.AnsweredEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import timber.log.Timber;

public class GameActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks,
        AnswersAdapter.AnswersAdapterOnClickHandler {

    public ActivityGameBinding binding;

    //Loaders
    private static final int LOADER_ID_CURSOR_NOT_ANSWERED = 1;
    private static final int LOADER_ID_CURSOR_QUESTIONS = 2;

    //For Recycler view
    private Cursor cursor;
    public AnswersAdapter answersAdapter;

    //For question building
    private String questionCorrectAnswer;
    public String questionFirebaseId;
    private String[] questionsSelectionArgs;
    private String questionSelection;

    //For scoring
    CountDownTimer countDownTimer;
    boolean countDownTimerIsRunning = false;
    long timeLeft;
    int scoreQuestion;
    int scoreTimerQuestion;
    int scoreGame;
    int questionsRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game);

        questionsRemaining = 10;

        TextView finishView = findViewById(R.id.activity_game_tv_finish);
        finishView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.INTENT_TO_GAME_RETURN_KEY, "muie");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        //Call answers loader
        getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_NOT_ANSWERED, null, GameActivity.this).forceLoad();
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle bundle) {
        switch (id) {
            case LOADER_ID_CURSOR_NOT_ANSWERED:
                String notAnsweredSelection = AnsweredEntry.ANSWERD_STATUS + "=? OR " + AnsweredEntry.ANSWERD_STATUS + "=?";
                String[] notAnsweredSelectionArgs = {String.valueOf(Constants.ANSWER_STATUS_INCORRECT), String.valueOf(Constants.ANSWER_STATUS_NO_ANSWER)};
                return new CursorLoader(this,
                        AnsweredEntry.ANSWERED_URI,
                        Constants.PROJECTION_ANSWERS,
                        notAnsweredSelection,
                        notAnsweredSelectionArgs,
                        null);

            case LOADER_ID_CURSOR_QUESTIONS:
                return new CursorLoader(this,
                        QuestionsEntry.QUESTIONS_URI,
                        Constants.PROJECTION_QUESTIONS,
                        questionSelection,
                        questionsSelectionArgs,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_CURSOR_NOT_ANSWERED:
                StringBuilder questionSelectionBuilder = new StringBuilder();
                Cursor cursorNotAnswered = (Cursor) data;
                if (cursorNotAnswered != null) {
                    if (cursorNotAnswered.getCount() > 0) {
                        //If there are less than 10 questions remaining in the database, questions remaining will be reassigned
                        if (cursorNotAnswered.getCount() < 10) {
                            questionsRemaining = cursorNotAnswered.getCount();
                        }

                        //Find all questions not answered by FirebaseId. These will be later passed as selection arguments.
                        questionsSelectionArgs = new String[cursorNotAnswered.getCount()];
                        for (int i = 0; i < cursorNotAnswered.getCount(); i++) {
                            if (i < cursorNotAnswered.getCount() - 1) {
                                questionSelectionBuilder.append(QuestionsEntry.QUESTIONS_FIREBASE_ID + "=? OR ");
                            } else if (i == cursorNotAnswered.getCount() - 1) {
                                questionSelectionBuilder.append(QuestionsEntry.QUESTIONS_FIREBASE_ID + "=?");
                            }
                            cursorNotAnswered.moveToPosition(i);
                            questionsSelectionArgs[i] = cursorNotAnswered.getString(cursorNotAnswered.getColumnIndex(AnsweredEntry.ANSWERED_FIREBASE_ID));
                        }
                        questionSelection = questionSelectionBuilder.toString();
                        cursorNotAnswered.close();

                        //Call questions loader
                        getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_QUESTIONS, null, GameActivity.this).forceLoad();

                    } else {
                        Timber.w("Cursor NotAnswered length is 0");
                        Toast.makeText(this, "Congratulations! You answered correctly all questions in this demo version. Good to know. You can always check out your answers in the user section... Or you can reset and start again :)", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Timber.e("Cursor NotAnswered is null");
                }
                break;
            case LOADER_ID_CURSOR_QUESTIONS:
                cursor = (Cursor) data;
                answersAdapter = new AnswersAdapter(this);
                binding.activityGameRv.setAdapter(answersAdapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                binding.activityGameRv.setLayoutManager(layoutManager);
                binding.activityGameRv.setHasFixedSize(true);
                binding.activityGameRv.setNestedScrollingEnabled(false);
                loadNextQuestion(cursor);
                break;
            default:
                Timber.e("Could not return cursor");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        getLoaderManager().destroyLoader(LOADER_ID_CURSOR_QUESTIONS);
    }

    private void loadNextQuestion(Cursor cursor) {
        if (cursor.getCount() > 0) {

            //Load question from cursor into adapter and shuffle the answers
            Random random = new Random();
            cursor.moveToPosition(random.nextInt(cursor.getCount()));
            questionFirebaseId = cursor.getString(cursor.getColumnIndex(QuestionsEntry.QUESTIONS_FIREBASE_ID));
            String questionBody = cursor.getString(cursor.getColumnIndex(QuestionsEntry.QUESTIONS_BODY));
            questionCorrectAnswer = cursor.getString(cursor.getColumnIndex(QuestionsEntry.QUESTIONS_CORRECT_ANSWER));
            String questionIncorrectAnswer1 = cursor.getString(cursor.getColumnIndex(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_01));
            String questionIncorrectAnswer2 = cursor.getString(cursor.getColumnIndex(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_02));
            String questionIncorrectAnswer3 = cursor.getString(cursor.getColumnIndex(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_03));

            binding.activityGameTvQuestion.setText(questionBody);
            List<String> answers = new ArrayList<>();
            answers.add(questionCorrectAnswer);
            answers.add(questionIncorrectAnswer1);
            answers.add(questionIncorrectAnswer2);
            answers.add(questionIncorrectAnswer3);
            Collections.shuffle(answers);
            answersAdapter.updateAnswers(answers);

            //Setup Timer
            countDownTimer = new CountDownTimer(Constants.TIMER_QUESTION_INTERVAL, Constants.TIMER_TICK_INTERVAL) {

                public void onTick(long millisUntilFinished) {
                    timeLeft = millisUntilFinished;
                    binding.activityGameTimer.setText("seconds remaining: " + millisUntilFinished / Constants.TIMER_TICK_INTERVAL);
                }

                public void onFinish() {
                    binding.activityGameTimer.setText("done!");
                }
            }.start();
        } else {
            Timber.e("Could not receive cursor to load question");
        }
    }

    @Override
    public void OnClick(String answer) {
        countDownTimer.cancel();
        int answeredStatus = Constants.ANSWER_STATUS_INCORRECT;

        if (answer.equals(questionCorrectAnswer)) {
            answeredStatus = Constants.ANSWER_STATUS_CORRECT;
            scoreTimerQuestion = scoreTimerQuestion + Math.round(timeLeft / 1000);
            scoreQuestion = scoreQuestion + Constants.SCORE_PER_QUESTION;
        }

        //Add answer to database
        String selection = AnsweredEntry.ANSWERED_FIREBASE_ID + "=?";
        String[] selectionArgs = new String[]{questionFirebaseId};
        ContentValues answeredValues = new ContentValues();
        answeredValues.put(AnsweredEntry.ANSWERED_ANSWER, answer);
        answeredValues.put(AnsweredEntry.ANSWERD_STATUS, answeredStatus);
        getContentResolver().update(AnsweredEntry.ANSWERED_URI, answeredValues, selection, selectionArgs);

        //If the quiz is not over, load next question. If the quiz is over go to result activity
        if (questionsRemaining > 0) {
            loadNextQuestion(cursor);
            questionsRemaining = questionsRemaining - 1;
        } else {
            Toast.makeText(this, "Quiz is over", Toast.LENGTH_SHORT).show();
        }
    }
}
