package com.example.victor.trivia.objects;

import android.os.Parcel;
import android.os.Parcelable;

/******
 * Created by Victor on 1/9/2019.
 ******/
public class Answered implements Parcelable {
    private String AnsweredFirebaseId;
    private String AnsweredAnswer;

    public static final Creator<Answered> CREATOR = new Creator<Answered>() {
        @Override
        public Answered createFromParcel(Parcel in) {
            return new Answered(in);
        }

        @Override
        public Answered[] newArray(int size) {
            return new Answered[size];
        }
    };

    public Answered() {}

    private Answered(Parcel in) {
        AnsweredFirebaseId = in.readString();
        AnsweredAnswer = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(AnsweredFirebaseId);
        parcel.writeString(AnsweredAnswer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Answered(String questionAnsweredFirebaseId, String questionAnswer) {
        this.AnsweredFirebaseId = questionAnsweredFirebaseId;
        this.AnsweredAnswer = questionAnswer;
    }

    public String getAnsweredFirebaseId() {return AnsweredFirebaseId;}
    public String getAnsweredAnswer() {return AnsweredAnswer;}
}
