package com.joost.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.joost.category.Category;
import com.joost.smartevent.SmartEvent;
import com.joost.smartplanner.R;
import com.joost.utilities.App;

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
 * DONE: add categoty table
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
    private static final String TABLE_CATEGORY = "categories";

    //Commen colum names
    private static final String KEY_ID = "_id";
    private static final String KEY_CREATED_AT = "created_at";

    //EVENTS table names
    //private static final String KEY_EVENT_ID = "event_id";
    private static final String KEY_EVENT_NAME = "event_name";
    private static final String KEY_EVENT_STARTTIME = "event_start_time";
    private static final String KEY_EVENT_ENDTIME = "event_end_time";
    private static final String KEY_EVENT_COLOR = "event_color";
    private static final String KEY_EVENT_CATEGORY_ID = "event_category_id";


    //CATEGORY table names
    //private static final String KEY_EVENT_ID = "event_id";
    private static final String KEY_CATEGORY_NAME = "name";
    private static final String KEY_CATEGORY_PARENT_ID = "parent_id";
    private static final String KEY_CATEGORY_LFT = "lft";
    private static final String KEY_CATEGORY_RGT = "rgt";
    private static final String KEY_CATEGORY_LAYER = "layer";
    private static final String KEY_CATEGORY_COLOR = "color";

    //Readable and writable database
    SQLiteDatabase database;

    //Create Tables commands
    private static final String CREATE_TABLE_EVENT = "CREATE TABLE "
            + TABLE_EVENT + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EVENT_NAME + " TEXT," + KEY_EVENT_CATEGORY_ID + " INTEGER," + KEY_EVENT_STARTTIME + " INTEGER,"
            + KEY_EVENT_ENDTIME + " INTEGER," + KEY_EVENT_COLOR + " INTEGER," + KEY_CREATED_AT + " DATETIME" + ")";

    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE "
            + TABLE_CATEGORY + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CATEGORY_NAME + " TEXT," + KEY_CATEGORY_PARENT_ID + " INTEGER,"
            + KEY_CATEGORY_LFT + " INTEGER," + KEY_CATEGORY_RGT + " INTEGER," + KEY_CATEGORY_LAYER + " INTEGER," + KEY_CATEGORY_COLOR + " INTEGER," + KEY_CREATED_AT + " DATETIME" + ")";

    public DatabaseHelper(Context context){
        //TODO: After testing save database in secure location (not on SDCard)
        super(context, "/mnt/sdcard/"+DATABASE_NAME, null, DATABASE_VERSION);
        //super(context, DATABASE_NAME, null, DATABASE_VERSION);

        database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create database
        db.execSQL(CREATE_TABLE_EVENT);
        db.execSQL(CREATE_TABLE_CATEGORY);
        //Make first Category entry
        //TODO: load from file else make this
        Log.d("DATABASE", "added first category");
        createCategory(new Category(0,"Categories",-1,1,2,0, App.getContext().getResources().getColor(R.color.material_red)),db); //-1 as parent_id to indicate that there is no parent
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Delete old database and recreate new one (run create method)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);

        onCreate(db);
    }


    ///////////////////////////////////////////////////////////
    //SMARTEVENT methods
    //TODO: create method to save all smartevents in file in order to fill new database on database update
    //TODO: get all events from a specific category id
    ///////////////////////////////////////////////////////////
    /**
     * Create new entity in offline database from SmartEvent
     * @param sEvent
     * @return
     */
    public long createSmartEvent(SmartEvent sEvent){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_NAME, sEvent.getName()); //String can go in text
        values.put(KEY_EVENT_CATEGORY_ID, sEvent.getCategoryId());
        values.put(KEY_EVENT_STARTTIME, sEvent.getStartTime().getTimeInMillis()); //Calendar converted to long to go in Integer
        values.put(KEY_EVENT_ENDTIME, sEvent.getEndTime().getTimeInMillis()); //Calendar converted to long to go in Integer
        values.put(KEY_EVENT_COLOR, sEvent.getColor()); //Color int can go in Integer
        values.put(KEY_CREATED_AT, getDateTime());

        long event_id = db.insert(TABLE_EVENT, null, values);
        db.close();
        Log.d("DEBUG","Event created");
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
        SmartEvent smartEvent = null;

        if(c!=null){
            c.moveToFirst();
            Calendar startTime = new GregorianCalendar();
            Calendar endTime = new GregorianCalendar();
            startTime.setTimeInMillis(c.getLong(c.getColumnIndex(KEY_EVENT_STARTTIME)));
            endTime.setTimeInMillis(c.getLong(c.getColumnIndex(KEY_EVENT_ENDTIME)));
            smartEvent = new SmartEvent(c.getLong(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(KEY_EVENT_NAME)), startTime, endTime, c.getInt(c.getColumnIndex(KEY_EVENT_COLOR)), c.getLong(c.getColumnIndex(KEY_EVENT_CATEGORY_ID)));
        }
        db.close();
        return smartEvent;
    }

    /**
     * Get all events stored in then offline database
     * @return Array list of SmartEvents
     */
    public List<SmartEvent> getAllEvents(){
        Log.d("DatabaseHelper", "getAllevents Started");
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
                SmartEvent smartEvent = new SmartEvent(c.getLong(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(KEY_EVENT_NAME)), startTime, endTime, c.getInt(c.getColumnIndex(KEY_EVENT_COLOR)), c.getLong(c.getColumnIndex(KEY_EVENT_CATEGORY_ID)));
                Log.d("SmartEvent", smartEvent.toJsonString());
                events.add(smartEvent);
            }while(c.moveToNext());
        }
        db.close();
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
        values.put(KEY_EVENT_CATEGORY_ID, sEvent.getCategoryId());
        values.put(KEY_EVENT_STARTTIME, sEvent.getStartTime().getTimeInMillis()); //Calendar converted to long to go in Integer
        values.put(KEY_EVENT_ENDTIME, sEvent.getEndTime().getTimeInMillis()); //Calendar converted to long to go in Integer
        values.put(KEY_EVENT_COLOR, sEvent.getColor()); //Color int can go in Integer
        values.put(KEY_CREATED_AT, getDateTime());

        int id = db.update(TABLE_EVENT, values, KEY_ID + " = ?", new String[]{String.valueOf(sEvent.getId())});
        db.close();
        return id;
    }

    /**
     * Delete Event in offline database with the corresponding id
     * @param event_id Id of event needed to be deleted
     */
    public void deleteEvent(long event_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENT, KEY_ID + " = ?", new String[] { String.valueOf(event_id)});
        db.close();
    }

    ///////////////////////////////////////////////////////////
    //CATEGORY methods
    //TODO: receive all children from one parent category
    //TODO: Method to automatically add lft and rgt to categories (create based)
    //TODO: implement something to easy get the indentation
    //TODO: create method to save all categories in file in order to fill new database on database update
    //TODO: get path to node TEST
    //DONE: rebuild tree and TEST
    //DONE: get tree from specific parent (parent and all children)
    ///////////////////////////////////////////////////////////


    public boolean rebuildCategoryTree(Category parent, long left) {
        SQLiteDatabase db = this.getWritableDatabase();
        //First time method is called for highes parent in most cases
        rebuildCategory(parent, left, db);
        db.close();
        return true;
    }

    /**
     * Kan niet goed werken met reqursive dingen!
     * @param parent
     * @param left
     * @return
     */
    public long rebuildCategory(Category parent, long left, SQLiteDatabase db){
        long right = left + 1;
        String childrenString = "SELECT * FROM "+TABLE_CATEGORY+" WHERE "+KEY_CATEGORY_PARENT_ID+" = "+parent.getId()+"";
        Cursor c = db.rawQuery(childrenString, null);
        if(c!=null) {
            if (c.moveToFirst()) {
                do {
                    Category category = new Category(c.getLong(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)), c.getLong(c.getColumnIndex(KEY_CATEGORY_PARENT_ID)), c.getLong(c.getColumnIndex(KEY_CATEGORY_LFT)), c.getLong(c.getColumnIndex(KEY_CATEGORY_RGT)), c.getInt(c.getColumnIndex(KEY_CATEGORY_COLOR)));
                    right = rebuildCategory(category, right, db);
                } while (c.moveToNext());
            }
        }

        //update database entry
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_LFT, left);
        values.put(KEY_CATEGORY_RGT, right);
        int id = db.update(TABLE_CATEGORY,values, KEY_ID + " = ?", new String[] {String.valueOf(parent.getId())});

        return right + 1;
    }


    /**
     *
     * @param node to get path from
     * @return
     */
    public List<Category> getPathToNode(Category node){
        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT * FROM "+TABLE_CATEGORY+" WHERE "+KEY_CATEGORY_LFT+" < "+node.getLft()+" AND "+KEY_CATEGORY_RGT+" > "+node.getRgt()+" ORDER BY "+KEY_CATEGORY_LFT+" ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                Category category = new Category(c.getLong(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)), c.getLong(c.getColumnIndex(KEY_CATEGORY_PARENT_ID)), c.getLong(c.getColumnIndex(KEY_CATEGORY_LFT)), c.getLong(c.getColumnIndex(KEY_CATEGORY_RGT)), c.getInt(c.getColumnIndex(KEY_CATEGORY_LAYER)), c.getInt(c.getColumnIndex(KEY_CATEGORY_COLOR)));
                categories.add(category);
            }while(c.moveToNext());
        }
        db.close();

        return categories;
    }

    /**
     * Create new entity in offline database from Category
     * @param sCategory
     * @return
     */
    public long createCategory(Category sCategory){
        SQLiteDatabase db = this.getWritableDatabase();
        int tmpLayer = -1;
        long tmpRight = 99999;

        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_ID + " = " + sCategory.getParent_id();
        Cursor c = db.rawQuery(selectQuery, null);
        if(c.moveToFirst()){
            tmpLayer = c.getInt(c.getColumnIndex(KEY_CATEGORY_LAYER)) + 1;
            tmpRight = c.getLong(c.getColumnIndex(KEY_CATEGORY_RGT));
        }

        String lftQuery = "UPDATE "+TABLE_CATEGORY+" SET "+KEY_CATEGORY_LFT+"="+KEY_CATEGORY_LFT+"+2 WHERE "+KEY_CATEGORY_LFT+">="+tmpRight;
        String rgtQuery = "UPDATE "+TABLE_CATEGORY+" SET "+KEY_CATEGORY_RGT+"="+KEY_CATEGORY_RGT+"+2 WHERE "+KEY_CATEGORY_RGT+">="+tmpRight;

        db.execSQL(lftQuery);
        db.execSQL(rgtQuery);

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, sCategory.getName()); //String can go in text
        values.put(KEY_CATEGORY_PARENT_ID, sCategory.getParent_id());
        values.put(KEY_CATEGORY_LFT, tmpRight);
        values.put(KEY_CATEGORY_RGT, tmpRight+1);
        values.put(KEY_CATEGORY_LAYER, tmpLayer);
        values.put(KEY_CATEGORY_COLOR, sCategory.getColor()); //Color int can go in Integer
        values.put(KEY_CREATED_AT, getDateTime());

        long category_id = db.insert(TABLE_CATEGORY, null, values);
        db.close();
        Log.d("CREATED Category", "Category id: "+category_id);
        return category_id;
    }

    /**
     * Create new entity in offline database from Category using inserted db
     * @param sCategory
     * @return
     */
    public long createCategory(Category sCategory, SQLiteDatabase db){

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, sCategory.getName()); //String can go in text
        values.put(KEY_CATEGORY_PARENT_ID, sCategory.getParent_id());
        values.put(KEY_CATEGORY_LFT, sCategory.getLft());
        values.put(KEY_CATEGORY_RGT, sCategory.getRgt());
        values.put(KEY_CATEGORY_LAYER, sCategory.getLayer());
        values.put(KEY_CATEGORY_COLOR, sCategory.getColor()); //Color int can go in Integer
        values.put(KEY_CREATED_AT, getDateTime());

        long category_id = db.insert(TABLE_CATEGORY, null, values);
        Log.d("CREATED Category", "Category id: "+category_id);
        return category_id;
    }

    /**
     * Return Category corresponding with the event_id
     * @param category_id id of Category to retreived from database
     * @return SmartEvent
     */
    public Category getCategory(long category_id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_ID + " = " + category_id;
        Cursor c = db.rawQuery(selectQuery, null);
        Category category = null;

        if(c!=null){
            c.moveToFirst();
            category = new Category(c.getLong(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)), c.getLong(c.getColumnIndex(KEY_CATEGORY_PARENT_ID)), c.getLong(c.getColumnIndex(KEY_CATEGORY_LFT)), c.getLong(c.getColumnIndex(KEY_CATEGORY_RGT)), c.getInt(c.getColumnIndex(KEY_CATEGORY_LAYER)), c.getInt(c.getColumnIndex(KEY_CATEGORY_COLOR)));
        }
        db.close();

        return category;
    }

    /**
     * Get all events stored in then offline database
     * @return Array list of Categories
     */
    public List<Category> getAllCategories(){
        Log.d("DatabaseHelper", "getAllCategories Started");
        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY + " ORDER BY " + KEY_CATEGORY_LFT + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                Category category = new Category(c.getLong(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)), c.getLong(c.getColumnIndex(KEY_CATEGORY_PARENT_ID)), c.getLong(c.getColumnIndex(KEY_CATEGORY_LFT)), c.getLong(c.getColumnIndex(KEY_CATEGORY_RGT)), c.getInt(c.getColumnIndex(KEY_CATEGORY_LAYER)), c.getInt(c.getColumnIndex(KEY_CATEGORY_COLOR)));
                categories.add(category);
            }while(c.moveToNext());
        }
        db.close();
        return categories;
    }

    /**
     * Get all events stored in then offline database
     * @return Array list of Categories
     */
    public List<Category> getCategoryTree(Category parent){
        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY + " WHERE "+KEY_CATEGORY_LFT+" BETWEEN "+parent.getLft()+" AND "+parent.getRgt();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                Category category = new Category(c.getLong(c.getColumnIndex(KEY_ID)), c.getString(c.getColumnIndex(KEY_CATEGORY_NAME)), c.getLong(c.getColumnIndex(KEY_CATEGORY_PARENT_ID)), c.getLong(c.getColumnIndex(KEY_CATEGORY_LFT)), c.getLong(c.getColumnIndex(KEY_CATEGORY_RGT)), c.getInt(c.getColumnIndex(KEY_CATEGORY_LAYER)), c.getInt(c.getColumnIndex(KEY_CATEGORY_COLOR)));
                categories.add(category);
            }while(c.moveToNext());
        }
        db.close();
        return categories;
    }

    /**
     * Update Event Id of SmartEvent needs to be equal to Id in Database
     * @param sCategory
     * @return
     * TODO: check if this is all good
     */
    public int updateCategory(Category sCategory){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, sCategory.getName()); //String can go in text
        values.put(KEY_CATEGORY_PARENT_ID, sCategory.getParent_id());
        values.put(KEY_CATEGORY_LFT, sCategory.getLft());
        values.put(KEY_CATEGORY_RGT, sCategory.getRgt());
        values.put(KEY_CATEGORY_COLOR, sCategory.getColor()); //Color int can go in Integer
        values.put(KEY_CREATED_AT, getDateTime());

        int id = db.update(TABLE_CATEGORY,values, KEY_ID + " = ?", new String[] {String.valueOf(sCategory.getId())});
        db.close();
        return id;
    }

    /**
     * Delete Category and all children from database
     * @param parent parent to be deleted (and all children)
     *                    TODO: Test delete category and all its children
     */
    public void deleteCategory(Category parent){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM "+TABLE_CATEGORY+" WHERE "+KEY_CATEGORY_LFT+" BETWEEN "+parent.getLft()+" AND "+parent.getRgt();
        db.execSQL(deleteQuery);
        db.close();

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
