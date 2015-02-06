package com.joost.smartevent;

import android.graphics.Color;

import com.alamkanak.weekview.WeekViewEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Joost on 21/12/2014.
 * DONE: Make EventManager to save and retreive smartEvents (DatabaseHelper) (20141223)
 * DONE: Make eventDone variable and getter and setter (20141221)
 * DONE: toJsonString and constructor from JsonString (20141221)
 */
public class SmartEvent extends WeekViewEvent {

    private Boolean eventDone = false;
    private long mCategoryId = -1;


    /**
     * Initializes the SmartEvent for week view.
     *
     * @param id          Id of event
     * @param name        Name of the event.
     * @param startYear   Year when the event starts.
     * @param startMonth  Month when the event starts.
     * @param startDay    Day when the event starts.
     * @param startHour   Hour (in 24-hour format) when the event starts.
     * @param startMinute Minute when the event starts.
     * @param endYear     Year when the event ends.
     * @param endMonth    Month when the event ends.
     * @param endDay      Day when the event ends.
     * @param endHour     Hour (in 24-hour format) when the event ends.
     * @param endMinute   Minute when the event ends.
     */
    public SmartEvent(long id, String name, int startYear, int startMonth, int startDay, int startHour, int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        super(id, name, startYear, startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute);
        mColor = Color.GREEN;
    }

    /**
     * Initializes the SmartEvent for week view.
     *
     * @param id        Id of event
     * @param name      Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime   The time when the event ends.
     */
    public SmartEvent(long id, String name, Calendar startTime, Calendar endTime) {
        super(id, name, startTime, endTime);
        mColor = Color.GREEN;
    }

    /**
     * Initializes the SmartEvent     *
     * @param id        Id of event
     * @param name      Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime   The time when the event ends.
     * @param color     The color of the event
     */
    public SmartEvent(long id, String name, Calendar startTime, Calendar endTime, int color) {
        super(id, name, startTime, endTime);
        mColor = color;
    }

    /**
     * Initializes the SmartEvent     *
     * @param id        Id of event
     * @param name      Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime   The time when the event ends.
     * @param color     The color of the event
     */
    public SmartEvent(long id, String name, Calendar startTime, Calendar endTime, int color, long mCategoryId) {
        super(id, name, startTime, endTime);
        mColor = color;
        this.mCategoryId = mCategoryId;
    }

    /**
     * Create SmartEvent from JSONString
     * @param jsonString String with the information of a previous created SmartEvent object
     */
    public SmartEvent(String jsonString){
        super(jsonString);
        //Try to parse string to JSONObject and get values from it
        try {
            JSONObject json = new JSONObject(jsonString);
            this.mColor = json.getInt("color");
        }catch(JSONException e){

        }

    }

    public Boolean getEventDone() {
        return eventDone;
    }

    public void setEventDone(Boolean eventDone) {
        this.eventDone = eventDone;
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long categoryId) {
        this.mCategoryId = categoryId;
    }

    public String toJsonString() {
        JSONObject json = new JSONObject();
        try {
            json.put("eventId", mId);
            json.put("name", mName);
            json.put("startTime", mStartTime.getTimeInMillis());
            json.put("endTime", mEndTime.getTimeInMillis());
            json.put("color", mColor);
            json.put("categoryId", mCategoryId);
        }catch(JSONException e){

        }
        return json.toString();
    }
}
