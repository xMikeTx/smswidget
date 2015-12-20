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

import dev.mike.R;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;

/**
 * The configuration screen for the ExampleAppWidgetProvider widget sample.
 */
public class ExampleAppWidgetConfigure extends Activity {
	static final String TAG = "ExampleAppWidgetConfigure";

	// Storage params
	private static final String PREFS_NAME = "dev.mike.smswidget.ExampleAppWidgetProvider";
	private static final String PREF_PREFIX_CONTACT_NAME = "prefix_contactName_";
	private static final String PREF_PREFIX_BACKGROUND = "prefix_background_";
	private static final String PREF_PREFIX_CONTACT_NUMBER = "prefix_contactNumber_";
	private static final String PREF_PREFIX_KEY = "prefix_";
	private static final String PREF_PREFIX_STARTSMS = "prefix_startSMSAPP";

	private static final String DEBUG_TAG = null;

	// on activity result cases
	private static final int CONTACT_PICKER_RESULT = 0;
	private static final int NUMBER_SELECTOR_RESULT = 1;

	// identifier
	protected static final String PUBLIC_STATIC_STRING_IDENTIFIER_NAME = "name";
	protected static final String PUBLIC_STATIC_STRING_IDENTIFIER_NUMBER = "number";

	protected static final int SMSTEXT_LENGTH = 145;

	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	// members for the ui elements
	EditText mAppWidgetPrefix;
	EditText mWidgetHeader;
	EditText mColorInput;
	Button mBtnColorPicker;
	TextView m_contactTv;
	CheckBox m_startSMSEditor;
	// Button m_selectContact;

	// members
	String m_message = null;
	String m_contactName = null;
	String m_contactNumber = null;

	String pubID = "a14da86309190e7";

	public ExampleAppWidgetConfigure() {
		super();
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// Set the result to CANCELED. This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);

		// Set the view layout resource to use.
		setContentView(R.layout.appwidget_configure);

		// Find the EditText
		mAppWidgetPrefix = (EditText) findViewById(R.id.appwidget_prefix);

		mWidgetHeader = (EditText) findViewById(R.id.heading);
		m_contactName = mWidgetHeader.getText().toString();
		mWidgetHeader.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {
		        	m_contactName = mWidgetHeader.getText().toString();  
		        	String contact = m_contactName + " <" + m_contactNumber + ">";
		    		m_contactTv.setText(contact);
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    }); 
		// find the contact textview
		m_contactTv = (TextView) findViewById(R.id.contact);

		// Find checkbox
		m_startSMSEditor = (CheckBox) findViewById(R.id.sendByOneClick);
		// Bind the action for the save button.
		findViewById(R.id.save_button).setOnClickListener(mOnClickListener);

		// Find the widget id from the intent.
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

		mBtnColorPicker = (Button)findViewById(R.id.btn_color);
		mBtnColorPicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		mColorInput = (EditText)findViewById(R.id.et_setcolor);

		// set name
		//m_contactName = mWidgetHeader.getText().toString();
		/*m_contactName = loadContactNamePref(ExampleAppWidgetConfigure.this,
				mAppWidgetId);*/

		// set number
		m_contactNumber = loadContactNumberPref(ExampleAppWidgetConfigure.this,
				mAppWidgetId);

		// combine name and number
		String contact = m_contactName + " <" + m_contactNumber + ">";
		m_contactTv.setText(contact);

		// set message
		mAppWidgetPrefix.setText(loadTitlePref(ExampleAppWidgetConfigure.this,
				mAppWidgetId));

