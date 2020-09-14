package com.example.practiceapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.practiceapp.R;
import com.example.practiceapp.TestAdapter;
import com.example.practiceapp.data.TestModel;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private List<TestModel> myList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TestAdapter testAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        testAdapter = new TestAdapter(this, myList);
        recyclerView.setAdapter(testAdapter);

        initializeViews();


    }


    private void initializeViews(){
        myList.add(new TestModel("Jaspreet", "Chander Vihar"));
        myList.add(new TestModel("Jaspreet", "Chander Vihar"));
        myList.add(new TestModel("Jaspreet", "Chander Vihar"));
        myList.add(new TestModel("Jaspreet", "Chander Vihar"));
        myList.add(new TestModel("Jaspreet", "Chander Vihar"));
        myList.add(new TestModel("Jaspreet", "Chander Vihar"));
        myList.add(new TestModel("Jaspreet", "Chander Vihar"));
        myList.add(new TestModel("Jaspreet", "Chander Vihar"));

        testAdapter.notifyDataSetChanged();



    }
}
