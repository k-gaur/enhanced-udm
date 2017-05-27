package com.kgapps.enhancedudm;

import android.os.Bundle;
import android.provider.UserDictionary.Words;
import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

public class AddNewWordsActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_words);
	}

	@SuppressWarnings({ "deprecation" })
	public void onAddButtonClick(View view)
	{
		TextView txt = (TextView)findViewById(R.id.editText1);
		String word = new String(txt.getText().toString());
		Context currentContext = getApplicationContext();
		int startPos=0,endPos=0,length=word.length(), counter=0;
		while(endPos<length)
		{
			if(word.charAt(endPos)==' ' || word.charAt(endPos)=='\n')
			{	
				Words.addWord(currentContext, word.substring(startPos,endPos), 255, Words.LOCALE_TYPE_ALL);
				counter++;
				startPos=endPos+1;	
			}
			else if(endPos==length-1)
			{
				Words.addWord(currentContext, word.substring(startPos,++endPos), 255, Words.LOCALE_TYPE_ALL);
				counter++;
			}	
			endPos++;
		}
		Toast.makeText(getApplicationContext(),String.valueOf(counter) +" Word(s) have been successfully added",Toast.LENGTH_SHORT).show();
		txt.setText(null);
	}
	
	public void onCancelButtonClick(View view)
	{
		finish();
	}
}
