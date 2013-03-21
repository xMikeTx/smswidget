/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.mike.smswidget;

import java.security.KeyStore.LoadStoreParameter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import dev.mike.R;

/**
 * A widget provider. We have a string that we pull from a preference in order
 * to show the configuration settings and the current time when the widget was
 * updated. We also register a BroadcastReceiver for time-changed and
 * timezone-changed broadcasts, and update then too.
 * 
 * <p>
 * See also the following files:
 * <ul>
 * <li>ExampleAppWidgetConfigure.java</li>
 * <li>ExampleBroadcastReceiver.java</li>
 * <li>res/layout/appwidget_configure.xml</li>
 * <li>res/layout/appwidget_provider.xml</li>
 * <li>res/xml/appwidget_provider.xml</li>
 * </ul>
 */
public class ExampleAppWidgetProvider extends AppWidgetProvider
{
    // log tag
    private static final String TAG = "ExampleAppWidgetProvider";

    // for the button
    public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";

    //Broadcast Receiver : singleton
    private static int m_isRegistered = 0;
    
    //locking button
    private static int m_isButtonLocked = 0;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	    int[] appWidgetIds)
    {
	Log.d(TAG, "onUpdate");
	// For each widget that needs an update, get the text that we should
	// display:
	// - Create a RemoteViews object for it
	// - Set the text in the RemoteViews object
	// - Tell the AppWidgetManager to show that views object for the widget.
	final int N = appWidgetIds.length;
	for (int i = 0; i < N; i++)
	{
	   int appWidgetId = appWidgetIds[i];
	   // String titlePrefix = ExampleAppWidgetConfigure.loadTitlePref(   context, appWidgetId);
	    
	    updateAppWidget(context, appWidgetManager, appWidgetId  );
	}

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
	Log.d(TAG, "onDeleted");
	// When the user deletes the widget, delete the preference associated
	// with it.
	final int N = appWidgetIds.length;
	for (int i = 0; i < N; i++)
	{
	    ExampleAppWidgetConfigure.deleteTitlePref(context, appWidgetIds[i]);
	    ExampleAppWidgetConfigure.deleteContactNamePref(context, appWidgetIds[i]);
	    ExampleAppWidgetConfigure.deleteContactNumberPref(context, appWidgetIds[i]);
	}
    }

    @Override
    public void onEnabled(Context context)
    {
//	Log.d(TAG, "onEnabled");
//	// When the first widget is created, register for the TIMEZONE_CHANGED
//	// and TIME_CHANGED
//	// broadcasts. We don't want to be listening for these if nobody has our
//	// widget active.
//	// This setting is sticky across reboots, but that doesn't matter,
//	// because this will
//	// be called after boot if there is a widget instance for this provider.
//	PackageManager pm = context.getPackageManager();
////	pm.setComponentEnabledSetting(new ComponentName(
////		"com.example.android.apis",
////		".appwidget.ExampleBroadcastReceiver"),
////		PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
////		PackageManager.DONT_KILL_APP);
////    }
//    pm.setComponentEnabledSetting(new ComponentName(
//    		"dev.mike",
//    		".smswidget.ExampleBroadcastReceiver"),
//    		PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//    		PackageManager.DONT_KILL_APP);
        }

    @Override
    public void onDisabled(Context context)
    {
	// When the first widget is created, stop listening for the
	// TIMEZONE_CHANGED and
	// TIME_CHANGED broadcasts.
//	Log.d(TAG, "onDisabled");
//	PackageManager pm = context.getPackageManager();
//	pm.setComponentEnabledSetting(new ComponentName(
//		"com.example.android.apis",
//		".appwidget.ExampleBroadcastReceiver"),
//		PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//		PackageManager.DONT_KILL_APP);
    }


    static void updateAppWidget(Context context,
	    AppWidgetManager appWidgetManager, int appWidgetId)
	    
    {
	Log.d(TAG, "updateAppWidget appWidgetId=" + appWidgetId);
	// Getting the string this way allows the string to be localized. The
	// format
	// string is filled in using java.util.Formatter-style format strings.
	// CharSequence text = context.getString(R.string.appwidget_text_format,
	// ExampleAppWidgetConfigure.loadTitlePref(context, appWidgetId),
	// "0x" + Long.toHexString(SystemClock.elapsedRealtime()));

	//CharSequence text = titlePrefix;
	CharSequence title = ExampleAppWidgetConfigure.loadTitlePref(context, appWidgetId);
	CharSequence name = ExampleAppWidgetConfigure.loadContactNamePref(context, appWidgetId);
	CharSequence number = ExampleAppWidgetConfigure.loadContactNumberPref(context, appWidgetId);
	// Construct the RemoteViews object. It takes the package name (in our
	// case, it's our
	// package, but it needs this because on the other side it's the widget
	// host inflating
	// the layout from our package).
	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider);
	//views.setTextViewText(R.id.appwidget_text, number);
	//views.setViewVisibility(R.id.appwidget_text, View.INVISIBLE);
	
	String btnText = (String) title;//.subSequence(0, 6);
