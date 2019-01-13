package com.example.victor.trivia.objects;

import android.os.Parcel;
import android.os.Parcelable;

/******
 * Created by Victor on 12/4/2018.
 ******/
public class Game implements Parcelable {
    private String[] gameAnswersFirebaseId;
    private int gameScoreTotalQuestions;
    private int gameScoreTotalTime;

    //Parcelable Implementation
    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeStringArray(gameAnswersFirebaseId);
        parcel.writeInt(gameScoreTotalQuestions);
        parcel.writeInt(gameScoreTotalTime);
    }

    //Constructors
    protected Game(Parcel in) {
        gameAnswersFirebaseId = in.createStringArray();
        gameScoreTotalQuestions = in.readInt();
        gameScoreTotalTime = in.readInt();
    }

    public Game() {}

    public Game(String[] gameAnswersFirebaseId, int gameScoreTotalQuestions, int gameScoreTotalTime) {
        this.gameAnswersFirebaseId = gameAnswersFirebaseId;
        this.gameScoreTotalQuestions = gameScoreTotalQuestions;
        this.gameScoreTotalTime = gameScoreTotalTime;
    }

//    Getters
    public String[] getGameAnswersFirebaseId() {return gameAnswersFirebaseId;}
    public int getGameScoreTotalQuestions() {return gameScoreTotalQuestions;}
    public int getGameScoreTotalTime() {return gameScoreTotalTime;}
}
