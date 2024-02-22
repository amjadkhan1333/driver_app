package com.commands;

import com.infodispatch.CommandQueue;
import com.settersgetters.Utils;

public class AckCommand {
    private final String DEBUG_TAG = "[AckCommands]";
    CommandQueue msgQueue = new CommandQueue();
    public String frame_24_cmd(String uid)
    {
        String Ack_Cmd ="^0624|" +
                uid + "|" +
                Utils.getImei_no()+ "|!";
        System.out.println("Inside the Ack---"+Ack_Cmd);
        return Ack_Cmd;
        //msgQueue.insertToQueue(Ack_Cmd);
    }
}
