package com.example.victor.trivia.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.victor.trivia.R;
import com.example.victor.trivia.fragments.AddQuestionFragment;
import com.example.victor.trivia.fragments.PlayFragment;
import com.example.victor.trivia.fragments.StatisticsFragment;

/******
 * Created by Victor on 11/21/2018.
 ******/
public class FragmentAdapter extends FragmentPagerAdapter {

    private final Context context;
    private final String userId;

    public FragmentAdapter(Context context, FragmentManager fm, String userId) {
        super(fm);
        this.context = context;
        this.userId = userId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PlayFragment.setPlayFragmentUserId(userId);
                return new PlayFragment();
            case 1:
                return new StatisticsFragment();
            case 2:
                return new AddQuestionFragment();
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.fragment_play);
            case 1:
                return context.getString(R.string.fragment_statistics);
            case 2:
                return context.getString(R.string.fragment_add_questions);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
