package com.example.victor.trivia.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.victor.trivia.objects.Answer;
import com.example.victor.trivia.objects.Question;

import com.example.victor.trivia.databinding.CardResultAnswersBinding;

import java.util.List;

/******
 * Created by Victor on 1/14/2019.
 ******/
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private Context context;
    private List<Question> questions;
    private List<Answer> answers;

    public ResultAdapter(Context context, List<Question> questions, List<Answer> answers) {
        this.context = context;
        this.questions = questions;
        this.answers = answers;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        CardResultAnswersBinding cardResultAnswersBinding = CardResultAnswersBinding.inflate(layoutInflater, viewGroup, false);
        return new ResultViewHolder(cardResultAnswersBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ResultViewHolder resultViewHolder, int i) {
        Question question = questions.get(i);
        Answer answer = answers.get(i);
        String answeredAnswer = answer.getAnswerAnswer();
        String[] answers = new String[]{
                question.getQuestionCorrectAnswer(),
                question.getQuestionIncorrectAnswer01(),
                question.getQuestionIncorrectAnswer02(),
                question.getQuestionIncorrectAnswer03()
        };

        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswerTime.setText(answer.getAnswerTime());
        resultViewHolder.cardResultAnswersBinding.cardResultTvQuestionBody.setText(question.getQuestionBody());
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer01.setText(question.getQuestionCorrectAnswer());
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer02.setText(question.getQuestionIncorrectAnswer01());
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer03.setText(question.getQuestionIncorrectAnswer02());
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer04.setText(question.getQuestionIncorrectAnswer03());
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswerDescription.setText(question.getQuestionDescription());
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswerPhoto.setText(question.getQuestionPhotoUrl());

    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public void updateResult(List<Question> questions, List<Answer> answers){
        this.questions = questions;
        this.answers = answers;
        notifyDataSetChanged();
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {

        private final CardResultAnswersBinding cardResultAnswersBinding;

        public ResultViewHolder(CardResultAnswersBinding cardResultAnswersBinding) {
            super(cardResultAnswersBinding.getRoot());
            this.cardResultAnswersBinding = cardResultAnswersBinding;
        }
    }
}
