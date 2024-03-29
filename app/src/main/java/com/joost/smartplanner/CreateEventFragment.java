package com.joost.smartplanner;


import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;


/**
 * created by Joost
 * DONE: put create event in fullscreen dialog
 */
public class CreateEventFragment extends Fragment {

    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_event, container, false);


        //Get textviews
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
