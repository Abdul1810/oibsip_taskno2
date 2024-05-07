package com.oasis.todo.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oasis.todo.adapter.Task;

import java.util.ArrayList;
import java.util.List;

public class DatabaseProvider extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "Users";
    private static final String TABLE_TASKS = "Tasks";

    // Users table columns
    private static final String COLUMN_USER_ID = "ID";
    private static final String COLUMN_USERNAME = "USERNAME";
    private static final String COLUMN_PASSWORD = "PASSWORD";

    // Tasks table columns
    private static final String COLUMN_TASK_ID = "ID";
    private static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    private static final String COLUMN_IS_COMPLETED = "IS_COMPLETED";

    public DatabaseProvider(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Tasks table
        String CREATE_TASKS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_TASKS + "("
                + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_IS_COMPLETED + " INT DEFAULT 0"
                + ")";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

        // Create tables again
        onCreate(db);
    }

    // Insert a new user into Users table
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Check if a username already exists in Users table
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{username, password}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Insert a new task into Tasks table
    public boolean insertTask(String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, description);

        long result = db.insert(TABLE_TASKS, null, values);
        db.close();
        return result != -1;
    }

    // Get all tasks from Tasks table
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                int completedIndex = cursor.getColumnIndex("IS_COMPLETED");
                if (descriptionIndex == -1) {
                    // Handle the situation when the column does not exist
                    Log.e("DatabaseProvider", "COLUMN_DESCRIPTION does not exist in the cursor");
                } else {
                    String description = cursor.getString(descriptionIndex);
                    boolean isCompleted = cursor.getInt(completedIndex) == 1;
                    Task task = new Task(description, isCompleted);
                    tasksList.add(task);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasksList;
    }

    public void updateTaskStatus(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_COMPLETED, task.isChecked ? 1 : 0);
        db.update(TABLE_TASKS, values, COLUMN_DESCRIPTION + " = ?", new String[]{task.title});
        db.close();
    }

    public boolean deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASKS, COLUMN_DESCRIPTION + " = ?", new String[]{task.title});
        db.close();
        return result > 0;
    }
}
