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
public class EditEventDialogFragment extends DialogFragment implements OnBackPressedListener{

    private Toolbar toolbar;
    private Spinner categorySpinner;
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;
    private EditText editTextEventName;

    private GregorianCalendar startDateTime = new GregorianCalendar(Locale.getDefault());
    private GregorianCalendar endDateTime = new GregorianCalendar(Locale.getDefault());
    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("H:mm");
    private final int DEFAULT_EVENT_TIME = 1;
    private DatabaseHelper dbH;
    private List<Category> allCategories;
    private SmartWeekView mSmartWeekView;
    private CalendarFragment mCalendarFragment;

    //If event is tmp event more things need to be done when finished
    private boolean isTmpEvent = false;
    private SmartEvent mEditEvent = null;
    private int selectedCategory = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogStyle);

        dbH = ((MainFragmentActivity)getActivity()).getDatabaseHelper();
        getAllCategories();
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
        View root = inflater.inflate(R.layout.fragment_edit_event, container, false);

        ///////////////////////
        //if event is not set dismiss fragment (something went wrong and nothing can be done at this point
        //////////////////////
        if(mEditEvent == null){
            Log.e("Edit event", "ERROR no event is set for the fragment. Please setEditEvent before showing fragment");
            dismiss();
        }

        ///////////////////////
        //onbackpressedlistener
        //////////////////////
        ((MainFragmentActivity)getActivity()).setOnBackClickListener(this);

        ///////////////////////
        //Toolbar
        //////////////////////
        toolbar = (Toolbar) root.findViewById(R.id.edit_event_app_bar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setTitle("Edit Event");
        toolbar.inflateMenu(R.menu.menu_edit_event);

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
                        if(isTmpEvent) {
                            createNewEvent();
                        }else {
                            editEvent();
                        }
                        dismiss();
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

        /////////////////////////
        //Set information from editEvent
        /////////////////////////
        getAllCategories();
        if(!isTmpEvent) {//Only when existing event is edited
            editTextEventName.setText(mEditEvent.getName());
        }
        startDateTime = (GregorianCalendar) mEditEvent.getStartTime().clone();
        endDateTime = (GregorianCalendar) mEditEvent.getEndTime().clone();
        for(int i = 0; i<allCategories.size(); i++){
            if(mEditEvent.getCategoryId() == allCategories.get(i).getId()){
                selectedCategory = i;
            }
        }


        startDate.setText(startDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(startDateTime.getTime()));
        endDate.setText(endDateTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) + ", " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(endDateTime.getTime()));
        startTime.setText(simpleTimeFormat.format(startDateTime.getTime()));
        endTime.setText(simpleTimeFormat.format(endDateTime.getTime()));

        //Set spinners
        CategorySpinnerAdapter categorySpinnerAdapter = new CategorySpinnerAdapter(getActivity(), R.layout.custom_spinner_item, allCategories);
        categorySpinner.setAdapter(categorySpinnerAdapter);
        categorySpinner.setSelection(selectedCategory);

        //set onclicklistners
        //TODO: set date and time to current time, or selected dateTime if so selected
        //DONE: make it so that startDateTime is always before endDateTime
        //DONE: fix that after 23:00 sometimes the date is not set correctly (more than 1 hour extra because something wen wrong with the time calculations)
        //DONE: Set title when it is not set (debug)
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
        builder.setTitle("Discard Changes");
        builder.setMessage("Do you want to discard all the changes for this event?");
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

        if(editTextEventName.getText().toString()!=""&&editTextEventName.getText()!=null&&editTextEventName.getText().toString()!=null&&editTextEventName.getText().length()!=0){
            eventName = editTextEventName.getText().toString();
        }
        //Event Color
        int eventColor = allCategories.get(categorySpinner.getSelectedItemPosition()).getColor();
        //CategoryId
        long categoryId = allCategories.get(categorySpinner.getSelectedItemPosition()).getId();
        //The new event
        SmartEvent newEvent = new SmartEvent(0, eventName, startDateTime, endDateTime, eventColor, categoryId);

        //Cancel for now need to be updated
        dbH.createSmartEvent(newEvent);

        //TODO: Remove tmpEvent from tmp save in calendarFragment
        if(mCalendarFragment!=null){
            mCalendarFragment.deleteTmpEvent(mEditEvent.getId());
        }

        //Toast for feedback
        Toast.makeText(getActivity(),eventName + " Created", Toast.LENGTH_SHORT).show();

        //Refresh SmartWeekView when it is set
        if(mSmartWeekView!=null && mCalendarFragment!=null){
            mCalendarFragment.getAllEvents();
            mSmartWeekView.notifyDatasetChanged();
            Log.d("DEBUG","View notified");
        }
    }

    private void editEvent() {
        //Refresh categories
        getAllCategories();
        //Event name
        String eventName = "(No title)";
        if(editTextEventName.getText().toString()!=""&&editTextEventName.getText()!=null&&editTextEventName.getText().toString()!=null&&editTextEventName.getText().length()!=0){
            eventName = editTextEventName.getText().toString();
        }
        mEditEvent.setName(eventName);
        mEditEvent.setStartTime(startDateTime);
        mEditEvent.setEndTime(endDateTime);
        mEditEvent.setColor(allCategories.get(categorySpinner.getSelectedItemPosition()).getColor());
        mEditEvent.setCategoryId(allCategories.get(categorySpinner.getSelectedItemPosition()).getId());

        dbH.updateEvent(mEditEvent);

        //Toast for feedback
        Toast.makeText(getActivity(),eventName + " Edited", Toast.LENGTH_SHORT).show();

        //Refresh SmartWeekView when it is set
        if(mSmartWeekView!=null && mCalendarFragment!=null){
            mCalendarFragment.getAllEvents();
            mSmartWeekView.notifyDatasetChanged();
            Log.d("DEBUG","View notified");
        }
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

    public void setEditEvent(SmartEvent mEditEvent) {
        this.mEditEvent = mEditEvent;
        if(mEditEvent.getId()<0){
            isTmpEvent = true;
        }
    }
}


