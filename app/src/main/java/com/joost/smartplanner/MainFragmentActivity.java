package com.joost.smartplanner;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.database.DatabaseHelper;

/**
 * Created by: Joost 2014-12-28
 * DONE: Create toolbar fragment (toolbar from compat 21+) (20150101)
 * TODO: Add "add event page" with animations (as in Calendar from google)
 * TODO: Add navigation drawer
 * DONE: fix database problem (20150102)
 */
public class MainFragmentActivity extends ActionBarActivity {

    FragmentManager fragmentManager;
    private Toolbar toolbar;
    private DatabaseHelper databaseHelper;
    final String CALENDAR_FRAGMENT_TAG = "calendar_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment_appbar);

        //DatabaseHelper init
        databaseHelper = new DatabaseHelper(this);

        fragmentManager = getFragmentManager();

        //Toolbar initiation
        toolbar  = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
        toolbar.inflateMenu(R.menu.menu_main);
        //debug
        toolbarHeight();

        //Add calendar fragment at start of activity
        //getFragmentManager().beginTransaction().add(R.id.mainFragmentContainer, new CalendarFragment(),CALENDAR_FRAGMENT_TAG).addToBackStack(null).commit();

        //Navigation fragment
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout), toolbar);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_calendar:
                toolbarHeight();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void slideCalendar(){
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

    public void toolbarHeight(){
        if(toolbar != null){
            Log.d("Toolbar","Toolbar Height: "+toolbar.getHeight());
        }else{
            Log.d("Toolbar","Toolbar is null");
        }

    }

    public Toolbar getToolbar(){
        return toolbar;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

}
