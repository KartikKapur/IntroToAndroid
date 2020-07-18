package com.bignerdranch.android.geoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "QUIZACTIVITY";
    private static final String KEY_INDEX = "index";
    private static final String KEY_INCORRECT = "incorrect";
    private static final String KEY_CORRECT = "correct";
    private static final String KEY_CHEAT="cheat";
    private static final int MAX_CHEAT = 3;
    private static final int REQUEST_CODE_CHEAT = 0;


    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionTextView;
    private TextView mBuildVersion;
    private Button mCheatButton;
    private boolean mIsCheater;

    private ArrayList<Integer> mCorrect = new ArrayList<>();
    private ArrayList<Integer> mIncorrect = new ArrayList<>();
    private ArrayList<Integer> mCheated = new ArrayList<>();

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_asia, true),
            new Question(R.string.question_australia, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_australia2, true),
    };
    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCorrect = savedInstanceState.getIntegerArrayList(KEY_CORRECT);
            mIncorrect = savedInstanceState.getIntegerArrayList(KEY_INCORRECT);
            mCheated = savedInstanceState.getIntegerArrayList(KEY_CHEAT);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex < 0) {
                    mCurrentIndex = mQuestionBank.length - 1;
                } else {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                }
                mIsCheater = false;
                updateQuestion();
            }
        });

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue);
                startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        Log.d(TAG, "Build Version");
        mBuildVersion = (TextView) findViewById(R.id.build_version);
        Log.d(TAG, "Build Version2");
        mBuildVersion.setText("Android Version " + Integer.toString(Build.VERSION.SDK_INT));
        Log.d(TAG, "Build Version");
        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putIntegerArrayList(KEY_CORRECT, mCorrect);
        outState.putIntegerArrayList(KEY_INCORRECT, mIncorrect);
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        if (mCheated.contains(mCurrentIndex)){
            mIsCheater =true;
        }
        if (mCheated.size() == MAX_CHEAT){
            mCheatButton.setVisibility(View.GONE);
        }
        if (mCorrect.contains(mCurrentIndex) || mIncorrect.contains(mCurrentIndex) || mCheated.contains(mCurrentIndex)) {
            mTrueButton.setVisibility(View.GONE);
            mFalseButton.setVisibility(View.GONE);
        } else {
            mTrueButton.setVisibility(View.VISIBLE);
            mFalseButton.setVisibility(View.VISIBLE);
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if (mIsCheater) {
            mCheated.add(mCurrentIndex);
            messageResId = R.string.judgement_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mCorrect.add(mCurrentIndex);
            } else {
                messageResId = R.string.incorrect_toast;
                mIncorrect.add(mCurrentIndex);
            }
        }
        updateQuestion();
        Toast t = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP, 0, 0);
        t.show();
        calculateScore();
    }


    private void calculateScore() {
        Log.d(TAG, Integer.toString(mCorrect.size() + mIncorrect.size()));
        if (mCorrect.size() + mIncorrect.size() +mCheated.size() == mQuestionBank.length) {
            Toast t = Toast.makeText(this,
                    "You got " + Integer.toString(mCorrect.size()) + "/" + Integer.toString(mQuestionBank.length) + " correct",
                    Toast.LENGTH_LONG);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();
        }

    }
}