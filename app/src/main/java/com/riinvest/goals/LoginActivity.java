package com.riinvest.goals;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.riinvest.goals.Utils.DatabaseHandler;

import java.text.ParseException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static android.app.PendingIntent.getActivity;
import static android.widget.Toast.makeText;

public class LoginActivity extends AppCompatActivity {

    private static final int LENGTH_SHORT = '3' ;
    private DatabaseHandler db;

    Button registerButton;
    Button loginButton;
    EditText emailInput;
    EditText passwordInput;

    public static LoginActivity newInstance(){
        return new LoginActivity();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        try {
            db = new DatabaseHandler(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.openDatabase();

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_login);

        registerButton = findViewById(R.id.register);
        loginButton = findViewById(R.id.login);
        emailInput = (EditText)findViewById(R.id.login_email);
        passwordInput = (EditText)findViewById(R.id.login_password);





        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(myIntent);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                System.out.println(emailInput.getText().toString());
                System.out.println(passwordInput.getText().toString());
                Context context = getApplicationContext();

                if(emailInput.getText().toString().length() < 1){

                    @SuppressLint("WrongConstant") Toast toast = Toast.makeText(context, "Email should not be empty!", 5);
                    toast.setMargin(0,50);
                    toast.show();
                }
                else if(passwordInput.getText().toString().length() < 1){
                    @SuppressLint("WrongConstant") Toast toast = Toast.makeText(context, "Password should not be empty!", 5);
                    toast.setMargin(0,50);
                    toast.show();
                }
                if (emailInput.getText().toString().length() > 0 && passwordInput.getText().toString().length() > 0) {

                    Cursor cur = db.LoginUser(emailInput.getText().toString(), passwordInput.getText().toString());

                    if (cur.getCount() != 0) {
                        String jwtToken = Jwts.builder().claim("email", emailInput.getText().toString()).claim("password", passwordInput.getText().toString())
                                .signWith(SignatureAlgorithm.HS256, "secret".getBytes())
                                .compact();
                        cur.moveToFirst();
                        if (cur != null) {
                            do {
                                myEdit.putString("token", jwtToken);
                                myEdit.putString("userID", cur.getString(0));

                                myEdit.commit();

                                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(myIntent);


                            } while (cur.moveToNext());
                        }
                    } else {
                        @SuppressLint("WrongConstant") Toast toast = Toast.makeText(context, "Your email or password is incorrect!", 5);
                        toast.setMargin(0,50);
                        toast.show();
                    }

                }
            }



        });
        }

    }





