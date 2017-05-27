package com.kgapps.enhancedudm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.provider.UserDictionary.Words;
import android.widget.Toast;

public class AsyncDictAdder extends AsyncTask<Object,Object,String>
{
	String word;
	Context currentContext;
	Activity currentActivity;
	ProgressDialog progressDialogue;
	static int totalWords;
	final static int frequency = 255;
	
	AsyncDictAdder(Activity currentActivity,Context currentContext,String word)
	{
		this.word=word;
		this.currentContext=currentContext;
		this.currentActivity=currentActivity;
		progressDialogue = new ProgressDialog(currentActivity);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(Object... arg0) 
	{
		// TODO Auto-generated method stub
		
		int startPos=0,endPos=0,length=word.length(),counter=0;
		while(endPos<length)
		{
			if(word.charAt(endPos)==' ' || word.charAt(endPos)=='\n')
			{	
				Words.addWord(currentContext, word.substring(startPos,endPos), frequency, Words.LOCALE_TYPE_ALL);
				counter++;
				startPos=endPos+1;	
				publishProgress(counter);
			}
			else if(endPos==length-1)
			{
				Words.addWord(currentContext, word.substring(startPos,++endPos), frequency, Words.LOCALE_TYPE_ALL);
				counter++;
				publishProgress(counter);
			}	
			if(isCancelled()==true)
			{
				break;
			}
			endPos++;
		}
		totalWords=0;
		return String.valueOf(counter);
	}
	
	@SuppressWarnings({ "static-access", "deprecation" })
	@Override
	protected void onPreExecute()
	{
		progressDialogue.setMessage("Please wait, the process can take some time depending upon the number of words to be added.");
		progressDialogue.setTitle("Adding words");
		progressDialogue.setProgressStyle(progressDialogue.STYLE_HORIZONTAL);
		progressDialogue.setMax(totalWords);
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
		Toast.makeText(currentContext,result+" Word(s) have been successfully added",Toast.LENGTH_LONG).show();
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
		Toast.makeText(currentContext,result+" Word(s) have been successfully added",Toast.LENGTH_LONG).show();
	}
}
