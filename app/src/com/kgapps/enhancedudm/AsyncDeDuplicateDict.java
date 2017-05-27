package com.kgapps.enhancedudm;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.UserDictionary.Words;
import android.widget.Toast;

public class AsyncDeDuplicateDict extends AsyncTask<Object,Object,String>
{
	Cursor mCursor;
	Context currentContext;
	Activity currentActivity;
	ProgressDialog progressDialogue;
	
	AsyncDeDuplicateDict(Activity currentActivity,Context currentContext,Cursor mCursor)
	{
		this.mCursor=mCursor;
		this.currentContext=currentContext;
		this.currentActivity=currentActivity;
		progressDialogue = new ProgressDialog(currentActivity);
	}
	
	@Override
	protected String doInBackground(Object... arg0) 
	{
		// TODO Auto-generated method stub
		 List<String> words = new ArrayList<String>();
		 String newWord;
		 int counter = 0,totalDuplicateWords=0;
		 String mSelectionClause = Words._ID + " = ?";
		 int wordIndex = mCursor.getColumnIndex(Words.WORD);
		 int idIndex = mCursor.getColumnIndex(Words._ID);
		 while(mCursor.moveToNext())
		 {
			 if(words.contains(newWord=mCursor.getString(wordIndex)))
				 totalDuplicateWords++;
			 else
				 words.add(newWord);
		 }
		 progressDialogue.setMax(totalDuplicateWords);
		 mCursor.moveToFirst();
		 words.clear();
		 ContextWrapper obj = new ContextWrapper(currentContext);
		 do
		 {
			 if(words.contains(newWord=mCursor.getString(wordIndex)))
			 {
				 String[] mSelectionArgs = {String.valueOf(mCursor.getLong(idIndex))};
				 obj.getContentResolver().delete(
				 Words.CONTENT_URI, // the user dictionary content URI
				 mSelectionClause, // the column to select on
				 mSelectionArgs // the value to compare to
				 );
				 publishProgress(counter++);
			 }
			 else
				 words.add(newWord);
			 if(isCancelled()==true)
			 {
			 	 break;
			 }
		 }while(mCursor.moveToNext());
		 
		 return String.valueOf(counter);
	}
	
	@SuppressWarnings({ "static-access", "deprecation" })
	@Override
	protected void onPreExecute()
	{
		progressDialogue.setMessage("Please wait, the process can take some time.");
		progressDialogue.setTitle("De-duplicating dictionary");
		progressDialogue.setProgressStyle(progressDialogue.STYLE_HORIZONTAL);
		progressDialogue.setProgress(0);
		progressDialogue.setCancelable(false);
		progressDialogue.setButton("Stop", new DialogInterface.OnClickListener() 
		{
		    @Override
		    public void onClick(DialogInterface dialog, int which) 
		    {
		    	cancel(true);
		    }
		});		
		progressDialogue.show();
	}
	
	@Override
	protected void onCancelled(String result)
	{
		Toast.makeText(currentContext,"User dictionary has been partially de-duplicated: " + result + " word(s) have been deleted",Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onProgressUpdate(Object... values)
	{
		progressDialogue.setProgress(((Integer)values[0]));
	}
	
	@Override
	protected void onPostExecute(String result)
	{
		progressDialogue.dismiss();
		if (Integer.parseInt(result)==0)
			Toast.makeText(currentContext,"User dictionary contains no duplicate words", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(currentContext,"User dictionary has been de-duplicated successfully: " + result + " word(s) have been deleted", Toast.LENGTH_LONG).show();
	}

}
