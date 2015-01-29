package com.joost.smartplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.alamkanak.weekview.SmartWeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.joost.database.DatabaseHelper;
import com.joost.smartevent.SmartEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * DONE: Add database helper to mainActivity (20141226)
 * DONE: Add Event when longPressed (already impl in WeekView) (20141226)
 * DONE: Get Events from database (20141226)
 * DONE: delete event (20141226)
 * DONE: redraw when event is created (20141226)
 * Done: Add Floating Action Button (FAB) (20141227)
 */
public class MainActivity extends Activity implements SmartWeekView.MonthChangeListener, SmartWeekView.EventClickListener, SmartWeekView.EventLongPressListener, SmartWeekView.EmptyClickListener{

    private SmartWeekView mSmartWeekView;
    private DatabaseHelper db;
    private List<SmartEvent> allSmartEvents = new ArrayList<SmartEvent>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Construct DatabaseHelper
        db = new DatabaseHelper(getApplicationContext());
        allSmartEvents = new ArrayList<SmartEvent>(db.getAllEvents());



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
    public void onEventLongPress(final WeekViewEvent weekViewEvent, RectF rectF) {
        //PopUp
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Event?");
        alert.setMessage("Are you sure you want to delete " + weekViewEvent.getName() + "?");

        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                db.deleteEvent(weekViewEvent.getId());
                allSmartEvents = db.getAllEvents();
                //Redraw with events refreshed
                mSmartWeekView.notifyDatasetChanged();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    //comment
    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        /**
         * DONE: make "getEvent(int newYear, int newMonth)"method to return events for the specific month and year to be drawn in the calendar (20141226)
         */

        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>(getSmartEvents(newYear, newMonth));


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
    public void onEmptyClickCalendar(final Calendar tappedDay) {
        Log.d("Calendar Object", tappedDay.toString());
        //Create new SmartEvent with popup to ask name and randomize color
        Boolean confirmed = false;

        //PopUp
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Get Event Name");
        alert.setMessage("What is the name of the event");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Calendar startTime = (Calendar)tappedDay.clone();
                Calendar endTime = (Calendar)tappedDay.clone();
                endTime.add(Calendar.HOUR_OF_DAY, 1);
                SmartEvent newEvent = new SmartEvent(0,input.getText().toString(), startTime, endTime);
                db.createSmartEvent(newEvent);
                allSmartEvents = db.getAllEvents();

                //Redraw with events refreshed
                mSmartWeekView.notifyDatasetChanged();


            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
        }
        });

        alert.show();



    }

    public List<WeekViewEvent> getSmartEvents(int newYear, int newMonth){
        List<WeekViewEvent> tmpList = new ArrayList<>();

        //Itterate over allSmartEvents
        for(SmartEvent tmpEvent : allSmartEvents){
            if(tmpEvent.getStartTime().get(Calendar.YEAR) == newYear && tmpEvent.getStartTime().get(Calendar.MONTH) == newMonth){
                Log.d("Event:", "Event: " + tmpEvent.getId() + " = " + tmpEvent.toJsonString());
                tmpList.add((WeekViewEvent)tmpEvent);
            }
        }
        return tmpList;
    }
}
