package com.riinvest.goals;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.riinvest.goals.Adapters.GoalsAdapter;
import com.riinvest.goals.Model.GoalModel;
import com.riinvest.goals.Model.UserModel;
import com.riinvest.goals.Utils.DatabaseHandler;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Objects;

import static android.os.ParcelFileDescriptor.MODE_APPEND;

public class AddNewGoal extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newGoalText;
    private Button newGoalSaveButton;

    private DatabaseHandler db;



    Button startDateButton;
    Button endDateButton;

    Button doneButton;
    DatePickerDialog startDate;
    DatePickerDialog endDate;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    public static AddNewGoal newInstance(){
        return new AddNewGoal();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newGoalText = Objects.requireNonNull(getView()).findViewById(R.id.newGoalText);
        newGoalSaveButton = getView().findViewById(R.id.newGoalSaveButton);
        startDateButton = getView().findViewById(R.id.start_date_button);
        endDateButton = getView().findViewById(R.id.end_date_button);
        doneButton = getView().findViewById(R.id.done);


        @SuppressLint("WrongConstant") SharedPreferences sh = this.getContext().getSharedPreferences("MySharedPref", Context.MODE_APPEND);

        String userID = sh.getString("userID", "");


        boolean isUpdate = false;
        try {
            db = new DatabaseHandler(getActivity());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.openDatabase();



        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            doneButton.setVisibility(View.VISIBLE);
            String goal = bundle.getString("task");
            String startDate = bundle.getString("startDate");
            String endDate = bundle.getString("endDate");

            newGoalText.setText(goal);
            startDateButton.setText(startDate);
            endDateButton.setText(endDate);

            assert goal != null;
            if(goal.length() > 0)
                newGoalSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
        }

        if(isUpdate == false){
            doneButton.setVisibility(View.INVISIBLE);
        }





        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                startDate = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                startDateButton.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);
//                startDate.getDatePicker().setMinDate(System.currentTimeMillis());
                startDate.show();
            }
        });


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.updateStatus(bundle.getInt("id"), "FINISHED");
                dismiss();
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                endDate = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                endDateButton.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);
//                endDate.getDatePicker().setMinDate(System.currentTimeMillis());
                endDate.show();
            }
        });


        newGoalSaveButton.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newGoalSaveButton.setEnabled(false);
                    newGoalSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    newGoalSaveButton.setEnabled(true);
                    newGoalSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        final boolean finalIsUpdate = isUpdate;
        newGoalSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String goal = newGoalText.getText().toString();
                String startDate = startDateButton.getText().toString();
                String endDate = endDateButton.getText().toString();



                if(finalIsUpdate && goal.length() > 0 && startDate.length() > 0 && endDate.length() > 0){
                    db.updateGoal(bundle.getInt("id"), goal, startDate, endDate);
                }
                else if(goal.length() > 0 && startDate.length() > 0 && endDate.length() > 0) {
                    GoalModel goalModel = new GoalModel();
                    goalModel.setUserID(Integer.parseInt(userID));
                    goalModel.setGoal(goal);
                    goalModel.setStartDate(startDateButton.getText().toString());
                    goalModel.setEndDate(endDateButton.getText().toString());
                    goalModel.setStatus("ACTIVE");
                    db.insertGoal(goalModel);


                    System.out.println("getID:" + goalModel.getId());

                }
                dismiss();
            }
        });


    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }
}
