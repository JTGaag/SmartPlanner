package com.joost.smartplanner;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.SmartWeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.joost.database.DatabaseHelper;
import com.joost.fab.FloatingActionButton;
import com.joost.smartevent.SmartEvent;
import com.joost.utilities.App;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Joost on 27/12/2014.
 * DONE: port all smartWeekview actions to this fragment (20141228)
 * TODO: Research impact of parsing SmartEvent to WeekViewEvent in order to give to SmartWeekView-->WeekView onMonthChange()
 * TODO: Investigate event draw when scrolling fast
 * TODO: fix jumping to current time after event created, but overscrolling to beyond 24h
 * TODO: add tempEvents when clicked on calender to add new event (without information exept the time clicked on) these temp evnts need to be deleted after onPause and are not saved in the database. When clicked on these display the create event dialogfragment with the right time
 * DONE: put DatabaseHelper in Activity to be used by all fragments
 */
public class CalendarFragment extends Fragment implements SmartWeekView.MonthChangeListener, SmartWeekView.EventClickListener, SmartWeekView.EventLongPressListener, SmartWeekView.EmptyClickListener{

    //Variables for SmartWeekView to work (including databaseHandler and storage for received events)
    private SmartWeekView mSmartWeekView;
    private DatabaseHelper db;
    private List<SmartEvent> allSmartEvents = new ArrayList<SmartEvent>();
    private List<SmartEvent> tmpSmartEvents = new ArrayList<SmartEvent>();
    private FloatingActionButton fab;
    private CalendarFragment calendarFragment;

    final String CALENDAR_FRAGMENT_TAG = "calendar_fragment";
    final String CREATE_EVENT_FRAGMENT_TAG = "create_event_fragment";
    final String CATEGORY_FRAGMENT_TAG = "category_fragment";
    private static final String EDIT_EVENT_FRAGMENT_TAG = "edit_event_fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Construct DatabaseHelper
        //TODO: fix no database error (not the quickfix)
        db = ((MainFragmentActivity)getActivity()).getDatabaseHelper();
        if(db!=null) {
            allSmartEvents = new ArrayList<SmartEvent>(db.getAllEvents());
        }
        calendarFragment = this;
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

        //Initiaze fab
        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                CreateEventDialogFragment frag = new CreateEventDialogFragment();
                frag.setSmartWeekView(mSmartWeekView);
                frag.setCalendarFragment(calendarFragment);
                frag.show(ft, CREATE_EVENT_FRAGMENT_TAG);
            }
        });

        //return the view
        return view;
    }


    @Override
    public void onEmptyClickXY(float x, float y) {

    }

    @Override
    public void onEmptyClickCalendar(Calendar tappedDay) {
        //Log.d("Calendar Object", tappedDay.toString());
        //Create new SmartEvent with popup to ask name and randomize color
        // TODO: Add nice popup to set Time and More for new SmartEvent etc.
        // TODO: check if final start and endtime are correct (quick fix)

        final Calendar startDateTime = (Calendar)tappedDay.clone();
        startDateTime.set(Calendar.MINUTE, 0);
        final Calendar endDateTime = (Calendar)startDateTime.clone();
        endDateTime.add(Calendar.HOUR_OF_DAY, 1);

        SmartEvent tmpEvent =  new SmartEvent(-tmpSmartEvents.size()-1, "New Event",startDateTime, endDateTime, App.getContext().getResources().getColor(R.color.tmp_event_color),-1);
        tmpSmartEvents.add(tmpEvent);
        mSmartWeekView.notifyDatasetChanged();
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        if(event.getId()>=0) { //Id is larger or equal to 0 if it is a saved event
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            EditEventDialogFragment frag = new EditEventDialogFragment();
            frag.setSmartWeekView(mSmartWeekView);
            frag.setCalendarFragment(calendarFragment);
            frag.setEditEvent(db.getSmartEvent(event.getId()));
            frag.show(ft, EDIT_EVENT_FRAGMENT_TAG);
        }else{//If the id is smaller then 0 it is a tmp event (need to be edited
            //Retrieve SmartEvent from WeekViewEvent out of tmpList
            SmartEvent editEvent = null;
            for(int i = 0; i < tmpSmartEvents.size(); i++){
                SmartEvent tmpEvent = tmpSmartEvents.get(i);
                Log.d("TMP event", "ID: "+event.getId());
                if(tmpEvent.getId() == event.getId()){
                    editEvent = tmpEvent;
                }
            }
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            EditEventDialogFragment frag = new EditEventDialogFragment();
            frag.setSmartWeekView(mSmartWeekView);
            frag.setCalendarFragment(calendarFragment);
            //Get right event
            if(editEvent!=null) {
                frag.setEditEvent(editEvent);
            }
            frag.show(ft, EDIT_EVENT_FRAGMENT_TAG);
        }
    }

    @Override
    public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
        Log.d("Event", "ID: "+event.getId());
        if(event.getId()>=0) { //Id is larger or equal to 0 if it is a saved event
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
        }else{//If the id is smaller then 0 it is a tmp event (need to be edited
            deleteTmpEvent(event.getId());
        }
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.

        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>(getSmartEvents(newYear, newMonth-1));
        events.addAll(getTmpSmartEvents(newYear, newMonth-1));
        return events;
    }

    //Get events from database
    public List<WeekViewEvent> getSmartEvents(int newYear, int newMonth){
        Log.d("CalendarFragment","Year: " + newYear + " Month: " + newMonth);
        List<WeekViewEvent> tmpList = new ArrayList<>();

        //Itterate over allSmartEvents
        for(SmartEvent tmpEvent : allSmartEvents){
            Log.d("Eobtained events", "Year: "+tmpEvent.getStartTime().get(Calendar.YEAR) + " Month: " + tmpEvent.getStartTime().get(Calendar.MONTH));
            if(tmpEvent.getStartTime().get(Calendar.YEAR) == newYear && tmpEvent.getStartTime().get(Calendar.MONTH) == newMonth){
                //Log.d("Event:", "Event: " + tmpEvent.getId() + " = " + tmpEvent.toJsonString());
                tmpList.add((WeekViewEvent)tmpEvent);
            }
        }
        return tmpList;
    }

    public List<WeekViewEvent> getTmpSmartEvents(int newYear, int newMonth){
        List<WeekViewEvent> tmpList = new ArrayList<WeekViewEvent>();
        for(SmartEvent tmpEvent : tmpSmartEvents){
            if(tmpEvent.getStartTime().get(Calendar.YEAR) == newYear && tmpEvent.getStartTime().get(Calendar.MONTH) == newMonth){
                tmpList.add((WeekViewEvent)tmpEvent);
            }
        }
        return  tmpList;
    }

    public void getAllEvents(){
        allSmartEvents = db.getAllEvents();
    }

    public void deleteTmpEvent(long tmpEventId){
        SmartEvent deleteEvent = null;
        for(int i = 0; i < tmpSmartEvents.size(); i++){
            SmartEvent tmpEvent = tmpSmartEvents.get(i);
            Log.d("TMP event", "ID: "+tmpEventId);
            if(tmpEvent.getId() == tmpEventId){
                deleteEvent = tmpEvent;
            }
        }
        if(deleteEvent!=null) {
            tmpSmartEvents.remove(deleteEvent);
            mSmartWeekView.notifyDatasetChanged();
        }
    }
}
