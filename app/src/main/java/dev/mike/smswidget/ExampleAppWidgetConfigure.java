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
import dev.mike.smswidget.util.SharedPrefHelper;

import android.app.ActionBar;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	private LinearLayout mLlColors;

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
		mLlColors = (LinearLayout)findViewById(R.id.ll_colors);
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
		mColorInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int color = 0;
				try {
					color = Color.parseColor(s.toString());
				}catch (Exception e){

				}
				if(color!=0)
				mColorInput.setBackgroundColor(color);

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		// set name
		//m_contactName = mWidgetHeader.getText().toString();
		/*m_contactName = loadContactNamePref(ExampleAppWidgetConfigure.this,
				mAppWidgetId);*/

		// set number
		m_contactNumber = SharedPrefHelper.getInstance().loadContactNumberPref(ExampleAppWidgetConfigure.this,
				mAppWidgetId);

		// combine name and number
		String contact = m_contactName + " <" + m_contactNumber + ">";
		m_contactTv.setText(contact);

		// set message
		mAppWidgetPrefix.setText( SharedPrefHelper.getInstance().loadTitlePref(ExampleAppWidgetConfigure.this,
				mAppWidgetId));

		// create color list
		Resources res = getResources();
		int width = (int)res.getDimension(R.dimen.image_width);
		RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(width, width);
		int margin = 15;
		params.setMargins(margin,margin,margin,margin);
		addColorView(params, Color.parseColor("#F44336"));
		addColorView(params, Color.parseColor("#E91E63"));
		addColorView(params, Color.parseColor("#9C27B0"));
		addColorView(params, Color.parseColor("#673AB7"));
		addColorView(params, Color.parseColor("#3F51B5"));
		addColorView(params, Color.parseColor("#2196F3"));
		addColorView(params, Color.parseColor("#03A9F4"));
		addColorView(params, Color.parseColor("#00BCD4"));
		addColorView(params, Color.parseColor("#009688"));
		addColorView(params, Color.parseColor("#4CAF50"));
		addColorView(params, Color.parseColor("#8BC34A"));
		addColorView(params, Color.parseColor("#CDDC39"));
		addColorView(params, Color.parseColor("#FFEB3B"));
		addColorView(params, Color.parseColor("#FFC107"));
		addColorView(params, Color.parseColor("#FF9800"));
		addColorView(params, Color.parseColor("#795548"));
		addColorView(params, Color.parseColor("#9E9E9E"));
		addColorView(params, Color.parseColor("#607D8B"));
		// Ad things
		// Create the adView
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

	}

	private void addColorView(RelativeLayout.LayoutParams params, final int c) {
		Button colorView1 = new Button(this);
		colorView1.setLayoutParams(params);
		colorView1.setBackgroundColor(c);
		colorView1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mColorInput!=null){
					mColorInput.setText(String.format("#%06X", 0xFFFFFF & c));
				}
			}
		});
		mLlColors.addView(colorView1);
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
				SharedPrefHelper.getInstance().saveContactNumberPref(this, mAppWidgetId, m_contactNumber);
				SharedPrefHelper.getInstance().saveContactNamePref(this, mAppWidgetId, m_contactName);
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
				SharedPrefHelper.getInstance().saveWidgetBackground(context, mAppWidgetId, color);
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
						SharedPrefHelper.getInstance().saveTitlePref(context, mAppWidgetId, titlePrefix);
						SharedPrefHelper.getInstance().saveContactNumberPref(context, mAppWidgetId, number);
						SharedPrefHelper.getInstance().saveContactNamePref(context, mAppWidgetId, name);


						// store checkboxValue
						if (m_startSMSEditor.isChecked())
							SharedPrefHelper.getInstance().saveStartSMSAPPPref(context, mAppWidgetId, "1");
						else
							SharedPrefHelper.getInstance().saveStartSMSAPPPref(context, mAppWidgetId, "0");
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
}
