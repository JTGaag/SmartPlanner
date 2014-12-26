package com.joost.smartplanner.smartevent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Joost on 23/12/2014.
 * DONE: Test DatabaseHelper (20141226)
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Log tag
    private static final String LOG = "DatabaseHelper";

    // Database version
    private static final int DATABASE_VERSION = 3;

    //Database name
    private static final String DATABASE_NAME = "smartPlannerDatabase";

    //Table names
    private static final String TABLE_EVENT = "events";

    //Commen colum names
    private static final String KEY_ID = "_id";
    private static final String KEY_CREATED_AT = "created_at";

    //EVENTS table names
    //private static final String KEY_EVENT_ID = "event_id";
    private static final String KEY_EVENT_NAME = "event_name";
    private static final String KEY_EVENT_STARTTIME = "event_start_time";
    private static final String KEY_EVENT_ENDTIME = "event_end_time";
    private static final String KEY_EVENT_COLOR = "event_color";

    //Create Tables
    private static final String CREATE_TABLE_EVENT = "CREATE TABLE "
            + TABLE_EVENT + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EVENT_NAME + " TEXT," + KEY_EVENT_STARTTIME + " INTEGER,"
            + KEY_EVENT_ENDTIME + " INTEGER," + KEY_EVENT_COLOR + " INTEGER,"+ KEY_CREATED_AT + " DATETIME" + ")";

    public DatabaseHelper(Context context){
        //TODO: After testing save database in secure location (not on SDCard)
        super(context, "/mnt/sdcard/"+DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create database
        db.execSQL(CREATE_TABLE_EVENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete old database and recreate new one (run create method)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);

        onCreate(db);
    }

    /**
     * Create new entity in offline databse from SmartEvent
     * @param sEvent
     * @return
     */
    public long createSmartEvent(SmartEvent sEvent){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_NAME, sEvent.getName()); //String can go in text
        values.put(KEY_EVENT_STARTTIME, sEvent.getStartTime().getTimeInMillis()); //Calendar converted to long to go in Integer
        values.put(KEY_EVENT_ENDTIME, sEvent.getEndTime().getTimeInMillis()); //Calendar converted to long to go in Integer
        values.put(KEY_EVENT_COLOR, sEvent.getColor()); //Color int can go in Integer
        values.put(KEY_CREATED_AT, getDateTime());

        long event_id = db.insert(TABLE_EVENT, null, values);

        return event_id;
    }

    /**
     * Return SmartEvent coresponding with the event_id
     * @param event_id id of SmartEvent to retreived from database
     * @return SmartEvent
     */
    public SmartEvent getSmartEvent(long event_id){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_EVENT + " WHERE " + KEY_ID + " = " + event_id;

        Cursor c = db.rawQuery(selectQuery, null);

        if(c!=null){
            c.moveToFirst();
        }

        Calendar startTime = new GregorianCalendar();
        Calendar endTime = new GregorianCalendar();
        startTime.setTimeInMillis(c.getLong(c.getColumnIndex(KEY_EVENT_STARTTIME)));
        endTime.setTimeInMillis(c.getLong(c.getColumnIndex(KEY_EVENT_ENDTIME)));
        SmartEvent smartEvent = new SmartEvent(c.getLong(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(KEY_EVENT_NAME)), startTime, endTime,c.getInt(c.getColumnIndex(KEY_EVENT_COLOR)));

        return smartEvent;
    }

    /**
     * Get all events stored in then offline database
     * @return Array list of SmartEvents
     */
    public List<SmartEvent> getAllEvents(){
        List<SmartEvent> events = new ArrayList<SmartEvent>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                //Log.d("Event", "Name: " + c.getString(c.getColumnIndex(KEY_EVENT_NAME)) + " StartTime: " + Long.toString(c.getLong(c.getColumnIndex(KEY_EVENT_STARTTIME))));
                Calendar startTime = new GregorianCalendar();
                Calendar endTime = new GregorianCalendar();
                startTime.setTimeInMillis(c.getLong(c.getColumnIndex(KEY_EVENT_STARTTIME)));
                endTime.setTimeInMillis(c.getLong(c.getColumnIndex(KEY_EVENT_ENDTIME)));
                SmartEvent smartEvent = new SmartEvent(c.getLong(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(KEY_EVENT_NAME)), startTime, endTime, c.getInt(c.getColumnIndex(KEY_EVENT_COLOR)));
                Log.d("SmartEvent", smartEvent.toJsonString());
                events.add(smartEvent);
            }while(c.moveToNext());
        }

        //Log.d("Event list", events.toString());
        return events;
    }

    /**
     * Update Event Id of SmartEvent needs to be equal to Id in Database
     * @param sEvent
     * @return
     */
    public int updateEvent(SmartEvent sEvent){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_NAME, sEvent.getName()); //String can go in text
        values.put(KEY_EVENT_STARTTIME, sEvent.getStartTime().getTimeInMillis()); //Calendar converted to long to go in Integer
        values.put(KEY_EVENT_ENDTIME, sEvent.getEndTime().getTimeInMillis()); //Calendar converted to long to go in Integer
        values.put(KEY_EVENT_COLOR, sEvent.getColor()); //Color int can go in Integer
        values.put(KEY_CREATED_AT, getDateTime());

        return db.update(TABLE_EVENT,values, KEY_ID + " = ?", new String[] {String.valueOf(sEvent.getId())});
    }

    /**
     * Delete Event in offline database with the corresponding id
     * @param event_id Id of event needed to be deleted
     */
    public void deleteEvent(long event_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENT, KEY_ID + " = ?", new String[] { String.valueOf(event_id)});
    }

    /**
     * get Date time in simple date time format
     */
    private String getDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void closeDB(){
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null && db.isOpen()){
            db.close();
        }
    }
}
