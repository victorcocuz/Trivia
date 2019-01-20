package com.example.victor.trivia.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.victor.trivia.R;
import com.example.victor.trivia.data.TriviaContract;
import com.example.victor.trivia.data.TriviaContract.AnswersEntry;
import com.example.victor.trivia.databinding.FragmentUserBinding;
import com.example.victor.trivia.objects.Answer;
import com.example.victor.trivia.utilities.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    //Main
    private String userId;
    private List<Answer> answers = new ArrayList<>();
    private Context context;

    //Loaders
    private static final int LOADER_ID_CURSOR_ANSWERED = 1;

    //Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference questionsDatabaseReference, answersDatabaseReference;
    private ValueEventListener valueEventListener;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentUserBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false);
        View rootView = binding.getRoot();

        //Shared Preferences
        if (getContext() != null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME_USER, Context.MODE_PRIVATE);
            userId = sharedPreferences.getString(Constants.SHARED_PREFERENCES_USER_ID, Constants.CONSTANT_ANONYMUOS);
        }

        getLoaderManager().initLoader(LOADER_ID_CURSOR_ANSWERED, null, this).forceLoad();

        //Get all answers from Firebase
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        answersDatabaseReference = firebaseDatabase.getReference().child(userId).child(TriviaContract.AnswersEntry.ANSWERS_TABLE_NAME);
//        valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Timber.e("user snapshot answers count %s", dataSnapshot.getChildrenCount());
//                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
//                    Answer answer = singleSnapshot.getValue(Answer.class);
//                    answers.add(answer);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        answersDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
//
//        Timber.e("number if answers is %s", answers.size());

        return rootView;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int i, @Nullable Bundle bundle) {
        switch (i) {
            case LOADER_ID_CURSOR_ANSWERED:
                String[] projection = {AnswersEntry.ANSWERS_STATUS, AnswersEntry.ANSWERS_CATEGORY};
                return new CursorLoader(context,
                        AnswersEntry.ANSWERS_URI,
                        projection,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_CURSOR_ANSWERED:
                Cursor cursor = (Cursor) data;
                int[] categories = new int[8];
                int[] categoriesCorrect = new int[8];
                if (cursor != null && cursor.getCount() > 0) {
                    Timber.e("cursor count is %s",cursor.getCount());
                    //Add counters for each category for every answer and add counters for each correct answer
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        int category = cursor.getInt(cursor.getColumnIndex(AnswersEntry.ANSWERS_CATEGORY));
                        categories[category - 1] += 1;
                        if(cursor.getInt(cursor.getColumnIndex(AnswersEntry.ANSWERS_STATUS)) == 1){
                            categoriesCorrect[category - 1] ++;
                        }
                    }
                    for (int i = 0; i < 7; i++) {
                        Timber.e("category is %s", i);
                        Timber.e("category counter is %s", categories[i]);
                        Timber.e("category correct counter is %s", categoriesCorrect[i]);
                    }
                } else {
                    Timber.e("Cursor returned is null");
                }
                break;
            default:
                Timber.e("Could not return cursor");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
