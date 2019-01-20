package com.example.victor.trivia.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.victor.trivia.data.TriviaContract.QuestionsEntry;
import com.example.victor.trivia.objects.Question;
import com.example.victor.trivia.R;
import com.example.victor.trivia.databinding.FragmentAddQuestionBinding;
import com.example.victor.trivia.utilities.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddQuestionFragment extends Fragment {

    //Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference questionsDatabaseReference;

    //Question variables
    private int questionCategory;
    private String questionBody;
    private String questionAnswerCorrect;
    private String questionAnswerIncorrect01, questionAnswerIncorrect02, questionAnswerIncorrect03;
    private String questionDescription;
    private String questionPhotoUrl;

    //Listen for changes
    private boolean questionHasChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            questionHasChanged = true;
            return false;
        }
    };

    public AddQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentAddQuestionBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_question, container, false);
        View rootView = binding.getRoot();

        //Setup Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        questionsDatabaseReference = firebaseDatabase.getReference().child(QuestionsEntry.QUESTIONS_TABLE_NAME);

        //Setup Spinner
        ArrayAdapter spinnerCategoryAdapter = ArrayAdapter.createFromResource(getContext(), R.array.array_categories, android.R.layout.simple_spinner_item);
        spinnerCategoryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        binding.addQuestionCategorySpinner.setAdapter(spinnerCategoryAdapter);

        binding.addQuestionCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        binding.addQuestionCategorySpinner.setOnTouchListener(touchListener);
        binding.addQuestionAnswerCorrectEditText.setOnTouchListener(touchListener);
        binding.addQuestionAnswerIncorrect01EditText.setOnTouchListener(touchListener);
        binding.addQuestionAnswerIncorrect02EditText.setOnTouchListener(touchListener);
        binding.addQuestionAnswerIncorrect03EditText.setOnTouchListener(touchListener);
        binding.addQuestionDescriptionEditText.setOnTouchListener(touchListener);
        binding.addQuestionPhotoUrlEditText.setOnTouchListener(touchListener);

        //Submit Question
        binding.addQuestionSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionCategory == Constants.QUESTION_CATEGORY_NONE) {
                    Toast.makeText(getContext(), R.string.toast_category_none_description, Toast.LENGTH_SHORT).show();
                } else if (binding.addQuestionBodyEditText.getText().length() == 0
                        || binding.addQuestionAnswerCorrectEditText.getText().length() == 0
                        || binding.addQuestionAnswerIncorrect01EditText.getText().length() == 0
                        || binding.addQuestionAnswerIncorrect02EditText.getText().length() == 0
                        || binding.addQuestionAnswerIncorrect03EditText.getText().length() == 0
                        || binding.addQuestionDescriptionEditText.getText().length() == 0
                        || binding.addQuestionPhotoUrlEditText.getText().length() == 0) {
                    Toast.makeText(getContext(), R.string.toast_question_incomplete_description, Toast.LENGTH_SHORT).show();
                } else {
                    //Get Question and Answers from EditText
                    questionBody = binding.addQuestionBodyEditText.getText().toString();
                    questionAnswerCorrect = binding.addQuestionAnswerCorrectEditText.getText().toString();
                    questionAnswerIncorrect01 = binding.addQuestionAnswerIncorrect01EditText.getText().toString();
                    questionAnswerIncorrect02 = binding.addQuestionAnswerIncorrect02EditText.getText().toString();
                    questionAnswerIncorrect03 = binding.addQuestionAnswerIncorrect03EditText.getText().toString();
                    questionDescription = binding.addQuestionDescriptionEditText.getText().toString();
                    questionPhotoUrl = binding.addQuestionPhotoUrlEditText.getText().toString();

                    //Submit question to Firebase
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
                    binding.addQuestionBodyEditText.setText(Constants.CONSTANT_NULL);
                    binding.addQuestionAnswerCorrectEditText.setText(Constants.CONSTANT_NULL);
                    binding.addQuestionAnswerIncorrect01EditText.setText(Constants.CONSTANT_NULL);
                    binding.addQuestionAnswerIncorrect02EditText.setText(Constants.CONSTANT_NULL);
                    binding.addQuestionAnswerIncorrect03EditText.setText(Constants.CONSTANT_NULL);
                    binding.addQuestionDescriptionEditText.setText(Constants.CONSTANT_NULL);
                    binding.addQuestionPhotoUrlEditText.setText(Constants.CONSTANT_NULL);
                }
            }
        });

        return rootView;
    }
}
