package com.example.victor.trivia.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.CountDownTimer;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.victor.trivia.R;

import com.example.victor.trivia.adapters.AnswersAdapter;
import com.example.victor.trivia.databinding.ActivityGameBinding;

//Helpers
import com.example.victor.trivia.helpers.Constants;
import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;
import com.example.victor.trivia.data.TriviaContract.AnsweredEntry;
import com.example.victor.trivia.objects.Answer;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

public class GameActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks,
        AnswersAdapter.AnswersAdapterOnClickHandler {

    public ActivityGameBinding binding;

    //Loaders
    private static final int LOADER_ID_CURSOR_ANSWERED = 1;
    private static final int LOADER_ID_CURSOR_QUESTIONS = 2;

    //For Recycler view
    private Cursor cursorQuestions;
    public AnswersAdapter answersAdapter;

    //For question building
    private String questionCorrectAnswer;
    public String questionFirebaseId;
    private String[] questionsSelectionArgs = null;
    private String questionsSelection = null;

    //For scoring
    CountDownTimer countDownTimer;
    long timeLeft;
    int scoreQuestion;
    int scoreTime;
    int scoreTotalQuestions;
    int scoreTotalTime;
    int scoreGame;
    int questionsRemaining;
    float answerTime;
    int numberOfQuestionsAnswered;
    int numberOfQuestionsAnsweredCorrect;

    //Others
    ArrayList<Answer> gameAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game);

        questionsRemaining = 10;

        //Set up recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.activityGameRv.setLayoutManager(layoutManager);
        binding.activityGameRv.setHasFixedSize(true);
        binding.activityGameRv.setNestedScrollingEnabled(false);
        answersAdapter = new AnswersAdapter(this, this);
        binding.activityGameRv.setAdapter(answersAdapter);

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
        getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_ANSWERED, null, GameActivity.this).forceLoad();
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle bundle) {
        switch (id) {
            case LOADER_ID_CURSOR_ANSWERED:
                String answeredSelection = AnsweredEntry.ANSWERED_STATUS + "=?";
                String[] answeredSelectionArgs = {String.valueOf(Constants.ANSWER_STATUS_CORRECT)};
                return new CursorLoader(this,
                        AnsweredEntry.ANSWERED_URI,
                        Constants.PROJECTION_ANSWERS,
                        answeredSelection,
                        answeredSelectionArgs,
                        null);

            case LOADER_ID_CURSOR_QUESTIONS:
                return new CursorLoader(this,
                        QuestionsEntry.QUESTIONS_URI,
                        Constants.PROJECTION_QUESTIONS,
                        questionsSelection,
                        questionsSelectionArgs,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_CURSOR_ANSWERED:
                StringBuilder questionSelectionBuilder = new StringBuilder();
                Cursor cursorAnswered = (Cursor) data;
                if (cursorAnswered != null) {
                    if (cursorAnswered.getCount() > 0) {

                        //Find all questions answered filtering by FirebaseId. These will be later passed as selection arguments.
                        questionsSelectionArgs = new String[cursorAnswered.getCount()];
                        questionSelectionBuilder.append(QuestionsEntry.QUESTIONS_FIREBASE_ID + " NOT IN (");
                        for (int i = 0; i < cursorAnswered.getCount(); i++) {
                            if (i < cursorAnswered.getCount() - 1) {
                                questionSelectionBuilder.append("?, ");
                            } else if (i == cursorAnswered.getCount() - 1) {
                                questionSelectionBuilder.append("?)");
                            }
                            cursorAnswered.moveToPosition(i);
                            questionsSelectionArgs[i] = cursorAnswered.getString(cursorAnswered.getColumnIndex(AnsweredEntry.ANSWERED_FIREBASE_QUESTION_ID));
                        }

                        questionsSelection = questionSelectionBuilder.toString();
                        Timber.e("SELECTION BUILDER IS%s", questionsSelection);

                    } else {
                        Timber.v("There are no answers yet in the Database");
                        questionsSelection = null;
                        questionsSelectionArgs = null;
                    }

                    //Call questions loader
                    getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_QUESTIONS, null, GameActivity.this).forceLoad();

                    cursorAnswered.close();
                } else {
                    Timber.e("Cursor NotAnswered is null");
                }
                break;
            case LOADER_ID_CURSOR_QUESTIONS:
                cursorQuestions = (Cursor) data;
                if (cursorQuestions != null) {
                    if (cursorQuestions.getCount() > 0) {
                        //If there are less than 10 questions remaining in the database, questions remaining will be reassigned
                        if (cursorQuestions.getCount() < 10) {
                            questionsRemaining = cursorQuestions.getCount();
                        }
                        Timber.e("question count %s", cursorQuestions.getCount());
                        //Load Question
                        loadNextQuestion(cursorQuestions);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.toast_no_more_questions), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Timber.e("Could not receive cursorQuestions to load question");
                }
                break;
            default:
                Timber.e("Could not return cursors");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        getLoaderManager().destroyLoader(LOADER_ID_CURSOR_QUESTIONS);
    }

    private void loadNextQuestion(final Cursor cursorQuestions) {
        //Load question from cursorQuestions into adapter
        Random random = new Random();
        cursorQuestions.moveToPosition(random.nextInt(cursorQuestions.getCount()));
        questionFirebaseId = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_FIREBASE_ID));
        String questionBody = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_BODY));
        questionCorrectAnswer = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_CORRECT_ANSWER));
        String questionIncorrectAnswer1 = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_01));
        String questionIncorrectAnswer2 = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_02));
        String questionIncorrectAnswer3 = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_03));

        //Shuffle answer options
        binding.activityGameTvQuestion.setText(questionBody);
        List<String> answers = new ArrayList<>();
        answers.add(questionCorrectAnswer);
        answers.add(questionIncorrectAnswer1);
        answers.add(questionIncorrectAnswer2);
        answers.add(questionIncorrectAnswer3);
        Collections.shuffle(answers);
        answersAdapter.updateAnswers(answers);

        //Setup Timer
        countDownTimer = new CountDownTimer(getResources().getInteger(R.integer.score_timer_question_interval), getResources().getInteger(R.integer.score_timer_tick_interval)) {

            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                binding.activityGameTimer.setText(String.valueOf(millisUntilFinished / getResources().getInteger(R.integer.score_timer_tick_interval)));
            }

            public void onFinish() {
                if (questionsRemaining > 0) {
                    loadNextQuestion(cursorQuestions);
                } else {
                    StartResultActivity();
                }
            }
        }.start();
    }

    @Override
    public void OnClick(String chosenAnswer) {
        countDownTimer.cancel();
        int answeredStatus = Constants.ANSWER_STATUS_INCORRECT;
        numberOfQuestionsAnswered ++;
        scoreQuestion = 0;
        scoreTime = 0;
        answerTime = (getResources().getInteger(R.integer.score_timer_question_interval) - timeLeft) / (float) getResources().getInteger(R.integer.score_timer_tick_interval);

        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        String formattedAnswerTime = formatter.format(answerTime);

        //Calculate score for correct answers
        if (chosenAnswer.equals(questionCorrectAnswer)) {
            Toast.makeText(this, "FUCK YEAH", Toast.LENGTH_SHORT).show();
            answeredStatus = Constants.ANSWER_STATUS_CORRECT;
            scoreTime = Math.round(timeLeft / (getResources().getInteger(R.integer.score_timer_tick_interval)));
            scoreTotalTime = scoreTotalTime + scoreTime;
            scoreQuestion = getResources().getInteger(R.integer.score_per_question);
            scoreTotalQuestions = scoreTotalQuestions + scoreQuestion;
            numberOfQuestionsAnsweredCorrect ++;
        }

        //Add answer to database
        Answer answer = new Answer(questionFirebaseId,
                answeredStatus,
                chosenAnswer,
                scoreQuestion,
                scoreTime,
                formattedAnswerTime);
        gameAnswers.add(answer);

        //If the quiz is not over, load next question. If the quiz is over go to result activity
        if (questionsRemaining > 0) {
            loadNextQuestion(cursorQuestions);
            Timber.e("QUESTIONS REMAINING %s", questionsRemaining);
            questionsRemaining = questionsRemaining - 1;
        } else {
            scoreGame = scoreTotalQuestions + scoreTotalTime;
            Toast.makeText(this, "Quiz is over", Toast.LENGTH_SHORT).show();
        }
    }

    private void StartResultActivity() {
        Intent goToResultActivity = new Intent(this, ResultActivity.class);
        goToResultActivity.putParcelableArrayListExtra(Constants.INTENT_ACTIVITY_RESULT_ANSWER_ARRAY, gameAnswers);
        goToResultActivity.putExtra(Constants.INTENT_ACTIVITY_RESULT_SCORE_TOTAL_QUESTIONS, scoreTotalQuestions);
        goToResultActivity.putExtra(Constants.INTENT_ACTIVITY_RESULT_SCORE_TOTAL_TIME, scoreTotalTime);
        goToResultActivity.putExtra(Constants.INTENT_ACTIVITY_RESULT_NUMBER_QUESTIONS, numberOfQuestionsAnswered);
        goToResultActivity.putExtra(Constants.INTENT_ACTIVITY_RESULT_NUMBER_QUESTIONS_CORRECT, numberOfQuestionsAnsweredCorrect);
        startActivity(goToResultActivity);
    }
}
