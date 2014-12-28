package com.joost.smartplanner;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alamkanak.weekview.SmartWeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.joost.smartplanner.smartevent.DatabaseHelper;
import com.joost.smartplanner.smartevent.SmartEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Joost on 27/12/2014.
 * DONE: port all smartWeekview actions to this fragment (20141228)
 * TODO: Research impact of parsing SmartEvent to WeekViewEvent in order to give to SmartWeekView-->WeekView onMonthChange()
 */
public class CalendarFragment extends Fragment implements SmartWeekView.MonthChangeListener, SmartWeekView.EventClickListener, SmartWeekView.EventLongPressListener, SmartWeekView.EmptyClickListener{

    //Variables for SmartWeekView to work (including databaseHnadler and storage for retreived events)
    private SmartWeekView mSmartWeekView;
    private DatabaseHelper db;
    private List<SmartEvent> allSmartEvents = new ArrayList<SmartEvent>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Construct DatabaseHelper
        db = new DatabaseHelper(getActivity().getApplicationContext());
        allSmartEvents = new ArrayList<SmartEvent>(db.getAllEvents());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_calendar, container, false);

        // Get a reference for the week view in the layout.
        mSmartWeekView = (SmartWeekView) view.findViewById(R.id.smartWeekView);

        // Set an action when any event is clicked.
        mSmartWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mSmartWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mSmartWeekView.setEventLongPressListener(this);

        //Set emptyClickListener
        mSmartWeekView.setEmptyClickListener(this);

        //return the view
        return view;
    }


    @Override
    public void onEmptyClickXY(float x, float y) {

    }

    @Override
    public void onEmptyClickCalendar(Calendar tappedDay) {
        Log.d("Calendar Object", tappedDay.toString());
        //Create new SmartEvent with popup to ask name and randomize color
        // TODO: Add nice popup to set Time and More for new SmartEvent etc.
        // TODO: check if final start and endtime are correct (quick fix)
        Boolean confirmed = false;

        //PopUp
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Get Event Name");
        alert.setMessage("What is the name of the event");

        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        alert.setView(input);


        final Calendar startTime = (Calendar)tappedDay.clone();
        final Calendar endTime = (Calendar)tappedDay.clone();

        //Ok Button
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                endTime.add(Calendar.HOUR_OF_DAY, 1);
                SmartEvent newEvent = new SmartEvent(0, input.getText().toString(), startTime, endTime);
                db.createSmartEvent(newEvent);
                allSmartEvents = db.getAllEvents();

                //Redraw with events refreshed
                mSmartWeekView.notifyDatasetChanged();
            }
        });
        //Cancel button
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        //Show alert
        alert.show();
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
        //PopUp asking to delete event
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Delete Event?");
        alert.setMessage("Are you sure you want to delete " + event.getName() + "?");

        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                db.deleteEvent(event.getId());
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

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>(getSmartEvents(newYear, newMonth));
        return events;
    }

    //Get events from database
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
