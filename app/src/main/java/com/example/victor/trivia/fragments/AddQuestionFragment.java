package com.example.victor.trivia.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.victor.trivia.R;
import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;
import com.example.victor.trivia.databinding.FragmentAddQuestionBinding;
import com.example.victor.trivia.objects.Question;
import com.example.victor.trivia.utilities.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddQuestionFragment extends Fragment {

    //Main
    private DatabaseReference questionsDatabaseReference;
    private Context context;

    //Question variables
    private int questionCategory;
    private String questionBody;
    private String questionAnswerCorrect;
    private String questionAnswerIncorrect01, questionAnswerIncorrect02, questionAnswerIncorrect03;
    private String questionDescription;
    private String questionPhotoUrl;

    public AddQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentAddQuestionBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_question, container, false);
        View rootView = binding.getRoot();

        //Setup FireBase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        questionsDatabaseReference = firebaseDatabase.getReference().child(QuestionsEntry.QUESTIONS_TABLE_NAME);

        //Setup Spinner
        ArrayAdapter spinnerCategoryAdapter = ArrayAdapter.createFromResource(context, R.array.array_categories, android.R.layout.simple_spinner_item);
        spinnerCategoryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        binding.fragmentAddQuestionSpCategory.setAdapter(spinnerCategoryAdapter);

        binding.fragmentAddQuestionSpCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);

                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.category_none))) {
                        questionCategory = Constants.QUESTION_CATEGORY_NONE;
                    } else if (selection.equals(getString(R.string.category_arts))) {
                        questionCategory = Constants.QUESTION_CATEGORY_ARTS_AND_CULTURE;
                    } else if (selection.equals(getString(R.string.category_geography))) {
                        questionCategory = Constants.QUESTION_CATEGORY_GEOGRAPHY;
                    } else if (selection.equals(getString(R.string.category_history))) {
                        questionCategory = Constants.QUESTION_CATEGORY_HISTORY;
                    } else if (selection.equals(getString(R.string.category_movies))) {
                        questionCategory = Constants.QUESTION_CATEGORY_MOVIES;
                    } else if (selection.equals(getString(R.string.category_music))) {
                        questionCategory = Constants.QUESTION_CATEGORY_MUSIC;
                    } else if (selection.equals(getString(R.string.category_science))) {
                        questionCategory = Constants.QUESTION_CATEGORY_SCIENCE;
                    } else if (selection.equals(getString(R.string.category_social_sciences))) {
                        questionCategory = Constants.QUESTION_CATEGORY_SOCIAL_SCIENCES;
                    } else if (selection.equals(getString(R.string.category_sport))) {
                        questionCategory = Constants.QUESTION_CATEGORY_SPORT;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Submit Question
        binding.fragmentAddQuestionCvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionCategory == Constants.QUESTION_CATEGORY_NONE) {
                    Toast.makeText(getContext(), R.string.toast_category_none_description, Toast.LENGTH_SHORT).show();
                } else if (binding.fragmentAddQuestionEtBody.getText().length() == 0
                        || binding.fragmentAddQuestionEtAnswerCorrect.getText().length() == 0
                        || binding.fragmentAddQuestionEtAnswerIncorrect01.getText().length() == 0
                        || binding.fragmentAddQuestionEtAnswerIncorrect02.getText().length() == 0
                        || binding.fragmentAddQuestionEtAnswerIncorrect03.getText().length() == 0
                        || binding.fragmentAddQuestionEtDescription.getText().length() == 0
                        || binding.fragmentAddQuestionEtPhotoUrl.getText().length() == 0) {
                    Toast.makeText(getContext(), R.string.toast_question_incomplete_description, Toast.LENGTH_SHORT).show();
                } else {
                    //Get Question and Answers from EditText
                    questionBody = binding.fragmentAddQuestionEtBody.getText().toString();
                    questionAnswerCorrect = binding.fragmentAddQuestionEtAnswerCorrect.getText().toString();
                    questionAnswerIncorrect01 = binding.fragmentAddQuestionEtAnswerIncorrect01.getText().toString();
                    questionAnswerIncorrect02 = binding.fragmentAddQuestionEtAnswerIncorrect02.getText().toString();
                    questionAnswerIncorrect03 = binding.fragmentAddQuestionEtAnswerIncorrect03.getText().toString();
                    questionDescription = binding.fragmentAddQuestionEtDescription.getText().toString();
                    questionPhotoUrl = binding.fragmentAddQuestionEtPhotoUrl.getText().toString();

                    //Submit question to FireBase
                    Question question = new Question(questionCategory,
                            questionBody,
                            questionAnswerCorrect,
                            questionAnswerIncorrect01,
                            questionAnswerIncorrect02,
                            questionAnswerIncorrect03,
                            questionDescription,
                            questionPhotoUrl);
                    questionsDatabaseReference.push().setValue(question);
                    Toast.makeText(getContext(), getString(R.string.toast_question_submitted), Toast.LENGTH_SHORT).show();

                    //Empty query fields
                    binding.fragmentAddQuestionEtBody.setText(Constants.CONSTANT_NULL);
                    binding.fragmentAddQuestionEtAnswerCorrect.setText(Constants.CONSTANT_NULL);
                    binding.fragmentAddQuestionEtAnswerIncorrect01.setText(Constants.CONSTANT_NULL);
                    binding.fragmentAddQuestionEtAnswerIncorrect02.setText(Constants.CONSTANT_NULL);
                    binding.fragmentAddQuestionEtAnswerIncorrect03.setText(Constants.CONSTANT_NULL);
                    binding.fragmentAddQuestionEtDescription.setText(Constants.CONSTANT_NULL);
                    binding.fragmentAddQuestionEtPhotoUrl.setText(Constants.CONSTANT_NULL);

                    //Close keyboard
                    if (getActivity() != null) {
                        View currentView = getActivity().getCurrentFocus();
                        if (currentView != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