//	if(btnText.length()>6)
//	{btnText.concat("...");}
	//views.setTextViewText(R.id.button, btnText);
	views.setTextViewText(R.id.tv_contact_name, name);
	views.setTextViewText(R.id.appwidget_text, btnText);
	Intent active = new Intent(context, ExampleAppWidgetProvider.class);
	active.setAction(ACTION_WIDGET_RECEIVER);
	active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

	//active.putExtra("msg", text);
	//m_msgText = text.toString();
	//Toast.makeText(context, "updateAPP" + text, Toast.LENGTH_SHORT).show();
//	actionPendingIntentList[appWidgetId]=PendingIntent.getBroadcast(context,
//		0, active, 0);
	PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context,
		appWidgetId, active, 0);
	views.setOnClickPendingIntent(R.id.button, actionPendingIntent);
	//views.setOnClickPendingIntent(R.id.button, actionPendingIntentList[appWidgetId]);
	// Tell the widget manager

	appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    // ---sends an SMS message to another device---
    private void sendSMS(Context _context, String phoneNumber, String message)
    {
 	
    	if(m_isButtonLocked == 0)
    	{
    	m_isButtonLocked = 1;
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
 
        Intent sentIntent = new Intent(SENT);
        sentIntent.putExtra("SENT_MSG", message);
        sentIntent.putExtra("SENT_NUMBER", phoneNumber);
        
        PendingIntent sentPI = PendingIntent.getBroadcast(_context, 0,
        		sentIntent, PendingIntent.FLAG_ONE_SHOT);
        
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(_context, 0,
            new Intent(DELIVERED), PendingIntent.FLAG_ONE_SHOT);
 
        
        //only register once
        if(m_isRegistered == 0)
        {
        	m_isRegistered = 1;
        	registeringBroadcastReceiver(_context, SENT, DELIVERED);        
        }
        Log.i(TAG,"Send SMS"); // LogCat message // TAG=”TestActivity”
        SmsManager sms = SmsManager.getDefault();
        //sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);  
        sms.sendTextMessage(phoneNumber, null, message, sentPI, null);  
       	}
    	else
    	{
    		 Toast.makeText(_context, "Button Locked! Wait until SMS is sent!", 
                     Toast.LENGTH_SHORT).show();
    	}
    }
    private void addSmsToInboxHistory(Context arg0, String phoneNumber, String message) {
		final String ADDRESS = "address";
        //final String PERSON = "person";
        final String DATE = "date";
        final String READ = "read";
        final String STATUS = "status";
        final String TYPE = "type";
        final String BODY = "body";

        Calendar cal = Calendar.getInstance();
cal.getTimeInMillis();

        //int MESSAGE_TYPE_INBOX = 1;
        //int MESSAGE_TYPE_SENT = 2; http://osdir.com/ml/AndroidDevelopers/2009-03/msg02449.html

    	 ContentValues values = new ContentValues();
    	 values.put(ADDRESS, phoneNumber);
    	 values.put(DATE,  cal.getTimeInMillis());
    	 values.put(READ, 1);
    	 values.put(STATUS, -1);
    	 values.put(TYPE, 2);
    	 values.put(BODY, message);
         //ContentResolver contentResolver = getContentResolver();
         Uri inserted = arg0.getContentResolver().insert(Uri.parse("content://sms"), values);
         Log.i(TAG,"SMS added to history!");
	}
	private void registeringBroadcastReceiver(Context _context, String SENT, String DELIVERED) {
		//---when the SMS has been sent---
        _context.getApplicationContext().registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                    	//if successful sent, add sms to history
                        Bundle extras = arg1.getExtras();
                        String msg = extras.getString("SENT_MSG");
                        String number = extras.getString("SENT_NUMBER");
                        addSmsToInboxHistory(arg0,number,msg);
                        
                        Toast.makeText(arg0, "SMS sent: " + msg, 
                                Toast.LENGTH_SHORT).show(); 
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(arg0, "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(arg0, "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(arg0, "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(arg0, "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                m_isButtonLocked = 0; //enable button
            }
        }, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        _context.getApplicationContext().registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                    	//TODO: LOCK Widget
//                    	Bundle bundle = arg1.getExtras();        
//                        SmsMessage[] msgs = null;
//                        String str = ""; 
//                        if (bundle != null)
//                        {
//                            //---retrieve the SMS message received---
//                            Object[] pdus = (Object[]) bundle.get("pdus");
//                            msgs = new SmsMessage[pdus.length];            
//                            for (int i=0; i<msgs.length; i++)
//                            {
//                                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
//                                str += "SMS from " + msgs[i].getOriginatingAddress();                     
//                                str += " :";
//                                str += msgs[i].getMessageBody().toString();
//                                str += "\n";        
//                            }
//                            //---display the new SMS message---
//                            Toast.makeText(arg0, str, Toast.LENGTH_SHORT).show();
//                        }  
                        Toast.makeText(arg0, "SMS delivered", 
                               Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                    	//TODO: LOCK Widge                    
                        Toast.makeText(arg0, "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));
	}

    @Override
    public void onReceive(Context context, Intent intent)
    {// v1.5 fix that doesn't call onDelete Action
	final String action = intent.getAction();
	if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action))
	{
	    final int appWidgetId = intent.getExtras().getInt(
		    AppWidgetManager.EXTRA_APPWIDGET_ID,
		    AppWidgetManager.INVALID_APPWIDGET_ID);
	    if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
	    {
		this.onDeleted(context, new

		int[] { appWidgetId });
	    }
	} else
	{ // check, if our Action was called
	    if (intent.getAction().equals(ACTION_WIDGET_RECEIVER))
	    {

		 // Find the widget id from the intent. 
	    Bundle extras = intent.getExtras();
		int appWidgetId = extras.getInt( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
//		try
//		{
//		    msg = intent.getStringExtra("msg");
//		} catch (NullPointerException e)
//		{
//		    Log.e("Error", "msg = null");
//		}
		//Toast.makeText(context,"onReceive id: "+ appWidgetId + msg, Toast.LENGTH_SHORT).show();
		
		
		// sendSMS
		// Uri smsUri = Uri.parse("tel:066475021214");
		// Intent intentSMS = new Intent(Intent.ACTION_VIEW, smsUri);
		// intentSMS.putExtra("sms_body", msg);
		// intentSMS.setType("vnd.android-dir/mms-sms");
		// intentSMS.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(intentSMS);
		// sendSMS("066475021214",msg);
		//sendSMS(context, "066475021214", ExampleAppWidgetConfigure.loadTitlePref(
		//	    context, appWidgetId));
		
		 if(ExampleAppWidgetConfigure.loadStartSMSAppPref(context, appWidgetId ).equals("1"))
		 {
			 sendSMS(context, ExampleAppWidgetConfigure.loadContactNumberPref(context, appWidgetId), ExampleAppWidgetConfigure.loadTitlePref(
					    context, appWidgetId));
		 }
		 else //send via the sms app
		 {
			 Uri uri = Uri.parse("smsto:"+ExampleAppWidgetConfigure.loadContactNumberPref(context, appWidgetId)); 
			 Intent startSMSappIntent = new Intent(Intent.ACTION_SENDTO, uri); 
			 startSMSappIntent.putExtra("sms_body", ExampleAppWidgetConfigure.loadTitlePref(
					    context, appWidgetId));   
			 startSMSappIntent.addFlags(startSMSappIntent.FLAG_ACTIVITY_NEW_TASK);
			 context.startActivity(startSMSappIntent);
		 }
	
//		PendingIntent contentIntent = PendingIntent.getActivity(
//			context, 0, intent, 0);
//		NotificationManager notificationManager = (NotificationManager) context
//			.getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification noty = new Notification(R.drawable.app_icon,
//			"SMS sent to GACKL!", System.currentTimeMillis());
//		noty.setLatestEventInfo(context, "Notice", msg, contentIntent);
//		notificationManager.notify(1, noty);
	    }
	    super.onReceive(context, intent);
	}
    }
}
