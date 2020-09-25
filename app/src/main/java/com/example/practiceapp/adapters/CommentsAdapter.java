package com.example.practiceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.practiceapp.R;
import com.example.practiceapp.data.Comments;

import java.util.List;

import ro.andreidobrescu.emojilike.Emoji;
import ro.andreidobrescu.emojilike.EmojiConfig;
import ro.andreidobrescu.emojilike.EmojiLikeTouchDetector;
import ro.andreidobrescu.emojilike.EmojiLikeView;
import ro.andreidobrescu.emojilike.IActivityWithEmoji;
import ro.andreidobrescu.emojilike.OnEmojiSelectedListener;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private static Context context;
    private List<Comments> commentsList;
    private OnReactionTouchListener listener;

    public CommentsAdapter(Context context,List<Comments> commentsList,  OnReactionTouchListener listener ){
        this.commentsList = commentsList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_comment, parent, false);

        // Return a new holder instance
        CommentsViewHolder viewHolder = new CommentsViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsViewHolder holder, int position) {

        Comments comments = commentsList.get(position);

        Glide.with(context).load(comments.getUserPhoto()).into(holder.userImageView);

        holder.commentTextView.setText(comments.getComment());

        holder.liketextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onReaction(holder.emojiView, holder.reactionImageView, holder.liketextView);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{

        private TextView commentTextView, liketextView;
        private ImageView userImageView, reactionImageView;
        private EmojiLikeView emojiView;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            commentTextView = itemView.findViewById(R.id.commentTextView);
            userImageView = itemView.findViewById(R.id.userImageView);
            liketextView = itemView.findViewById(R.id.likeButton);
            emojiView  = itemView.findViewById(R.id.emojiView);
            reactionImageView = itemView.findViewById(R.id.reactionImageView);

        }
    }

    public interface  OnReactionTouchListener{
        void onReaction(EmojiLikeView emojiView, ImageView reactionImageView, TextView likeTextView);
    }
}
