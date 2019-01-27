package com.example.victor.trivia.objects;

import android.os.Parcel;
import android.os.Parcelable;

/******
 * Created by Victor on 12/4/2018.
 ******/
@SuppressWarnings("ALL")
public class Game implements Parcelable {
    private String[] gameAnswersFireBaseId;
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

    //Constructors
    private Game(Parcel in) {
        gameAnswersFireBaseId = in.createStringArray();
        gameScoreTotalQuestions = in.readInt();
        gameScoreTotalTime = in.readInt();
    }

    @SuppressWarnings("unused")
    public Game() {
    }

    public Game(String[] gameAnswersFireBaseId, int gameScoreTotalQuestions, int gameScoreTotalTime) {
        this.gameAnswersFireBaseId = gameAnswersFireBaseId;
        this.gameScoreTotalQuestions = gameScoreTotalQuestions;
        this.gameScoreTotalTime = gameScoreTotalTime;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeStringArray(gameAnswersFireBaseId);
        parcel.writeInt(gameScoreTotalQuestions);
        parcel.writeInt(gameScoreTotalTime);
    }
}
