package com.example.victor.trivia.objects;

import android.os.Parcel;
import android.os.Parcelable;

/******
 * Created by Victor on 12/4/2018.
 ******/
public class Score implements Parcelable {
    private int userQuestionPoints;
    private int userTimePoints;
    private int userLevel;
    private int userNumberOfQuestionsAnswered;
    private int userNumberOfQuestionsAnsweredCorrect;
    private float userPercentageCorrect;
    private float userAverageAnswerTime;

    //Parcelable Implementation
    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(userQuestionPoints);
        parcel.writeInt(userTimePoints);
        parcel.writeInt(userLevel);
        parcel.writeInt(userNumberOfQuestionsAnswered);
        parcel.writeInt(userNumberOfQuestionsAnsweredCorrect);
        parcel.writeFloat(userPercentageCorrect);
        parcel.writeFloat(userAverageAnswerTime);
    }

    //Constructors
    protected Score(Parcel in) {
        userQuestionPoints = in.readInt();
        userTimePoints = in.readInt();
        userLevel = in.readInt();
        userNumberOfQuestionsAnswered = in.readInt();
        userNumberOfQuestionsAnsweredCorrect = in.readInt();
        userPercentageCorrect = in.readFloat();
        userAverageAnswerTime = in.readFloat();
    }

    public Score() {
        //Empty Constructor
    }

    public Score(int userQuestionPoints, int userTimePoints, int userLevel, int userNumberOfQuestionsAnswered, int userNumberOfQuestionsAnsweredCorrect, float userPercentageCorrect, float userAverageAnswerTime) {
        this.userQuestionPoints = userQuestionPoints;
        this.userTimePoints = userTimePoints;
        this.userLevel = userLevel;
        this.userNumberOfQuestionsAnswered = userNumberOfQuestionsAnswered;
        this.userNumberOfQuestionsAnsweredCorrect = userNumberOfQuestionsAnsweredCorrect;
        this.userPercentageCorrect = userPercentageCorrect;
        this.userAverageAnswerTime = userAverageAnswerTime;
    }

    //Getters
    public int getUserQuestionPoints() {return userQuestionPoints;}
    public int getUserTimePoints() {return userTimePoints;}
    public int getUserLevel() {return userLevel;}
    public int getUserNumberOfQuestionsAnswered() {return userNumberOfQuestionsAnswered;}
    public int getUserNumberOfQuestionsAnsweredCorrect() {return userNumberOfQuestionsAnsweredCorrect;}
    public float getUserPercentageCorrect() {return userPercentageCorrect;}
    public float getUserAverageAnswerTime() {return userAverageAnswerTime;}
}
