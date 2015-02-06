package com.joost.smartplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alamkanak.weekview.SmartWeekView;
import com.joost.category.Category;
import com.joost.database.DatabaseHelper;
import com.joost.layout.CategorySpinnerAdapter;
import com.joost.smartevent.SmartEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Joost on 27/01/2015.
 */
public class CreateEventDialogFragment extends DialogFragment implements OnBackPressedListener{

    private Toolbar toolbar;
    private Spinner categorySpinner;
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;
    private EditText editTextEventName;
    private Boolean endTimeSet = false;
    private Boolean startTimeSet = false;
    private Boolean endDateSet = false;
    private Boolean startDateSet = false;
    private GregorianCalendar startDateTime = new GregorianCalendar(Locale.getDefault());
    private GregorianCalendar endDateTime = new GregorianCalendar(Locale.getDefault());
    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("H:mm");
    private final int DEFAULT_EVENT_TIME = 1;
    private DatabaseHelper dbH;
    private List<Category> allCategories;
    private SmartWeekView mSmartWeekView;
    private CalendarFragment mCalendarFragment;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogStyle);

        dbH = ((MainFragmentActivity)getActivity()).getDatabaseHelper();
        getAllCategories();
        endDateTime.add(Calendar.HOUR_OF_DAY, DEFAULT_EVENT_TIME);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if(d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_event, container, false);

        ///////////////////////
        //onbackpressedlistener
        //////////////////////
        ((MainFragmentActivity)getActivity()).setOnBackClickListener(this);

        ///////////////////////
        //Toolbar
        //////////////////////
        toolbar = (Toolbar) root.findViewById(R.id.create_event_app_bar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setTitle("New Event");
        toolbar.inflateMenu(R.menu.menu_create_event);

        //OnClickListeners
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Confirm dialog
                cancelDialog();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.menu_action_save:
                        createNewEvent();
                        //Close fragment

                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        /////////////////////////
        //Get Input things
        /////////////////////////
        categorySpinner = (Spinner) root.findViewById(R.id.category_spinner);
        startDate = (TextView) root.findViewById(R.id.create_event_start_date_tv);
        startTime = (TextView) root.findViewById(R.id.create_event_start_time_tv);
        endDate = (TextView) root.findViewById(R.id.create_event_end_date_tv);
        endTime = (TextView) root.findViewById(R.id.create_event_end_time_tv);
        editTextEventName = (EditText) root.findViewById(R.id.editTextEventName);

        startDate.setText(startDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(startDateTime.getTime()));
        endDate.setText(endDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(endDateTime.getTime()));
        startTime.setText(simpleTimeFormat.format(startDateTime.getTime()));
        endTime.setText(simpleTimeFormat.format(endDateTime.getTime()));

        //Set spinners
        CategorySpinnerAdapter categorySpinnerAdapter = new CategorySpinnerAdapter(getActivity(), R.layout.custom_spinner_item, allCategories);
        categorySpinner.setAdapter(categorySpinnerAdapter);

        //set onclicklistners
        //TODO: set date and time to current time, or selected dateTime if so selected
        //TODO: make it so that startDateTime is always before endDateTime
        //DONE: fix that after 23:00 sometimes the date is not set correctly (more than 1 hour extra because something wen wrong with the time calculations)
        //TODO: Set title when it is not set (debug)
        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        //Get difference to set time to make sure endDateTime > startDateTime
                        Long differenceMillis = endDateTime.getTimeInMillis() - startDateTime.getTimeInMillis();
                        Long differenceMinute = differenceMillis / 1000 / 60; //Later it need to be parsed to int (get a problem if difference is more than 24 days)

                        startDateTime.set(year, month, day);

                        startDate.setText(startDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(startDateTime.getTime()));
                        //startDate set
                        startDateSet = true;

                        //set endDate to the startDate if endDate is not yet set
                        if(!endDateSet){
                            //Changed to something new (commeted if not working

//                            //Add one hour to set startDateTime
//                            int hourDif = endDateTime.get(Calendar.HOUR_OF_DAY) - startDateTime.get(Calendar.HOUR_OF_DAY);
//                            if(hourDif<0){hourDif = hourDif+24;}
//                            int minuteDif = endDateTime.get(Calendar.MINUTE) - startDateTime.get(Calendar.MINUTE);
//                            Log.d("Difference","HourDif: "+hourDif+" minuteDif: "+minuteDif);
//
//                            endDateTime = (GregorianCalendar)startDateTime.clone();
//                            endDateTime.add(Calendar.HOUR_OF_DAY, hourDif);
//                            endDateTime.add(Calendar.MINUTE, minuteDif);

                            endDateTime = (GregorianCalendar)startDateTime.clone();
                            endDateTime.add(Calendar.MINUTE, (int)Math.abs(differenceMinute));

                            Log.d("New dates","start: "+startDateTime.toString());
                            Log.d("New dates","  end: "+endDateTime.toString());
                            //Set time and Date fields
                            endTime.setText(simpleTimeFormat.format(endDateTime.getTime()));
                            endDate.setText(endDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(endDateTime.getTime()));
                        }

                        //when endTime > startTime
                        if(startDateTime.getTimeInMillis()>endDateTime.getTimeInMillis()){
                            endDateTime = (GregorianCalendar)startDateTime.clone();
                            endDateTime.add(Calendar.MINUTE, (int)Math.abs(differenceMinute));
                            endTime.setText(simpleTimeFormat.format(endDateTime.getTime()));
                            endDate.setText(endDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(endDateTime.getTime()));
                        }
                    }
                }, startDateTime.get(Calendar.YEAR), startDateTime.get(Calendar.MONTH), startDateTime.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        startTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        //Get difference to set time to make sure endDateTime > startDateTime
                        Long differenceMillis = endDateTime.getTimeInMillis() - startDateTime.getTimeInMillis();
                        Long differenceMinute = differenceMillis / 1000 / 60; //Later it need to be parsed to int (get a problem if difference is more than 24 days)

                        startDateTime.set(Calendar.HOUR_OF_DAY, hour);
                        startDateTime.set(Calendar.MINUTE, minute);

                        startTime.setText(simpleTimeFormat.format(startDateTime.getTime()));
                        //startime set
                        startTimeSet = true;
                        //Same as date
                        if(!endTimeSet){
                            endDateTime.set(Calendar.HOUR_OF_DAY, hour);
                            endDateTime.add(Calendar.HOUR_OF_DAY, DEFAULT_EVENT_TIME);
                            endDateTime.set(Calendar.MINUTE, minute);

                            //Set time and Date fields
                            endTime.setText(simpleTimeFormat.format(endDateTime.getTime()));
                            endDate.setText(endDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(endDateTime.getTime()));
                        }

                        //when endTime > startTime
                        if(startDateTime.getTimeInMillis()>endDateTime.getTimeInMillis()){
                            endDateTime = (GregorianCalendar)startDateTime.clone();
                            endDateTime.add(Calendar.MINUTE, (int)Math.abs(differenceMinute));
                            endTime.setText(simpleTimeFormat.format(endDateTime.getTime()));
                            endDate.setText(endDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(endDateTime.getTime()));
                        }
                    }
                }, startDateTime.get(Calendar.HOUR_OF_DAY),startDateTime.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        //Get difference to set time to make sure endDateTime > startDateTime
                        Long differenceMillis = endDateTime.getTimeInMillis() - startDateTime.getTimeInMillis();
                        Long differenceMinute = differenceMillis / 1000 / 60; //Later it need to be parsed to int (get a problem if difference is more than 24 days)

                        endDateTime.set(year, month, day);

                        endDate.setText(endDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(endDateTime.getTime()));

                        endDateSet = true;

                        if(!startDateSet){
//                            int hourDif = endDateTime.get(Calendar.HOUR_OF_DAY) - startDateTime.get(Calendar.HOUR_OF_DAY);
//                            if(hourDif<0){hourDif = hourDif+24;}
//                            int minuteDif = endDateTime.get(Calendar.MINUTE) - startDateTime.get(Calendar.MINUTE);
//                            startDateTime = (GregorianCalendar) endDateTime.clone();
//                            startDateTime.add(Calendar.HOUR_OF_DAY, -hourDif);
//                            startDateTime.add(Calendar.MINUTE, -minuteDif);

                            startDateTime = (GregorianCalendar)endDateTime.clone();
                            startDateTime.add(Calendar.MINUTE, -(int)Math.abs(differenceMinute));

                            //Set time and Date fields
                            startTime.setText(simpleTimeFormat.format(startDateTime.getTime()));
                            startDate.setText(startDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(startDateTime.getTime()));
                        }

                        //when endTime > startTime
                        if(startDateTime.getTimeInMillis()>endDateTime.getTimeInMillis()){
                            startDateTime = (GregorianCalendar)endDateTime.clone();
                            startDateTime.add(Calendar.MINUTE, -(int)Math.abs(differenceMinute));
                            startTime.setText(simpleTimeFormat.format(startDateTime.getTime()));
                            startDate.setText(startDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(startDateTime.getTime()));
                        }
                    }
                }, endDateTime.get(Calendar.YEAR), endDateTime.get(Calendar.MONTH), endDateTime.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        //Get difference to set time to make sure endDateTime > startDateTime
                        Long differenceMillis = endDateTime.getTimeInMillis() - startDateTime.getTimeInMillis();
                        Long differenceMinute = differenceMillis / 1000 / 60; //Later it need to be parsed to int (get a problem if difference is more than 24 days)

                        endDateTime.set(Calendar.HOUR_OF_DAY, hour);
                        endDateTime.set(Calendar.MINUTE, minute);

                        endTime.setText(simpleTimeFormat.format(endDateTime.getTime()));
                        endTimeSet = true;

                        if(!startTimeSet){
                            startDateTime.set(Calendar.HOUR_OF_DAY, hour);
                            startDateTime.add(Calendar.HOUR_OF_DAY, -DEFAULT_EVENT_TIME);
                            startDateTime.set(Calendar.MINUTE, minute);

                            //Set time and Date fields
                            startTime.setText(simpleTimeFormat.format(startDateTime.getTime()));
                            startDate.setText(startDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(startDateTime.getTime()));
                        }

                        //when endTime > startTime
                        if(startDateTime.getTimeInMillis()>endDateTime.getTimeInMillis()){
                            startDateTime = (GregorianCalendar)endDateTime.clone();
                            startDateTime.add(Calendar.MINUTE, -(int)Math.abs(differenceMinute));
                            startTime.setText(simpleTimeFormat.format(startDateTime.getTime()));
                            startDate.setText(startDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(startDateTime.getTime()));
                        }
                    }
                }, endDateTime.get(Calendar.HOUR_OF_DAY), endDateTime.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });


        return root;
    }



    private void cancelDialog(){
        //Confirm dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Discard Event");
        builder.setMessage("Do you want to discard this event?");
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createNewEvent(){
        //Refresh categories
        getAllCategories();
        //Event name
        String eventName = "(No title)";
        if(editTextEventName.getText().toString()!=""){
            eventName = editTextEventName.getText().toString();
        }
        //Event Color
        int eventColor = allCategories.get(categorySpinner.getSelectedItemPosition()).getColor();
        //CategoryId
        long categoryId = allCategories.get(categorySpinner.getSelectedItemPosition()).getId();
        //The new event
        SmartEvent newEvent = new SmartEvent(0, eventName, startDateTime, endDateTime, eventColor, categoryId);

        //Put in dataBase
        dbH.createSmartEvent(newEvent);

        //Toast for feedback
        Toast.makeText(getActivity(),eventName + "Created", Toast.LENGTH_SHORT).show();

        //Refresh SmartWeekView when it is set
        if(mSmartWeekView!=null && mCalendarFragment!=null){
            mCalendarFragment.getAllEvents();
            mSmartWeekView.notifyDatasetChanged();
            Log.d("DEBUG","View notified");
        }
        Log.d("Debug", "Dismiss now");
        dismiss();
    }

    public SmartWeekView getSmartWeekView() {
        return mSmartWeekView;
    }

    public void setSmartWeekView(SmartWeekView mSmartWeekView) {
        this.mSmartWeekView = mSmartWeekView;
        Log.d("Debug", "SmartWeekView set");
    }

    public void setCalendarFragment(CalendarFragment mCalendarFragment) {
        this.mCalendarFragment = mCalendarFragment;
    }

    private void getAllCategories() {
        allCategories = dbH.getAllCategories();
    }

    @Override
    public void onBackPressedCallBack() {
        cancelDialog();
    }
}
