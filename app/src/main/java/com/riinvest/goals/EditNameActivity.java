package com.riinvest.goals;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.riinvest.goals.Utils.DatabaseHandler;

import java.text.ParseException;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class EditNameActivity extends AppCompatActivity {

    ImageView backButton;
    EditText user_nameText;
    EditText user_surnameText;
    Button saveButton;

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        Objects.requireNonNull(getSupportActionBar()).hide();


        @SuppressLint("WrongConstant") SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);

        final String userID = sh.getString("userID", "");

        try {
            db = new DatabaseHandler(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.openDatabase();


        user_nameText = findViewById(R.id.user_name);
        user_surnameText = findViewById(R.id.user_surname);
        backButton = findViewById(R.id.back);
        saveButton = findViewById(R.id.save);



        Cursor cur = db.getLoggedInUser(userID);

        if(cur!=null && cur.getCount() > 0)
        {
            if (cur.moveToFirst())
            {
                do {
                    user_nameText.setText(cur.getString(1));
                    user_surnameText.setText(cur.getString(2));
                } while (cur.moveToNext());
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = getApplicationContext();

                if(user_nameText.getText().toString().length() > 0 && user_surnameText.getText().toString().length() > 0){
                db.updateUserInfo(parseInt(userID), user_nameText.getText().toString(), user_surnameText.getText().toString());
                final Intent i = new Intent(EditNameActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }else{
                @SuppressLint("WrongConstant") Toast toast = Toast.makeText(context, "Fields should not be empty!", 5);
                toast.setMargin(0,50);
                toast.show();
            }
        }});

    }
}
