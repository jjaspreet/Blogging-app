package com.example.practiceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.practiceapp.R;
import com.example.practiceapp.data.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> posts;
    private PostClickListener postClickListener;

    public PostAdapter(Context context, List<Post> posts, PostClickListener postClickListener) {
        this.context = context;
        this.posts = posts;
        this.postClickListener = postClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_post, parent, false);

        // Return a new holder instance
        PostViewHolder viewHolder = new PostViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = posts.get(position);

        holder.titleTextView.setText(post.getTitle());
        Glide.with(context).load(post.getUserPhoto()).into(holder.userImageView);
        Glide.with(context).load(post.getPicture()).into(holder.postImageView);

        holder.postLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postClickListener != null){
                    postClickListener.onPOstClick();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        private TextView titleTextView;
        private ImageView postImageView, userImageView;
        private RelativeLayout postLayout;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
            postImageView = itemView.findViewById(R.id.postImageView);
            userImageView = itemView.findViewById(R.id.userImageView);
            postLayout = itemView.findViewById(R.id.postLayout);

        }
    }

   public interface PostClickListener{
        void onPOstClick();
   }
}
