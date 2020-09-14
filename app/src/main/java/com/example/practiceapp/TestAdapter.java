package com.example.practiceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practiceapp.data.TestModel;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

    private Context context;
    private List<TestModel> list;
    private boolean isChildVisible = false;

    public TestAdapter(Context context,List<TestModel> list ){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        TestViewHolder viewHolder = new TestViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TestViewHolder holder, int position) {

        TestModel testModel = list.get(position);


        holder.nameTextView.setText(testModel.getName());
        holder.addressTextView.setText(testModel.getAddress());




        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!isChildVisible){
                   holder.addressTextView.setVisibility(View.VISIBLE);
                   holder.imageView.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                   isChildVisible = true;
               }
              else{
                   holder.addressTextView.setVisibility(View.GONE);
                   holder.imageView.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                   isChildVisible = false;
               }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TestViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTextView, addressTextView;
        private RelativeLayout parentLayout;
        private ImageView imageView;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.textViewName);
            imageView = itemView.findViewById(R.id.imageViewIcon);


        }
    }
}
