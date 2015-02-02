package com.joost.layout;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joost.category.Category;
import com.joost.smartplanner.R;

import java.util.List;

/**
 * Created by Joost on 02/02/2015.
 */
public class CategoryListViewAdapter extends ArrayAdapter<Category> {

    Activity context;
    List<Category> data;

    public CategoryListViewAdapter(Activity context, int resource, List<Category> objects) {
        super(context, resource, objects);
        this.context = context;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.category_list_item, parent, false);
        View categoryColor = (View)row.findViewById(R.id.categoryColor);
        GradientDrawable categoryColorDrawable = (GradientDrawable) categoryColor.getBackground();
        TextView categoryName = (TextView)row.findViewById(R.id.categoryName);
        LinearLayout indentView = (LinearLayout)row.findViewById(R.id.indentView);

        //Set margin to have indentations
        if(categoryColor!=null){
            Resources res = context.getResources();
            int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16*data.get(position).getLayer()+4, res.getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(left, ViewGroup.LayoutParams.MATCH_PARENT);
            indentView.setLayoutParams(params);
        }
        //Set color
        if(categoryColorDrawable!=null){
            categoryColorDrawable.setColor(data.get(position).getColor());
        }
        //Set Name
        if(categoryName!=null){
            categoryName.setText(data.get(position).getName());
        }

        return row;
    }



    public void swapData(List<Category> objects){
        super.clear();
        super.addAll(objects);
        this.data = objects;
        notifyDataSetChanged();
    }
}
