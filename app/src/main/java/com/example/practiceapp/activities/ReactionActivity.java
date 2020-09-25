package com.example.practiceapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.practiceapp.R;

public class ReactionActivity extends AppCompatActivity implements View.OnClickListener, Animation.AnimationListener {

    private TextView txtPostStatus;
    private Button btnMain, btnLike, btnHaha, btnLove, btnCry, btnShock, btnAngry;
    private LinearLayout emojisLayout;
    private Animation animFadeIn, animFadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction);
        initViews();


    }

    private void initViews() {
        txtPostStatus = findViewById(R.id.txtPostStatus);
        btnMain = findViewById(R.id.btnClick);
        btnLike = findViewById(R.id.btnLike);
        btnHaha = findViewById(R.id.btnHaha);
        btnLove = findViewById(R.id.btnLove);
        btnCry = findViewById(R.id.btnCry);
        btnShock = findViewById(R.id.btnShock);
        btnAngry = findViewById(R.id.btnAngry);
        emojisLayout = findViewById(R.id.emojisLayout);

        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                android.R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                android.R.anim.fade_out);

        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);

        emojisLayout.setVisibility(View.GONE);

        btnMain.setOnClickListener(this);
        btnLike.setOnClickListener(this);
        btnLove.setOnClickListener(this);
        btnHaha.setOnClickListener(this);
        btnCry.setOnClickListener(this);
        btnShock.setOnClickListener(this);
        btnAngry.setOnClickListener(this);

        btnMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                emojisLayout.startAnimation(animFadeIn);
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClick:
                txtPostStatus.setText("Like");
                emojisLayout.setVisibility(View.GONE);
                btnMain.setText("Like");
                break;
            case R.id.btnLike:
                txtPostStatus.setText("Like");
                emojisLayout.startAnimation(animFadeOut);
                btnMain.setText("Like");
                break;
            case R.id.btnLove:
                txtPostStatus.setText("Love");
                emojisLayout.startAnimation(animFadeOut);
                btnMain.setText("Love");
                break;
            case R.id.btnHaha:
                txtPostStatus.setText("Haha");
                emojisLayout.startAnimation(animFadeOut);
                btnMain.setText("Haha");
                break;
            case R.id.btnCry:
                txtPostStatus.setText("sad");
                emojisLayout.startAnimation(animFadeOut);
                btnMain.setText("sad");
                break;
            case R.id.btnShock:
                txtPostStatus.setText("Shock");
                emojisLayout.startAnimation(animFadeOut);
                btnMain.setText("Shock");
                break;
            case R.id.btnAngry:
                txtPostStatus.setText("Angry");
                emojisLayout.startAnimation(animFadeOut);
                btnMain.setText("Angry");
                break;
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation
        // check for fade in animation
        if (animation == animFadeOut) {
            emojisLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // Animation is repeating
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // Animation started
        if (animation == animFadeIn) {
            emojisLayout.setVisibility(View.VISIBLE);
        }
    }
}

