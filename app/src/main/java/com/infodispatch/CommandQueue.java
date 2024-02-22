package com.infodispatch;

import java.util.Vector;

public class CommandQueue {
	private final String DEBUG_TAG = "commandQueue";
	private static Vector<String> msgQueue = new Vector<String>();
	private static Vector<String> msgQueue_Prior = new Vector<String>();

	public boolean insertToQueue(String command) {
		try
		{
			if(msgQueue.size() > 256)
			{
				msgQueue.clear();
			}
			msgQueue.insertElementAt(command, msgQueue.size());
		}
		catch(Exception Eww)
		{
			return false;
		}
		return true;
	}	
	public String fetchCmdFromQueue()
	{
		String retData = "";
		try
		{
			retData = msgQueue.elementAt(0).toString();			
		}	
		catch(Exception Eww)
		{		
		}
		return retData;
	}
	public boolean deleteCmdFromQueue()
	{
		try
		{
			msgQueue.removeElementAt(0);

			return true;
		}	
		catch(Exception Eww)
		{}
		return false;
	}
	public boolean clearQueue()
	{
		try
		{
			msgQueue.clear();
			return true;
		}	
		catch(Exception Eww)
		{}
		return false;
	}
	
	/*
	 * Methods to process high Priority queue.
	 */
	public boolean insertToPriorQueue(String command)
	{
		try
		{
			if(msgQueue_Prior.size() > 1)
			{
				msgQueue_Prior.clear();
			}
			
			msgQueue_Prior.insertElementAt(command, msgQueue_Prior.size());	

		}
		catch(Exception Eww)
		{			

			return false;
		}
		return true;
	}	
	public String fetchCmdFromPriorQueue()
	{
		String retData = "";
		try
		{
			retData = msgQueue_Prior.elementAt(0).toString();			
		}	
		catch(Exception Eww)
		{		
		}
		return retData;
	}
	public boolean deleteCmdFromPriorQueue()
	{
		try
		{
			msgQueue_Prior.removeElementAt(0);

			return true;
		}	
		catch(Exception Eww)
		{}
		return false;
	}
	public int priorQueue_Size()
	{
		try
		{
			return msgQueue_Prior.size();
		}	
		catch(Exception Eww)
		{}
		return 0;
	}
	public boolean clearPriorQueue()
	{
		try
		{
			msgQueue_Prior.clear();
			return true;
		}	
		catch(Exception Eww)
		{}
		return false;
	}
}
