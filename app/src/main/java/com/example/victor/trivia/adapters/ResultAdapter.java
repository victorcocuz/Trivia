package com.example.victor.trivia.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.victor.trivia.R;
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
    private List<String> correctAnswers;

    public ResultAdapter(Context context) {
        this.context = context;
    }

    public void updateResults(List<Question> questions, List<Answer> answers, List<String> correctAnswers) {
        this.questions = questions;
        this.answers = answers;
        this.correctAnswers = correctAnswers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        CardResultAnswersBinding cardResultAnswersBinding = CardResultAnswersBinding.inflate(layoutInflater, viewGroup, false);
        return new ResultViewHolder(cardResultAnswersBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ResultViewHolder resultViewHolder, int position) {
        Question question = questions.get(position);
        Answer answer = answers.get(position);
        String answeredAnswer = answer.getAnswerAnswer();
        String correctAnswer = correctAnswers.get(position);

        String[] answerOptions = new String[]{
                question.getQuestionCorrectAnswer(),
                question.getQuestionIncorrectAnswer01(),
                question.getQuestionIncorrectAnswer02(),
                question.getQuestionIncorrectAnswer03()
        };

        //Set answer backgrounds for incorrect answer
        if(!answeredAnswer.equals(correctAnswer)){
            if (answeredAnswer.equals(answerOptions[0])) {
                resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer01.setBackgroundColor(context.getResources().getColor(R.color.colorSecondary));
            } else if (answeredAnswer.equals(answerOptions[1])) {
                resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer02.setBackgroundColor(context.getResources().getColor(R.color.colorSecondary));
            } else if (answeredAnswer.equals(answerOptions[2])) {
                resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer03.setBackgroundColor(context.getResources().getColor(R.color.colorSecondary));
            } else if (answeredAnswer.equals(answerOptions[3])) {
                resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer04.setBackgroundColor(context.getResources().getColor(R.color.colorSecondary));
            }
        }

        //Set answer backgrounds for correct answer
        if (correctAnswer.equals(answerOptions[0])) {
            resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer01.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else if (correctAnswer.equals(answerOptions[1])) {
            resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer02.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else if (correctAnswer.equals(answerOptions[2])) {
            resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer03.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else if (correctAnswer.equals(answerOptions[3])) {
            resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer04.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }

        //Set answer texts
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

    public class ResultViewHolder extends RecyclerView.ViewHolder {

        private final CardResultAnswersBinding cardResultAnswersBinding;

        public ResultViewHolder(CardResultAnswersBinding cardResultAnswersBinding) {
            super(cardResultAnswersBinding.getRoot());
            this.cardResultAnswersBinding = cardResultAnswersBinding;
        }
    }
}
