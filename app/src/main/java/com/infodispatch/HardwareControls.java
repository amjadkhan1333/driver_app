package com.infodispatch;

import android.content.Context;
import android.os.PowerManager;

import com.log.MyLog;

import static com.services.PollingService.DEBUG_KEY;

public class HardwareControls {
	 private static String DEBUG_TAG = "[HardwareControls]";
	 public static void backlightON(Context context){
		 try {
	         PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);         
	         PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |  PowerManager.ACQUIRE_CAUSES_WAKEUP, "MessageReader");
	         wakeLock.acquire();
	         Thread.sleep(1000);
	         wakeLock.release();	         
		 }
		 catch(Exception e)
		 {
			 MyLog.appendLog(DEBUG_KEY+"HardwareControls"+e.getMessage());
		 }
	 }

}
