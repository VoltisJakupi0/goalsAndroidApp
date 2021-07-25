package com.riinvest.goals.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.riinvest.goals.Model.GoalModel;
import com.riinvest.goals.Model.UserModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 12;
    private static final String NAME = "goalsDatabase";
    private static final String GOAL_TABLE = "goal";
    private static final String USER_TABLE = "user";
    private static final String ID = "id";
    private static final String GOAL = "goal";
    private static final String STATUS = "status";
    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";


    private static final String USER_NAME = "name";
    private static final String USER_SURNAME = "surname";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String USER_ID = "userID";


    private static final String CREATE_GOAL_TABLE = "CREATE TABLE " + GOAL_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + GOAL + " TEXT, "
            + STATUS + " TEXT, " + USER_ID + " INTEGER, " + START_DATE + " TEXT, " + END_DATE + " TEXT)";

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_NAME + " TEXT, "
            + USER_SURNAME + " TEXT, " + EMAIL + " TEXT, " + PASSWORD + " TEXT)";

    private SQLiteDatabase db;
    Context mContext;

    public DatabaseHandler(Context context) throws ParseException {
        super(context, NAME, null, VERSION);
        mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_GOAL_TABLE);
        db.execSQL(CREATE_USER_TABLE);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed

        db.execSQL("DROP TABLE IF EXISTS " + GOAL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);

        onCreate(db);

        // Create tables again
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertGoal(GoalModel goal){
        ContentValues cv = new ContentValues();
        cv.put(USER_ID, goal.getUserID());
        cv.put(GOAL, goal.getGoal());
        cv.put(START_DATE, goal.getStartDate());
        cv.put(END_DATE, goal.getEndDate());
        cv.put(STATUS, goal.getStatus());
        db.insert(GOAL_TABLE, null, cv);
    }

    public boolean RegisterUser(UserModel user){
        try {
            ContentValues cv = new ContentValues();
            cv.put(USER_NAME, user.getUser_name());
            cv.put(USER_SURNAME, user.getUser_surname());
            cv.put(EMAIL, user.getEmail());
            cv.put(PASSWORD, user.getPassword());
            db.insert(USER_TABLE, null, cv);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    public Cursor LoginUser(String email, String password) {
        Cursor myCursor = db.query(USER_TABLE,
                new String[] { ID, EMAIL, PASSWORD },
                EMAIL + "='" + email + "' AND " +
                        PASSWORD + "='" + password + "'", null, null, null, null);

        if (myCursor != null) {
            myCursor.moveToFirst();
        }
        return myCursor;
    }

    public Cursor getLoggedInUser(String userID){

        Cursor myCursor = db.query(USER_TABLE,  new String[] {ID, USER_NAME, USER_SURNAME}, ID + "='" + userID + "'", null, null, null, null);

        if (myCursor != null) {
            myCursor.moveToFirst();
        }
        return myCursor;
    }

    public List<UserModel> getAllUsers(){
        List<UserModel> usersList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(USER_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        UserModel user = new UserModel();
                        user.setId(cur.getInt(cur.getColumnIndex(ID)));
                        user.setUser_name(cur.getString(cur.getColumnIndex(USER_NAME)));
                        user.setUser_surname(cur.getString(cur.getColumnIndex(USER_SURNAME)));
                        user.setEmail(cur.getString(cur.getColumnIndex(EMAIL)));
                        user.setPassword(cur.getString(cur.getColumnIndex(PASSWORD)));
                        usersList.add(user);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return usersList;
    }


    public List<GoalModel> getActiveGoals(){
        List<GoalModel> goalList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(GOAL_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        @SuppressLint("WrongConstant") SharedPreferences sh = mContext.getSharedPreferences("MySharedPref", Context.MODE_APPEND);

                        String userID = sh.getString("userID", "");

                        String userId = cur.getString(cur.getColumnIndex(USER_ID));


                        Date todayDate = new Date();

                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Date endDate = format.parse(cur.getString(cur.getColumnIndex(END_DATE)));


                        String status = cur.getString(cur.getColumnIndex(STATUS));

                        System.out.println( this.compareTwoDates(todayDate, endDate));

                        if((this.compareTwoDates(todayDate, endDate) != 1) && status.equals("ACTIVE") && userID.equals(userId) ){

                            GoalModel goal = new GoalModel();
                            goal.setUserID(cur.getInt(cur.getColumnIndex(USER_ID)));
                            goal.setId(cur.getInt(cur.getColumnIndex(ID)));
                            goal.setGoal(cur.getString(cur.getColumnIndex(GOAL)));
                            goal.setStatus(cur.getString(cur.getColumnIndex(STATUS)) == "FINISHED" ? "FINISHED" : "FAILED");
                            this.updateStatus(cur.getInt(cur.getColumnIndex(ID)), cur.getString(cur.getColumnIndex(STATUS)) == "FINISHED" ? "FINISHED" : "FAILED");
                            goal.setStartDate(cur.getString(cur.getColumnIndex(START_DATE)));
                            goal.setEndDate(cur.getString(cur.getColumnIndex(END_DATE)));


                            goalList.add(goal);
                        }
                    }
                    while(cur.moveToNext());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return goalList;
    }

    private Date parseDate(String date, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(date);
    }

    public static int compareTwoDates(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            int retVal = date1.compareTo(date2);

            if (retVal > 0)
                return 1; // date1 is greatet than date2
            else if (retVal == 0) // both dates r equal
                return 0;

        }
        return -1; // date1 is less than date2
    }

    public List<GoalModel> getAllHistoryGoals(){
        List<GoalModel> goalList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(GOAL_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        Date todayDate = new Date();

                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Date endDate = format.parse(cur.getString(cur.getColumnIndex(END_DATE)));


                        String status = cur.getString(cur.getColumnIndex(STATUS));

                        @SuppressLint("WrongConstant") SharedPreferences sh = mContext.getSharedPreferences("MySharedPref", Context.MODE_APPEND);

                        String userID = sh.getString("userID", "");

                        String userId = cur.getString(cur.getColumnIndex(USER_ID));

                        if(userID.equals(userId)) {
                            if (this.compareTwoDates(todayDate, endDate) == 1 || status.equals("FINISHED")) {

                                GoalModel goal = new GoalModel();
                                goal.setUserID(cur.getInt(cur.getColumnIndex(USER_ID)));
                                goal.setId(cur.getInt(cur.getColumnIndex(ID)));
                                goal.setGoal(cur.getString(cur.getColumnIndex(GOAL)));
                                goal.setStatus(cur.getString(cur.getColumnIndex(STATUS)).equals("FINISHED") ? "FINISHED" : "FAILED");
                                this.updateStatus(cur.getInt(cur.getColumnIndex(ID)), cur.getString(cur.getColumnIndex(STATUS)) == "FINISHED" ? "FINISHED" : "FAILED");
                                goal.setStartDate(cur.getString(cur.getColumnIndex(START_DATE)));
                                goal.setEndDate(cur.getString(cur.getColumnIndex(END_DATE)));


                                goalList.add(goal);
                            }
                        }

                    }
                    while(cur.moveToNext());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return goalList;
    }

    public List<GoalModel> getFailedHistoryGoals(){
        List<GoalModel> goalList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(GOAL_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        Date todayDate = new Date();

                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Date endDate = format.parse(cur.getString(cur.getColumnIndex(END_DATE)));

                        @SuppressLint("WrongConstant") SharedPreferences sh = mContext.getSharedPreferences("MySharedPref", Context.MODE_APPEND);

                        String userID = sh.getString("userID", "");

                        String userId = cur.getString(cur.getColumnIndex(USER_ID));

                        System.out.println(userID + "userID");
                        System.out.println(userId + "userIDBack");


                        if((this.compareTwoDates(todayDate, endDate) == 1) && userID.equals(userId)){
                            GoalModel goal = new GoalModel();
                            goal.setUserID(cur.getInt(cur.getColumnIndex(USER_ID)));
                            goal.setId(cur.getInt(cur.getColumnIndex(ID)));
                            goal.setGoal(cur.getString(cur.getColumnIndex(GOAL)));
                            goal.setStatus(cur.getString(cur.getColumnIndex(STATUS)).equals("FINISHED") ? "FINISHED" : "FAILED");
                            this.updateStatus(cur.getInt(cur.getColumnIndex(ID)), cur.getString(cur.getColumnIndex(STATUS)) == "FINISHED" ? "FINISHED" : "FAILED");
                            goal.setStartDate(cur.getString(cur.getColumnIndex(START_DATE)));
                            goal.setEndDate(cur.getString(cur.getColumnIndex(END_DATE)));


                            goalList.add(goal);
                        }

                    }
                    while(cur.moveToNext());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return goalList;
    }

    public List<GoalModel> getFinishedHistoryGoals(){
        List<GoalModel> goalList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(GOAL_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        Date todayDate = new Date();

                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Date endDate = format.parse(cur.getString(cur.getColumnIndex(END_DATE)));


                        String status = cur.getString(cur.getColumnIndex(STATUS));

                        @SuppressLint("WrongConstant") SharedPreferences sh = mContext.getSharedPreferences("MySharedPref", Context.MODE_APPEND);

                        String userID = sh.getString("userID", "");

                        String userId = cur.getString(cur.getColumnIndex(USER_ID));

                        if(status.equals("FINISHED") && userID.equals(userId)){

                            GoalModel goal = new GoalModel();
                            goal.setId(cur.getInt(cur.getColumnIndex(ID)));
                            goal.setGoal(cur.getString(cur.getColumnIndex(GOAL)));
                            goal.setStatus(cur.getString(cur.getColumnIndex(STATUS)).equals("FINISHED") ? "FINISHED" : "FAILED");
                            this.updateStatus(cur.getInt(cur.getColumnIndex(ID)), cur.getString(cur.getColumnIndex(STATUS)) == "FINISHED" ? "FINISHED" : "FAILED");
                            goal.setStartDate(cur.getString(cur.getColumnIndex(START_DATE)));
                            goal.setEndDate(cur.getString(cur.getColumnIndex(END_DATE)));


                            goalList.add(goal);
                        }

                    }
                    while(cur.moveToNext());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return goalList;
    }


    public void updateUserInfo(int id, String name, String surname){
        ContentValues cv = new ContentValues();
        cv.put(USER_NAME, name);
        cv.put(USER_SURNAME, surname);
        db.update(USER_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }


    public void updateStatus(int id, String status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(GOAL_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateGoal(int id, String goal, String startDate, String endDate) {
        ContentValues cv = new ContentValues();
        cv.put(GOAL, goal);
        cv.put(START_DATE, startDate);
        cv.put(END_DATE, endDate);
        db.update(GOAL_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteGoal(int id){
        db.delete(GOAL_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}
