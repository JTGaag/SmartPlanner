package com.joost.smartplanner;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Created by: Joost 2014-12-28
 * TODO: Create toolbar fragment
 * TODO: Add "add event page" with animations (as in Calendar from google)
 */
public class MainFragmentActivity extends Activity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);

        fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.mainFragmentContainer, new CalendarFragment());
        ft.commit();
    }



}
