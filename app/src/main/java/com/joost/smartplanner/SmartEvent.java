package com.joost.smartplanner;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;

/**
 * Created by Joost on 21/12/2014.
 * TODO: Make EventManager to save and retreive smartEvents
 * DONE: Make eventDone variable and getter and setter (20141221)
 */
public class SmartEvent extends WeekViewEvent {

    private Boolean eventDone = false;


    /**
     * Initializes the event for week view.
     *
     * @param id
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
    }

    /**
     * Initializes the event for week view.
     *
     * @param id
     * @param name      Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime   The time when the event ends.
     */
    public SmartEvent(long id, String name, Calendar startTime, Calendar endTime) {
        super(id, name, startTime, endTime);
    }

    public Boolean getEventDone() {
        return eventDone;
    }

    public void setEventDone(Boolean eventDone) {
        this.eventDone = eventDone;
    }
}
