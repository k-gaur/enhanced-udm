package com.kgapps.enhancedudm;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.content.Context;

public class DumpFileIntoDictionary 
{
	public static void dumpUsingPath(Activity currentActivity,Context currentContext,String filePath) throws IOException
	{
		FileReader inputFileReader = new FileReader(filePath);
		StringBuilder word = new StringBuilder();
		int ch;
		while((ch=inputFileReader.read())!=-1)
		{
			word.append((char)ch);
			if((char)ch==' ' || (char)(ch) == '\n')
				AsyncDictAdder.totalWords++;
		}
		inputFileReader.close();
		AsyncDictAdder backFileIntoDictAdder = new AsyncDictAdder(currentActivity, currentContext, word.toString());
		backFileIntoDictAdder.execute();
	}
	
	public static void dumpUsingStream(Activity currentActivity,Context currentContext,InputStream file) throws IOException
	{
		
		StringBuilder word = new StringBuilder();
		int ch;
		while((ch=file.read())!=-1)
		{
			word.append((char)ch);
			if((char)ch==' ' || (char)(ch) == '\n')
				AsyncDictAdder.totalWords++;
		}
		AsyncDictAdder.totalWords++;
		AsyncDictAdder backFileIntoDictAdder = new AsyncDictAdder(currentActivity, currentContext, word.toString());
		backFileIntoDictAdder.execute();
	}
}
