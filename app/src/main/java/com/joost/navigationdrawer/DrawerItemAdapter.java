package com.joost.navigationdrawer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joost.smartplanner.R;
import com.joost.utilities.App;

import java.util.Collections;
import java.util.List;

/**
 * Created by Joost on 04/01/2015.
 * TODO: make a more elegant code to add differnt view to recycler view
 */
public class DrawerItemAdapter extends RecyclerView.Adapter<DrawerItemAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    List<DrawerItem> data = Collections.emptyList();

    public DrawerItemAdapter(Context context, List<DrawerItem> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getViewType();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = null;
        View view;
        switch(viewType){
            case 0:     //Item
                view = inflater.inflate(R.layout.custom_row, parent, false);
                holder = new MyViewHolder(view, viewType);
                break;
            case 1:     //Divider
                view = inflater.inflate(R.layout.divider, parent, false);
                holder = new MyViewHolder(view, viewType);
                break;
            default:
                break;
        }

        return holder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DrawerItem current = data.get(position);
        if(position==0 && holder.viewType == 0){
            holder.itemView.setMinimumHeight(App.getContext().getResources().getDimensionPixelSize(R.dimen.navigation_drawer_first_item_height));
            holder.itemView.setPadding( App.getContext().getResources().getDimensionPixelSize(R.dimen.zero_padding),
                    App.getContext().getResources().getDimensionPixelSize(R.dimen.navigation_drawer_top_padding),
                    App.getContext().getResources().getDimensionPixelSize(R.dimen.zero_padding),
                    App.getContext().getResources().getDimensionPixelSize(R.dimen.zero_padding)
            );
        }
        switch(holder.viewType){
            case 0:
                holder.title.setText(current.getTitle());
                holder.icon.setImageResource(current.getIconId());
                break;
            case 1:
                //Nothing for divider
                break;
            default:
                break;
        }
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        ImageView icon;
        int viewType;
        View itemView;

        public MyViewHolder(View itemView, int viewType) {
            super(itemView);
            this.itemView = itemView;
            this.viewType = viewType;
            switch(viewType) {
                case 0:
                    title = (TextView) itemView.findViewById(R.id.listText);
                    icon = (ImageView) itemView.findViewById(R.id.listIcon);
                    break;
                case 1:
                    //Nothing
                    break;
                default:
                    break;
            }

        }
    }
}
