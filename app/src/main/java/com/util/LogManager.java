package com.util;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class LogManager 
{
	private String mLogfile;
	private boolean mSaveflag=false;
	private StringBuilder StringMsg=new StringBuilder();
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("hh:mm:ss");
	private SimpleDateFormat mCurrentTimeFormat = new SimpleDateFormat("MMddHHmmssSSS");
	
	public void SetLogFileName(String filename)
	{
		mLogfile=getPath()+ File.separator+filename;
	}
	
	public String GetCurrentTime()
	{
		return mCurrentTimeFormat.format(System.currentTimeMillis());
	}
	
	public void SaveLogToFile(boolean flag)
	{
		mSaveflag=flag;
	}
	
	private static String getPath()
	{
		String path =  Environment.getExternalStorageDirectory().getPath().toString();
		File dir = new File(path + File.separator + "Datahub");
		if (dir != null) {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (dir.canWrite()) {
				path = dir.getPath();
			}
		}
		return path;
	}
	
	public void Savebuffer(String buffer)
	{
		if(!mSaveflag)return;
		
		try 
		 {
			BufferedWriter writer = new BufferedWriter(new FileWriter(mLogfile, true));
        	writer.write(buffer);
			writer.flush();
            writer.close();
        } catch (FileNotFoundException e)
		{
        	
        } catch (IOException e) {
        	
        }
	}
	
	public void Savebuffer(byte[] buffer) 
	{
		if(!mSaveflag)return;
		
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(mLogfile,true);
			try {
				fileOutputStream.write(buffer, 0, buffer.length);
				fileOutputStream.flush();
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void SaveHexLog(byte[] buffer)
	{	
		if(!mSaveflag)return;
		
	   	String sRecTime = mSimpleDateFormat.format(new java.util.Date());
	   	StringMsg.append(sRecTime);
	   	StringMsg.append("  {");
	   	StringMsg.append(Utils.ByteArrToHex(buffer));
	   	StringMsg.append("}");
	   	StringMsg.append("\r\n");
	   	if(StringMsg.length()>512)
	   	{
	   		Savebuffer(StringMsg.substring(0, StringMsg.length()));
	   		StringMsg.delete(0, StringMsg.length());
	   	}	
	}
}
