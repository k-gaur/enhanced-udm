package com.kgapps.enhancedudm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary.Words;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DeleteMultipleWordsActivity extends Activity
{
	ListView multiListView;
	SimpleCursorAdapter mAdapter;
	List<Long> selectedItemsId;
	CheckedTextView chkBox;
	
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
		setContentView(R.layout.activity_delete_multiple_words);
		selectedItemsId = new ArrayList<Long>();
		
		String[] columns = new String[] { Words.WORD, Words._ID};
		int[] placeTo = new int[] { R.id.multi_list_entry};
		
		multiListView = (ListView) findViewById(R.id.ListView1);
		Cursor mCursor = getWordsCursor();
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.multi_list_entry, mCursor, columns, placeTo);
		multiListView.setAdapter(mAdapter=adapter);
		multiListView.setItemsCanFocus(false);
		multiListView.setFastScrollEnabled(true);
		multiListView.setOnItemClickListener(multiOnItemClickListener);
	}
	OnItemClickListener multiOnItemClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			// TODO Auto-generated method stub
			chkBox = (CheckedTextView) view.findViewById(R.id.multi_list_entry);
			if(!selectedItemsId.contains(id))
				selectedItemsId.add(id);
			else
				selectedItemsId.remove(id);
			chkBox.toggle();
		}		
	}; 
	
	public void onDeleteSelectedButtonClick(View v)
	{
		if(selectedItemsId.size()>=1)
		{
			AlertDialog.Builder deleteTextDialogue = new AlertDialog.Builder(this);
			deleteTextDialogue.setTitle("Delete multiple words");
			deleteTextDialogue.setCancelable(false);
			deleteTextDialogue.setMessage("Are you sure to delete the selected word(s)?");
			deleteTextDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
			{	
				@Override
				public void onClick(DialogInterface arg0, int arg1)
				{
					// TODO Auto-generated method stub
				
					// Defines selection criteria for the rows you want to delete
					String mSelectionClause = Words._ID + " = ?";
					int counter= 0;
	
					while(counter<selectedItemsId.size())
					{
						String[] mSelectionArgs = {selectedItemsId.get(counter++).toString()};
						getContentResolver().delete(
						Words.CONTENT_URI, // the user dictionary content URI
						mSelectionClause, // the column to select on
						mSelectionArgs
						);
					}
					Toast.makeText(getApplicationContext(),String.valueOf(counter) + (counter>1?" words have ":" word has ")  + "been deleted successfully", Toast.LENGTH_LONG).show();
					selectedItemsId.clear();
				}
			});
			deleteTextDialogue.setNegativeButton("No", new DialogInterface.OnClickListener()
			{	
				@Override
				public void onClick(DialogInterface arg0, int arg1)
				{
					// TODO Auto-generated method stub
					selectedItemsId.clear();
				}
			});
			deleteTextDialogue.show();
		}
		else
			Toast.makeText(getApplicationContext(),"No word is selected: Operation cancelled", Toast.LENGTH_LONG).show();
	}
}