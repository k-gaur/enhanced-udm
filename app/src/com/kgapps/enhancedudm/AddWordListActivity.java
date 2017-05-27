package com.kgapps.enhancedudm;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.*;

public class AddWordListActivity extends Activity
{
	private static AlertDialog.Builder confirmBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_word_list);
	}
	
	//private static 
	public void showConfirmationDialogue(final InputStream file, String lang)
	{
		final Activity currentActivity = this;
		confirmBox = new AlertDialog.Builder(this);
		confirmBox.setTitle("Add Word List");
		confirmBox.setMessage("Are you sure to add " + lang + " words to the user dictionary?");
		confirmBox.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{	
			@Override
			public void onClick(DialogInterface arg0, int arg1) 
			{
				// TODO Auto-generated method stub
				try
				{
					DumpFileIntoDictionary.dumpUsingStream(currentActivity, getApplicationContext(), file);
				}
				catch(IOException e)
				{
					//nothing to do
				}
			}
		});
		confirmBox.setNegativeButton("No", new DialogInterface.OnClickListener() 
		{	
			@Override
			public void onClick(DialogInterface arg0, int arg1)
			{
				// TODO Auto-generated method stub
				//Perform no task
			}
		});
		confirmBox.show();
	}
	
	public void onAddEnglishWordsButtonClick(View view)
	{
		showConfirmationDialogue(getResources().openRawResource(R.raw.english), "English");
	}
	
	public void onAddHinglishWordsButtonClick(View view)
	{
		showConfirmationDialogue(getResources().openRawResource(R.raw.hinglish), "Hinglish");
	}
	
	public void onAddEnglishSlangWordsButtonClick(View view)
	{
		showConfirmationDialogue(getResources().openRawResource(R.raw.slang), "English slang");
	}
	
	public void  onAddEnglishShorthandWords(View view)
	{
		showConfirmationDialogue(getResources().openRawResource(R.raw.shorthands), "popular English shorthand");
	}
	
	public void onAddMarathiWordsButtonClick(View view)
	{
		showConfirmationDialogue(getResources().openRawResource(R.raw.marathi), "Marathi");
	}
	
	public void onAddTamilWordsButtonClick(View view)
	{
		
	}
	
	public void onAddBengaliWordsButtonClick(View view)
	{
		showConfirmationDialogue(getResources().openRawResource(R.raw.bengali), "Bengali");
	}
	
	public void onAddOriyaWordsButtonClick(View view)
	{
		showConfirmationDialogue(getResources().openRawResource(R.raw.oriya), "Oriya");
	}
	
	public void onAddTeluguWordsButtonClick(View view)
	{
		showConfirmationDialogue(getResources().openRawResource(R.raw.telugu), "Telugu");
	}
	
	public void onAddGujaratiWordsButtonClick(View view)
	{
		
	}
	
	public void onAddUrduWordsButtonClick(View view)
	{
		
	}
	
	public void onAddFrenchWordsButtonClick(View view)
	{
		
	}
	
	public void onAddArabicWordsButtonClick(View view)
	{
		
	}
	
	public void onAddSpanishWordsButtonClick(View view)
	{
		showConfirmationDialogue(getResources().openRawResource(R.raw.spanish), "Spanish");
	}
	
	public void onAddGermanWordsButtonClick(View view)
	{
		showConfirmationDialogue(getResources().openRawResource(R.raw.german), "German");
	}
	
}
