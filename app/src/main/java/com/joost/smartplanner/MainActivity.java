package com.joost.smartplanner;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alamkanak.weekview.SmartWeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * TODO: Add database helper to mainActivity
 * TODO: Add Event when longPressed (already impl in WeekView)
 * TODO: Get Events from database
 */
public class MainActivity extends ActionBarActivity implements SmartWeekView.MonthChangeListener, SmartWeekView.EventClickListener, SmartWeekView.EventLongPressListener, SmartWeekView.EmptyClickListener{

    private SmartWeekView mSmartWeekView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference for the week view in the layout.
        mSmartWeekView = (SmartWeekView) findViewById(R.id.weekView);

        // Set an action when any event is clicked.
        mSmartWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mSmartWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mSmartWeekView.setEventLongPressListener(this);

        //Set emptyClickListener
        mSmartWeekView.setEmptyClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEventClick(WeekViewEvent weekViewEvent, RectF rectF) {

    }

    @Override
    public void onEventLongPress(WeekViewEvent weekViewEvent, RectF rectF) {

    }

    //comment
    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        /**
         * TODO: make "getEvent(int newYear, int newMonth)"method to return events for the specific month and year to be drawn in the calendar
         */

        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<>();

        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.HOUR, 1);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR, 2);
        endTime.set(Calendar.MONTH, newMonth-1);
        endTime.set(Calendar.YEAR, newYear);

        WeekViewEvent event = new WeekViewEvent(1, "Test Event", startTime, endTime);
        event.setColor(Color.RED);

        events.add(event);
        return events;
    }

    @Override
    public void onEmptyClickXY(float x, float y) {
        Log.d("coordinates", "X: " + Float.toString(x) + " Y: " + Float.toString(y) );

        int leftDaysWithGaps = (int) -(Math.round(mSmartWeekView.getCurrentOrigin().x / (mSmartWeekView.getWidthPerDay() + mSmartWeekView.getColumnGap())));
        Log.d("leftDaysWithGap", "LeftDaysWithGap: " + Integer.toString(leftDaysWithGaps));
        Log.d("DayWidth", Float.toString(mSmartWeekView.getWidthPerDay() + mSmartWeekView.getColumnGap()));
    }

    @Override
    public void onEmptyClickCalendar(Calendar tappedDay) {
        Log.d("Calendar Object", tappedDay.toString());
    }
}
