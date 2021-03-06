package com.example.victor.trivia.activities;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.victor.trivia.R;
import com.example.victor.trivia.adapters.AnswersAdapter;
import com.example.victor.trivia.data.TriviaContract.AnswersEntry;
import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;
import com.example.victor.trivia.databinding.ActivityGameBinding;
import com.example.victor.trivia.objects.Answer;
import com.example.victor.trivia.objects.Question;
import com.example.victor.trivia.objects.Score;
import com.example.victor.trivia.utilities.Constants;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

//Helpers

@SuppressWarnings("deprecation")
public class GameActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks,
        AnswersAdapter.AnswersAdapterOnClickHandler {

    //Extras for result
    private final ArrayList<Answer> forResultAnswers = new ArrayList<>();
    private Bundle mSavedInstanceState;

    //Loaders
    private static final int LOADER_ID_CURSOR_ANSWERED = 1;
    private static final int LOADER_ID_CURSOR_QUESTIONS = 2;

    //For Recycler view
    private Cursor cursorQuestions;
    private final ArrayList<Question> forResultQuestions = new ArrayList<>();
    private final ArrayList<String> forResultCorrectAnswers = new ArrayList<>();
    private String questionCorrectAnswer, questionBody;
    //Main
    private ActivityGameBinding binding;
    private String[] questionsSelectionArgs = null;
    private String questionsSelection = null;
    private List<Integer> cursorPositions = null;
    private AnswersAdapter answersAdapter;

    //For question building
    private ArrayList<String> answers;
    private String questionFireBaseId;

    //For scoring
    private CountDownTimer countDownTimer;
    private long timeLeft;
    private int scoreQuestion;
    private int scoreTime;
    private int scoreTotalQuestions;
    private int scoreTotalTime;
    private int numberOfQuestionsRemaining;
    private int numberOfQuestionsAnswered;
    private int numberOfQuestionsAnsweredCorrect;
    private float totalAnswerTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize main components
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game);
        mSavedInstanceState = savedInstanceState;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setSupportActionBar(binding.activityGameTb);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setIcon(getDrawable(R.drawable.ic_logo_small));
            }
        }
        setTitle(getString(R.string.activity_game));

        //Reset score
        numberOfQuestionsRemaining = getResources().getInteger(R.integer.score_questions_per_quiz);
        numberOfQuestionsAnswered = 0;
        numberOfQuestionsAnsweredCorrect = 0;
        scoreQuestion = 0;
        scoreTime = 0;
        scoreTotalQuestions = 0;
        scoreTotalTime = 0;

        //Set up recycler view
        binding.activityGameRv.setVisibility(View.INVISIBLE);
        binding.activityGameTvQuestion.setVisibility(View.INVISIBLE);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.activityGameRv.setLayoutManager(layoutManager);
        binding.activityGameRv.setHasFixedSize(true);
        binding.activityGameRv.setNestedScrollingEnabled(false);
        answersAdapter = new AnswersAdapter(this, this);
        binding.activityGameRv.setAdapter(answersAdapter);

        //Call answers loader
        if (mSavedInstanceState != null) {
            getSupportLoaderManager().restartLoader(LOADER_ID_CURSOR_ANSWERED, null, GameActivity.this).forceLoad();
        } else {
            getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_ANSWERED, null, GameActivity.this).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle bundle) {
        switch (id) {
            case LOADER_ID_CURSOR_ANSWERED:
                String answeredSelection = AnswersEntry.ANSWERS_STATUS + "=?";
                String[] answeredSelectionArgs = {String.valueOf(Constants.ANSWER_STATUS_CORRECT)};
                return new CursorLoader(this,
                        AnswersEntry.ANSWERS_URI,
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
                //noinspection ConstantConditions
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

                        //Find all questions answered filtering by FireBaseId. These will be later passed as selection arguments in the questions cursor
                        questionsSelectionArgs = new String[cursorAnswered.getCount()];
                        questionSelectionBuilder.append(QuestionsEntry.QUESTIONS_FIREBASE_ID + " NOT IN (");
                        for (int i = 0; i < cursorAnswered.getCount(); i++) {
                            if (i < cursorAnswered.getCount() - 1) {
                                questionSelectionBuilder.append("?, ");
                            } else if (i == cursorAnswered.getCount() - 1) {
                                questionSelectionBuilder.append("?)");
                            }
                            cursorAnswered.moveToPosition(i);
                            questionsSelectionArgs[i] = cursorAnswered.getString(cursorAnswered.getColumnIndex(AnswersEntry.ANSWERS_FIREBASE_QUESTION_ID));
                        }

                        questionsSelection = questionSelectionBuilder.toString();

                    } else {
                        Timber.v("There are no answers yet in the Database");
                        questionsSelection = null;
                        questionsSelectionArgs = null;
                    }

                    //Call questions loader
                    if (mSavedInstanceState != null) {
                        getSupportLoaderManager().restartLoader(LOADER_ID_CURSOR_QUESTIONS, null, GameActivity.this).forceLoad();
                    } else {
                        getSupportLoaderManager().initLoader(LOADER_ID_CURSOR_QUESTIONS, null, GameActivity.this).forceLoad();
                    }

                    getSupportLoaderManager().destroyLoader(LOADER_ID_CURSOR_ANSWERED);
                } else {
                    Timber.e("Cursor NotAnswered is null");
                }
                break;
            case LOADER_ID_CURSOR_QUESTIONS:
                cursorQuestions = (Cursor) data;
                if (cursorQuestions != null) {
                    if (cursorQuestions.getCount() > 0) {

                        //If there are less than 10 questions remaining in the database, questions remaining will be reassigned
                        if (cursorQuestions.getCount() < getResources().getInteger(R.integer.score_questions_per_quiz)) {
                            numberOfQuestionsRemaining = cursorQuestions.getCount() - 1;
                        }

                        //Create a random order for the cursor
                        cursorPositions = new ArrayList<>();
                        for (int i = 0; i < cursorQuestions.getCount(); i++) {
                            cursorPositions.add(i);
                        }
                        Collections.shuffle(cursorPositions);

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
        if (getSupportLoaderManager().getLoader(LOADER_ID_CURSOR_ANSWERED) != null) {
            getSupportLoaderManager().destroyLoader(LOADER_ID_CURSOR_ANSWERED);
        }
        if (getSupportLoaderManager().getLoader(LOADER_ID_CURSOR_QUESTIONS) != null) {
            getSupportLoaderManager().destroyLoader(LOADER_ID_CURSOR_QUESTIONS);
        }
    }

    @Override
    public void OnClick(String chosenAnswer) {
        countDownTimer.cancel();
        int answeredStatus = Constants.ANSWER_STATUS_INCORRECT;

        //Calculate score for all answers
        numberOfQuestionsAnswered++;
        scoreQuestion = 0;
        scoreTime = 0;
        float answerTime = (getResources().getInteger(R.integer.score_timer_question_interval) - timeLeft) / (float) getResources().getInteger(R.integer.score_timer_tick_interval);
        totalAnswerTime = answerTime + totalAnswerTime;
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        String formattedAnswerTime = formatter.format(answerTime);

        //Calculate score for correct answers
        if (chosenAnswer.equals(questionCorrectAnswer)) {
            numberOfQuestionsAnsweredCorrect++;
            answeredStatus = Constants.ANSWER_STATUS_CORRECT;
            scoreTime = Math.round(timeLeft / (getResources().getInteger(R.integer.score_timer_tick_interval))) + 1;
            scoreTotalTime = scoreTotalTime + scoreTime;
            scoreQuestion = getResources().getInteger(R.integer.score_per_question);
            scoreTotalQuestions = scoreTotalQuestions + scoreQuestion;
        }

        //Add information for result;
        Question forResultQuestion = new Question(
                cursorQuestions.getInt(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_CATEGORY)),
                questionBody,
                answers.get(0),
                answers.get(1),
                answers.get(2),
                answers.get(3),
                cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_ANSWER_DESCRIPTION)),
                cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_PHOTO_URL)));
        forResultQuestions.add(forResultQuestion);

        Answer forResultAnswer = new Answer(questionFireBaseId,
                answeredStatus,
                chosenAnswer,
                scoreQuestion,
                scoreTime,
                formattedAnswerTime,
                cursorQuestions.getInt(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_CATEGORY)));
        forResultAnswers.add(forResultAnswer);

        //If the quiz is not over, load next question. If the quiz is over go to result activity
        if (numberOfQuestionsRemaining > 0) {
            loadNextQuestion(cursorQuestions);
        } else {
            StartResultActivity();
        }
    }

    private void loadNextQuestion(final Cursor cursorQuestions) {
        String questionsRemaining = getResources().getInteger(R.integer.score_questions_per_quiz) - numberOfQuestionsRemaining + "/" + getResources().getInteger(R.integer.score_questions_per_quiz);
        binding.activityGameTvQuestionsRemaining.setText(questionsRemaining);
        numberOfQuestionsRemaining = numberOfQuestionsRemaining - 1;

        //Load question from cursorQuestions into adapter in a random and non repeating order
        cursorQuestions.moveToPosition(cursorPositions.get(numberOfQuestionsRemaining));
        questionFireBaseId = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_FIREBASE_ID));
        questionBody = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_BODY));
        questionCorrectAnswer = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_CORRECT_ANSWER));
        String questionIncorrectAnswer1 = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_01));
        String questionIncorrectAnswer2 = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_02));
        String questionIncorrectAnswer3 = cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_INCORRECT_ANSWER_03));

        //Shuffle answer options
        binding.activityGameTvQuestion.setText(questionBody);
        forResultCorrectAnswers.add(questionCorrectAnswer);
        answers = new ArrayList<>();
        answers.add(questionCorrectAnswer);
        answers.add(questionIncorrectAnswer1);
        answers.add(questionIncorrectAnswer2);
        answers.add(questionIncorrectAnswer3);
        Collections.shuffle(answers);
        answersAdapter.updateAnswers(answers);

        if (binding.activityGameRv.getVisibility() == View.INVISIBLE) {
            binding.activityGameRv.setVisibility(View.VISIBLE);
        }
        if (binding.activityGameTvQuestion.getVisibility() == View.INVISIBLE) {
            binding.activityGameTvQuestion.setVisibility(View.VISIBLE);
        }

        //Setup Timer
        countDownTimer = new CountDownTimer(getResources().getInteger(R.integer.score_timer_question_interval), getResources().getInteger(R.integer.score_timer_tick_interval)) {

            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                binding.activityGameTvTimer.setText(String.valueOf(millisUntilFinished / getResources().getInteger(R.integer.score_timer_tick_interval)));
            }

            public void onFinish() {
                if (numberOfQuestionsRemaining > 0) {
                    loadNextQuestion(cursorQuestions);
                } else {
                    StartResultActivity();
                }
            }
        }.start();
    }

    private void StartResultActivity() {
        Score forResultScore;
        if (numberOfQuestionsAnswered > 0) {
            forResultScore = new Score(
                    scoreTotalQuestions,
                    scoreTotalTime,
                    0,
                    numberOfQuestionsAnswered,
                    numberOfQuestionsAnsweredCorrect,
                    (float) numberOfQuestionsAnsweredCorrect / (float) numberOfQuestionsAnswered,
                    totalAnswerTime / (float) numberOfQuestionsAnswered);
        } else {
            forResultScore = new Score(
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0);
        }
        Intent goToResultActivity = new Intent(this, ResultActivity.class);
        goToResultActivity.putParcelableArrayListExtra(Constants.INTENT_ACTIVITY_RESULT_QUESTIONS_ARRAY, forResultQuestions);
        goToResultActivity.putParcelableArrayListExtra(Constants.INTENT_ACTIVITY_RESULT_ANSWERS_ARRAY, forResultAnswers);
        goToResultActivity.putExtra(Constants.INTENT_ACTIVITY_RESULT_SCORE, forResultScore);
        goToResultActivity.putExtra(Constants.INTENT_ACTIVITY_RESULT_CORRECT_ANSWERS, forResultCorrectAnswers);
        startActivity(goToResultActivity);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        countDownTimer.cancel();
        finish();
    }
}
