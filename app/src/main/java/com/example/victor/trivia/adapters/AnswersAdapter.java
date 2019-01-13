package com.example.victor.trivia.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.victor.trivia.databinding.CardAnswersBinding;

import java.util.List;

/******
 * Created by Victor on 1/12/2019.
 ******/
public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswersViewHolder> {

    private Context context;
    private List<String> answers;

    public interface AnswersAdapterOnClickHandler {
        void OnClick(String answer);
    }

    private final AnswersAdapterOnClickHandler answersAdapterOnClickHandler;

    public AnswersAdapter(Context context, AnswersAdapterOnClickHandler answersAdapterOnClickHandler) {
        this.answersAdapterOnClickHandler = answersAdapterOnClickHandler;
        this.context = context;
    }

    @NonNull
    @Override
    public AnswersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        CardAnswersBinding cardAnswersBinding = CardAnswersBinding.inflate(layoutInflater, viewGroup, false);
        return new AnswersViewHolder(cardAnswersBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswersViewHolder answersViewHolder, int i) {
        if (answers != null) {
            if (answers.size() > 0) {
                answersViewHolder.cardAnswersBinding.cardAnswersTvAnswer1.setText(answers.get(i));
            }
        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public void updateAnswers(List<String> answers) {
        this.answers = answers;
        notifyDataSetChanged();
    }

    public class AnswersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CardAnswersBinding cardAnswersBinding;

        public AnswersViewHolder(CardAnswersBinding cardAnswersBinding) {
            super(cardAnswersBinding.getRoot());
            this.cardAnswersBinding = cardAnswersBinding;
            cardAnswersBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String answer = answers.get(getAdapterPosition());
            answersAdapterOnClickHandler.OnClick(answer);
        }
    }
}
