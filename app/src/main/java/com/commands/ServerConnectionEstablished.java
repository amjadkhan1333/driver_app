package com.commands;

import com.infodispatch.CommandQueue;
import com.settersgetters.ConfigData;
import com.settersgetters.Utils;

public class ServerConnectionEstablished {
	private final String DEBUG_TAG = "[ServerConnectionEstablished]";
	CommandQueue msgQueue = new CommandQueue();	
    public void frame_0001_cmd()
    {
         String Server_Connection_Cmd ="^0001|01|" +
				 Utils.getImei_no() + "|" +
				 ConfigData.firmwareVersion + "," +
				 ConfigData.buildDate+ "|0|" +
				 Utils.getImei_no()+ "|!";
         msgQueue.insertToQueue(Server_Connection_Cmd);
    }
}
