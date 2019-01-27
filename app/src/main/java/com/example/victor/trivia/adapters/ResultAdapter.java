package com.example.victor.trivia.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.victor.trivia.R;
import com.example.victor.trivia.databinding.CardResultAnswersBinding;
import com.example.victor.trivia.objects.Answer;
import com.example.victor.trivia.objects.Question;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

/******
 * Created by Victor on 1/14/2019.
 ******/
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private final Context context;
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
    public void onBindViewHolder(@NonNull ResultViewHolder resultViewHolder, int position) {
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

        //Set the check drawable for correct answers and uncheck drawable for incorrect answers
        if (answeredAnswer.equals(correctAnswer)) {
            resultViewHolder.cardResultAnswersBinding.cardResultIvCheck.setImageResource(R.drawable.ic_check);
        } else {
            resultViewHolder.cardResultAnswersBinding.cardResultIvCheck.setImageResource(R.drawable.ic_uncheck);
        }

        //Set answer backgrounds for incorrect answer
        if (!answeredAnswer.equals(correctAnswer)) {
            if (answeredAnswer.equals(answerOptions[0])) {
                resultViewHolder.cardResultAnswersBinding.cardResultRvAnswer01.setBackgroundColor(context.getResources().getColor(R.color.colorIncorrect));
            } else if (answeredAnswer.equals(answerOptions[1])) {
                resultViewHolder.cardResultAnswersBinding.cardResultRvAnswer02.setBackgroundColor(context.getResources().getColor(R.color.colorIncorrect));
            } else if (answeredAnswer.equals(answerOptions[2])) {
                resultViewHolder.cardResultAnswersBinding.cardResultRvAnswer03.setBackgroundColor(context.getResources().getColor(R.color.colorIncorrect));
            } else if (answeredAnswer.equals(answerOptions[3])) {
                resultViewHolder.cardResultAnswersBinding.cardResultRvAnswer04.setBackgroundColor(context.getResources().getColor(R.color.colorIncorrect));
            }
        }

        //Set answer backgrounds for correct answer
        if (correctAnswer.equals(answerOptions[0])) {
            resultViewHolder.cardResultAnswersBinding.cardResultRvAnswer01.setBackgroundColor(context.getResources().getColor(R.color.colorCorrect));
        } else if (correctAnswer.equals(answerOptions[1])) {
            resultViewHolder.cardResultAnswersBinding.cardResultRvAnswer02.setBackgroundColor(context.getResources().getColor(R.color.colorCorrect));
        } else if (correctAnswer.equals(answerOptions[2])) {
            resultViewHolder.cardResultAnswersBinding.cardResultRvAnswer03.setBackgroundColor(context.getResources().getColor(R.color.colorCorrect));
        } else if (correctAnswer.equals(answerOptions[3])) {
            resultViewHolder.cardResultAnswersBinding.cardResultRvAnswer04.setBackgroundColor(context.getResources().getColor(R.color.colorCorrect));
        }

        //Set answer texts
        String questionNumber = context.getString(R.string.activity_result_question)
                + " "
                + String.valueOf(position + 1)
                + "/"
                + context.getResources().getInteger(R.integer.score_questions_per_quiz);
        resultViewHolder.cardResultAnswersBinding.cardResultQuestionNumber.setText(questionNumber);

        String answerTime = context.getString(R.string.activity_result_time)
                + " "
                + answer.getAnswerTime()
                + "s";
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswerTime.setText(answerTime);

        resultViewHolder.cardResultAnswersBinding.cardResultTvQuestionBody.setText(question.getQuestionBody());
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer01.setText(question.getQuestionCorrectAnswer());
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer02.setText(question.getQuestionIncorrectAnswer01());
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer03.setText(question.getQuestionIncorrectAnswer02());
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswer04.setText(question.getQuestionIncorrectAnswer03());
        resultViewHolder.cardResultAnswersBinding.cardResultTvAnswerDescription.setText(question.getQuestionDescription());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Picasso.get()
                    .load(question.getQuestionPhotoUrl())
                    .error(Objects.requireNonNull(context.getDrawable(R.drawable.ic_uncheck)))
                    .into(resultViewHolder.cardResultAnswersBinding.cardResultIvAnswerPhoto);
        }
    }

    @Override
    public int getItemCount() {
        if (questions != null) {
            if (questions.size() > 0) {
                return questions.size();
            }
        }
        return 0;
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {

        private final CardResultAnswersBinding cardResultAnswersBinding;

        ResultViewHolder(CardResultAnswersBinding cardResultAnswersBinding) {
            super(cardResultAnswersBinding.getRoot());
            this.cardResultAnswersBinding = cardResultAnswersBinding;
        }
    }
}
