package com.example.practiceapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.practiceapp.R;
import com.example.practiceapp.data.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity {

    private ImageView postImageView, userImageView;
    private TextView titleTextView, descriptionTextView, dateTextVew;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        postImageView = findViewById(R.id.postDetailsImage);
        userImageView = findViewById(R.id.userPostImageView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Comments").push();

        titleTextView = findViewById(R.id.postDetailsTitleTextView);
        descriptionTextView = findViewById(R.id.postDetailsDescriptionTextView);
        dateTextVew = findViewById(R.id.postDetailsUploadDatetextView);

        Gson gson = new Gson();
        String strObj = getIntent().getStringExtra("Post");
        Post obj = gson.fromJson(strObj, Post.class);

        Glide.with(this).load(obj.getPicture()).into(postImageView);
        Glide.with(this).load(obj.getUserPhoto()).into(userImageView);

        titleTextView.setText(obj.getTitle());
        descriptionTextView.setText(obj.getDescription());

        Long clientId = ((Number)obj.getTimeStamp()).longValue();
        String getDate = timeStampToString(clientId);
        dateTextVew.setText(getDate);



    }

    private String timeStampToString(long timeInMillies){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timeInMillies);

        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }
}
