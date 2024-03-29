package com.alamkanak.weekview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

import com.joost.smartplanner.R;

import java.util.Calendar;

/**
 * Created by Joost on 23/12/2014.
 * DONE: get all the added features in this class (20141224)
 * TODO: Override all gestures in SmartWeekView (to get clean WeekView)
 * Done: Draw view directly correct (date not half)
 * TODO: after event is created do not go to current time, but to created event start time
 * DONE: when create view do not shift out of 24h (onPredraw)
 */
public class SmartWeekView extends WeekView {

    //Variables
    private Paint mCurrentTimeLinePaint;

    //Attributes and their default values
    private int mCurrentTimeLineColor = Color.rgb(230, 0, 0);
    private int mCurrentTimeLineHeight = 2;
    private int mHourFormat = 2;

    //PreDrawLister to draw correctly
    private ViewTreeObserver.OnPreDrawListener preDrawListener = null;

    public enum HourFormat{
        TWELVE, TWENTYFOUR;
    }


    public SmartWeekView(Context context) {
        this(context, null);
    }

    public SmartWeekView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartWeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        /**
         * DONE: fix read xml attributes (Constructor error) (20141224)
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SmartWeekView, 0, 0);
        try{
            //Get specific view info from xml and assign it to their values
            mCurrentTimeLineColor = a.getColor(R.styleable.SmartWeekView_currentHourLineColor, mCurrentTimeLineColor);
            mCurrentTimeLineHeight = a.getDimensionPixelSize(R.styleable.SmartWeekView_currentHourLineHeight, mCurrentTimeLineHeight);
            mHourFormat = a.getInt(R.styleable.SmartWeekView_hourFormat, mHourFormat);
        } finally{
            a.recycle();
        }
        init();
    }

    private void init() {
        /**
         * Added by Joost
         * Prepare current time color paint
         */
        mCurrentTimeLinePaint = new Paint();
        mCurrentTimeLinePaint.setStyle(Paint.Style.STROKE);
        mCurrentTimeLinePaint.setStrokeWidth(mCurrentTimeLineHeight);
        mCurrentTimeLinePaint.setColor(mCurrentTimeLineColor);

        this.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("SmartWeekView", "OnMeasure Called");
        Log.d("SmartWeekView", "getHeight: " + getHeight());
        /**
         * Done: Get current time and verticaly scroll to the time (20141220)
         * Done: Set limit (not before 0:00 and after 24:00 [moved code to onMeasure method to gein axcess to getHeight()] (20141221)
         */
        Calendar rightNow = Calendar.getInstance();
        float currentHour = (float) rightNow.get(Calendar.HOUR_OF_DAY) + (rightNow.get(Calendar.MINUTE)/60.00f);
        getCurrentOrigin().y = -(getHourHeight())*(currentHour-1);

        //For animation to not exceed time frame of 24h
        if (preDrawListener == null) {
            preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    Log.d("onPreDraw", "getHeight: " + getHeight());
                    getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
                    if (getCurrentOrigin().y > 0){
                        getCurrentOrigin().y = 0;
                    }else if (getCurrentOrigin().y  < -(getHourHeight() * 24 + getHeaderTextHeight() + getHeaderRowPadding() * 2 - getHeight())){
                        getCurrentOrigin().y = -(getHourHeight() * 24 + getHeaderTextHeight() + getHeaderRowPadding() * 2 - getHeight());
                    }
                    return true;
                }
            };
            getViewTreeObserver().addOnPreDrawListener(preDrawListener);
        }

        //Set limit (without animations)
        if (getCurrentOrigin().y > 0){
            getCurrentOrigin().y = 0;
        }else if (getCurrentOrigin().y  < -(getHourHeight() * 24 + getHeaderTextHeight() + getHeaderRowPadding() * 2 - getHeight())){
            getCurrentOrigin().y = -(getHourHeight() * 24 + getHeaderTextHeight() + getHeaderRowPadding() * 2 - getHeight());
        }
    }


    /**
     * DONE: Redesign onDraw that layers will work (20141224)
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        // Draw the header row.
        drawHeaderRowAndEvents(canvas);

        //Draw currentTimeLine
        drawCurrentHourLine(canvas);


        //Variables needed for header and text
        int leftDaysWithGaps = (int) -(Math.ceil(getCurrentOrigin().x / (getWidthPerDay() + getColumnGap())));
        float startPixel = getCurrentOrigin().x + (getWidthPerDay() + getColumnGap()) * leftDaysWithGaps + getHeaderColumnWidth();
        //Draw header
        drawHeaderAndText(canvas, leftDaysWithGaps, startPixel);

        // Draw the time column and all the axes/separators.
        drawTimeColumnAndAxes(canvas);

        drawHideRect(canvas);

    }

    private void drawCurrentHourLine(Canvas canvas){

        Calendar day;

        int leftDaysWithGaps = (int) -(Math.ceil(getCurrentOrigin().x / (getWidthPerDay() + getColumnGap())));
        float startPixel = getCurrentOrigin().x + (getWidthPerDay() + getColumnGap()) * leftDaysWithGaps + getHeaderColumnWidth();
        // Iterate through each day.

        for (int dayNumber = leftDaysWithGaps + 1; dayNumber <= leftDaysWithGaps + getNumberOfVisibleDays() + 1; dayNumber++) {

            day = (Calendar) getToday().clone();
            day.add(Calendar.DATE, dayNumber - 1);
            boolean sameDay = isSameDay(day, getToday());


            float start =  (startPixel < getHeaderColumnWidth() ? getHeaderColumnWidth() : startPixel);

            /**
             * Edit by Joost
             * Draw hour line
             * Done: Set Line on right time (20141219)
             */
            //Log.d("SameDay","SameDay?: " + Boolean.toString(sameDay) + " dayNumber: " + Integer.toString(dayNumber));
            if (sameDay){
                //Log.d("Start", Float.toString(startPixel));
                Calendar rightNow = Calendar.getInstance();
                float currentTimeLine[] = new float[4];
                float currentHour = (float) rightNow.get(Calendar.HOUR_OF_DAY) + (rightNow.get(Calendar.MINUTE)/60.00f);//7.25f;
                float top = getHeaderTextHeight() + getHeaderRowPadding() * 2 + getCurrentOrigin().y + getHourHeight() * currentHour + getTimeTextHeight()/2 + getHeaderMarginBottom();
                currentTimeLine[0] = start;
                currentTimeLine[1] = currentTimeLine[3] = top;
                currentTimeLine[2] = startPixel + getWidthPerDay();
                canvas.drawLines(currentTimeLine, mCurrentTimeLinePaint);
            }
            startPixel += getWidthPerDay() + getColumnGap();
        }
    }

    /**
     * Returns the current time line height
     * @return The current time line height
     */
    public int getCurrentTimeLineHeight() {
        return mCurrentTimeLineHeight;
    }

    /**
     * Sets the current time line height
     * @param mCurrentTimeLineHeight The new current time line height
     */
    public void setCurrentTimeLineHeight(int mCurrentTimeLineHeight) {
        this.mCurrentTimeLineHeight = mCurrentTimeLineHeight;
    }


    @Override
    protected String getTimeString(int hour) {
        if (mHourFormat==1) {
            String amPm;
            if (hour >= 0 && hour < 12) amPm = "AM";
            else amPm = "PM";
            if (hour == 0) hour = 12;
            if (hour > 12) hour -= 12;
            return String.format("%02d %s", hour, amPm);
        }else if (mHourFormat==2){
            return String.format("%02d:00", hour);
        }else{
            return String.format("%02d:00", hour);
        }
    }
}
