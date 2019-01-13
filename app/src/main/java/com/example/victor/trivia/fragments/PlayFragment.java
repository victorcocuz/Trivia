package com.example.victor.trivia.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.victor.trivia.R;
import com.example.victor.trivia.activities.GameActivity;

//Helpers
import com.example.victor.trivia.helpers.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends Fragment {
    TextView startPlayView;

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        startPlayView = rootView.findViewById(R.id.fragment_play_button);

        startPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startGameForIntent = new Intent(rootView.getContext(), GameActivity.class);
                startActivityForResult(startGameForIntent, Constants.INTENT_TO_GAME_REQUEST_CODE);
//                startActivity(startGameForIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INTENT_TO_GAME_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra(Constants.INTENT_TO_GAME_RETURN_KEY);
                startPlayView.setText(result);
            }
        }
    }
}
