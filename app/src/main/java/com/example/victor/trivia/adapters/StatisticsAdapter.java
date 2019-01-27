package com.example.victor.trivia.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.victor.trivia.R;
import com.example.victor.trivia.databinding.CardStatisticsBinding;

/******
 * Created by Victor on 1/20/2019.
 ******/
public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder> {

    private Context context;
    private int[] questionsPositions;
    private int[] questionsPerCategory;
    private int[] questionsPerCategoryPercentage;
    private int counter;

    public StatisticsAdapter(Context context) {
        this.context = context;
    }

    public void updateResults(int[] questionsPositions, int[] questionsPerCategory, int[] questionsPerCategoryPercentage, int counter) {
        this.questionsPositions = new int[counter];
        this.questionsPerCategory = new int[counter];
        this.questionsPerCategoryPercentage = new int[counter];
        this.questionsPositions = questionsPositions;
        this.questionsPerCategory = questionsPerCategory;
        this.questionsPerCategoryPercentage = questionsPerCategoryPercentage;
        this.counter = counter;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatisticsAdapter.StatisticsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        CardStatisticsBinding cardStatisticsBinding = CardStatisticsBinding.inflate(layoutInflater, viewGroup, false);
        return new StatisticsViewHolder(cardStatisticsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsAdapter.StatisticsViewHolder statisticsViewHolder, int position) {
        String[] categories = context.getResources().getStringArray(R.array.array_categories);

        TypedArray imagesArray = context.getResources().obtainTypedArray(R.array.array_logos);
        statisticsViewHolder.cardStatisticsBinding.cardStatisticsIvCategoryLogo.setImageResource(imagesArray.getResourceId(questionsPositions[position], -1));
        imagesArray.recycle();

        statisticsViewHolder.cardStatisticsBinding.cardStatisticsTvCategory.setText(categories[questionsPositions[position] + 1]);
        String questionsAnswered = String.valueOf(questionsPerCategory[position]) + " " + context.getString(R.string.statistics_questions_answered);
        statisticsViewHolder.cardStatisticsBinding.cardStatisticsTvNumberOfAnswers.setText(questionsAnswered);
        statisticsViewHolder.cardStatisticsBinding.cardStatisticsTvPercentageCorrect.setText(String.format("%s%%", String.valueOf(questionsPerCategoryPercentage[position])));

        //Hide last line in cardview
        if (position == counter - 1) {
            statisticsViewHolder.cardStatisticsBinding.cardStatisticsVLine.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return counter;
    }

    public class StatisticsViewHolder extends RecyclerView.ViewHolder {

        private final CardStatisticsBinding cardStatisticsBinding;

        public StatisticsViewHolder(CardStatisticsBinding cardStatisticsBinding) {
            super(cardStatisticsBinding.getRoot());
            this.cardStatisticsBinding = cardStatisticsBinding;
        }
    }
}
