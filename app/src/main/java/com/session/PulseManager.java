package com.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.log.MyLog;

import java.util.Calendar;

public class PulseManager {
    private String TAG = getClass().getSimpleName();
    Context _context;
    int PRIVATE_MODE = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editorPref;
    public static final String KEY_PULSE_PREF = "KEY_PULSE_PREF";
    public static final String KEY_PULSE_WAT = "KEY_PULSE_WAT";
    public static final String KEY_TRIP_WAT = "KEY_TRIP_WAT";
    public static final String KEY_TRIP_START = "KEY_TRIP_START";
    public static final String KEY_TRIP_END = "KEY_TRIP_END";
    public static final String KEY_TRIP_FARE = "KEY_TRIP_FARE";
    public static final String KEY_TRIP = "KEY_TRIP";

    public static final String KEY_ODO_METER = "KEY_ODO_METER";
    public static final String KEY_ODOKILO_METER = "KEY_ODOKILO_METER";

    public static final String KEY_START_ODO_METER = "KEY_START_ODO_METER";
    public static final String KEY_START_ODOKILO_METER = "KEY_START_ODOKILO_METER";

    public static final String KEY_END_ODO_METER = "KEY_END_ODO_METER";
    public static final String KEY_END_ODOKILO_METER = "KEY_END_ODOKILO_METER";

    public static final String KEY_EXTRA_DISTANCE = "KEY_EXTRA_DISTANCE";
    public static final String KEY_DISTANCE = "KEY_DISTANCE";
    public static final String KEY_PULSE_COUNT = "KEY_PULSE_COUNT";
    public static final String KEY_PREV_COUNT = "KEY_PREV_COUNT";
    public static final String KEY_NEXT_COUNT = "KEY_NEXT_COUNT";
    public static final String KEY_DIFF_COUNT = "KEY_DIFF_COUNT";
    public static final String KEY_GPSLAT = "KEY_GPSLAT";
    public static final String KEY_GPSLON = "KEY_GPSLON";

    public static final String KEY_JSON_DATA = "KEY_JSON_DATA";
    public static final String KEY_JSON_PROFILE = "KEY_JSON_PROFILE";

    public static final String KEY_PULSE = "KEY_PULSE";
    public static final String KEY_MIN_DISTANCE = "KEY_MIN_DISTANCE";
    public static final String KEY_STARTING_FARE = "KEY_STARTING_FARE";

    public static final String KEY_WAITING_MIN = "KEY_WAITING_MIN";
    public static final String KEY_WAITING_FARE = "KEY_WAITING_FARE";

    public static final String KEY_HOTEL_JOB = "KEY_HOTEL_JOB";
    public static final String KEY_DATA_HUB = "KEY_DATA_HUB";

    private static final String KEY_OVERSPEED_COUNT = "KEY_OVERSPEED_COUNT";
    private static final String KEY_OVERSPEED_FLAG = "KEY_OVERSPEED_FLAG";

    private static final String KEY_MAX_OVERSPEED_COUNT = "KEY_MAX_OVERSPEED_COUNT";
    private static final String KEY_MAX_OVERSPEED_FLAG = "KEY_MAX_OVERSPEED_FLAG";

    private static final String KEY_PAYMENT_TYPE = "KEY_PAYMENT_TYPE";
    private static final String KEY_NO_SPEED_COUNT = "KEY_NO_SPEED_COUNT";

    private static final String KEY_SPEED_VALUE = "KEY_SPEED_VALUE";
    private static final String KEY_MAX_SPEED_VALUE = "KEY_MAX_SPEED_VALUE";

    private static final String KEY_SEAT_1 = "KEY_SEAT_1";
    private static final String KEY_SEAT_2 = "KEY_SEAT_2";
    private static final String KEY_SEAT_3 = "KEY_SEAT_3";
    private static final String KEY_SEAT_4 = "KEY_SEAT_4";
    private static final String KEY_SEAT_5 = "KEY_SEAT_5";
    private static final String KEY_SEAT_6 = "KEY_SEAT_6";
    private static final String KEY_SEAT_7 = "KEY_SEAT_7";
    private static final String KEY_SEAT_8 = "KEY_SEAT_8";
    private static final String KEY_SEAT_COUNT = "KEY_SEAT_COUNT";
    private static final String KEY_POWER = "KEY_POWER";
    private static final String KEY_IGNITION = "KEY_IGNITION";

    public static final String KEY_GEO = "KEY_GEO";

    public static final String KEY_EMIRATE_1_GEO = "KEY_EMIRATE_1_GEO";
    public static final String KEY_EMIRATE_2_GEO = "KEY_EMIRATE_2_GEO";
    public static final String KEY_EMIRATE_3_GEO = "KEY_EMIRATE_3_GEO";

    public static final String KEY_TOLL_1_GEO = "KEY_TOLL_1_GEO";
    public static final String KEY_TOLL_2_GEO = "KEY_TOLL_2_GEO";
    public static final String KEY_TOLL_3_GEO = "KEY_TOLL_3_GEO";
    public static final String KEY_TOLL_4_GEO = "KEY_TOLL_4_GEO";
    public static final String KEY_TOLL_5_GEO = "KEY_TOLL_5_GEO";
    public static final String KEY_TOLL_6_GEO = "KEY_TOLL_6_GEO";
    public static final String KEY_TOLL_7_GEO = "KEY_TOLL_7_GEO";

