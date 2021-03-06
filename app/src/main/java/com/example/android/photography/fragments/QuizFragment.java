package com.example.android.photography.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.photography.R;
import com.example.android.photography.data.Question;
import com.example.android.photography.views.TimeLeftView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class QuizFragment extends Fragment implements View.OnClickListener {

    public static final int QUIZ_SIZE = 5;
    public static final int QUIZ_TIME = 60 * 1000;
    public static List<Question> questions;
    OnFeedbackReadyListener mCallback;
    int index = 0, questionNo = 1, correctAnswerCount = 0, wrongAnswerCount = 0;
    boolean passChecker = false;
    String currentAnswer;
    @BindView(R.id.question_id_tv)
    TextView questionIdTV;
    @BindView(R.id.time_left)
    TextView timeLeftTV;
    @BindView(R.id.question_tv)
    TextView questionTV;
    @BindView(R.id.option_one_tv)
    Button optionOneButton;
    @BindView(R.id.option_two_tv)
    Button optionTwoButton;
    @BindView(R.id.option_three_tv)
    Button optionThreeButton;
    @BindView(R.id.time_view)
    TimeLeftView timeLeftView;
    CountDownTimer quizTIme;

    public static QuizFragment newInstance(List<Question> q) {
        questions = q;
        return new QuizFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        ButterKnife.bind(this, view);


        optionOneButton.setOnClickListener(this);
        optionTwoButton.setOnClickListener(this);
        optionThreeButton.setOnClickListener(this);


        buildQuiz(questions);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_setting) {
            return true;
        }
        if (itemId == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //This method is used to display quiz questions and options to the user
    public void buildQuiz(List<Question> questions) {

        if (index > questions.size() - 1) {
            stopTimer();
            showResults();
        } else {
            Question currentQuestion = questions.get(index);

            String question = getString(R.string.question_text, currentQuestion.question);
            String questionId = getString(R.string.question_id_text, questionNo, QUIZ_SIZE);
            String option_one = getString(R.string.option_one, currentQuestion.optionOne);
            String option_two = getString(R.string.option_two, currentQuestion.optionTwo);
            String option_three = getString(R.string.option_three, currentQuestion.optionThree);
            questionIdTV.setText(questionId);
            questionTV.setText(question);
            optionOneButton.setText(option_one);
            optionTwoButton.setText(option_two);
            optionThreeButton.setText(option_three);
            currentAnswer = currentQuestion.correctAnswer;
            if (quizTIme != null)
                stopTimer();
            startQuizTimer(QUIZ_TIME);
            index++;
            questionNo++;
        }
    }

    //This method is called when the user answers the last question of the quiz
    private void showResults() {
        if (correctAnswerCount >= 3) {
            passChecker = true;
        } else {
            passChecker = false;
        }
        mCallback.onFeedbackReady(passChecker, false);
    }

    //This method keeps time per question
    public void startQuizTimer(long timeLeftMillis) {
        quizTIme = new CountDownTimer(timeLeftMillis, 1000) {
            @Override
            public void onTick(long millis) {
                long minutes = millis / (60 * 1000);
                long seconds = millis / 1000;
                timeLeftTV.setText(minutes + " : " + seconds);
                timeLeftView.setValue((int) millis / 1000);
            }

            @Override
            public void onFinish() {
                timeLeftTV.setText("0 : 00");
                timeLeftView.setValue(0);
                showTimeUp();
            }
        }.start();
    }

    public void stopTimer() {
        timeLeftTV.setText("0 : 00");
        timeLeftView.setValue(0);
        quizTIme.cancel();
    }

    //This method is called when the time runs out before the user is able to
    // provide an answer to the given question.
    private void showTimeUp() {
        mCallback.onFeedbackReady(passChecker, true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnFeedbackReadyListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    "must implement OnFeedbackReadyListener");
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        String answer = null;
        if (viewId == R.id.option_one_tv) {
            answer = optionOneButton.getText().toString();
        } else if (viewId == R.id.option_two_tv) {
            answer = optionTwoButton.getText().toString();
        } else if (viewId == R.id.option_three_tv) {
            answer = optionThreeButton.getText().toString();
        }
        checkAnswer(answer, currentAnswer);

        buildQuiz(questions);
    }

    //This method checks the answer provided by the user against the answer to the
    // selected question and update the related variables accordingly
    private void checkAnswer(String selected, String answer) {
        if (selected.equals(answer)) {
            correctAnswerCount++;
        } else {
            wrongAnswerCount++;
        }
    }

    public interface OnFeedbackReadyListener {
        void onFeedbackReady(boolean pass, boolean timeout);
    }
}