		// Ad things
		// Create the adView
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

	}

	public void onClickSearch(View _view) {
		/*Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);*/
		  Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
          intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
          startActivityForResult(intent, 0);    
		
	}

	public static Bitmap loadContactPhoto(ContentResolver cr, long id) {
		Uri uri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, id);
		InputStream input = ContactsContract.Contacts
				.openContactPhotoInputStream(cr, uri);
		if (input == null) {
			return null;
		}
		return BitmapFactory.decodeStream(input);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT:
			/*	Uri result = data.getData();
				Intent i = new Intent(this, SelectNumber.class);
				i.setData(result);
				startActivityForResult(i, NUMBER_SELECTOR_RESULT);*/
				 if (data != null) {
				        Uri uri = data.getData();

				        if (uri != null) {
				            Cursor c = null;
				            try {
				                c = getContentResolver().query(uri, new String[]{ 
				                            ContactsContract.CommonDataKinds.Phone.NUMBER, 
				                            ContactsContract.CommonDataKinds.Phone.TYPE },
				                        null, null, null);

				                if (c != null && c.moveToFirst()) {
				                    String number = c.getString(0);
				                    int type = c.getInt(1);
				                    showSelectedNumber(type, number);
				                    m_contactNumber = number;
				                    String contact = m_contactName + " <" + m_contactNumber + ">";
				                    m_contactTv.setText(contact);
				                }
				            } finally {
				                if (c != null) {
				                    c.close();
				                }
				            }
				        }
				    }

				break;

			case NUMBER_SELECTOR_RESULT:
				m_contactNumber = data.getStringExtra(PUBLIC_STATIC_STRING_IDENTIFIER_NUMBER);
				m_contactName = data.getStringExtra(PUBLIC_STATIC_STRING_IDENTIFIER_NAME);
				saveContactNumberPref(this, mAppWidgetId, m_contactNumber);
				saveContactNamePref(this, mAppWidgetId, m_contactName);
				String contact = m_contactName + " <" + m_contactNumber + ">";

				m_contactTv.setText(contact);
				((Button) findViewById(R.id.btn_searchContact)).setText("Change receiver");
				Log.w(DEBUG_TAG, "Result : " + m_contactNumber + m_contactName);
				break;
			}
		} else {
			switch (requestCode) {
			case NUMBER_SELECTOR_RESULT:
				Toast.makeText(
						getApplicationContext(),
						"No number available for this contact -  choose another one!",
						Toast.LENGTH_SHORT);
				Log.w(DEBUG_TAG,
						"No number available for this contact -  choose another one!");
				break;
			}
			Log.w(DEBUG_TAG, "Warning: activity result not ok");
		}
	}

	public void showSelectedNumber(int type, String number) {
	    Toast.makeText(this, type + ": " + number, Toast.LENGTH_LONG).show();      
	}

	View.OnClickListener mOnClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			final Context context = ExampleAppWidgetConfigure.this;

			if(mColorInput.getText()!=null ) {
				String color =  mColorInput.getText().toString();
				if(!color.contains("#"))
					color = "#" + color;
				try {
					Color.parseColor(color);
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(ExampleAppWidgetConfigure.this,"Invalid color",Toast.LENGTH_LONG).show();
					return;
				}
				saveWidgetBackground(context, mAppWidgetId, color);
			}

			String number = m_contactNumber;// m_receiverEditBox.getText().toString();
			if (number != null) {
				if (!number.equals("No Number")|| !m_startSMSEditor.isChecked()) {
					Log.w(DEBUG_TAG, "number: " + number);
					if (number.length() != 0 ||!m_startSMSEditor.isChecked())
					// Number is not yet set from the contacts
					{
						Log.w(DEBUG_TAG, "number: " + number);

						String titlePrefix = mAppWidgetPrefix.getText()
								.toString();
						// String number =
						// m_receiverEditBox.getText().toString();
						String name = mWidgetHeader.getText().toString();// m_receiverNameEditBox.getText().toString();
						// if (number != number) {
						// if (titlePrefix != null) {
						// When the button is clicked, save the string in our
						// prefs and
						// return that they
						// clicked OK.
						saveTitlePref(context, mAppWidgetId, titlePrefix);
						saveContactNumberPref(context, mAppWidgetId, number);
						saveContactNamePref(context, mAppWidgetId, name);


						// store checkboxValue
						if (m_startSMSEditor.isChecked())
							saveStartSMSAPPPref(context, mAppWidgetId, "1");
						else
							saveStartSMSAPPPref(context, mAppWidgetId, "0");
						// Push widget update to surface with newly set prefix
						AppWidgetManager appWidgetManager = AppWidgetManager
								.getInstance(context);
						ExampleAppWidgetProvider.updateAppWidget(context,
								appWidgetManager, mAppWidgetId);

						// Make sure we pass back the original appWidgetId
						Intent resultValue = new Intent();
						resultValue.putExtra(
								AppWidgetManager.EXTRA_APPWIDGET_ID,
								mAppWidgetId);

						setResult(RESULT_OK, resultValue);
						finish();
					}
				}
				Toast.makeText(context, "Select a number", Toast.LENGTH_SHORT);
			}
			// }
			// }
			// Toast.makeText(getApplicationContext(),
			// "Number or Text is invallid", Toast.LENGTH_SHORT);
		}

	};

	// Write the prefix to the SharedPreferences object for this widget
	static void saveTitlePref(Context context, int appWidgetId, String text) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
		prefs.commit();
	}

	// Read the prefix from the SharedPreferences object for this widget.
	// If there is no preference saved, get the default from a resource
	static String loadTitlePref(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String prefix = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
		if (prefix != null) {
			return prefix;
		} else {
			return context.getString(R.string.appwidget_prefix_default);
		}
	}

	// Write the prefix to the SharedPreferences object for this widget
	static void saveContactNamePref(Context context, int appWidgetId,
			String text) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(PREF_PREFIX_CONTACT_NAME + appWidgetId, text);
		prefs.commit();
	}

	// Read the prefix from the SharedPreferences object for this widget.
	// If there is no preference saved, get the default from a resource
	static String loadContactNamePref(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String prefix = prefs.getString(PREF_PREFIX_CONTACT_NAME + appWidgetId,
				null);
		if (prefix != null) {
			return prefix;
		} else {
			return context.getString(R.string.no_name);
		}
	}

	static String loadWidgetBackground(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String prefix = prefs.getString(PREF_PREFIX_BACKGROUND + appWidgetId,	"");
		return prefix;
	}

	// Write the prefix to the SharedPreferences object for this widget
	static void saveWidgetBackground(Context context, int appWidgetId,
									String _color) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(PREF_PREFIX_BACKGROUND + appWidgetId, _color);
		prefs.commit();
	}



	static String loadStartSMSAppPref(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String prefix = prefs.getString(PREF_PREFIX_STARTSMS + appWidgetId,
				null);
		if (prefix != null) {
			return prefix;
		} else {
			return "1";
		}
	}

	// Write the prefix to the SharedPreferences object for this widget
	static void saveContactNumberPref(Context context, int appWidgetId,
			String text) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(PREF_PREFIX_CONTACT_NUMBER + appWidgetId, text);
		prefs.commit();
	}

	// Write the prefix to the SharedPreferences object for this widget
	static void saveStartSMSAPPPref(Context context, int appWidgetId,
			String text) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(PREF_PREFIX_STARTSMS + appWidgetId, text);
		prefs.commit();
	}

	// Read the prefix from the SharedPreferences object for this widget.
	// If there is no preference saved, get the default from a resource
	static String loadContactNumberPref(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String prefix = prefs.getString(PREF_PREFIX_CONTACT_NUMBER
				+ appWidgetId, null);
		if (prefix != null) {
			return prefix;
		} else {
			return context.getString(R.string.no_number);
		}
	}

	static void deleteTitlePref(Context context, int appWidgetId) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.remove(PREF_PREFIX_KEY + appWidgetId);
		prefs.commit();
	}

	static void deleteContactNumberPref(Context context, int appWidgetId) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.remove(PREF_PREFIX_CONTACT_NUMBER + appWidgetId);
		prefs.commit();
	}

	static void deleteContactNamePref(Context context, int appWidgetId) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.remove(PREF_PREFIX_CONTACT_NAME + appWidgetId);
		prefs.commit();
	}

	static void loadAllTitlePrefs(Context context,
			ArrayList<Integer> appWidgetIds, ArrayList<String> texts) {
	}
}
