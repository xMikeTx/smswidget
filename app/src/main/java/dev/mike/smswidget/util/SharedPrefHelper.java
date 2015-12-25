package dev.mike.smswidget.util;

import android.content.Context;
import android.content.SharedPreferences;

import dev.mike.R;
import dev.mike.smswidget.MyApplication;

/**
 * Created by mtpub_000 on 25.12.2015.
 */
public class SharedPrefHelper {

    private static final String PREFS_NAME = "dev.mike.smswidget.ExampleAppWidgetProvider";
    private static SharedPrefHelper instance;
    private final SharedPreferences prefs = MyApplication.getContext().getSharedPreferences( PREFS_NAME, 0);;

    // Storage params
    private static final String PREF_PREFIX_CONTACT_NAME = "prefix_contactName_";
    private static final String PREF_PREFIX_BACKGROUND = "prefix_background_";
    private static final String PREF_PREFIX_CONTACT_NUMBER = "prefix_contactNumber_";
    private static final String PREF_PREFIX_KEY = "prefix_";
    private static final String PREF_PREFIX_STARTSMS = "prefix_startSMSAPP";

    public static SharedPrefHelper getInstance(){
        if(instance == null){
            instance = new SharedPrefHelper();
        }
        return instance;
    }

    private SharedPrefHelper(){
    }

    // Write the prefix to the SharedPreferences object for this widget
    public void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = this.prefs.edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public String loadTitlePref(Context context, int appWidgetId) {
        String prefix = this.prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (prefix != null) {
            return prefix;
        } else {
            return context.getString(R.string.appwidget_prefix_default);
        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    public void saveContactNamePref(Context context, int appWidgetId,
                                    String text) {
        SharedPreferences.Editor prefs = this.prefs.edit();
        prefs.putString(PREF_PREFIX_CONTACT_NAME + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public String loadContactNamePref(Context context, int appWidgetId) {
        SharedPreferences prefs = this.prefs;
        String prefix = prefs.getString(PREF_PREFIX_CONTACT_NAME + appWidgetId,
                null);
        if (prefix != null) {
            return prefix;
        } else {
            return context.getString(R.string.no_name);
        }
    }

    public String loadWidgetBackground(Context context, int appWidgetId) {
        SharedPreferences prefs = this.prefs;
        String prefix = prefs.getString(PREF_PREFIX_BACKGROUND + appWidgetId,	"");
        return prefix;
    }

    // Write the prefix to the SharedPreferences object for this widget
    public void saveWidgetBackground(Context context, int appWidgetId,
                                     String _color) {
        SharedPreferences.Editor prefs = this.prefs.edit();
        prefs.putString(PREF_PREFIX_BACKGROUND + appWidgetId, _color);
        prefs.apply();
    }



    public String loadStartSMSAppPref(Context context, int appWidgetId) {
        SharedPreferences prefs = this.prefs;
        String prefix = prefs.getString(PREF_PREFIX_STARTSMS + appWidgetId,
                null);
        if (prefix != null) {
            return prefix;
        } else {
            return "1";
        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    public void saveContactNumberPref(Context context, int appWidgetId,
                                      String text) {
        SharedPreferences.Editor prefs = this.prefs.edit();
        prefs.putString(PREF_PREFIX_CONTACT_NUMBER + appWidgetId, text);
        prefs.apply();
    }

    // Write the prefix to the SharedPreferences object for this widget
    public void saveStartSMSAPPPref(Context context, int appWidgetId,
                                    String text) {
        SharedPreferences.Editor prefs = this.prefs.edit();
        prefs.putString(PREF_PREFIX_STARTSMS + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public String loadContactNumberPref(Context context, int appWidgetId) {
        SharedPreferences prefs = this.prefs;
        String prefix = prefs.getString(PREF_PREFIX_CONTACT_NUMBER
                + appWidgetId, null);
        if (prefix != null) {
            return prefix;
        } else {
            return context.getString(R.string.no_number);
        }
    }

    public void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = this.prefs.edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    public void deleteContactNumberPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = this.prefs.edit();
        prefs.remove(PREF_PREFIX_CONTACT_NUMBER + appWidgetId);
        prefs.apply();
    }

    public void deleteContactNamePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = this.prefs.edit();
        prefs.remove(PREF_PREFIX_CONTACT_NAME + appWidgetId);
        prefs.apply();
    }

    public void deleteWidgetBackground(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = this.prefs.edit();
        prefs.remove(PREF_PREFIX_BACKGROUND + appWidgetId);
        prefs.apply();
    }
}
