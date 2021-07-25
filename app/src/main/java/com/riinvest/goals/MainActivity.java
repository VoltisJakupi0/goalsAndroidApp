package com.riinvest.goals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.riinvest.goals.Adapters.GoalsAdapter;
import com.riinvest.goals.Adapters.HomeGoalsAdapter;
import com.riinvest.goals.Model.GoalModel;
import com.riinvest.goals.Model.UserModel;
import com.riinvest.goals.Utils.DatabaseHandler;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler db;

    ImageView settingsButton;
    Button logoutButton;
    CircleImageView profileButton;
    CardView goalsButton;
    CardView historyButton;
    CardView editNameButton;
    CardView aboutUsButton;
    CardView termsConditionsButton;
    CardView privacyPolicyButton;

    TextView usernameText;
    LinearLayout profileContent;
    LinearLayout mainContent;
    LinearLayout goalsContent;



    boolean showProfile;
    private RecyclerView goalsRecyclerView;
    private HomeGoalsAdapter homeGoalsAdapter;
    private List<GoalModel> goalsList;

    public static MainActivity newInstance(){
        return new MainActivity();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        showProfile = false;
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_main);


        @SuppressLint("WrongConstant") SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);

        String userID = sh.getString("userID", "");

        try {
            db = new DatabaseHandler(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.openDatabase();


        aboutUsButton = findViewById(R.id.aboutUs);
        privacyPolicyButton = findViewById(R.id.privacy);
        termsConditionsButton = findViewById(R.id.termsConditions);
        historyButton = findViewById(R.id.history);
        settingsButton = findViewById(R.id.settings);
        usernameText = findViewById(R.id.username);
        goalsButton = findViewById(R.id.goals);
        editNameButton = findViewById(R.id.editName);
        profileButton = findViewById(R.id.profile);
        profileContent = findViewById(R.id.profileContent);
        mainContent = findViewById(R.id.mainContent);
        logoutButton = findViewById(R.id.logout);
        goalsContent = findViewById(R.id.goalsContent);


        Cursor cur = db.getLoggedInUser(userID);

        goalsRecyclerView = findViewById(R.id.tasksRecyclerView);
        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        homeGoalsAdapter = new HomeGoalsAdapter(db,MainActivity.this);
        goalsRecyclerView.setAdapter(homeGoalsAdapter);



        goalsList = db.getActiveGoals();



        LinearLayout layout = (LinearLayout) findViewById(R.id.emptyView);
        Collections.reverse(goalsList);

        homeGoalsAdapter.setGoals(goalsList);


        if(goalsList.size() > 0){
            layout.setVisibility(View.INVISIBLE);
        }else{
            layout.setVisibility(View.VISIBLE);
        }

        if (cur != null && cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                do {
                    usernameText.setText(cur.getString(1) + " " + cur.getString(2));
                } while (cur.moveToNext());
            }
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(myIntent);
                myEdit.remove("token").commit();
                myEdit.remove("userID").commit();
                finish();
            }
        });

        goalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {
                Intent myIntent = new Intent(getApplicationContext(), AddGoalActivity.class);
                startActivity(myIntent);

            }
        });

        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {
                Intent myIntent = new Intent(getApplicationContext(), EditNameActivity.class);
                startActivity(myIntent);

            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showProfile == false) {
                    profileContent.setVisibility(View.VISIBLE);
                    mainContent.setVisibility(View.GONE);
                    goalsContent.setVisibility(View.GONE);

                    showProfile = true;
                } else {
                    profileContent.setVisibility(View.GONE);
                    mainContent.setVisibility(View.VISIBLE);
                    goalsContent.setVisibility(View.VISIBLE);

                    showProfile = false;
                }
            }
        });



        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showProfile == false) {
                    profileContent.setVisibility(View.VISIBLE);
                    mainContent.setVisibility(View.GONE);
                    goalsContent.setVisibility(View.GONE);

                    showProfile = true;
                } else {
                    profileContent.setVisibility(View.GONE);
                    mainContent.setVisibility(View.VISIBLE);
                    goalsContent.setVisibility(View.VISIBLE);

                    showProfile = false;
                }
            }
        });

        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), AboutUsActivity.class);
                startActivity(myIntent);
            }
        });

        privacyPolicyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cookiebot.com/en/privacy-policy-generator-gdpr/?gclid=Cj0KCQjwl_SHBhCQARIsAFIFRVVRaBnBlBnx2Q3WD44qVO-adT5r-b3oxrz2h2sTPKThfrOxyj87gIoaAmX1EALw_wcB"));
                startActivity(browserIntent);
            }
        });

        termsConditionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), TermsConditionsActivity.class);
                startActivity(myIntent);
            }
        });


        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(myIntent);
            }
        });

    }
}
