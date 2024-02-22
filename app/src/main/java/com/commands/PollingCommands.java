package com.commands;

import android.util.Log;

import com.infodispatch.CommandQueue;
import com.settersgetters.ConfigData;
import com.settersgetters.GpsData;
import com.settersgetters.Utils;

public class PollingCommands {
    private String TAG = getClass().getSimpleName();
    private CommandQueue msgQueue = new CommandQueue();

    public void frame_09_cmd() {
        Log.e("QUEUE", "frame_09_cmd: " + GpsData.getGpsOdometer());
        String Polling_Cmd = "^0609|" +
                "0" + "|" +
                GpsData.getLatitude() + "|" +
                GpsData.getLongitude() + "|" +
                GpsData.getGpsSpeed() + "|" +
                GpsData.getGpsDirection() + "|" +
                GpsData.getGpsTime() + "|" +
                "2|" +
                ConfigData.getDeviceStatus() + "|" +
                "A" + "|" +//9
                String.valueOf(GpsData.getSeatSensor()) + "|" +//10
                "0" + "|" +//11
                "0" + "|" +//12
                "0" + "|" +//13
                GpsData.getEmirateID() + "|" +//14 GEOFENCE ID
                GpsData.getTollID() + "|" +//15 TOLL ID
                GpsData.getGpsOdometer() + "|" +//16
                "0" + "|" +//17
                GpsData.getEmirateCharges() + "|" +//18 GEO FENCE
                GpsData.getTollCharges() + "|" +//19 TOLL CHARGE
                Utils.getIPAddress(true) + "|" +
                Utils.getImei_no() + "|!";
        msgQueue.insertToPriorQueue(Polling_Cmd);
        GpsData.setExtraID(0,0);
        Log.e(TAG, "frame_09_cmd: "+Polling_Cmd );
    }
}
