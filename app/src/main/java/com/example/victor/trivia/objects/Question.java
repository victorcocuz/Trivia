package com.example.victor.trivia.objects;

import android.os.Parcel;
import android.os.Parcelable;

/******
 * Created by Victor on 12/4/2018.
 ******/
public class Question implements Parcelable {
    private int questionId;
    private int questionType;
    private int questionCategory;
    private int questionDifficulty;
    private String questionBody;
    private String questionCorrectAnswer;
    private String questionIncorrectAnswer01;
    private String questionIncorrectAnswer02;
    private String questionIncorrectAnswer03;
    private String questionDescription;
    private String questionPhotoUrl;

    //Parcelable Implementation
    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(questionId);
        parcel.writeString(questionBody);
        parcel.writeString(questionCorrectAnswer);
        parcel.writeString(questionIncorrectAnswer01);
        parcel.writeString(questionIncorrectAnswer02);
        parcel.writeString(questionIncorrectAnswer03);
        parcel.writeString(questionDescription);
        parcel.writeString(questionPhotoUrl);
    }

    //Constructors
    private Question(Parcel in) {
        questionCategory = in.readInt();
        questionBody = in.readString();
        questionCorrectAnswer = in.readString();
        questionIncorrectAnswer01 = in.readString();
        questionIncorrectAnswer02 = in.readString();
        questionIncorrectAnswer03 = in.readString();
        questionDescription = in.readString();
        questionPhotoUrl = in.readString();
    }

    public Question() {
    }

    public Question(int questionCategory, String questionBody, String questionCorrectAnswer, String questionIncorrectAnswer01, String questionIncorrectAnswer02, String questionIncorrectAnswer03, String questionAnswerDescription, String questionPhotoUrl) {
        this.questionCategory = questionCategory;
        this.questionBody = questionBody;
        this.questionCorrectAnswer = questionCorrectAnswer;
        this.questionIncorrectAnswer01 = questionIncorrectAnswer01;
        this.questionIncorrectAnswer02 = questionIncorrectAnswer02;
        this.questionIncorrectAnswer03 = questionIncorrectAnswer03;
        this.questionDescription = questionAnswerDescription;
        this.questionPhotoUrl = questionPhotoUrl;
    }

    //Getters
    public int getQuestionCategory() {
        return questionCategory;
    }

    public String getQuestionBody() {
        return questionBody;
    }

    public String getQuestionCorrectAnswer() {
        return questionCorrectAnswer;
    }

    public String getQuestionIncorrectAnswer01() {
        return questionIncorrectAnswer01;
    }

    public String getQuestionIncorrectAnswer02() {
        return questionIncorrectAnswer02;
    }

    public String getQuestionIncorrectAnswer03() {
        return questionIncorrectAnswer03;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public String getQuestionPhotoUrl() {
        return questionPhotoUrl;
    }

}
