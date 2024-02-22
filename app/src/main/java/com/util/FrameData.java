package com.util;

public class FrameData 
{
	private byte mcmd;
	private int mdataLength;
	private byte[] mdatabuffer=null;
	
	public void setcmd(byte cmd)
	{
		mcmd=cmd;
	}
	
	public void setDataLength(int Length)
	{
		mdataLength=Length;
	}
	
	public void setdatabuffer(byte[] Buf)
	{
		mdatabuffer=new byte[Buf.length];
		System.arraycopy(Buf, 0, mdatabuffer, 0, Buf.length);
	}
	
	public byte getcmd()
	{
		return mcmd;
	}
	
	public int getDataLength()
	{
		return mdataLength;
	}
	
	public byte[] getdatabuffer()
	{
		return mdatabuffer;
	}
}
