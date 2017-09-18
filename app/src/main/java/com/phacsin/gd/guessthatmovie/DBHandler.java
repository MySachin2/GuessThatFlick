package com.phacsin.gd.guessthatmovie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by GD on 8/26/2017.
 */

public class DBHandler extends SQLiteOpenHelper {

    static String DATABASE_NAME = "Hollywood";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    public void onCreate(SQLiteDatabase db) {
        String TABLE_ACTIVE_NOTIFICATION = "CREATE TABLE score(movie_name TEXT,score TEXT,type TEXT,letters_shown TEXT,letters_lost TEXT,status TEXT,hint TEXT,paid INTEGER,order_no INTEGER)";
        db.execSQL(TABLE_ACTIVE_NOTIFICATION);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS score");
        onCreate(database);
    }

    public void insertScore (String movie_name, String type,String letters_chosen,String hint)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("movie_name", movie_name);
        contentValues.put("type", type);
        contentValues.put("status", "Not Played");
        contentValues.put("score", "90");
        contentValues.put("letters_shown", letters_chosen);
        contentValues.put("hint", hint);
        contentValues.put("paid", 0);
        contentValues.put("order_no", 0);

        db.insert("score", null, contentValues);
    }

    public void updateScore (String name,String type,String score,String letters_shown,String letters_lost)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "UPDATE score SET score = '" + score + "',letters_shown = '" + letters_shown + "',letters_lost = '" + letters_lost +"' WHERE movie_name='"+name+"' AND type = '" +  type+ "'";
            db.execSQL(query);
        }catch (SQLiteException e) {
            Log.d("sqlite",e.toString());
        }
    }

    public void updateStatus (String name,String type,String status,int order_no)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "UPDATE score SET status = '" + status + "',order_no = " + order_no + " WHERE movie_name='"+name+"' AND type = '" +  type+ "'";
            db.execSQL(query);
        }catch (SQLiteException e) {
            Log.d("sqlite",e.toString());
        }
    }

    public void updatePaid (String name,String type,int paid)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "UPDATE score SET paid = " + paid + " WHERE movie_name='"+name+"' AND type = '" +  type+ "'";
            db.execSQL(query);
        }catch (SQLiteException e) {
            Log.d("sqlite",e.toString());
        }
    }

    public void removeScore (String name,String type)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "DELETE FROM score WHERE movie_name='"+name+"' AND type = '" +  type+ "'";
            db.execSQL(query);
        }catch (SQLiteException e) {
            Log.d("sqlite",e.toString());
        }
    }

    public int getMovieCount(String type)
    {
        String countQuery = "SELECT  * FROM score WHERE type = '" + type + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int getCompletedMovieCount(String type)
    {
        String countQuery = "SELECT  * FROM score WHERE type = '" + type + "' AND status = 'Completed'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int getNotPlayedMovieCount(String type)
    {
        String countQuery = "SELECT  * FROM score WHERE type = '" + type + "' AND status = 'Not Played'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public List<ScoreClass> getAllScores(String type)
    {
        List<ScoreClass> list = new ArrayList<ScoreClass>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * from score WHERE type='" + type + "' ORDER BY order_no ASC", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            ScoreClass details = new ScoreClass();
            details.movie_name = res.getString(res.getColumnIndex("movie_name"));
            details.score = res.getString(res.getColumnIndex("score"));
            details.status = res.getString(res.getColumnIndex("status"));
            details.letters_shown = res.getString(res.getColumnIndex("letters_shown"));
            details.letters_lost = res.getString(res.getColumnIndex("letters_lost"));
            details.hint = res.getString(res.getColumnIndex("hint"));

            list.add(details);
            res.moveToNext();
        }
        Collections.reverse(list);
        return list;
    }

    public boolean movieExists(String movie_name,String type)
    {
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * from score WHERE movie_name = '" + movie_name + "' AND type = '" +  type+ "'", null );
        int cnt = res.getCount();
        res.close();

        return cnt!=0;
    }

    public String getMoviePlayedStatus(String movie_name,String type)
    {
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * from score WHERE movie_name = '" + movie_name + "' AND type = '" +  type+ "'", null );
        res.moveToFirst();
        if(res.getCount()!=0) {
            Log.d("TAG",res.getString(res.getColumnIndex("status")));
            String status = res.getString(res.getColumnIndex("status"));
            res.close();
            return status;
        }
        else
            return "Not Played";
    }

    public boolean isPaid(String movie_name,String type)
    {
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT paid from score WHERE movie_name = '" + movie_name + "' AND type = '" +  type+ "'", null );
        int cnt = res.getCount();
        res.moveToFirst();
        int paid = res.getInt(0);
        res.close();
        if(cnt==0)
            return false;
        else
        {
            Log.d("Paid",paid + "");
            return paid != 0;
        }

    }

}