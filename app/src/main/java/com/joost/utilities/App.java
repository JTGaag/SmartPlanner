package com.joost.utilities;

import android.app.Application;
import android.content.Context;

/**
 * Created by Joost on 22/01/2015.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
