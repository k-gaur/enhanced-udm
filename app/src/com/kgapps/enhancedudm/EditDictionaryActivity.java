package com.kgapps.enhancedudm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary.Words;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class EditDictionaryActivity extends Activity
{
	private Cursor getWordsCursor()
	{
		// A "projection" defines the columns that will be returned for each row
		String[] mProjection =
		{
		Words.WORD, // Contract class constant for the word column name
		Words._ID
		};
		
		// Does a query against the table and returns a Cursor object
		Cursor mCursor = getContentResolver().query(
		Words.CONTENT_URI, 						// The content URI of the words table
		mProjection,							 // The columns to return for each row
		null, 									// Either null, or the word the user entered
		null, 									// Either empty, or the string the user entered
		Words.WORD + " ASC"); 					// The sort order for the returned rows
		
		return mCursor;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_dictionary);
		
		String[] columns = new String[] { Words.WORD};
		int[] placeTo = new int[] { R.id.list_entry};
		
		ListView listView = (ListView) findViewById(R.id.listview);

		Cursor mCursor = getWordsCursor();
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.list_entry, mCursor, columns, placeTo);
		listView.setAdapter(mAdapter);
		listView.setFastScrollEnabled(true);
		registerForContextMenu(listView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_edit_dictionary, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle item selection
		switch (item.getItemId()) 
		{
			case R.id.menu_clear_user_dict:AlertDialog.Builder deleteTextDialogue = new AlertDialog.Builder(this);
												deleteTextDialogue.setTitle("Clear user dictionary");
												deleteTextDialogue.setCancelable(false);
												deleteTextDialogue.setMessage("This will delete all the words in the user dictionary. Are you sure to continue?");
												deleteTextDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
												{	
													@Override
													public void onClick(DialogInterface arg0, int arg1)
													{
														// TODO Auto-generated method stub
											
														int totalRowsDeleted = getContentResolver().delete(
														Words.CONTENT_URI, // the user dictionary content URI
														null, // the column to select on
														null // the value to compare to
														);
														Toast.makeText(getApplicationContext(),"User dictionary has been cleared successfully: " + String.valueOf(totalRowsDeleted) + " words have been deleted", Toast.LENGTH_LONG).show();
														finish();	
													}
												});
												deleteTextDialogue.setNegativeButton("No", new DialogInterface.OnClickListener()
												{	
													@Override
													public void onClick(DialogInterface arg0, int arg1)
													{
														// TODO Auto-generated method stub
													}
												});
												deleteTextDialogue.show(); 
												break;
			
			case R.id.menu_de_duplicate: final Activity currentActivity = this;
										 AlertDialog.Builder confirmTextDialogue = new AlertDialog.Builder(this);
										 confirmTextDialogue.setTitle("De-duplicate dictionary");
										 confirmTextDialogue.setCancelable(false);
										 confirmTextDialogue.setMessage("This will de-duplicate the user dictionary. It might take a long time to complete.\nContinue?");
										 confirmTextDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
										 {	
										 	 @Override
											 public void onClick(DialogInterface arg0, int arg1)
											 {
												 // TODO Auto-generated method stub
												 AsyncDeDuplicateDict executer = new AsyncDeDuplicateDict(currentActivity, getApplicationContext(), getWordsCursor());
												 executer.execute();
											 }
										 });
										 confirmTextDialogue.setNegativeButton("No", new DialogInterface.OnClickListener()
										 {	
										 	 @Override
											 public void onClick(DialogInterface arg0, int arg1)
											 {
											 	// TODO Auto-generated method stub
											 }
										 });
										 confirmTextDialogue.show();
										 break;
			case R.id.menu_multi_delete:startActivity(new Intent(getApplicationContext(), DeleteMultipleWordsActivity.class)); 
										break;	
			default:
					return super.onContextItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_long_press_dict_item, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) 
		{
			case R.id.long_press_edit: AlertDialog.Builder editTextDialogue = new AlertDialog.Builder(this);
										editTextDialogue.setTitle("Edit word");
										editTextDialogue.setCancelable(false);
										editTextDialogue.setMessage("Enter the new word in the textbox");
										final EditText input = new EditText(this);
										input.setHint("Enter the new word");
										editTextDialogue.setView(input);
										editTextDialogue.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
										{	
											@Override
											public void onClick(DialogInterface arg0, int arg1)
											{
												// TODO Auto-generated method stub
												
												ContentValues mUpdateValues = new ContentValues();
												
												// Defines selection criteria for the rows you want to update
												String mSelectionClause = Words._ID + " = ?";
												String[] mSelectionArgs = {((Long)info.id).toString()};
											
												/*
												* Sets the updated value and updates the selected words.
												*/
												mUpdateValues.put(Words.WORD,input.getText().toString());
												mUpdateValues.put(Words.FREQUENCY, "128");
												
												getContentResolver().update(
												Words.CONTENT_URI, // the user dictionary content URI
												mUpdateValues, // the columns to update
												mSelectionClause, // the column to select on
												mSelectionArgs // the value to compare to
												);
												Toast.makeText(getApplicationContext(),"Word has been updated successfully", Toast.LENGTH_LONG).show();
											}
										});
										editTextDialogue.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
										{	
											@Override
											public void onClick(DialogInterface arg0, int arg1)
											{
												// TODO Auto-generated method stub
											}
										});
										editTextDialogue.show();
									    break;
			
			case R.id.long_press_delete:AlertDialog.Builder deleteTextDialogue = new AlertDialog.Builder(this);
										deleteTextDialogue.setTitle("Delete word");
										deleteTextDialogue.setCancelable(false);
										deleteTextDialogue.setMessage("Are you sure to delete this word?");
										deleteTextDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
										{	
											@Override
											public void onClick(DialogInterface arg0, int arg1)
											{
												// TODO Auto-generated method stub
											
												// Defines selection criteria for the rows you want to delete
												String mSelectionClause = Words._ID + " = ?";
												String[] mSelectionArgs = {((Long)info.id).toString()};
												
												getContentResolver().delete(
												Words.CONTENT_URI, // the user dictionary content URI
												mSelectionClause, // the column to select on
												mSelectionArgs // the value to compare to
												);
												Toast.makeText(getApplicationContext(),"Word has been deleted successfully", Toast.LENGTH_LONG).show();
											}
										});
										deleteTextDialogue.setNegativeButton("No", new DialogInterface.OnClickListener()
										{	
											@Override
											public void onClick(DialogInterface arg0, int arg1)
											{
												// TODO Auto-generated method stub
											}
										});
										deleteTextDialogue.show();
										break;
			default:
					return super.onContextItemSelected(item);
		}
		return false;
	}
}
