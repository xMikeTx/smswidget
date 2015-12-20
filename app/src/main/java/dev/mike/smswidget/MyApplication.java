package dev.mike.smswidget;

import android.app.Application;
import android.content.Context;

/**
 * Created by mtpub_000 on 20.12.2015.
 */
public class MyApplication extends Application {

    private static Context context;

    public static Context getContext(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
