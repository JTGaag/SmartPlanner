package com.joost.smartplanner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Created by Joost on 27/01/2015.
 */
public class CreateEventDialogFragment extends DialogFragment {
    private Toolbar toolbar;
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogStyle);
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
        //Toolbar
        //////////////////////
        toolbar = (Toolbar) root.findViewById(R.id.create_event_app_bar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setTitle("New Event");
        toolbar.inflateMenu(R.menu.menu_create_event);

        /////////////////////////
        //Get Input things
        /////////////////////////
        startDate = (TextView) root.findViewById(R.id.create_event_start_date_tv);
        startTime = (TextView) root.findViewById(R.id.create_event_start_time_tv);
        endDate = (TextView) root.findViewById(R.id.create_event_end_date_tv);
        endTime = (TextView) root.findViewById(R.id.create_event_end_time_tv);

        //set onclicklistners
        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        startDate.setText(day + "/" + month + "/" + year);
                    }
                }, 2015, 01, 27);
                datePickerDialog.show();
            }
        });

        startTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        startTime.setText(hour + ":" + minute);
                    }
                }, 12,15, true);
                timePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        endDate.setText(day + "/" + month + "/" + year);
                    }
                }, 2015, 01, 27);
                datePickerDialog.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        endTime.setText(hour + ":" + minute);
                    }
                }, 12,15, true);
                timePickerDialog.show();
            }
        });


        return root;
    }
}
