package com.manager;


import android.util.Log;

import android_serialport_api.SerialPort;
import com.util.LogManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;


public abstract class SerialPortManager
{
	public final static String TAG = "SerialPortManager";
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private String sPort="/dev/ttyS4";
	private int iBaudRate=9600;
	private boolean _isOpen=false;
	private int SLEEP_TIME=500;
	private LogManager mUARTUp=new LogManager();
	private LogManager mUARTDown=new LogManager();
	public SerialPortManager(String sPort, int iBaudRate)
	{
		this.sPort = sPort;
		this.iBaudRate=iBaudRate;
	}
	
	public SerialPortManager(){
		this("/dev/ttyS4",115200);
	}
	
	public SerialPortManager(String sPort){
		this(sPort,9600);
	}
	
	public SerialPortManager(String sPort, String sBaudRate){
		this(sPort, Integer.parseInt(sBaudRate));
	}
	
	public void open() throws SecurityException, IOException, InvalidParameterException
	{
		mUARTUp.SetLogFileName("UARTUp_"+mUARTUp.GetCurrentTime()+".log");
		mUARTDown.SetLogFileName("UARTDown_"+mUARTDown.GetCurrentTime()+".log");
		
		mSerialPort =  new SerialPort(new File(sPort), iBaudRate, 0);
		mOutputStream = mSerialPort.getOutputStream();
		mInputStream = mSerialPort.getInputStream();
		mReadThread = new ReadThread();
		mReadThread.start();
		_isOpen=true;
	}
	
	public void close()
	{
		if(mReadThread != null)
		{
			mReadThread.exit();
			mReadThread = null;
		}
		try {
			if (mInputStream != null) {
				mInputStream.close();
				mInputStream = null;
			}
			if (mOutputStream != null) {
				mOutputStream.close();
				mOutputStream = null;
			}
			if (mSerialPort != null) 
			{
				mSerialPort.close();
				mSerialPort = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		_isOpen=false;
	}
	
	public void send(byte[] bOutArray)
	{
		try
		{
			mOutputStream.write(bOutArray);
			mUARTDown.SaveHexLog(bOutArray);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getBaudRate()
	{
		return iBaudRate;
	}
	
	public boolean setBaudRate(int iBaud)
	{
		if (_isOpen)
		{
			return false;
		} else
		{
			iBaudRate = iBaud;
			return true;
		}
	}
	
	public String getPort()
	{
		return sPort;
	}
	
	public boolean setPort(String sPort)
	{
		if (_isOpen)
		{
			return false;
		} else
		{
			this.sPort = sPort;
			return true;
		}
	}
	
	public boolean isOpen()
	{
		return _isOpen;
	}
	
	public void sendData(byte[] frame)
	{
		send(frame);
		onDataSended(frame);
	}
	
	private class ReadThread extends Thread
	{
		private static final int BUFF_MAX_SIZE = 1024;
		byte[] mBuffer=new byte[BUFF_MAX_SIZE];
		private int received;
		private boolean mReadThreadRun;
		public void exit() 
		{
			mReadThreadRun = false;
		}
		@Override
		public void run() 
		{
			if (mInputStream == null) 
				return;
			
			mReadThreadRun = true;
			try{
				if(mInputStream.available() > 0)
				{
					mInputStream.skip(mInputStream.available());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			while(mReadThreadRun) 
			{
				try
				{
					received = mInputStream.available();
					if(received>=ProtocolManager.FRAME_MIN_LENGTH)
					{
						Log.e(TAG, "run: READ THREAD RUN");
						if (received > BUFF_MAX_SIZE) {
							received = BUFF_MAX_SIZE;
						}
						mInputStream.read(mBuffer, 0, received);
						byte [] ReceivedBuffer=new byte[received];
						System.arraycopy(mBuffer, 0, ReceivedBuffer, 0, received);
						onDataReceived(ReceivedBuffer);
						mUARTUp.SaveHexLog(ReceivedBuffer);
					}
					
					try
					{
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				} catch (Throwable e)
				{
					e.printStackTrace();
					return;
				}
			}
		}
	}
	protected abstract void onDataReceived(byte[] databuf);
	protected abstract void onDataSended(byte[] databuf);
}