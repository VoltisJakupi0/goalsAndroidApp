package com.riinvest.goals;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.riinvest.goals.Model.UserModel;
import com.riinvest.goals.Utils.DatabaseHandler;

import java.text.ParseException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseHandler db;

    Button registerButton;
    Button loginButton;
    EditText nameInput;
    EditText surnameInput;
    EditText emailInput;
    EditText passwordInput;

    public static RegisterActivity newInstance(){
        return new RegisterActivity();
    }

    public boolean emailValidator(EditText etMail) {

        // extract the entered data from the EditText
        String emailToText = etMail.getText().toString();

        // Android offers the inbuilt patterns which the entered
        // data from the EditText field needs to be compared with
        // In this case the the entered data needs to compared with
        // the EMAIL_ADDRESS, which is implemented same below
        if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.activity_register);


        try {
            db = new DatabaseHandler(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.openDatabase();


        loginButton = findViewById(R.id.login);
        registerButton = (Button) findViewById(R.id.register);
        nameInput = (EditText)findViewById(R.id.name);
        surnameInput = (EditText)findViewById(R.id.surname);
        emailInput = (EditText)findViewById(R.id.email);
        passwordInput = (EditText)findViewById(R.id.password);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(myIntent);

            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();

                if(nameInput.getText().toString().length() < 1){

                    @SuppressLint("WrongConstant") Toast toast = Toast.makeText(context, "Name should not be empty!", 5);
                    toast.setMargin(0,50);
                    toast.show();
                }
                else if(surnameInput.getText().toString().length() < 1){

                    @SuppressLint("WrongConstant") Toast toast = Toast.makeText(context, "Surname should not be empty!", 5);
                    toast.setMargin(0,50);
                    toast.show();
                }
                else if(emailValidator(emailInput) == false){
                    @SuppressLint("WrongConstant") Toast toast = Toast.makeText(context, "Email should be a valid email!", 5);
                    toast.setMargin(0,50);
                    toast.show();
                }
                else if(passwordInput.getText().toString().length() < 5){
                    @SuppressLint("WrongConstant") Toast toast = Toast.makeText(context, "Password should be at least 5 characters!", 5);
                    toast.setMargin(0,50);
                    toast.show();
                }

                if ( nameInput.getText().toString().length() > 0 && surnameInput.getText().toString().length() > 0 && emailValidator(emailInput) == true && passwordInput.getText().toString().length() >= 5) {

                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    UserModel user = new UserModel();
                    user.setUser_name(nameInput.getText().toString());
                    user.setUser_surname(surnameInput.getText().toString());
                    user.setEmail(emailInput.getText().toString());
                    user.setPassword(passwordInput.getText().toString());
                    boolean registerSuccess = db.RegisterUser(user);

                    if (registerSuccess == true) {
                        @SuppressLint("WrongConstant") Toast toast = Toast.makeText(context, "The user has been registered with success!", 5);
                        toast.setMargin(0,50);
                        toast.show();
                        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(myIntent);


                    }

                }

            }
        });

    }
}
