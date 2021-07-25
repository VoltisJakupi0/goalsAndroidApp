package com.riinvest.goals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.riinvest.goals.Adapters.GoalsAdapter;
import com.riinvest.goals.Model.GoalModel;
import com.riinvest.goals.Model.UserModel;
import com.riinvest.goals.Utils.DatabaseHandler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class AddGoalActivity extends AppCompatActivity implements DialogCloseListener{

    private DatabaseHandler db;

    private RecyclerView goalsRecyclerView;
    private GoalsAdapter goalsAdapter;
    private FloatingActionButton fab;


    ImageView backButton;

    private List<GoalModel> goalsList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        Objects.requireNonNull(getSupportActionBar()).hide();

        try {
            db = new DatabaseHandler(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.openDatabase();

        goalsRecyclerView = findViewById(R.id.tasksRecyclerView);
        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        goalsAdapter = new GoalsAdapter(db,AddGoalActivity.this);
        goalsRecyclerView.setAdapter(goalsAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(goalsAdapter));
        itemTouchHelper.attachToRecyclerView(goalsRecyclerView);

        fab = findViewById(R.id.fab);
        backButton = findViewById(R.id.back);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                final Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });


        goalsList = db.getActiveGoals();



        LinearLayout layout = (LinearLayout) findViewById(R.id.emptyView);
        Collections.reverse(goalsList);

        goalsAdapter.setGoals(goalsList);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewGoal.newInstance().show(getSupportFragmentManager(), AddNewGoal.TAG);
            }
        });


        System.out.println(goalsAdapter.getItemCount());
        if(goalsAdapter.getItemCount() > 0){
            layout.setVisibility(View.INVISIBLE);
        }else{
            layout.setVisibility(View.VISIBLE);
        }


    }



    @Override
    public void handleDialogClose(DialogInterface dialog){
        goalsList = db.getActiveGoals();
        Collections.reverse(goalsList);
        goalsAdapter.setGoals(goalsList);
        goalsAdapter.notifyDataSetChanged();
        LinearLayout layout = (LinearLayout) findViewById(R.id.emptyView);

        if(goalsList.size() > 0){
            layout.setVisibility(View.INVISIBLE);
        }else{
            layout.setVisibility(View.VISIBLE);
        }
    }




}

