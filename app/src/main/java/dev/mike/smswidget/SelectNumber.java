/**
 * 

package dev.mike.smswidget;

import java.util.ArrayList;
import java.util.List;

import dev.mike.R;



import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Mike
 *
 *
public class SelectNumber extends ListActivity implements OnItemClickListener{
	
	private static final String DEBUG_TAG = null;
	protected static final String PUBLIC_STATIC_STRING_IDENTIFIER_NAME = "name";
	protected static final String PUBLIC_STATIC_STRING_IDENTIFIER_NUMBER = "number";
	String m_name = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	  Uri result = getIntent().getData();
	  ArrayList<String> numbers = new ArrayList<String>();
	  numbers.clear();
	  Log.v(DEBUG_TAG, "selectNumber: "  + result.toString());  

		Cursor cursor = getContentResolver().query(	result, null, null,	null, null);
		Log.v(DEBUG_TAG, "Got a contact result: " + cursor.toString()); 
		while (cursor != null && cursor.moveToNext()&& !cursor.isClosed()) {
			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			m_name = contactName;
			
			int hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			if (hasPhone > 0) {
				Log.v(DEBUG_TAG, "Has phone : ");  
				// You know it has a number so now query it like this
				Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,	null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId, null, null);
				while (phones != null && phones.moveToNext()) 
				{
					String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					Log.v(DEBUG_TAG, "Phonenumber : " + phoneNumber); 
					
					if(numbers.contains(phoneNumber)) //only add if the number is not already in the list
					{
						//do nothing
					}
					else
						numbers.add(phoneNumber);
				}
				if(phones != null)
				phones.close();
			}
			if (cursor != null)
			cursor.close();
			  
			if(numbers.size()==0)
			{
				Toast.makeText(getBaseContext(), "No number available for this contact", Toast.LENGTH_SHORT);
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
			else if(numbers.size() == 1)//if there is only 1 number return it
			{
				Intent resultIntent = new Intent();
				resultIntent.putExtra(PUBLIC_STATIC_STRING_IDENTIFIER_NUMBER, numbers.get(0));
				resultIntent.putExtra(PUBLIC_STATIC_STRING_IDENTIFIER_NAME, m_name);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
			else
			{
				
				
			setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, numbers));
		
			  ListView lv = getListView();
			  lv.setTextFilterEnabled(true);
			  lv.setOnItemClickListener(this);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Toast.makeText(getApplicationContext(), ((TextView) arg1).getText(),
		          Toast.LENGTH_SHORT).show();
		String number = (String) arg0.getItemAtPosition(arg2);
		Intent resultIntent = new Intent();
		resultIntent.putExtra(PUBLIC_STATIC_STRING_IDENTIFIER_NUMBER, number);
		resultIntent.putExtra(PUBLIC_STATIC_STRING_IDENTIFIER_NAME, m_name);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
} */
