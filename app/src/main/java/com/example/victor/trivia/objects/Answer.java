package com.example.victor.trivia.objects;

import android.os.Parcel;
import android.os.Parcelable;

/******
 * Created by Victor on 1/9/2019.
 ******/
public class Answer implements Parcelable {
    private String answerFirebaseQuestionId;
    private int answerStatus;
    private String answerAnswer;
    private int answerScoreQuestion;
    private int answerScoreTime;
    private String answerTime;
    private int answerCategory;

    //Parcelable Implementation
    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(answerFirebaseQuestionId);
        parcel.writeInt(answerStatus);
        parcel.writeString(answerAnswer);
        parcel.writeInt(answerScoreQuestion);
        parcel.writeInt(answerScoreTime);
        parcel.writeString(answerTime);
        parcel.writeInt(answerCategory);
    }

    //Constructors
    private Answer(Parcel in) {
        answerFirebaseQuestionId = in.readString();
        answerStatus = in.readInt();
        answerAnswer = in.readString();
        answerScoreQuestion = in.readInt();
        answerScoreTime = in.readInt();
        answerTime = in.readString();
        answerCategory = in.readInt();
    }

    public Answer() {}

    public Answer(String answerFirebaseQuestionId, int answerStatus, String answerAnswer, int answerScoreQuestion, int answerScoreTime, String answerTime, int answerCategory) {
        this.answerFirebaseQuestionId = answerFirebaseQuestionId;
        this.answerStatus = answerStatus;
        this.answerAnswer = answerAnswer;
        this.answerScoreQuestion = answerScoreQuestion;
        this.answerScoreTime = answerScoreTime;
        this.answerTime = answerTime;
        this.answerCategory = answerCategory;
    }

    //Getters
    public String getAnswerFirebaseQuestionId() {return answerFirebaseQuestionId;}
    public int getAnswerStatus() {return answerStatus;}
    public String getAnswerAnswer() {return answerAnswer;}
    public int getAnswerScoreQuestion() {return answerScoreQuestion;}
    public int getAnswerScoreTime() {return answerScoreTime;}
    public String getAnswerTime() {return answerTime;}
    public int getAnswerCategory() {return answerCategory;}
}
