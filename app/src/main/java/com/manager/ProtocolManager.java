package com.manager;


import com.util.FrameData;

public class ProtocolManager
{ 
	public static final byte START                   = (byte)0x01;
	public static final byte HEARTBEAT               = (byte)0x02; 
	public static final byte VERSION                 = (byte)0x03;
	/*********************** uart********************/
	public static final byte UART_CONFIG             = (byte)0x04; 
	public static final byte UART_GET                = (byte)0x05;
	public static final byte UART_RESTORE            = (byte)0x06;
	/*********************** Sensor in  *************/
	public static final byte SENSOR_IN               = (byte)0x07;
	/*********************** Power     *************/
	public static final byte VOLTAGE                 = (byte)0x08;
	/*********************** Speed in    *************/
	public static final byte SPEED_IN                = (byte)0x09;
	/***********************Sensor out    *************/
	public static final byte SENSOR_OUT              = (byte)0x0A;
	public static final byte SENSOR_OUT_GET          = (byte)0x0B;
	/***********************System upgrade*************/
	public static final byte UPGRADE_START           = (byte)0x0C;
	public static final byte UPGRADE_DATE_SEND       = (byte)0x0D;
	public static final byte UPGRADE_END             = (byte)0x0E;
	/*********************** ACC          *************/
	public static final byte ACC_OFF_SETTINGS        = (byte)0x10;
	public static final byte ACC_OFF_GET             = (byte)0x11;
	public static final byte ACC_POWER_OFF           = (byte)0x12;
	
	/*********************** RS232 RS485  *************/
	public static final int  UART1                   = 1; //RS232_2
	public static final int  UART2                   = 2; //RS232_3
	public static final int  UART3                   = 3; //RS485
	public static final int  UART4                   = 4; //RS232_5
	public static final int  UART_MAX                = 5;
	/***********************  RS232 RS485 transmission *************/
	public static final byte UART1_TRANSMISSION      = (byte)0xF1; //RS232_2 transmission
	public static final byte UART2_TRANSMISSION      = (byte)0xF2; //RS232_3 transmission
	public static final byte UART3_TRANSMISSION      = (byte)0xF3; //RS485 transmission
	public static final byte UART4_TRANSMISSION      = (byte)0xF4; //RS232_5 transmission
	public static final byte UART_MAX_TRANSMISSION   = (byte)0xF5;
	public static final int  UART_TRANSMISSION_MAX_LEN = 512;  //max transmission frame (byte)
	/***********************   uart1 debug        *************/
	public static final byte UART_DEBUG              = (byte) 0xFF;//RS232_2 Debug
	/***********************   frame        *************/
	public static final int  FRAME_MIN_LENGTH        = 4;
	public static final byte FRAME_HEADER            = (byte) 0xAA; 
	public static final byte FRAME_END               = (byte) 0xAA;
	public static final byte ESCAPE_CHAR             = (byte) 0xAB;
	public static final byte ESCAPE_EXT_1            = (byte) 0x01;
	public static final byte ESCAPE_EXT_2            = (byte) 0x02;
	public static final int  HEARTBEAT_TIME          = 5000;//heartbeat(ms) 
	/**************** frame received and sended   *************/
	public static final int  FRAME_RECEIVED          = 0; 
	public static final int  FRAME_SENDED            = 1; 
	public static final int  FRAME_BUFFER_LENGTH	 = 2048;
	public static final int  MAX_UPGRADE_SEND_TIMES  = 10; 
	public static final String KEY_FRAME_BUF         = "framebuf";
	public static final String DATAHUB_CHARSETNAME   = "ISO-8859-1";
	
	public static final byte[] heartbeat = { 
		(byte) 0xAA, (byte) 0x06, (byte) 0x02,(byte) 0xE1, (byte) 0xE2, (byte) 0xE3, (byte) 0xE4,(byte)0xAA
	};
	
