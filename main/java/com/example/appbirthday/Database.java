package com.example.appbirthday;

import static com.example.appbirthday.ui.add.AddFragment.daysRemaining;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.appbirthday.ui.add.AddFragment;
import com.example.appbirthday.ui.list.ItemModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    //Data of database
    private static final String BIRTHDAY_TABLE_CREATE = "CREATE TABLE if not exists birthday(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, date TEXT NOT NULL, daysRemaining INTEGER NOT NULL)";
    public static final String DB_NAME = "appbirthday6.sqlite";
    private static final int DB_VERSION = 1;
    private static Database sInstance;

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    //Singleton
    public static synchronized Database getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        if (sInstance == null) {
            sInstance = new Database(context, name, factory, version);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(BIRTHDAY_TABLE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS birthday");
        db.execSQL(BIRTHDAY_TABLE_CREATE);
    }
    //Method to add a record
    public long addRecord(String name, String date, int daysRemaining){
        long resultado = -1;
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            cv.put("date", date);
            cv.put("daysRemaining", daysRemaining);
            resultado = db.insert("birthday", null, cv);
            db.close();


        }
        return resultado;
    }
    //Method to update the remaining days for all records.
    public void updateRemainingDays() throws ParseException {
        int id;
        String date;

        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(" SELECT id,date FROM birthday", null);

        if (c.moveToFirst()) {
            do {
                id = c.getInt(0);
                date = c.getString(1);

                SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date formatDate = null;
                formatDate = originalFormat.parse(date);

                SimpleDateFormat finalFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateFormat = finalFormat.format(formatDate);
                int days = (int) daysRemaining(dateFormat);

                ContentValues cv = new ContentValues();
                cv.put("daysRemaining", days);
                String[] args = new String []{String.valueOf(id)};
                db.update("birthday", cv, "id=?", args);
            } while(c.moveToNext());
        }

    }
    //Method to delete a record
    public void deleteRecord(int id){
        SQLiteDatabase db = getWritableDatabase();
        String[] args = new String[]{String.valueOf(id)};
        db.delete("birthday", "id=?", args);
    }
    //Method to update a record
    public void updateRecord(int id, String name, String date) throws ParseException {
            checkDateFormat(date);
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            cv.put("date", date);
            String[] args = new String[]{String.valueOf(id)};
            db.update("birthday", cv, "id=?", args);
    }
    //Method to check if the database is empty
    public boolean checkEmpty(){

        int count = 0;
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT count(*) FROM birthday", null);

        try {
            if(cursor != null)
                if(cursor.getCount() > 0){
                    cursor.moveToFirst();
                    count = cursor.getInt(0);
                }
        }finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        if(count>0)
            return false;
        else
            return true;
    }
    //Method to get and save all the records in a item model list.
    public List<ItemModel> showItems() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM birthday ORDER BY daysRemaining ASC", null);
        List<ItemModel> items = new ArrayList<>();
        if (cursor.moveToFirst()){
            do{
                items.add(new ItemModel(cursor.getString(1),cursor.getString(2)));
            }while (cursor.moveToNext());
        }
        return items;
    }
    //Method to check the correct date format.
    private String checkDateFormat(String date) throws ParseException {
        boolean result = false;
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date formatDate = null;
        formatDate = originalFormat.parse(date);

        return formatDate.toString();
    }
    //Method to check de name length at modify and add.
    public boolean checkNameLength(String name){
        if(name.length() > 16){
            return false;
        }
        else{
            return true;
        }
    }

}