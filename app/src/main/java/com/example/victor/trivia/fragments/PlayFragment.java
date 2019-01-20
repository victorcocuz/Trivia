package com.example.victor.trivia.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.victor.trivia.R;
import com.example.victor.trivia.activities.GameActivity;

//Helpers
import com.example.victor.trivia.utilities.Constants;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends Fragment {
    private static String userId;
    TextView startPlayView;

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Main
        final View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        startPlayView = rootView.findViewById(R.id.fragment_play_button);

        startPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startGameIntent = new Intent(rootView.getContext(), GameActivity.class);
                startGameIntent.putExtra(Constants.INTENT_ACTIVITY_GAME_USER_ID, userId);
                startActivity(startGameIntent);
            }
        });
        return rootView;
    }

    public static void setPlayFragmentUserId(String userId){
        PlayFragment.userId = userId;
    }
}
