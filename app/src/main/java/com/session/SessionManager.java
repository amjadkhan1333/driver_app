package com.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.log.MyLog;
import com.settersgetters.GpsData;

import java.util.HashMap;

public class SessionManager {
    private String TAG = getClass().getSimpleName();
    Context _context;
    int PRIVATE_MODE = 0;
    SharedPreferences prefGpsOdo;
    SharedPreferences.Editor editorGpsOdo;
    public static final String KEY_GPS_ODO = "KEY_GPS_ODO";
    public static final String KEY_GPS_LAT = "KEY_GPS_LAT";
    public static final String KEY_GPS_LON = "KEY_GPS_LON";
    public static final String KEY_GPS_WAT = "KEY_GPS_WAT";

    public SessionManager(Context context) {
        this._context = context;
        prefGpsOdo = _context.getSharedPreferences(KEY_GPS_ODO, PRIVATE_MODE);
        editorGpsOdo = prefGpsOdo.edit();
    }

    public void createGPSOdoSession() {
        try {
            editorGpsOdo.clear();
            editorGpsOdo.putString(KEY_GPS_ODO, String.valueOf(GpsData.getGpsOdometer()));
            editorGpsOdo.commit();
        } catch (Exception Eww) {

        }
    }

    public void setLatitude(double latitude, double longitude) {
        try {
            double oldlatitude, oldlongitude;
            oldlatitude = prefGpsOdo.getFloat(KEY_GPS_LAT, 0.0f);
            oldlongitude = prefGpsOdo.getFloat(KEY_GPS_LON, 0.0f);

            if (oldlatitude - latitude > 0.0009 || oldlongitude - latitude > 0.0009) {
                editorGpsOdo.putFloat(KEY_GPS_LAT, Float.parseFloat(String.valueOf(latitude)));
                editorGpsOdo.putFloat(KEY_GPS_LON, Float.parseFloat(String.valueOf(longitude)));
                editorGpsOdo.commit();
                Log.e(TAG, "setLatitude: 1");
            } else if (latitude - oldlatitude > 0.0009 || latitude - oldlongitude > 0.0009) {
                editorGpsOdo.putFloat(KEY_GPS_LAT, Float.parseFloat(String.valueOf(latitude)));
                editorGpsOdo.putFloat(KEY_GPS_LON, Float.parseFloat(String.valueOf(longitude)));
                editorGpsOdo.commit();
                Log.e(TAG, "setLatitude: 2");
            } else {
                int waitingTime = prefGpsOdo.getInt(KEY_GPS_WAT, 0);
                waitingTime = waitingTime + 1;
                MyLog.appendLog("Session : " + waitingTime);
                editorGpsOdo.putInt(KEY_GPS_WAT, waitingTime);
                editorGpsOdo.commit();
                Log.e(TAG, "setLatitude: 3" + waitingTime);
            }
        } catch (Exception e) {
            Log.e(TAG, "setLatitude: " + e.getMessage());
        }
    }

    public void resetWating() {
        MyLog.appendLog("Session : resetWating");
        editorGpsOdo.putInt(KEY_GPS_WAT, 0);
        editorGpsOdo.commit();
        Log.e(TAG, "setLatitude: 4");
    }


    public HashMap<String, String> getGPSOdoDetails() {
        try {
            HashMap<String, String> gpsOdoSession = new HashMap<String, String>();
            gpsOdoSession.put(KEY_GPS_ODO, prefGpsOdo.getString(KEY_GPS_ODO, "0.0"));
            return gpsOdoSession;
        } catch (Exception Eww) {
        }
        return null;
    }
}
