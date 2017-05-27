package com.kgapps.enhancedudm;

import android.os.Bundle;
import android.os.Environment;
import android.provider.UserDictionary.Words;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.*;
import android.widget.Toast;
import android.app.AlertDialog;
import java.io.*;
import java.util.Date;

public class MainUiActivity extends Activity 
{
	private static final int filePickCode=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_ui);
	}
	
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
		null); 									// The sort order for the returned rows
		
		return mCursor;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
        switch (requestCode) 
        {
            case filePickCode:  // If the file selection was successful
                				if (resultCode == RESULT_OK) 
                				{
            						// Get the path of the selected file
            						String filePath = data.getData().getPath();
            						try
            						{
										int fileNameLength = filePath.length();
										//if file extension is *.txt
										if(filePath.charAt(fileNameLength-1) == 't' && filePath.charAt(fileNameLength-2) == 'x' && filePath.charAt(fileNameLength-3) == 't')
											DumpFileIntoDictionary.dumpUsingPath(this, getApplicationContext(), filePath);
										else
											Toast.makeText(getApplicationContext(),"Please select a text file (*.txt files)",Toast.LENGTH_LONG).show();
									} 
            						catch (IOException e) 
            						{
										//nothing to do
            						}
                				}
                				//if no file is selected
                				else
            						Toast.makeText(getApplicationContext(),"No file is selected: Operation cancelled",Toast.LENGTH_LONG).show();
                				break;
        }
    }
	
	public void onAddNewWordsButtonClick(View view)
	{
		startActivity(new Intent(getApplicationContext(), AddNewWordsActivity.class));
	}
	
	public void onBrowseEditDictionaryButtonClick(View view)
	{
		if(getWordsCursor().getCount()==0)
			Toast.makeText(getApplicationContext(),"User dictionary is empty: Operation cancelled",Toast.LENGTH_LONG).show();
		else
			startActivity(new Intent(getApplicationContext(), EditDictionaryActivity.class));
	}
	
	public void onRestoreWordsFromTextFileButtonClick(View view)
	{
		AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
		msgBox.setTitle("Open Text File");
		msgBox.setMessage("Open a text file containing a list of words (in UTF format). A text file ends with a .txt entension" +
				". Words should be seperated by a space or a newline.\n" +
				"Please ensure that the external storage is mounted before proceeding.");
		msgBox.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{	
			@Override
			public void onClick(DialogInterface arg0, int arg1) 
			{
				// TODO Auto-generated method stub
				String externalStorageState = Environment.getExternalStorageState();
				//checking if the external storage is mounted or not
				if(externalStorageState.equalsIgnoreCase(Environment.MEDIA_MOUNTED) || externalStorageState.equalsIgnoreCase(Environment.MEDIA_MOUNTED_READ_ONLY))
				{
					Intent fileChooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
					fileChooserIntent.setType("file/*");
					startActivityForResult(fileChooserIntent, filePickCode); 
				}
				else
					Toast.makeText(getApplicationContext(),"External storage is not mounted: Operation cancelled",Toast.LENGTH_LONG).show();
			}
		});
		msgBox.show();	
	}
	
	@SuppressWarnings({ "unused" })
	@SuppressLint({ "SdCardPath", "DefaultLocale" })
	public void onBackupWordsToTextFileButtonClick(View view)
	{
		final Cursor mCursor = getWordsCursor();
		if(mCursor.getCount() == 0)
			Toast.makeText(getApplicationContext(),"User dictionary is empty: Operation cancelled", Toast.LENGTH_LONG).show();
		else if(new String(Environment.getExternalStorageState()).equalsIgnoreCase(Environment.MEDIA_MOUNTED)) 
		{
			File rootDirectory = new File(Environment.getExternalStorageDirectory()+"/EnhancedUDM/Backups/");
			if(!rootDirectory.exists())
				rootDirectory.mkdirs();	
			String word;
			String outFileName = new String("Backup_" + DateFormat.format("dd-MM-yy_hh-mm-ss",(new Date()).getTime())+ ".txt");
			final File outputFile = new File(rootDirectory, outFileName);
			
			AlertDialog.Builder confirmBox = new AlertDialog.Builder(this);
			confirmBox.setTitle("Backup words");
			confirmBox.setMessage("The words will be backed up to \n" + outputFile.getPath() + "\nContinue?");
			confirmBox.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{	
				@Override
				public void onClick(DialogInterface arg0, int arg1) 
				{
					// TODO Auto-generated method stub
					// Determine the column index of the column named "word"
					int index = mCursor.getColumnIndex(Words.WORD);
					FileWriter outputFileWriter;
					try 
					{
						outputFileWriter = new FileWriter(outputFile.getPath(),true);
						while (mCursor.moveToNext()) 
							outputFileWriter.write(mCursor.getString(index) + "\n");
						
						Toast.makeText(getApplicationContext(), String.valueOf(mCursor.getCount()) + " words have been backup up to " + outputFile.toString() ,Toast.LENGTH_LONG).show();
						outputFileWriter.close();
					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
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
		else
			Toast.makeText(getApplicationContext(),"Storage media not found: Operation cancelled",Toast.LENGTH_LONG).show();		
	}
	
	public void onAddWordListButtonClick(View view)
	{
		startActivity(new Intent(getApplicationContext(), AddWordListActivity.class));
	}
	
	public void onImportWordsFromWhatsappButtonClick(View view)
	{
		
	}
	
	public void onAboutButtonClick(View view)
	{
		Toast.makeText(getApplicationContext(),"App version: 1\n\nEverything seems stable and working.",Toast.LENGTH_LONG).show();
	}
	
	public void onRateAppButtonClick(View view)
	{
		
	}
	
	public void onCreditsButtonClick(View view)
	{
		AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
		msgBox.setTitle("Enhanced UDM: Credits");
		msgBox.setMessage("Will add credits later :P");
		//msgBox.setMessage("Hi, i am Kush aka Rose boy and i am presenting my first android app for all the android fanboys out there.\nThis is a utility app made by me in my free time. It all started when i was searching for Hinglish dictionary for stock android keyboard. I have already tried numerous 3rd party keyboards and a few of them were actually 'good' but none of them had the same effciency and response time as the stock android keyboard has, really kudos to Google for making such a wonderful keyboard!\nBut as they say, nothing is perfect, unfortunately stock android keyboard does not provide much help when it comes to dealing with the inbuilt user dictionary. Necessity is the mother of invention and this is indeed true. So here i am with this app in a hope that it proves to be as much useful for you as it has been for me.\nI would like to take this opportunity to thank my guru Mr. CS Yadav who re-invented my trust in programming and my friends, specially Ankit aka Terminator and Ish aka Demonstrator for helping me whenever i asked them to, and obviously my parents too for bearing with me in all those sleepless nights i had while developing this project.\nBored by the story? Well no issues, it has already ended, cheers :)");
		msgBox.setPositiveButton("Great, sounds cool!", null);
		msgBox.show();
	}
	
	public void onFaqButtonClick(View view)
	{
		AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
		msgBox.setTitle("Enhanced UDM: FAQ");
		msgBox.setMessage("1. What this app is all about ?\n\n" +
						"Ans: UDM stands for User Dictionary Manager. As the app name suggests 'Enhanced UDM' is a feature-rich user dictionary manager for stock android keyboard." +
						" It has the following features:\n" +
						"\ta) Backup user dictionary words to external text file\n" +
						"\tb) Restore user dictionary words from external text file\n" +
						"\tc) Add words manually into the dictionary\n" +
						"\td) Update/Delete words from the dictionary\n" +
						"\te) Clear the user dictionary\n" +
						"\tf) De-duplicate the user dictionary\n" +
						"\tg) Download and add commonly used words in many different languages into the user dictionary\n" +
						"\th) Extract words from WhatsApp IM app and add them to the dictionary\n\n" +
						"2. There are many similar apps in the market, what's so unique about this app ?\n\n" +
						"Ans: I agree there are many similar apps in the market but what sets this app apart from them is the " +
						"easy and simplicity which it works with. Apart from that it also has a unique feature of extracting words " +
						"which you have entered in your WhatsApp chats and including them in your user dictionary. Saves a lot of hassle, isn't it? But beware, it can become " +
						"really slow at times because WhatsApp chats can be huge and extracting words from it can take some time.\n\n" +
						"3. Why app look is so boring?\n\n" +
						"Ans: Well to be honest i myself feel that app UI is a bit boring and too basic. But that's because i believe that getting your work done" +
						" is more important than who does it. Also since i wanted to target almost all the devices available out there from Froyo to Kitkat" +
						", so the app UI had to be basic so that the older android versions can also support it.\n\n" +
						"4. Why does the app has annoying Ads?\n\n" +
						"Ans: Well currently i am going through a big financial problem and including advertisements is the only way which could help me get some money" +
						" from this app while still letting the users use it for free. So please bear with me untill my problem is solved, after which" +
						" i'll make the app ad-free.\n\n" +
						"5. Does this app affects 3rd party keyboards' dictionaries too?\n\n" +
						"Ans: Well it depends upon which dictionary those keyboards use. Currently almost all the 3rd party keyboards use their own dictionaries" +
						", so this app won't be affecting them in any way. It was meant for the management of stock android user dictionary only. " +
						"If you don't have stock keyboard i would recommend you to try it as i feel it is the most responsive keyboard out there. It can be " +
						"downloaded from the android play store.\n\n" +
						"6. I am getting bugs while using this app, what to do?\n\n" +
						"Ans: I tried my best to make the app as bugfree and stable as possible but still testing it on each and every android platform is " +
						"not possible. If it shows you bugs while using i request you to re-download it from the play store. If this doesn't help, you can also " +
						"mail me about the bug to my id: enhancedudm.dev@gmail.com.");
		msgBox.setPositiveButton("Yep, gotcha!", null);
		msgBox.show();
	}
}