	private static byte[] CreateFrameDataArray(byte cmd, byte[] data)
	{
		byte[] framedata=null;
		if(data!=null)
		{
			framedata=new byte[data.length+1];
			framedata[0]=cmd;
			System.arraycopy(data, 0, framedata, 1, data.length);
		}else
		{
			framedata=new byte[1];
			framedata[0]=cmd;
		}
		return framedata;
	 }
	 
	 public static byte[] CreateFrameArray(byte cmd, byte[] data)
	 {
		byte[] framedata=CreateFrameDataArray(cmd,data);
	 	byte[] framebuf=new byte[FRAME_BUFFER_LENGTH];
	 	int dataPos = 0;
	 	framebuf[dataPos++] = FRAME_HEADER;
	 	byte calcXor = 0;
	 	for (int idx=0; idx<framedata.length; idx++)
	 	{
	 		calcXor ^= framedata[idx];
	 	}
	 	if (calcXor == FRAME_HEADER)
	 	{
	 		framebuf[dataPos++] = ESCAPE_CHAR;
	 		framebuf[dataPos++] = ESCAPE_EXT_1;
	 	}
	 	else if (calcXor == ESCAPE_CHAR)
	 	{
	 		framebuf[dataPos++] = ESCAPE_CHAR;
	 		framebuf[dataPos++] = ESCAPE_EXT_2;
	 	}
	 	else
	 	{
	 		framebuf[dataPos++] = calcXor;
	 	}

	 	for (int idx=0; idx<framedata.length; idx++)
	 	{
	 		if (framedata[idx] == FRAME_HEADER)
	 		{
	 			framebuf[dataPos++] = ESCAPE_CHAR;
	 			framebuf[dataPos++] = ESCAPE_EXT_1;
	 		}
	 		else if (framedata[idx] == ESCAPE_CHAR)
	 		{
	 			framebuf[dataPos++] = ESCAPE_CHAR;
	 			framebuf[dataPos++] = ESCAPE_EXT_2;
	 		}
	 		else
	 		{
	 			framebuf[dataPos++] = framedata[idx];
	 		}
	 	}
	 	framebuf[dataPos++] = FRAME_END;
	 	byte[] framearray=new byte[dataPos];
	 	System.arraycopy(framebuf, 0, framearray, 0, dataPos);
	 	return framearray;
	 }
	 
	 public static FrameData CreateFrameData(byte[] Framebuffer)
	 {
		 FrameData Data=new FrameData();
		 byte[] pCurFrame=new byte[FRAME_BUFFER_LENGTH];
		 if(Framebuffer[0]!=FRAME_HEADER&&Framebuffer[Framebuffer.length-1]!=FRAME_END)
		 {
			 return null;
		 }
		 
		 int framePos = 0;
		 for (int idx=1; idx<Framebuffer.length-1; idx++)
		 {
			if (Framebuffer[idx] == ESCAPE_CHAR)
			{
				idx++;
				if (Framebuffer[idx] == ESCAPE_EXT_1)
				{
					pCurFrame[framePos++] = FRAME_HEADER;
				}
				else if (Framebuffer[idx] == ESCAPE_EXT_2)
				{
					pCurFrame[framePos++] = ESCAPE_CHAR;
				}
				else
				{
					return null;
				}
			}
			else
			{
				pCurFrame[framePos++] = Framebuffer[idx];
			}
		}
		
		byte frameXor = pCurFrame[0];
		int len	= framePos;	
		if ((len > 0) && (len <= FRAME_BUFFER_LENGTH))
		{
			byte calcXor = 0;
			for (int idx=0; idx<len; idx++)
			{
				calcXor ^= pCurFrame[idx+1];
			}
			
			if (calcXor != frameXor)
			{
				return null;
			}
			
			byte cmd = pCurFrame[1];
			Data.setcmd(cmd);
			int datalen	= len - 2;	
			if((datalen > 0) && (datalen <= FRAME_BUFFER_LENGTH))
			{
				Data.setDataLength(datalen);
				byte[] Databuff=new byte[datalen];
				System.arraycopy(pCurFrame, 2, Databuff, 0, datalen);
				Data.setdatabuffer(Databuff);
			}
		}
		else
		{
			return null;
		}
		return Data;
	 }
}
