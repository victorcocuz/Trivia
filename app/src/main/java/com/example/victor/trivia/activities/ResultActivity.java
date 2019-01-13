package com.example.victor.trivia.activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.victor.trivia.R;
import com.example.victor.trivia.helpers.Constants;
import com.example.victor.trivia.objects.Answer;

import java.util.ArrayList;

import timber.log.Timber;

public class ResultActivity extends AppCompatActivity {
    //Intent information
    ArrayList<Answer> answers;
    int scoreTotalQuestions;
    int scoreTotalTime;
    int numberOfQuestionsAnswered;
    int numberOfQuestionsAnsweredCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                answers = bundle.getParcelableArrayList(Constants.INTENT_ACTIVITY_RESULT_ANSWER_ARRAY);
                scoreTotalQuestions = bundle.getInt(Constants.INTENT_ACTIVITY_RESULT_NUMBER_QUESTIONS);
                scoreTotalTime = bundle.getInt(Constants.INTENT_ACTIVITY_RESULT_NUMBER_QUESTIONS_CORRECT);
                numberOfQuestionsAnswered = bundle.getInt(Constants.INTENT_ACTIVITY_RESULT_SCORE_TOTAL_QUESTIONS);
                numberOfQuestionsAnsweredCorrect = bundle.getInt(Constants.INTENT_ACTIVITY_RESULT_SCORE_TOTAL_TIME);
                Timber.e("SCORE QUESTIONS IS %s", scoreTotalQuestions);
                Timber.e("SCORE TOTAL TIME IS %s", scoreTotalTime);
                Timber.e("QUESTIONS ANSWERED ARE %s", numberOfQuestionsAnswered);
                Timber.e("QUESTIONS ANSWERED CORRECT ARE %s", numberOfQuestionsAnsweredCorrect);
            }
        }

        //        answersDatabaseReference.push().setValue(answer);
//
//        //Setup Firebase and synchronize with local database;
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference answersDatabaseReference = firebaseDatabase.getReference().child(AnsweredEntry.ANSWERED_TABLE_NAME);
//
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                String key;
//                Answer answer = dataSnapshot.getValue(Answer.class);
//
//                if (answer != null) {
////                    Boolean questionIsAlreadyAdded = false;
//                    key = dataSnapshot.getKey();
//
////                    if (cursorQuestions != null && key != null) {
////                        if (cursorQuestions.getCount() > 0) {
////                            for (int i = 0; i < cursorQuestions.getCount(); i++) {
////                                cursorQuestions.moveToPosition(i);
////                                if (key.equals(cursorQuestions.getString(cursorQuestions.getColumnIndex(QuestionsEntry.QUESTIONS_FIREBASE_ID)))) {
////                                    questionIsAlreadyAdded = true;
////                                }
////                            }
////                        }
////                        if (!questionIsAlreadyAdded) {
//                            //Add question to Questions Table in Database
//                            ContentValues answerValues = new ContentValues();
//                            answerValues.put(AnsweredEntry.ANSWERED_FIREBASE_ID, key);
//                            answerValues.put(AnsweredEntry.ANSWERED_FIREBASE_USER_ID, "");
//                            answerValues.put(AnsweredEntry.ANSWERED_FIREBASE_QUESTION_ID, answer.getAnswerFirebaseQuestionId());
//                            answerValues.put(AnsweredEntry.ANSWERED_STATUS, answer.getAnswerStatus());
//                            answerValues.put(AnsweredEntry.ANSWERED_ANSWER, answer.getAnswerAnswer());
//                            answerValues.put(AnsweredEntry.ANSWERED_SCORE_QUESTION, answer.getAnswerScoreQuestion());
//                            answerValues.put(AnsweredEntry.ANSWERED_SCORE_TIME, answer.getAnswerScoreTime());
//                            answerValues.put(AnsweredEntry.ANSWERED_TIME, answer.getAnswerTime());
//                            getContentResolver().insert(AnsweredEntry.ANSWERED_URI, answerValues);
////                        }
////                    } else {
////                        Timber.e("Could not download questions from FireBase");
////                    }
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//
//        answersDatabaseReference.addChildEventListener(childEventListener);
    }
}
