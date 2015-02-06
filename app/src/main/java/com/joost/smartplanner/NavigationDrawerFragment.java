package com.joost.smartplanner;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.joost.navigationdrawer.DrawerItem;
import com.joost.navigationdrawer.DrawerItemAdapter;
import com.joost.utilities.App;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Navigation drawer fragment as used in the SlideNerd youtube tutorials
 * TODO: Let fragments not overlap: remove previous fragment if a new one needs to be drawn
 * DONE: on click of divider views not in count (make it better)
 * TODO: image as view in recycler view
 */
public class NavigationDrawerFragment extends Fragment {

    public static final String PREF_FILE_NAME = "SmartPlannerPref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private DrawerItemAdapter adapter;

    private RecyclerView mRecyclerView;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;

    final String CALENDAR_FRAGMENT_TAG = "calendar_fragment";
    final String CREATE_EVENT_FRAGMENT_TAG = "create_event_fragment";
    final String CATEGORY_FRAGMENT_TAG = "category_fragment";

    //Menu Items
    //DONE: put Strings in string file and not hardcoded (created App(aplication) to access resurces in static method (needed because MenuItems are requested through static method in order to us without this fragment being created))
    static final MenuItem[] MENUITEMS = {   new MenuItem(R.drawable.ic_menu_black_24dp, App.getContext().getResources().getString(R.string.menu_calendar), 0, 1),   //item
            new MenuItem(R.drawable.ic_menu_black_24dp,App.getContext().getResources().getString(R.string.menu_create_event), 0, 2),                                //item
            new MenuItem(R.drawable.ic_menu_black_24dp,App.getContext().getResources().getString(R.string.menu_categories), 0, 3),                                  //item
            new MenuItem(R.drawable.ic_menu_black_24dp,App.getContext().getResources().getString(R.string.menu_upcoming_tasks), 0, 4),                              //item
            new MenuItem(R.drawable.ic_menu_black_24dp,App.getContext().getResources().getString(R.string.menu_divider), 1, 0),                                     //divider
            new MenuItem(R.drawable.ic_menu_black_24dp,App.getContext().getResources().getString(R.string.menu_day_view), 0, 5),                                    //item
            new MenuItem(R.drawable.ic_menu_black_24dp,App.getContext().getResources().getString(R.string.menu_week_view), 0, 6),                                   //item
            new MenuItem(R.drawable.ic_menu_black_24dp,App.getContext().getResources().getString(R.string.menu_month_view), 0, 7),                                  //item
            new MenuItem(R.drawable.ic_menu_black_24dp,App.getContext().getResources().getString(R.string.menu_divider), 1, 0),                                     //divider
            new MenuItem(R.drawable.ic_settings_black_24dp,App.getContext().getResources().getString(R.string.menu_settings), 0, 98),                               //item
            new MenuItem(R.drawable.ic_help_black_24dp,App.getContext().getResources().getString(R.string.menu_help), 0, 99)                                        //item
    };



    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));

        if(savedInstanceState != null){
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter = new DrawerItemAdapter(getActivity(), getData());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            //What to do with clicks on items go here
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getActivity(), "onClick " + MENUITEMS[position].getClickAction(), Toast.LENGTH_SHORT).show();
                switch(MENUITEMS[position].getClickAction()){
                    case 0: //Divider is clickedIk
                        break;
                    case 1:
//                        getFragmentManager().beginTransaction()
//                                .setCustomAnimations(R.animator.slide_left,
//                                        R.animator.slide_right,
//                                        R.animator.slide_left,
//                                        R.animator.slide_right)
//                                .add(R.id.mainFragmentContainer, new CalendarFragment(),
//                                        CALENDAR_FRAGMENT_TAG
//                                ).addToBackStack(null).commit();
                        mDrawerLayout.closeDrawers();
                        getFragmentManager().beginTransaction().add(R.id.mainFragmentContainer, new CalendarFragment(), CALENDAR_FRAGMENT_TAG).addToBackStack(null).commit();
                        break;
                    case 2:
//                        getFragmentManager().beginTransaction()
//                                .setCustomAnimations(R.animator.slide_left,
//                                        R.animator.slide_right,
//                                        R.animator.slide_left,
//                                        R.animator.slide_right)
//                                .add(R.id.mainFragmentContainer, new CreateEventFragment(),
//                                        CREATE_EVENT_FRAGMENT_TAG
//                                ).addToBackStack(null).commit();
                        mDrawerLayout.closeDrawers();
                        //getFragmentManager().beginTransaction().add(R.id.mainFragmentContainer, new CreateEventFragment(), CREATE_EVENT_FRAGMENT_TAG).addToBackStack(null).commit();
                        break;
                    case 3:
                        mDrawerLayout.closeDrawers();
                        getFragmentManager().beginTransaction().add(R.id.mainFragmentContainer, new CategoryFragment(), CATEGORY_FRAGMENT_TAG).addToBackStack(null).commit();
                        break;
                    default:

                        break;
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getActivity(), "onLongClick "+ MENUITEMS[position].getClickAction(), Toast.LENGTH_SHORT).show();
            }
        }));
        return layout;
    }

    public static List<DrawerItem> getData(){
        List<DrawerItem> data = new ArrayList<>();
        for(int i=0; i<MENUITEMS.length; i++){
            DrawerItem current = new DrawerItem();
            current.setIconId(MENUITEMS[i].getIcon());
            current.setTitle(MENUITEMS[i].getTitle());
            current.setViewType(MENUITEMS[i].getViewType());
            data.add(current);
        }
        return data;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer){
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, Boolean.toString(mUserLearnedDrawer));
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView );
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                toolbar.setAlpha(1-(slideOffset*0.6f));
            }
        };

        if(!mUserLearnedDrawer && !mFromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        //GestureDetector to get wright gesture (short, long etc)
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
            Log.d("DrawerListener", "Constructor invoked");
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    //Log.d("DrawerListener", "onLongPress "+e);
                    View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(childView!=null && clickListener!=null){
                        clickListener.onLongClick(childView, recyclerView.getChildPosition(childView));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if(childView!=null && clickListener!=null && gestureDetector.onTouchEvent(e)){
                clickListener.onClick(childView, rv.getChildPosition(childView));
            }

            //gestureDetector.onTouchEvent(e);
            return false;
        }


        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {


        }
    }

    public static interface ClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    static class MenuItem{
        int icon;
        String title;
        int viewType;
        int clickAction;

        /**
         * MenuItem
         * @param icon
         * @param title
         * @param viewType
         * @param clickAction
         */
        MenuItem(int icon, String title, int viewType, int clickAction) {
            this.icon = icon;
            this.title = title;
            this.viewType = viewType;
            this.clickAction = clickAction;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getViewType() {
            return viewType;
        }

        public void setViewType(int viewType) {
            this.viewType = viewType;
        }

        public int getClickAction() {
            return clickAction;
        }

        public void setClickAction(int clickAction) {
            this.clickAction = clickAction;
        }
    }
}