    public static final String KEY_EMIRATE = "KEY_EMIRATE";
    public static final String KEY_EMIRATE_1 = "KEY_EMIRATE_1";
    public static final String KEY_EMIRATE_2 = "KEY_EMIRATE_2";
    public static final String KEY_EMIRATE_3 = "KEY_EMIRATE_3";

    public static final String KEY_TOLL = "KEY_TOLL";
    public static final String KEY_TOLL_1 = "KEY_TOLL_1";
    public static final String KEY_TOLL_2 = "KEY_TOLL_2";
    public static final String KEY_TOLL_3 = "KEY_TOLL_3";
    public static final String KEY_TOLL_4 = "KEY_TOLL_4";
    public static final String KEY_TOLL_5 = "KEY_TOLL_5";
    public static final String KEY_TOLL_6 = "KEY_TOLL_6";
    public static final String KEY_TOLL_7 = "KEY_TOLL_7";
    public static final String KEY_TOLL_8 = "KEY_TOLL_8";

    public static final String KEY_EMIRATE_1_CHECK = "KEY_EMIRATE_1_CHECK";
    public static final String KEY_EMIRATE_2_CHECK = "KEY_EMIRATE_2_CHECK";
    public static final String KEY_EMIRATE_3_CHECK = "KEY_EMIRATE_3_CHECK";

    public static final String KEY_TOLL_1_CHECK = "KEY_TOLL_1_CHECK";
    public static final String KEY_TOLL_2_CHECK = "KEY_TOLL_2_CHECK";
    public static final String KEY_TOLL_3_CHECK = "KEY_TOLL_3_CHECK";
    public static final String KEY_TOLL_4_CHECK = "KEY_TOLL_4_CHECK";
    public static final String KEY_TOLL_5_CHECK = "KEY_TOLL_5_CHECK";
    public static final String KEY_TOLL_6_CHECK = "KEY_TOLL_6_CHECK";
    public static final String KEY_TOLL_7_CHECK = "KEY_TOLL_7_CHECK";
    public static final String KEY_TOLL_8_CHECK = "KEY_TOLL_8_CHECK";

    public static final String KEY_EXTRA_TOLL_FARE = "KEY_EXTRA_TOLL_FARE";
    public static final String KEY_EXTRA_FARE = "KEY_EXTRA_FARE";
    public static final String KEY_GPS_COUNT = "KEY_GPS_COUNT";

