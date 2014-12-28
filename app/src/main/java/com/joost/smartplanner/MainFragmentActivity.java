package com.joost.smartplanner;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by: Joost 2014-12-28
 * TODO: Create toolbar fragment
 * TODO: Add "add event page" with animations (as in Calendar from google)
 */
public class MainFragmentActivity extends Activity {

    FragmentManager fragmentManager;
    Button button;
    final String CALENDAR_FRAGMENT_TAG = "calendar_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);

        fragmentManager = getFragmentManager();

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = getFragmentManager().findFragmentByTag(CALENDAR_FRAGMENT_TAG);
                if (f != null) {
                    getFragmentManager().popBackStack();
                } else {
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.animator.slide_left,
                                    R.animator.slide_right,
                                    R.animator.slide_left,
                                    R.animator.slide_right)
                            .add(R.id.mainFragmentContainer, new CalendarFragment(),
                                    CALENDAR_FRAGMENT_TAG
                            ).addToBackStack(null).commit();
                }
            }
        });



    }



}
