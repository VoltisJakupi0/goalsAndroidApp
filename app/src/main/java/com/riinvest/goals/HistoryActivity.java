package com.riinvest.goals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.riinvest.goals.Adapters.GoalsAdapter;
import com.riinvest.goals.Adapters.HistoryGoalsAdapter;
import com.riinvest.goals.Model.GoalModel;
import com.riinvest.goals.Utils.DatabaseHandler;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private DatabaseHandler db;
    private HistoryGoalsAdapter historyGoalsAdapter;
    private RecyclerView goalsRecyclerView;

    Button allButton;
    Button finishedButton;
    Button failedButton;

    ImageView backButton;
    private List<GoalModel> allList;
    private List<GoalModel> finishedList;
    private List<GoalModel> failedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().hide(); //<< this

        try {
            db = new DatabaseHandler(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.openDatabase();


        allButton = findViewById(R.id.all);
        finishedButton = findViewById(R.id.finished);
        failedButton = findViewById(R.id.failed);


        historyGoalsAdapter = new HistoryGoalsAdapter(db,HistoryActivity.this);


        backButton = findViewById(R.id.back);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        allList = db.getAllHistoryGoals();

        finishedList = db.getFinishedHistoryGoals();
        failedList = db.getFailedHistoryGoals();

        goalsRecyclerView = findViewById(R.id.tasksRecyclerView);
        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        goalsRecyclerView.setAdapter(historyGoalsAdapter);


        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyGoalsAdapter.setGoals(allList);
                allButton.setBackgroundColor(Color.parseColor("#4f68ac"));
                finishedButton.setBackgroundColor(Color.parseColor("#cfd0d0"));
                failedButton.setBackgroundColor(Color.parseColor("#cfd0d0"));
            }
        });




        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyGoalsAdapter.setGoals(finishedList);
                finishedButton.setBackgroundColor(Color.parseColor("#4f68ac"));
                allButton.setBackgroundColor(Color.parseColor("#cfd0d0"));
                failedButton.setBackgroundColor(Color.parseColor("#cfd0d0"));
            }
        });

        failedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failedButton.setBackgroundColor(Color.parseColor("#4f68ac"));
                allButton.setBackgroundColor(Color.parseColor("#cfd0d0"));
                finishedButton.setBackgroundColor(Color.parseColor("#cfd0d0"));
                historyGoalsAdapter.setGoals(failedList);
            }
        });





        LinearLayout layout = (LinearLayout) findViewById(R.id.emptyView);
        Collections.reverse(allList);


        historyGoalsAdapter.setGoals(allList);


        if(allList.size() > 0){
            layout.setVisibility(View.INVISIBLE);
        }else{
            layout.setVisibility(View.VISIBLE);
        }



    }
}
