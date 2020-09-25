package com.example.practiceapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.practiceapp.R;
import com.example.practiceapp.adapters.CommentsAdapter;
import com.example.practiceapp.data.Comments;
import com.example.practiceapp.data.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ro.andreidobrescu.emojilike.Emoji;
import ro.andreidobrescu.emojilike.EmojiConfig;
import ro.andreidobrescu.emojilike.EmojiLikeTouchDetector;
import ro.andreidobrescu.emojilike.EmojiLikeView;
import ro.andreidobrescu.emojilike.IActivityWithEmoji;
import ro.andreidobrescu.emojilike.OnEmojiSelectedListener;

public class PostDetailsActivity extends AppCompatActivity implements IActivityWithEmoji {

    private ImageView postImageView, userImageView;
    private TextView titleTextView, descriptionTextView, dateTextVew;
    private FirebaseDatabase firebaseDatabase;
    private EditText commentEditTextView;
    private Button addCommentButton;
    private DatabaseReference databaseReference;
    private CommentsAdapter adapter;
    private RecyclerView commentRecyclerView;
    private EmojiLikeTouchDetector emojiLikeTouchDetector;
    private FirebaseUser currentUser;
    private List<Comments> commentsList = new ArrayList<>();
    private String strObj;
    private Post obj;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        postImageView = findViewById(R.id.postDetailsImage);
        userImageView = findViewById(R.id.userPostImageView);
        emojiLikeTouchDetector = new EmojiLikeTouchDetector();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Comments");

        titleTextView = findViewById(R.id.postDetailsTitleTextView);
        descriptionTextView = findViewById(R.id.postDetailsDescriptionTextView);
        dateTextVew = findViewById(R.id.postDetailsUploadDatetextView);
        commentEditTextView = findViewById(R.id.commentEditTextView);

        progressBar = findViewById(R.id.commentsProgressBar);

        commentRecyclerView  = findViewById(R.id.commentsRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        commentRecyclerView.setLayoutManager(layoutManager);
        commentRecyclerView.setHasFixedSize(true);

        adapter = new CommentsAdapter(this, commentsList, new CommentsAdapter.OnReactionTouchListener() {
            @Override
            public void onReaction(EmojiLikeView emojiView, final ImageView reactionImageView, final TextView likeTextView) {
                EmojiConfig.with(PostDetailsActivity.this)
                        .on(likeTextView)
                        .open(emojiView)
                        .addEmoji(new Emoji(R.drawable.like_fb, "Like"))
                        .addEmoji(new Emoji(R.drawable.haha, "Haha"))
                        .addEmoji(new Emoji(R.drawable.kiss, "Kiss"))
                        .addEmoji(new Emoji(R.drawable.sad, "Sad"))
                        .addEmoji(new Emoji(R.drawable.angry, ":P"))
                        .setEmojiViewInAnimation((AnimationSet) AnimationUtils.loadAnimation(PostDetailsActivity.this, R.anim.in_animation))
                        .setEmojiViewOutAnimation((AnimationSet) AnimationUtils.loadAnimation(PostDetailsActivity.this, R.anim.out_animation))
                        .setBackgroundImage(R.drawable.background_drawable)
                        .setOnEmojiSelectedListener(new OnEmojiSelectedListener() {
                            @Override
                            public void onEmojiSelected(Emoji emoji) {
                                likeTextView.setText(emoji.getDescription());
                                reactionImageView.setImageResource(emoji.getDrawable());
                            }
                        })
                        .setup();
            }
        });
        commentRecyclerView.setAdapter(adapter);





        addCommentButton = findViewById(R.id.addCommentButton);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){

                    progressBar.setVisibility(View.VISIBLE);

                    pushCommentToFirebase();
                }
            }
        });

        Gson gson = new Gson();
         strObj = getIntent().getStringExtra("Post");
         obj = gson.fromJson(strObj, Post.class);

        Glide.with(this).load(obj.getPicture()).into(postImageView);
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(userImageView);

        titleTextView.setText(obj.getTitle());
        descriptionTextView.setText(obj.getDescription());

        Long clientId = ((Number)obj.getTimeStamp()).longValue();
        String getDate = timeStampToString(clientId);
        dateTextVew.setText(getDate);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();

                for(DataSnapshot postSnap: snapshot.getChildren()){

                    Comments comments = postSnap.getValue(Comments.class);
                    if(comments.getUniquePostKey().equals(obj.getUniqueKey())){

                        commentsList.add(comments);
                    }

                }
                commentEditTextView.setText("");
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);

            }
        });



    }

    private void pushCommentToFirebase(){

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Comments").push();

        Comments comments = new Comments(commentEditTextView.getText().toString(), obj.getUserId(), currentUser.getPhotoUrl().toString() , obj.getUniqueKey());

        String key = databaseReference.getKey();
        comments.setUniqueKey(key);

        databaseReference.setValue(comments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(PostDetailsActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostDetailsActivity.this, "Task has been failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String timeStampToString(long timeInMillies){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timeInMillies);

        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }

    private boolean validateFields(){
        String comment = commentEditTextView.getText().toString();

        if(TextUtils.isEmpty(comment)){
            Toast.makeText(this, "Please add the comment", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void configureEmojiLike(EmojiConfig config) {

        emojiLikeTouchDetector.configure(config);
    }
}
