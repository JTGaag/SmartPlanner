package com.joost.layout;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joost.smartplanner.R;

import java.util.ArrayList;

/**
 * Created by Joost on 02/02/2015.
 */
public class ColorAdapter extends ArrayAdapter<SpinnerItemColor> {

    private Activity context;
    private ArrayList<SpinnerItemColor> data;

    public ColorAdapter(Activity context, int resource, ArrayList<SpinnerItemColor> objects) {
        super(context, resource, objects);
        this.context = context;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //The view of the spinner itself
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        //The view of the dropdown menu when the spinner is clicked
        View row = convertView;
        if(row == null){
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_layout_color, parent, false);
        }

        //Get the right object for that row
        SpinnerItemColor item = data.get(position);

        if(item!=null){
            //Set all the data in the view
            View colorCircle = (View)row.findViewById(R.id.colorCircle);
            GradientDrawable colorCircleDrawable = (GradientDrawable) colorCircle.getBackground();
            TextView colorName = (TextView)row.findViewById(R.id.colorName);
            if(colorCircleDrawable!=null){
                colorCircleDrawable.setColor(item.getColor());
            }
            if(colorName!=null){
                colorName.setText(item.getColorName());
            }

        }

        return row;
    }
}
