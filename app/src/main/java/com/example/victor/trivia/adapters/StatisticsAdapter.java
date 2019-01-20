package com.example.victor.trivia.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.victor.trivia.R;
import com.example.victor.trivia.databinding.CardStatisticsBinding;

/******
 * Created by Victor on 1/20/2019.
 ******/
public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder> {

    private Context context;
    private int numberOfCategories;
    private int[] questionsPositions = new int[8];
    private int[] questionsPerCategory = new int[8];
    private float[] questionsPerCategoryPercentage = new float[8];

    public StatisticsAdapter(Context context) {
        this.context = context;
    }

    public void updateResults(int[] questionsPositions, int[] questionsPerCategory, float[] questionsPerCategoryPercentage) {
      this.questionsPositions = questionsPositions;
        this.questionsPerCategory = questionsPerCategory;
        this.questionsPerCategoryPercentage = questionsPerCategoryPercentage;
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
        statisticsViewHolder.cardStatisticsBinding.cardStatisticsTvItemNumber.setText(String.valueOf(position + 1));
        statisticsViewHolder.cardStatisticsBinding.cardStatisticsTvCategory.setText(categories[questionsPositions[position] + 1]);
        statisticsViewHolder.cardStatisticsBinding.cardStatisticsTvNumberOfAnswers.setText(String.valueOf(questionsPerCategory[position]));
        statisticsViewHolder.cardStatisticsBinding.cardStatisticsTvPercentageCorrect.setText(String.valueOf(questionsPerCategoryPercentage[position]));
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class StatisticsViewHolder extends RecyclerView.ViewHolder {

        private final CardStatisticsBinding cardStatisticsBinding;

        public StatisticsViewHolder(CardStatisticsBinding cardStatisticsBinding) {
            super(cardStatisticsBinding.getRoot());
            this.cardStatisticsBinding = cardStatisticsBinding;
        }
    }
}