    public PulseManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(KEY_PULSE_PREF, PRIVATE_MODE);
        editorPref = pref.edit();
    }

    public void setPulseCount(int pulseCount) {
        try {
            editorPref.putInt(KEY_PULSE_COUNT, pulseCount);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setPulseCount: " + ""+pulseCount);
        } catch (Exception e) {
            Log.e(TAG, "setPulseCount: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setPulseCount: " + e.getMessage());
        }
    }

    public int getPulseCount() {
        return pref.getInt(KEY_PULSE_COUNT, 0);
    }

    public void setNextCount(int nextCount) {
        try {
            editorPref.putInt(KEY_NEXT_COUNT, nextCount);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setNextCount: " +""+nextCount);
        } catch (Exception e) {
            Log.e(TAG, "setNextCount: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setNextCount: " + e.getMessage());
        }
    }

    public int getNextCount() {
        return pref.getInt(KEY_NEXT_COUNT, 0);
    }

    public void setPrevCount(int prevCount) {
        try {
            editorPref.putInt(KEY_PREV_COUNT, prevCount);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setPrevCount: " + ""+prevCount);
        } catch (Exception e) {
            Log.e(TAG, "setPrevCount: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setPrevCount: " + e.getMessage());
        }
    }

    public int getPrevCount() {
        return pref.getInt(KEY_PREV_COUNT, 0);
    }

    public void setPulseWating(int pulseWating) {
        try {
            editorPref.putInt(KEY_PULSE_WAT, pulseWating);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setPulseWating: " + ""+pulseWating);
        } catch (Exception e) {
            Log.e(TAG, "Exception setPulseWaiting: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setPulseWaiting: " + ""+e.getMessage());
        }
    }

    public int getPulseWating() {
        return pref.getInt(KEY_PULSE_WAT, 0);
    }

    public void setTripWating(int tripWating) {
        try {
            editorPref.putInt(KEY_TRIP_WAT, tripWating);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setTripWating: " + ""+tripWating);
        } catch (Exception e) {
            Log.e(TAG, "setTripWaiting: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTripWating: " + e.getMessage());
        }
    }

    public int getTripWating() {
        return pref.getInt(KEY_TRIP_WAT, 0);
    }

    public void setWaitingMin(int tripFare) {
        try {
            tripFare = tripFare - 1;
            editorPref.putInt(KEY_WAITING_MIN, tripFare);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setTripFare: " + ""+tripFare);
        } catch (Exception e) {
            Log.e(TAG, "setTripFare: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTripFare: " + e.getMessage());
        }
    }

    public int getWaitingMin() {
        return pref.getInt(KEY_WAITING_MIN, 0);
    }

    public void setWaitingRate(float tripFare) {
        try {
            editorPref.putFloat(KEY_WAITING_FARE, tripFare);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setTripFare: " + ""+tripFare);
        } catch (Exception e) {
            Log.e(TAG, "setTripFare: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTripFare: " + e.getMessage());
        }
    }

    public float getWaitingRate() {
        return pref.getFloat(KEY_WAITING_FARE, 0);
    }

    public void setTripFare(float tripFare) {
        try {
            editorPref.putFloat(KEY_TRIP_FARE, tripFare);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setTripFare: " + ""+tripFare);
        } catch (Exception e) {
            Log.e(TAG, "setTripFare: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTripFare: " + e.getMessage());
        }
    }

    public float getTripFare() {
        return pref.getFloat(KEY_TRIP_FARE, 3);
    }

    public void setStartTrip(int tripStart) {
        try {
            editorPref.putInt(KEY_TRIP_START, tripStart);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setStartTrip: " +""+tripStart);
        } catch (Exception e) {
            Log.e(TAG, "setStartTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setStartTrip: " + e.getMessage());
        }
    }

    public int getStartTrip() {
        return pref.getInt(KEY_TRIP_START, 0);
    }

    public void setEndTrip(int tripEnd) {
        try {
            editorPref.putInt(KEY_TRIP_END, tripEnd);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEndTrip: " + ""+tripEnd);
        } catch (Exception e) {
            Log.e(TAG, "setEndTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setEndTrip: " + e.getMessage());
        }
    }

    public int getEndTrip() {
        return pref.getInt(KEY_TRIP_END, 0);
    }

    public void setTrip(boolean trip) {
        try {
            editorPref.putBoolean(KEY_TRIP, trip);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setTrip: " + ""+trip);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isTrip() {
        return pref.getBoolean(KEY_TRIP, false);
    }

    public void setPulse(boolean pulse) {
        try {
            editorPref.putBoolean(KEY_PULSE, pulse);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setPulse: " + ""+pulse);
        } catch (Exception e) {
            Log.e(TAG, "setPulse: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setPulse: " + e.getMessage());
        }
    }

    public boolean isPulse() {
        return pref.getBoolean(KEY_PULSE, false);
    }

    public void setJsonObject(String jsonObject){
        try {
            editorPref.putString(KEY_JSON_DATA, jsonObject+"\n");
            editorPref.commit();
        } catch (Exception e) {
            Log.e(TAG, "setJsonObject: " + e.getMessage());
        }
    }

    public String getJsonObject(){
        return pref.getString(KEY_JSON_DATA, "{}");
    }

    public void setLatitude(String latitude){
        try {
            editorPref.putString(KEY_GPSLAT, latitude);
            editorPref.commit();
        } catch (Exception e) {
            Log.e(TAG, "setJsonObject: " + e.getMessage());
        }
    }

    public String getLatitude(){
        return pref.getString(KEY_GPSLAT, "0.0");
    }

    public void setLongitude(String latitude){
        try {
            editorPref.putString(KEY_GPSLON, latitude);
            editorPref.commit();
        } catch (Exception e) {
            Log.e(TAG, "setJsonObject: " + e.getMessage());
        }
    }

    public String getLongitude(){
        return pref.getString(KEY_GPSLON, "0.0");
    }

    public void setJsonProfileObject(String jsonObject){
        try {
            editorPref.putString(KEY_JSON_PROFILE, jsonObject+"\n");
            editorPref.commit();
        } catch (Exception e) {
            Log.e(TAG, "setJsonObject: " + e.getMessage());
        }
    }

    public String getJsonProfileObject(){
        return pref.getString(KEY_JSON_PROFILE, "{}");
    }

    public void setDiffCount(int diffCount) {
        try {
            editorPref.putInt(KEY_DIFF_COUNT, diffCount);
            editorPref.commit();
        } catch (Exception e) {
            Log.e(TAG, "setDiffCount: " + e.getMessage());
        }
    }

    public int getDiffCount() {
        return pref.getInt(KEY_DIFF_COUNT, 0);
    }

    public void setOdometer(int pulseCount, int odometerKillo, int odometerMeter) {
        try {
//            Log.e(TAG, "setOdometer F: P " + pulseCount + " K " + +odometerKillo + " M " + odometerMeter);
            editorPref.putInt(KEY_PULSE_COUNT, pulseCount);
            editorPref.putInt(KEY_ODOKILO_METER, odometerKillo);
            editorPref.putInt(KEY_ODO_METER, odometerMeter);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setOdometer: pulseCount > " + ""+pulseCount+ " : odometerKillo > " + ""+odometerKillo+ " : odometerMeter > " + ""+odometerMeter);
        } catch (Exception e) {
//            Log.e(TAG, "setOdometer: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setOdometer: " + e.getMessage());
        }
    }

    public int getOdoMeter() {
        return pref.getInt(KEY_ODO_METER, 0);
    }

    public int getOdoKiloMeter() {
        return pref.getInt(KEY_ODOKILO_METER, 0);
    }

    public int getStartOdoMeter() {
        return pref.getInt(KEY_START_ODO_METER, 0);
    }

    public int getStartOdoKiloMeter() {
        return pref.getInt(KEY_START_ODOKILO_METER, 0);
    }

    public int getEndOdoMeter() {
        return pref.getInt(KEY_END_ODO_METER, 0);
    }

    public int getEndOdoKiloMeter() {
        return pref.getInt(KEY_END_ODOKILO_METER, 0);
    }

    public void setOdometer(int odometerKillo, int odometerMeter) {
        try {
//            Log.e(TAG, "setOdometer: K " + odometerKillo + " M " + odometerMeter);
            editorPref.putInt(KEY_ODOKILO_METER, odometerKillo);
            editorPref.putInt(KEY_ODO_METER, odometerMeter);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setOdometer: odometerKillo > " + ""+odometerKillo+ " : odometerMeter > " + ""+odometerMeter);
        } catch (Exception e) {
//            Log.e(TAG, "setOdometer: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setOdometer: " + e.getMessage());
        }
    }

    public void setStartOdometer() {
        try {
            Log.e(TAG, "setStartOdometer: K ");
            editorPref.putInt(KEY_START_ODOKILO_METER, pref.getInt(KEY_ODOKILO_METER, 0));
            editorPref.putInt(KEY_START_ODO_METER, pref.getInt(KEY_ODO_METER, 0));
            editorPref.commit();
            MyLog.appendLog(TAG + " : setStartOdometer: KM > " + ""+pref.getInt(KEY_ODOKILO_METER, 0)+ " : Meter > " + ""+pref.getInt(KEY_ODO_METER, 0));
        } catch (Exception e) {
            Log.e(TAG, "setStartOdometer: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setStartOdometer: " + e.getMessage());
        }
    }

    public void setEndOdometer() {
        try {
            Log.e(TAG, "setEndOdometer: K ");
            editorPref.putInt(KEY_END_ODOKILO_METER, pref.getInt(KEY_ODOKILO_METER, 0));
            editorPref.putInt(KEY_END_ODO_METER, pref.getInt(KEY_ODO_METER, 0));
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEndOdometer: KM > " + ""+pref.getInt(KEY_ODOKILO_METER, 0)+ " : Meter > " + ""+pref.getInt(KEY_ODO_METER, 0));
        } catch (Exception e) {
            Log.e(TAG, "setEndOdometer: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setEndOdometer: " + e.getMessage());
        }
    }

    public float getFloatOdometer() {
        float kiloMeter = getOdoKiloMeter();
        float meter = getOdoMeter();
        if (meter > 0)
            meter = meter / 10;
        else
            meter = 0.0f;
        kiloMeter = kiloMeter + meter;
        return kiloMeter;
    }
    public double getDoubleOdometer() {
        double kiloMeter = getOdoKiloMeter();
        double meter = getOdoMeter();
        if (meter > 0)
            meter = meter / 10;
        else
            meter = 0.0;
        kiloMeter = kiloMeter + meter;

        return kiloMeter;
    }
    public float getFloatStartOdometer() {
        float kiloMeter = getStartOdoKiloMeter();
        float meter = getStartOdoMeter();
        if (meter > 0)
            meter = meter / 10;
        else
            meter = 0.0f;
        kiloMeter = kiloMeter + meter;
        return kiloMeter;
    }

    public float getFloatEndOdometer() {
        float kiloMeter = getEndOdoKiloMeter();
        float meter = getEndOdoMeter();
        if (meter > 0)
            meter = meter / 10;
        else
            meter = 0.0f;
        kiloMeter = kiloMeter + meter;
        return kiloMeter;
    }

    public void setTripDistatnce(float kiloMeter) {
        try {
            editorPref.putFloat(KEY_DISTANCE, kiloMeter);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setTripDistatnce: " + "Session : kiloMeter" + kiloMeter);
        }catch (Exception ex){
            MyLog.appendLog(TAG + " : Exception setTripDistatnce: " + ex.getMessage());
        }
    }

    public double getTripDistance() {
        // double kiloMeter = getFloatOdometer() - getFloatStartOdometer();
        double kiloMeter = pref.getFloat(KEY_DISTANCE, 0);
        Log.e(TAG, "getTripDistance: TRIP DIS " + kiloMeter);
        try {
            MyLog.appendLog(TAG + "getTripDistance: TRIP DIS " + kiloMeter);
        }catch (Exception ex){
            MyLog.appendLog(TAG + " : Exception getTripDistance: " + ex.getMessage());
        }
        return kiloMeter;
    }


    public void setExtraTripDistatnce(float kiloMeter) {
        try {
            editorPref.putFloat(KEY_EXTRA_DISTANCE, kiloMeter);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setExtraTripDistatnce: " + "Session : kiloMeter" + kiloMeter);
        }catch (Exception ex){
            MyLog.appendLog(TAG + " : Exception setExtraTripDistatnce: " + ex.getMessage());
        }
    }

    public double getExtraTripDistance() {
        // double kiloMeter = getFloatOdometer() - getFloatStartOdometer();
        double kiloMeter = pref.getFloat(KEY_EXTRA_DISTANCE, 0);
        Log.e(TAG, "getExtraTripDistance: TRIP DIS " + kiloMeter);
        try {
            MyLog.appendLog(TAG + "getExtraTripDistance: TRIP DIS " + kiloMeter);
        }catch (Exception ex){
            MyLog.appendLog(TAG + " : Exception getExtraTripDistance: " + ex.getMessage());
        }
        return kiloMeter;
    }

    public void resetWating() {
        MyLog.appendLog("Session : resetWating");
        editorPref.putInt(KEY_TRIP_WAT, 0);
        editorPref.commit();
    }

    public boolean isDay(){
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 6 && timeOfDay < 21){
            //Toast.makeText(this, "DAY", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            //Toast.makeText(this, "Night", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void setMinDistance(float minDistance) {
        try {
            editorPref.putFloat(KEY_MIN_DISTANCE, minDistance);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setminDistance: " + ""+minDistance);
        } catch (Exception e) {
            Log.e(TAG, "setPulseCount: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setMinDistance: " + e.getMessage());
        }
    }

    public double getMinDistance() {
        double minTripDistance = pref.getFloat(KEY_MIN_DISTANCE, 0);
        //Log.e(TAG, "getTripDistance: MIN TRIP DISTANCE " + minTripDistance);
        try {
          //  MyLog.appendLog(TAG + "getMinDistance: MIN TRIP DISTANCE " + minTripDistance);
        }catch (Exception ex){
            MyLog.appendLog(TAG + " : Exception getMinDistance: " + ex.getMessage());
        }
        return minTripDistance;
    }

    public void setStartingFare(float startingFare) {
        try {
            editorPref.putFloat(KEY_STARTING_FARE, startingFare);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setStartingFare: " + ""+startingFare);
        } catch (Exception e) {
            Log.e(TAG, "setStartingFare: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setStartingFare: " + e.getMessage());
        }
    }

    public double getStartingFare() {
        double startingFare = pref.getFloat(KEY_STARTING_FARE, 0);
//        Log.e(TAG, "getStartingFare: STARTING FARE " + startingFare);
        try {
//            MyLog.appendLog(TAG + "getStartingFare: STARTING FARE " + startingFare);
        }catch (Exception ex){
            MyLog.appendLog(TAG + " : Exception getStartingFare: " + ex.getMessage());
        }
        return startingFare;
    }

    public void setDataHub(boolean dataHub) {
        try {
            editorPref.putBoolean(KEY_DATA_HUB, dataHub);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setDataHub: " + ""+dataHub);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isDataHub() {
        return pref.getBoolean(KEY_DATA_HUB, false);
    }

    public void setHotelJob(boolean hotelJob) {
        try {
            editorPref.putBoolean(KEY_HOTEL_JOB, hotelJob);
            editorPref.commit();
            MyLog.appendLog(TAG + " : set Hotel Job: " + ""+hotelJob);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isHotelJob() {
        return pref.getBoolean(KEY_HOTEL_JOB, false);
    }


    public void setEmirate(boolean setEmirate1) {
        try {
            editorPref.putBoolean(KEY_EMIRATE, setEmirate1);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEmirate: " + ""+setEmirate1);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isEmirate() {
        return pref.getBoolean(KEY_EMIRATE, false);
    }

    public void setEmirate1(boolean setEmirate1) {
        try {
            editorPref.putBoolean(KEY_EMIRATE_1, setEmirate1);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEmirate1: " + ""+setEmirate1);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isEmirate1() {
        return pref.getBoolean(KEY_EMIRATE_1, false);
    }


    public void setEmirate2(boolean setEmirate2) {
        try {
            editorPref.putBoolean(KEY_EMIRATE_2, setEmirate2);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEmirate2: " + ""+setEmirate2);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isEmirate2() {
        return pref.getBoolean(KEY_EMIRATE_2, false);
    }


    public void setEmirate3(boolean setEmirate3) {
        try {
            editorPref.putBoolean(KEY_EMIRATE_3, setEmirate3);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEmirate3: " + ""+setEmirate3);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isEmirate3() {
        return pref.getBoolean(KEY_EMIRATE_3, false);
    }


    public void setEmirate1check(boolean setEmirate1) {
        try {
            editorPref.putBoolean(KEY_EMIRATE_1_CHECK, setEmirate1);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEmirate1check : " + ""+setEmirate1);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isEmirate1check() {
        return pref.getBoolean(KEY_EMIRATE_1_CHECK, false);
    }


    public void setEmirate2check(boolean setEmirate2) {
        try {
            editorPref.putBoolean(KEY_EMIRATE_2_CHECK, setEmirate2);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEmirate2check : " + ""+setEmirate2);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isEmirate2check() {
        return pref.getBoolean(KEY_EMIRATE_2_CHECK, false);
    }


    public void setEmirate3check(boolean setEmirate3) {
        try {
            editorPref.putBoolean(KEY_EMIRATE_3_CHECK, setEmirate3);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEmirate3check : " + ""+setEmirate3);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isEmirate3check() {
        return pref.getBoolean(KEY_EMIRATE_3_CHECK, false);
    }



    public void setToll(boolean setEmirate1) {
        try {
            editorPref.putBoolean(KEY_TOLL, setEmirate1);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll: " + ""+setEmirate1);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll() {
        return pref.getBoolean(KEY_TOLL, false);
    }

    public void setToll1(boolean toll1) {
        try {
            editorPref.putBoolean(KEY_TOLL_1, toll1);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll1: " + ""+toll1);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll1() {
        return pref.getBoolean(KEY_TOLL_1, false);
    }

    public void setToll2(boolean toll2) {
        try {
            editorPref.putBoolean(KEY_TOLL_2, toll2);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll2: " + ""+toll2);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll2() {
        return pref.getBoolean(KEY_TOLL_2, false);
    }



    public void setToll3(boolean toll3) {
        try {
            editorPref.putBoolean(KEY_TOLL_3, toll3);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll3: " + ""+toll3);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll3() {
        return pref.getBoolean(KEY_TOLL_3, false);
    }



    public void setToll4(boolean toll4) {
        try {
            editorPref.putBoolean(KEY_TOLL_4, toll4);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll4: " + ""+toll4);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll4() {
        return pref.getBoolean(KEY_TOLL_4, false);
    }


    public void setToll5(boolean toll5) {
        try {
            editorPref.putBoolean(KEY_TOLL_5, toll5);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll5: " + ""+toll5);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll5() {
        return pref.getBoolean(KEY_TOLL_5, false);
    }



    public void setToll6(boolean toll6) {
        try {
            editorPref.putBoolean(KEY_TOLL_6, toll6);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll6: " + ""+toll6);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll6() {
        return pref.getBoolean(KEY_TOLL_6, false);
    }



    public void setToll7(boolean toll7) {
        try {
            editorPref.putBoolean(KEY_TOLL_7, toll7);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll7: " + ""+toll7);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll7() {
        return pref.getBoolean(KEY_TOLL_7, false);
    }



    public void setToll8(boolean toll8) {
        try {
            editorPref.putBoolean(KEY_TOLL_8, toll8);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll8: " + ""+toll8);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll8() {
        return pref.getBoolean(KEY_TOLL_8, false);
    }



    public void setToll1check(boolean toll1check) {
        try {
            editorPref.putBoolean(KEY_TOLL_1_CHECK, toll1check);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll1check: " + ""+toll1check);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll1check() {
        return pref.getBoolean(KEY_TOLL_1_CHECK, false);
    }

    public void setToll2check(boolean toll2) {
        try {
            editorPref.putBoolean(KEY_TOLL_2_CHECK, toll2);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll2check: " + ""+toll2);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll2check() {
        return pref.getBoolean(KEY_TOLL_2_CHECK, false);
    }



    public void setToll3check(boolean toll3) {
        try {
            editorPref.putBoolean(KEY_TOLL_3_CHECK, toll3);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll3check: " + ""+toll3);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll3check() {
        return pref.getBoolean(KEY_TOLL_3_CHECK, false);
    }



    public void setToll4check(boolean toll4) {
        try {
            editorPref.putBoolean(KEY_TOLL_4_CHECK, toll4);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll4check: " + ""+toll4);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll4check() {
        return pref.getBoolean(KEY_TOLL_4_CHECK, false);
    }


    public void setToll5check(boolean toll5) {
        try {
            editorPref.putBoolean(KEY_TOLL_5_CHECK, toll5);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll5check: " + ""+toll5);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll5check() {
        return pref.getBoolean(KEY_TOLL_5_CHECK, false);
    }



    public void setToll6check(boolean toll6) {
        try {
            editorPref.putBoolean(KEY_TOLL_6_CHECK, toll6);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll6check: " + ""+toll6);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll6check() {
        return pref.getBoolean(KEY_TOLL_6_CHECK, false);
    }



    public void setToll7check(boolean toll7) {
        try {
            editorPref.putBoolean(KEY_TOLL_7_CHECK, toll7);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll7check: " + ""+toll7);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll7check() {
        return pref.getBoolean(KEY_TOLL_7_CHECK, false);
    }
    public void setToll8check(boolean toll7) {
        try {
            editorPref.putBoolean(KEY_TOLL_8_CHECK, toll7);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll7check: " + ""+toll7);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isToll8check() {
        return pref.getBoolean(KEY_TOLL_8_CHECK, false);
    }


    public void setEmirate1geo(String setEmirate1) {
        try {
            editorPref.putString(KEY_EMIRATE_1_GEO, setEmirate1);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEmirate1: " + ""+setEmirate1);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }




    public String getEmirate1geo() {
        return pref.getString(KEY_EMIRATE_1_GEO, "");
    }


    public void setEmirate2geo(String setEmirate2) {
        try {
            editorPref.putString(KEY_EMIRATE_2_GEO, setEmirate2);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEmirate2: " + ""+setEmirate2);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public String getEmirate2geo() {
        return pref.getString(KEY_EMIRATE_2_GEO, "");
    }


    public void setEmirate3geo(String setEmirate3) {
        try {
            editorPref.putString(KEY_EMIRATE_3_GEO, setEmirate3);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setEmirate3: " + ""+setEmirate3);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public String getEmirate3geo() {
        return pref.getString(KEY_EMIRATE_3_GEO, "");
    }



    public void setToll1geo(String toll1) {
        try {
            editorPref.putString(KEY_TOLL_1_GEO, toll1);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll1: " + ""+toll1);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public String getToll1geo() {
        return pref.getString(KEY_TOLL_1_GEO, "");
    }

    public void setToll2geo(String toll2) {
        try {
            editorPref.putString(KEY_TOLL_2_GEO, toll2);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll2: " + ""+toll2);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public String getToll2geo() {
        return pref.getString(KEY_TOLL_2_GEO, "");
    }



    public void setToll3geo(String toll3) {
        try {
            editorPref.putString(KEY_TOLL_3_GEO, toll3);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll3: " + ""+toll3);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public String getToll3geo() {
        return pref.getString(KEY_TOLL_3_GEO, "");
    }



    public void setToll4geo(String toll4) {
        try {
            editorPref.putString(KEY_TOLL_4_GEO, toll4);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll4: " + ""+toll4);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public String getToll4geo() {
        return pref.getString(KEY_TOLL_4_GEO, "");
    }


    public void setToll5geo(String toll5) {
        try {
            editorPref.putString(KEY_TOLL_5_GEO, toll5);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll5: " + ""+toll5);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public String getToll5geo() {
        return pref.getString(KEY_TOLL_5_GEO, "");
    }



    public void setToll6geo(String toll6) {
        try {
            editorPref.putString(KEY_TOLL_6_GEO, toll6);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll6: " + ""+toll6);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public String getToll6geo() { return pref.getString(KEY_TOLL_6_GEO, ""); }



    public void setToll7geo(String toll7) {
        try {
            editorPref.putString(KEY_TOLL_7_GEO, toll7);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setToll7: " + ""+toll7);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public String getToll7geo() {
        return pref.getString(KEY_TOLL_7_GEO, "");
    }



    public void setGeoResponse(String geoResponse) {
        try {
            editorPref.putString(KEY_GEO, geoResponse);
            editorPref.commit();
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public String getGeoResponse() {
        return pref.getString(KEY_GEO, "");
    }

    public void setExtraFare(int startingFare) {
        try {
            if(startingFare != 0)
                editorPref.putInt(KEY_EXTRA_FARE, 17);
            else
                editorPref.putInt(KEY_EXTRA_FARE, 0);
            editorPref.commit();
            MyLog.appendLog(TAG + " : setExtraFare: " + ""+startingFare);
        } catch (Exception e) {
            Log.e(TAG, "setExtraFare: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setExtraFare: " + e.getMessage());
        }
    }

    public int getExtraFare() {
        int startingFare = pref.getInt(KEY_EXTRA_FARE, 0);
        try {
            MyLog.appendLog(TAG + "getExtraFare: EXTRA FARE " + startingFare);
        }catch (Exception ex){
            MyLog.appendLog(TAG + " : Exception getExtraFare: " + ex.getMessage());
        }
        return startingFare;
    }

    public void setExtraTollFare(int startingFare) {
        try {
            editorPref.putInt(KEY_EXTRA_TOLL_FARE, startingFare);
            editorPref.commit();
            MyLog.appendLog(TAG + "SET TOLL FARE: " + ""+startingFare);
        } catch (Exception e) {
            Log.e(TAG, "setExtraFare: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setExtraFare: " + e.getMessage());
        }
    }

    public int getExtraTollFare() {
        int startingFare = pref.getInt(KEY_EXTRA_TOLL_FARE, 0);
        try {
            MyLog.appendLog(TAG + "EXTRA TOLL FARE " + startingFare);
        }catch (Exception ex){
            MyLog.appendLog(TAG + " : Exception getExtraFare: " + ex.getMessage());
        }
        return startingFare;
    }

    public int getGpsCounter() {
        return pref.getInt(KEY_GPS_COUNT, 1);
    }

    public void setGpsCounter(int gpsCounter) {
        try {
            Log.e(TAG, "setGpsCounter: "+gpsCounter);
            editorPref.putInt(KEY_GPS_COUNT, gpsCounter);
            editorPref.commit();
        } catch (Exception e) {
            Log.e(TAG, "setGpsCounter: "+e.getMessage());
        }
    }


    public void setOverSpeedCount(int pulseCount) {
        try {
            editorPref.putInt(KEY_OVERSPEED_COUNT, pulseCount);
            editorPref.commit();
//            MyLog.appendLog(TAG + " : setPulseCount: " + ""+pulseCount);
        } catch (Exception e) {
            Log.e(TAG, "setPulseCount: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setPulseCount: " + e.getMessage());
        }
    }

    public int getOverSpeedCount() {
        return pref.getInt(KEY_OVERSPEED_COUNT, 0);
    }


    public void setOverSpeed(boolean dataHub) {
        try {
            editorPref.putBoolean(KEY_OVERSPEED_FLAG, dataHub);
            editorPref.commit();
//            MyLog.appendLog(TAG + " : setDataHub: " + ""+dataHub);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isOverSpeed() {
        return pref.getBoolean(KEY_OVERSPEED_FLAG, false);
    }


    public void setMaxOverSpeedCount(int pulseCount) {
        try {
            editorPref.putInt(KEY_MAX_OVERSPEED_COUNT, pulseCount);
            editorPref.commit();
//            MyLog.appendLog(TAG + " : setPulseCount: " + ""+pulseCount);
        } catch (Exception e) {
            Log.e(TAG, "setPulseCount: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setPulseCount: " + e.getMessage());
        }
    }

    public int getMaxOverSpeedCount() {
        return pref.getInt(KEY_MAX_OVERSPEED_COUNT, 0);
    }


    public void setMaxOverSpeed(boolean dataHub) {
        try {
            editorPref.putBoolean(KEY_MAX_OVERSPEED_FLAG, dataHub);
            editorPref.commit();
//            MyLog.appendLog(TAG + " : setDataHub: " + ""+dataHub);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isMaxOverSpeed() {
        return pref.getBoolean(KEY_MAX_OVERSPEED_FLAG, false);
    }

    public void setPaymentType(String paymentType){
        try {
            editorPref.putString(KEY_PAYMENT_TYPE, paymentType);
            editorPref.commit();
        } catch (Exception e) {
            Log.e(TAG, "setJsonObject: " + e.getMessage());
        }
    }

    public String getPaymentType(){
        return pref.getString(KEY_PAYMENT_TYPE, "NA");
    }


    public void setNoSpeedCount(int noSpeedCount) {
        try {
            editorPref.putInt(KEY_NO_SPEED_COUNT, noSpeedCount);
            editorPref.commit();
//            MyLog.appendLog(TAG + " : setPulseCount: " + ""+noSpeedCount);
        } catch (Exception e) {
            Log.e(TAG, "setPulseCount: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setPulseCount: " + e.getMessage());
        }
    }

    public int getNoSpeedCount() {
        return pref.getInt(KEY_NO_SPEED_COUNT, 0);
    }

    public void setOverSpeedValue(int overSpeedValue) {
        try {
            editorPref.putInt(KEY_SPEED_VALUE, overSpeedValue);
            editorPref.commit();
//            MyLog.appendLog(TAG + " : setPulseCount: " + "" + overSpeedValue);
        } catch (Exception e) {
            Log.e(TAG, "setPulseCount: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setPulseCount: " + e.getMessage());
        }
    }

    public int getOverSpeedValue() {
        return pref.getInt(KEY_SPEED_VALUE, 60);
    }

    public void setMaxOverSpeedValue(int maxOverSpeedValue) {
        try {
            editorPref.putInt(KEY_MAX_SPEED_VALUE, maxOverSpeedValue);
            editorPref.commit();
//            MyLog.appendLog(TAG + " : setPulseCount: " + "" + maxOverSpeedValue);
        } catch (Exception e) {
            Log.e(TAG, "setPulseCount: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setPulseCount: " + e.getMessage());
        }
    }

    public int getMaxOverSpeedValue() {
        return pref.getInt(KEY_MAX_SPEED_VALUE, 80);
    }


    public void setSeat1(int seat1) {
        try {
            editorPref.putInt(KEY_SEAT_1, seat1);
            editorPref.commit();
//            Log.e(TAG, "setSeat1: "+seat1);
//            MyLog.appendLog(TAG + " : KEY_SEAT_1: " + seat1);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public int isSeat1() {
        return pref.getInt(KEY_SEAT_1, 0);
    }

    public void setSeat2(int seat2) {
        try {
            editorPref.putInt(KEY_SEAT_2, seat2);
            editorPref.commit();
//            Log.e(TAG, "setSeat2: "+seat2);
//            MyLog.appendLog(TAG + " : KEY_SEAT_2: " + seat2);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public int isSeat2() {
        return pref.getInt(KEY_SEAT_2, 0);
    }

    public void setSeat3(int seat3) {
        try {
            editorPref.putInt(KEY_SEAT_3, seat3);
            editorPref.commit();
//            Log.e(TAG, "setSeat3: "+seat3);
//            MyLog.appendLog(TAG + " : KEY_SEAT_3: " + seat3);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public int isSeat3() {
        return pref.getInt(KEY_SEAT_3, 0);
    }

    public void setSeat4(int seat4) {
        try {
            editorPref.putInt(KEY_SEAT_4, seat4);
            editorPref.commit();
//            Log.e(TAG, "setSeat4: "+seat4);
//            MyLog.appendLog(TAG + " : KEY_SEAT_4: " + seat4);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public int isSeat4() {
        return pref.getInt(KEY_SEAT_4, 0);
    }

    public void setSeat5(int seat5) {
        try {
            editorPref.putInt(KEY_SEAT_5, seat5);
            editorPref.commit();
//            Log.e(TAG, "setSeat5: "+seat5);
//            MyLog.appendLog(TAG + " : KEY_SEAT_5: " + seat5);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public int isSeat5() {
        return pref.getInt(KEY_SEAT_5, 0);
    }

    public void setSeat6(int seat6) {
        try {
            editorPref.putInt(KEY_SEAT_6, seat6);
            editorPref.commit();
//            Log.e(TAG, "setSeat6: "+seat6);
//            MyLog.appendLog(TAG + " : KEY_SEAT_6: " + seat6);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public int isSeat6() {
        return pref.getInt(KEY_SEAT_6, 0);
    }

    public void setSeat7(int seat7) {
        try {
            editorPref.putInt(KEY_SEAT_7, seat7);
            editorPref.commit();
//            Log.e(TAG, "setSeat7: "+seat7);
//            MyLog.appendLog(TAG + " : KEY_SEAT_7: " + seat7);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public int isSeat7() {
        return pref.getInt(KEY_SEAT_7, 0);
    }

    public void setSeat8(int seat8) {
        try {
            editorPref.putInt(KEY_SEAT_8, seat8);
            editorPref.commit();
//            Log.e(TAG, "setSeat8: "+seat8);
//            MyLog.appendLog(TAG + " : KEY_SEAT_8: " + seat8);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public int isSeat8() {
        return pref.getInt(KEY_SEAT_8, 0);
    }


    public void setPassengerCount() {
        try {
            int count = 0;
            if(isSeat1() == 1)
                count = count + 1;
            if(isSeat2() == 1)
                count = count + 1;
            if(isSeat3() == 1)
                count = count + 1;
            if(isSeat4() == 1)
                count = count + 1;
            if(isSeat5() == 1)
                count = count + 1;
            if(isSeat6() == 1)
                count = count + 1;
            if(isSeat7() == 1)
                count = count + 1;
            if(isSeat8() == 1)
                count = count + 1;

            editorPref.putInt(KEY_SEAT_COUNT, count);
            editorPref.commit();
//            MyLog.appendLog(TAG + " : KEY_SEAT_COUNT: " + "" + count);
        } catch (Exception e) {
            Log.e(TAG, "setPulseCount: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setPulseCount: " + e.getMessage());
        }
    }

    public int getPassengerCount() {
        setPassengerCount();

        return pref.getInt(KEY_SEAT_COUNT, 0);
    }


    public void setPower(boolean dataHub) {
        try {
            editorPref.putBoolean(KEY_POWER, dataHub);
            editorPref.commit();
//            MyLog.appendLog(TAG + " : KEY_POWER : " + ""+dataHub);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public boolean isPower() {
        return pref.getBoolean(KEY_POWER, false);
    }



    public void setIgnition(int ignition) {
        try {
            editorPref.putInt(KEY_IGNITION,ignition);
            editorPref.commit();
            Log.e(TAG, "setSeatIgnition: "+ignition);
//            MyLog.appendLog(TAG + " : KEY_IGNITION : " + ignition);
        } catch (Exception e) {
            Log.e(TAG, "setTrip: " + e.getMessage());
            MyLog.appendLog(TAG + " : Exception setTrip: " + e.getMessage());
        }
    }

    public int isIgnition() {
        return pref.getInt(KEY_IGNITION, 0);
    }
}