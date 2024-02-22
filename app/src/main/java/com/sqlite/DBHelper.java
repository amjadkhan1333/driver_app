package com.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.infodispatch.GetDeviceTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    public String DEBUG_TAG = "[DBHelper]";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Info_Dispatch.db";
    ArrayList<Integer> counts;
    public static String JOB_COUNTER= "CREATE TABLE JOB_COUNTER"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "JOB_COUNT TEXT,"
            + "JOB_ID TEXT)";

    public static String SETTINGS_TABLE = "CREATE TABLE SETTINGS_TBL"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "LANGUAGE_SETTINGS TEXT,SOUND_SETTINGS TEXT)";
    public static String CLIENT_TABLE = "CREATE TABLE CLIENT_TBL"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "CLIENT_URL TEXT," +//1
            "POLLING_INTERVAL TEXT," +//2
            "CLIENT_NAME TEXT," +//3
            "POLLING_URL TEXT," +//4
            "CLIENT_IMG TEXT," +//5
            "IMEI_NO TEXT)";//6
    public static String DRIVER_PROFILE_TABLE = "CREATE TABLE DRIVER_PROFILE_TBL"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "DRIVER_NAME TEXT," +
            "PHONE_NO TEXT," +
            "DRIVER_ID TEXT," +
            "CAB_TYPE TEXT," +
            "VEHICLE_STATUS TEXT,"+
            "CREDIT_BALANCE TEXT," +
            "WALLET TEXT," +
            "PAYABLE_AMT TEXT," +
            "DRIVER_EARNINGS TEXT," +
            "DRIVER_RATINGS TEXT," +
            "INCIDENTS TEXT,"+
            "PERFORMANCE TEXT)";
    public static String MAIN_SCREEN_DATA_TABLE = "CREATE TABLE MAIN_SCREEN_DATA_TBL"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "SHIFT_ID TEXT,"
            + "SHIFT_DIST TEXT,"
            + "LOGIN_DATE_TIME TEXT,"
            + "COMPLETED_JOBS TEXT,"
            + "CANCELLED_JOBS TEXT,"
            + "EARNED_TIME TEXT,"
            + "TODAY_EARNINGS TEXT,"
            + "AVAILABLE_CREDIT_BAL TEXT,"
            + "AVAILABLE_WALLET_BAL TEXT)";

    public static String CURRENT_SCREEN= "CREATE TABLE CURRENT_SCREEN_TBL"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "CURRENT_PAGE TEXT)";
    public static String CALL_CENTER_CONFIG_TABLE = "CREATE TABLE CALL_CENTER_TABLE"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "FIXED_FARE TEXT,"
            + "BASE_KMS TEXT,"
            + "BASE_HOURS TEXT,"
            + "WAIT TEXT,"
            + "WAITING_FREE_MIN TEXT,"
            + "WAITING_RATE TEXT,"
            + "EXTRA_HOUR_RATE TEXT,"
            + "EXTRA_KM_RATE TEXT,"
            + "SERVICE_TAX TEXT,"
            + "TYPE_OF_TRIP TEXT)";
    public static String CONFIG_TABLE = "CREATE TABLE CONFIG_TBL"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "FIXED_FARE TEXT," +
            "BASE_KM TEXT," +
            "BASE_HOURS TEXT," +
            "EXTRA_KM_RATE TEXT," +
            "EXTRA_HOUR_RATE TEXT,"+
            "WAITING_FREE_MIN TEXT," +
            "WAITING_RATE TEXT," +
            "POLLING_INTERVAL TEXT," +
            "CLIENT_NAME TEXT," +
            "CURRENCY_TYPE TEXT," +
            "NIGHT_CHARGES_PERCENTAGE TEXT,"+
            "NIGHT_FARE_TIME_FROM TEXT," +
            "NIGHT_FARE_TIME_TO TEXT," +
            "INTERVAL_OF_ADS TEXT," +
            "ADS_STATUS TEXT," +
            "PAYMENT_GATEWAY TEXT," +
            "HOTSPOT_STATUS TEXT,"+
            "CREDIT_BALANCE TEXT,"+
            "WALLET_BALANCE TEXT,"+
            "DRIVER_ID TEXT,"+
            "AVAILABLE_BAL TEXT)";
    public static String JOB_TABLE = "CREATE TABLE JOB_TBL"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "JOB_TYPE TEXT," +
            "JOB_ID TEXT," +
            "PICKUP_LOC TEXT," +
            "DROP_LOC TEXT," +
            "NAME TEXT," +
            "MOBILE_NO TEXT," +
            "PICKUP_LAT TEXT," +
            "PICKUP_LONG TEXT," +
            "DROP_LAT TEXT," +
            "DROP_LONG TEXT," +
            "WAITING_TIME TEXT," +
            "TOTAL_TRIP_DIST TEXT," +
            "TOTAL_TRIP_DURATION TEXT," +
            "START_TIME TEXT,END_TIME TEXT," +
            "START_Date TEXT,END_Date TEXT," +
            "START_DATE_TIME TEXT," +
            "JOB_STATUS TEXT," +
            "TOTAL_BILL TEXT," +
            "PAYMENT_MODE TEXT," +
            "HOTSPOT_STATUS TEXT," +
            "CUSTOMER_PICKUP_TIME TEXT," +
            "HTML_BILL TEXT,"+
            "TYPE_OF_TRIP TEXT,"+
            "PROMO_CODE TEXT,"+
            "RUNNING_FARE TEXT,"+
            "START_GPS_OD TEXT)";

    public static String HISTORY_TABLE = "CREATE TABLE HISTORY_TBL"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "JOB_TYPE TEXT,JOB_ID TEXT,PICKUP_LOC TEXT,DROP_LOC TEXT,NAME TEXT,"
            + "MOBILE_NO TEXT,PICKUP_LAT TEXT,PICKUP_LONG TEXT,DROP_LAT TEXT,DROP_LONG TEXT,"
            + "WAITING_TIME TEXT,TOTAL_TRIP_DIST TEXT,TOTAL_TRIP_DURATION TEXT,"
            + "START_TIME TEXT,END_TIME TEXT,"
            + "START_Date TEXT,END_Date TEXT,"
            + "START_DATE_TIME TEXT,"
            + "JOB_STATUS TEXT,"
            + "TOTAL_BILL TEXT,PAYMENT_MODE TEXT," +
            "HOTSPOT_STATUS TEXT,created_date DATE DEFAULT CURRENT_DATE,created_time TIME DEFAULT CURRENT_TIMESTAMP)";
    public static String ACCEPTED_JOB_TABLE = "CREATE TABLE ACCEPTED_JOB_TBL"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "JOBID TEXT," +//1
            "PICUP_TIME TEXT," +//2
            "MOBILE_NO TEXT," +//3
            "CUSTOMER_NAME TEXT," +//4
            "PICKUP_LOC TEXT,"+//5
            "PICKUP_LAT TEXT," +//6
            "PICKUP_LON TEXT," +//7
            "DROP_LOC TEXT," +//8
            "DROP_LAT TEXT," +//9
            "DROP_LON TEXT," +//10
            "BASE_KM TEXT," +//11
            "BASE_HOURS TEXT," +//12
            "FIXED_FARE TEXT," +//13
            "EXTRA_KM_RATE TEXT," +//14
            "EXTRA_HOUR TEXT,"+//15
            "EXTRA_HOUR_RATE TEXT,"+//16
            "EXTRA_KM TEXT," +//17
            "WAITING_FREE_MIN TEXT," +//18
            "WAITING_RATE TEXT," +
            "BILLING_TYPE TEXT,"+
            "TYPE_OF_TRIP TEXT,"+
            "ETA TEXT,"+
            "DISTANCE TEXT)";
    public static String RSA_JOB_TABLE = "CREATE TABLE RSA_JOB_TBL"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "RSA_JOB_ID TEXT," +
            "NO_OF_SEATS TEXT," +
            "PICKUP_LOC TEXT," +
            "DROP_LOC TEXT," +
            "NAME TEXT," +
            "MOBILE_NO TEXT," +
            "PICKUP_LAT TEXT," +
            "PICKUP_LONG TEXT," +
            "DROP_LAT TEXT," +
            "DROP_LONG TEXT," +
            "TOTAL_TRIP_DIST TEXT," +
            "TOTAL_TRIP_DURATION TEXT," +
            "START_TIME TEXT,END_TIME TEXT," +
            "START_Date TEXT,END_Date TEXT," +
            "START_DATE_TIME TEXT," +
            "JOB_STATUS TEXT," +
            "TOTAL_BILL TEXT," +
            "PAYMENT_MODE TEXT," +
            "CUSTOMER_PICKUP_TIME TEXT," +
            "TYPE_OF_TRIP TEXT,"+
            "PROMO_CODE TEXT)";
    public static String CONFIG_DATA_TABLE = "CREATE TABLE CONFIG_DATA_TBL"+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "GOOGLE_API TEXT,"
            + "GOOGLE_API_KEY TEXT,"
            + "ARRIVE_DISTANCE TEXT,"
            + "MIN_BALANCE TEXT,"
            + "MAX_BALANCE TEXT,"
            + "ADVANCE_POPUP_TIME TEXT,"
            + "ADVANCE_POPUP_DURATION TEXT)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CONFIG_TABLE);
        db.execSQL(CURRENT_SCREEN);
        db.execSQL(JOB_COUNTER);
        db.execSQL(HISTORY_TABLE);
        db.execSQL(JOB_TABLE);
        db.execSQL(DRIVER_PROFILE_TABLE);
        db.execSQL(CLIENT_TABLE);
        db.execSQL(MAIN_SCREEN_DATA_TABLE);
        db.execSQL(CALL_CENTER_CONFIG_TABLE);
        db.execSQL(SETTINGS_TABLE);
        db.execSQL(CONFIG_DATA_TABLE);
        db.execSQL(ACCEPTED_JOB_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CONFIG_TBL");
        db.execSQL("DROP TABLE IF EXISTS JOB_COUNTER");
        db.execSQL("DROP TABLE IF EXISTS CURRENT_SCREEN_TBL");
        db.execSQL("DROP TABLE IF EXISTS MAIN_SCREEN_DATA_TBL");
        db.execSQL("DROP TABLE IF EXISTS HISTORY_TBL");
        db.execSQL("DROP TABLE IF EXISTS JOB_TBL");
        db.execSQL("DROP TABLE IF EXISTS CLIENT_TBL");
        db.execSQL("DROP TABLE IF EXISTS DRIVER_PROFILE_TBL");
        db.execSQL("DROP TABLE IF EXISTS CALL_CENTER_TABLE");
        db.execSQL("DROP TABLE IF EXISTS ACCEPTED_JOB_TBL");
        db.execSQL("DROP TABLE IF EXISTS CONFIG_DATA_TBL");
        db.execSQL("DROP TABLE IF EXISTS SETTINGS_TBL");
        onCreate(db);
    }
    public void insertClientDBValues(Map<String, String> map) {
        try{
            System.out.println("inside the CONFIG_TABLE insertData1");
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("POLLING_URL",map.get("pollingUrl"));
            values.put("POLLING_INTERVAL",map.get("PollingInterval"));
            values.put("CLIENT_IMG",map.get("clientImg"));
            values.put("CLIENT_NAME",map.get("ClientName"));
            values.put("CLIENT_URL",map.get("clientURL"));
            values.put("IMEI_NO", map.get("imeino"));
            db.insert("CLIENT_TBL", null, values);
            System.out.println("inside the CLIENT_TBL  insertData3");
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    public void updateClientDBValues(Map<String, String> map, int id) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("pollingUrl",map.get("pollingUrl"));
            values.put("PollingInterval",map.get("PollingInterval"));
            values.put("clientImg",map.get("clientImg"));
            values.put("ClientName",map.get("ClientName"));
            values.put("clientURL",map.get("clientURL"));
            db.update("CLIENT_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "update sqlite data");
        }
    }
    public  HashMap<String, String> getClientInfo() {
        HashMap<String, String> config_vals= new HashMap<String, String>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM CLIENT_TBL", null);
            while(c.moveToNext()) {
                config_vals.put("clientURL",c.getString(1));
                config_vals.put("PollingInterval",c.getString(2));
                config_vals.put("ClientName",c.getString(3));
                config_vals.put("pollingUrl",c.getString(4));
                config_vals.put("clientImg",c.getString(5));
                config_vals.put("imei",c.getString(6));
            }
            c.close();
        }
        catch(Exception e){

        }

        return config_vals;
    }
    public int getClient_Rec_Count() {
        int cnt=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM CLIENT_TABLE", null);
            cnt = c.getCount();
            c.close();
        } catch (Exception e) {

        }
        return cnt;
    }

    //-------------------------------INSERT THE VALUES TO THE TABLES--------------------------------------
    public void insertConfigValues(Map<String, String> map) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("FIXED_FARE", map.get("fixedFare"));
            values.put("BASE_KM", map.get("baseKms"));
            values.put("BASE_HOURS", map.get("baseHours"));
            values.put("EXTRA_KM_RATE", map.get("extraKmRate"));

            values.put("EXTRA_HOUR_RATE", map.get("extraHourRates"));
            values.put("WAITING_FREE_MIN", map.get("waitingFreeMinutes"));
            values.put("WAITING_RATE", map.get("waitingRate"));
            values.put("CURRENCY_TYPE", map.get("currencyType"));
            values.put("POLLING_INTERVAL", map.get("pollingInterval"));

            values.put("CLIENT_NAME", map.get("clientName"));
            values.put("NIGHT_CHARGES_PERCENTAGE", map.get("nightChargesinPercentage"));
            values.put("NIGHT_FARE_TIME_FROM", map.get("nightFareTimeFrom"));
            values.put("NIGHT_FARE_TIME_TO", map.get("nightFareTimeTo"));

            values.put("INTERVAL_OF_ADS", map.get("intervalOfAds"));
            values.put("ADS_STATUS", map.get("adsStatus"));
            values.put("PAYMENT_GATEWAY", map.get("paymentGatewayNeeded"));
            values.put("HOTSPOT_STATUS", map.get("hotSpotStatus"));

            values.put("CREDIT_BALANCE", map.get("creditBalance"));
            values.put("WALLET_BALANCE", map.get("walletBalance"));
            values.put("DRIVER_ID", map.get("driverId"));
            values.put("AVAILABLE_BAL", map.get("availableBalance"));
            //ADDED ON 14062020 - DHIRAJ
            //values.put("MIN_AMOUNT", map.get("minAmount"));
            //values.put("MIN_DISTANCE", map.get("minDistance"));

            db.insert("CONFIG_TBL", null, values);
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }

    }
    public void updateConfigValues(Map<String, String> map,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("FIXED_FARE", map.get("fixedFare"));
            values.put("BASE_KM", map.get("baseKms"));
            values.put("BASE_HOURS", map.get("baseHours"));
            values.put("EXTRA_KM_RATE", map.get("extraKmRate"));

            values.put("EXTRA_HOUR_RATE", map.get("extraHourRates"));
            values.put("WAITING_FREE_MIN", map.get("waitingFreeMinutes"));
            values.put("WAITING_RATE", map.get("waitingRate"));
            values.put("CURRENCY_TYPE", map.get("currencyType"));
            values.put("POLLING_INTERVAL", map.get("pollingInterval"));

            values.put("CLIENT_NAME", map.get("clientName"));
            values.put("NIGHT_CHARGES_PERCENTAGE", map.get("nightChargesinPercentage"));
            values.put("NIGHT_FARE_TIME_FROM", map.get("nightFareTimeFrom"));
            values.put("NIGHT_FARE_TIME_TO", map.get("nightFareTimeTo"));

            values.put("INTERVAL_OF_ADS", map.get("calculateOn"));
            values.put("ADS_STATUS", map.get("nightFareTimeTo"));
            values.put("PAYMENT_GATEWAY", map.get("serviceTaxPercentage"));
            values.put("HOTSPOT_STATUS", map.get("hotSpotStatus"));
            values.put("CREDIT_BALANCE", map.get("creditBalance"));
            values.put("WALLET_BALANCE", map.get("walletBalance"));
            values.put("DRIVER_ID", map.get("driverId"));
            values.put("AVAILABLE_BAL", map.get("availableBalance"));

            //values.put("MIN_AMOUNT", map.get("minAmount"));
            //values.put("MIN_DISTANCE", map.get("minDistance"));

            db.update("CONFIG_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }

    }
    public int getConfigValuesCount() {
        int cnt=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM CONFIG_TBL", null);
            cnt = c.getCount();
            c.close();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return cnt;
    }

    public  HashMap<String,String> getConfigData() {
        HashMap<String,String> configData = new HashMap<String,String>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM CONFIG_TBL", null);
            while(c.moveToNext())
            {
                configData.put("fixedFare",c.getString(1));
                configData.put("baseKms",c.getString(2));
                configData.put("baseHours",c.getString(3));
                configData.put("extraKmRate",c.getString(4));

                configData.put("extraHourRates",c.getString(5));
                configData.put("waitingFreeMinutes",c.getString(6));
                configData.put("waitingRate",c.getString(7));
                configData.put("pollingInterval",c.getString(8));
                configData.put("clientName",c.getString(9));
                configData.put("currencyType",c.getString(10));

                configData.put("nightChargesinPercentage",c.getString(11));
                configData.put("nightFareTimeFrom",c.getString(12));
                configData.put("nightFareTimeTo",c.getString(13));
                configData.put("intervalOfAds",c.getString(14));

                configData.put("adsStatus",c.getString(15));
                configData.put("paymentGateWay",c.getString(16));
                configData.put("hotspotStatus",c.getString(17));
                configData.put("creditBalance",c.getString(18));
                configData.put("walletBalance",c.getString(19));
                configData.put("driverId",c.getString(20));
                configData.put("availableBalance",c.getString(21));

//                configData.put("driverId",c.getString(22));
//                configData.put("availableBalance",c.getString(23));

                //configData.put("minAmount",c.getString(21));
                //configData.put("minDistance",c.getString(21));
            }
            c.close();
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return configData;
    }
    public void insetCallCenterDBValues(Map<String, String> map) {

        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("FIXED_FARE",map.get("fixedFare"));
            values.put("BASE_KMS",map.get("baseKms"));
            values.put("BASE_HOURS",map.get("baseHours"));
            values.put("WAIT",map.get("wait"));
            values.put("WAITING_FREE_MIN",map.get("waitingFreeMin"));
            values.put("WAITING_RATE",map.get("waitingRate"));
            values.put("EXTRA_HOUR_RATE",map.get("extraHourRates"));
            values.put("EXTRA_KM_RATE",map.get("extraKmRate"));
            values.put("SERVICE_TAX",map.get("serviceTax"));
            values.put("TYPE_OF_TRIP",map.get("typeofTrip"));
            db.insert("CALL_CENTER_TABLE", null, values);
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    public void updateCallCenterDBValues(Map<String, String> map, int id) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("FIXED_FARE",map.get("fixedFare"));
            values.put("BASE_KMS",map.get("baseKms"));
            values.put("BASE_HOURS",map.get("baseHours"));
            values.put("WAIT",map.get("wait"));
            values.put("WAITING_FREE_MIN",map.get("waitingFreeMin"));
            values.put("WAITING_RATE",map.get("waitingRate"));
            values.put("EXTRA_HOUR_RATE",map.get("extraHourRates"));
            values.put("EXTRA_KM_RATE",map.get("extraKmRate"));
            values.put("SERVICE_TAX",map.get("serviceTax"));
            values.put("TYPE_OF_TRIP",map.get("typeofTrip"));
            db.update("CALL_CENTER_TABLE", values, "id = ? ", new String[] { Integer.toString(id) } );
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "update sqlite data");
        }
    }
    public int getCallCenterRecordsCount() {
        int cnt=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM CALL_CENTER_TABLE", null);
            cnt = c.getCount();
            c.close();
        } catch (Exception e) {
        }
        return cnt;
    }
    public  HashMap<String, String> getCallCenterConfigInfo() {
        HashMap<String, String> config_vals=null;
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM CALL_CENTER_TABLE", null);
            while(c.moveToNext())
            {
                config_vals= new HashMap<String, String>();
                config_vals.put("fixedFare", c.getString(1));
                config_vals.put("baseKms", c.getString(2));
                config_vals.put("baseHours", c.getString(3));
                config_vals.put("wait", c.getString(4));
                config_vals.put("waitingFreeMin",c.getString(5));
                config_vals.put("waitingRate", c.getString(6));
                config_vals.put("extraHourRates", c.getString(7));
                config_vals.put("extraKmRate", c.getString(8));
                config_vals.put("serviceTax", c.getString(9));
                config_vals.put("typeofTrip", c.getString(10));
            }
            c.close();
        }
        catch(Exception e){

        }
        return config_vals;

    }
    //-------------------------------INSERT THE VALUES TO THE TABLES--------------------------------------
    public void insertCurrentJobDBValues(HashMap<String,String> map) {
        try{

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("JOB_TYPE",map.get("jobType"));
            values.put("JOB_ID",map.get("JobId"));
            values.put("PICKUP_LOC",map.get("pickupLoc"));
            values.put("DROP_LOC",map.get("dropLoc"));
            values.put("NAME",map.get("name"));

            values.put("MOBILE_NO",map.get("mobileNo"));
            values.put("PICKUP_LAT",map.get("pickupLatitude"));
            values.put("PICKUP_LONG",map.get("pickupLongitude"));
            values.put("DROP_LAT",map.get("dropLatitude"));
            values.put("DROP_LONG",map.get("dropLongitude"));

            values.put("WAITING_TIME",map.get("waitingTime"));
            values.put("TOTAL_TRIP_DIST",map.get("totalTripDist"));
            values.put("TOTAL_TRIP_DURATION",map.get("totalTripDuration"));
            values.put("START_TIME",map.get("startTime"));
            values.put("END_TIME",map.get("endTime"));
            values.put("START_Date",map.get("startDate"));
            values.put("END_Date",map.get("endDate"));
            values.put("START_DATE_TIME",map.get("startDateTimes"));
            values.put("CUSTOMER_PICKUP_TIME",map.get("customerPickUpTime"));
            values.put("HTML_BILL",map.get("htmlBill"));
            values.put("TOTAL_BILL",map.get("totalBill"));
            values.put("PAYMENT_MODE",map.get("paymentMode"));
            values.put("HOTSPOT_STATUS",map.get("hotspotStatus"));
            values.put("JOB_STATUS",map.get("jobStatus"));
            values.put("TYPE_OF_TRIP",map.get("typeOfTrip"));
            values.put("PROMO_CODE",map.get("promoCode"));
            values.put("RUNNING_FARE",map.get("runningFare"));
            values.put("START_GPS_OD",map.get("startGpsOdo"));
            db.insert("JOB_TBL", null, values);
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
    public void updateCurrentJobValues(HashMap<String,String> map,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("JOB_TYPE",map.get("jobType"));
            values.put("JOB_ID",map.get("JobId"));
            values.put("PICKUP_LOC",map.get("pickupLoc"));
            values.put("DROP_LOC",map.get("dropLoc"));
            values.put("NAME",map.get("name"));

            values.put("MOBILE_NO",map.get("mobileNo"));
            values.put("PICKUP_LAT",map.get("pickupLatitude"));
            values.put("PICKUP_LONG",map.get("pickupLongitude"));
            values.put("DROP_LAT",map.get("dropLatitude"));
            values.put("DROP_LONG",map.get("dropLongitude"));

            values.put("WAITING_TIME",map.get("waitingTime"));
            values.put("TOTAL_TRIP_DIST",map.get("totalTripDist"));
            values.put("TOTAL_TRIP_DURATION",map.get("totalTripDuration"));
            values.put("START_TIME",map.get("startTime"));
            values.put("END_TIME",map.get("endTime"));
            values.put("START_Date",map.get("startDate"));
            values.put("END_Date",map.get("endDate"));
            values.put("START_DATE_TIME",map.get("startDateTimes"));

            values.put("TOTAL_BILL",map.get("totalBill"));
            values.put("RUNNING_FARE",map.get("runningFare"));
            values.put("PAYMENT_MODE",map.get("paymentMode"));
            values.put("JOB_STATUS",map.get("jobStatus"));
            values.put("HOTSPOT_STATUS",map.get("hotspotStatus"));
            values.put("CUSTOMER_PICKUP_TIME",map.get("customerPickUpTime"));
            values.put("HTML_BILL",map.get("htmlBill"));
            values.put("RUNNING_FARE",map.get("runningFare"));

            db.update("JOB_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    public void updateRunningCurrentJobValues(HashMap<String,String> map,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("JOB_TYPE",map.get("jobType"));
            values.put("JOB_ID",map.get("JobId"));
            values.put("PICKUP_LOC",map.get("pickupLoc"));
            values.put("DROP_LOC",map.get("dropLoc"));
            values.put("PICKUP_LAT",map.get("pickupLatitude"));
            values.put("PICKUP_LONG",map.get("pickupLongitude"));

            values.put("WAITING_TIME",map.get("waitingTime"));
            values.put("TOTAL_TRIP_DIST",map.get("totalTripDist"));
            values.put("TOTAL_TRIP_DURATION",map.get("totalTripDuration"));

            values.put("START_TIME",map.get("startTime"));
            values.put("START_Date",map.get("startDate"));
            values.put("START_DATE_TIME",map.get("startDateTimes"));
            values.put("TOTAL_BILL",map.get("totalBill"));

            //values.put("RUNNING_FARE",map.get("runningFare"));

            db.update("JOB_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
       public void updateRunningJobStatus(String jobStatus,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("JOB_STATUS",jobStatus);
            db.update("JOB_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    public void updateStartGpsOd(String startJpsOdo,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("START_GPS_OD",startJpsOdo);
            db.update("JOB_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
    public void deleteHistoryData(){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("DELETE FROM HISTORY_TBL WHERE created_date ="+"'"+GetDeviceTime.getFifiteenDate()+"'", null);
            int count =c.getCount();
            c.close();
            System.out.println("Deleted History Date=>"+count);
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }

    }
    public void updateHtmlBillTotalFare(String htmlBill,String totalBill,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("HTML_BILL",htmlBill);
            values.put("TOTAL_BILL",totalBill);
            db.update("JOB_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
    public void updatePromo(String promoCode,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("PROMO_CODE",promoCode);
            db.update("JOB_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
    public void updateCallCenterJobValues(HashMap<String,String> map,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("JOB_ID",map.get("JobId"));
            values.put("NAME",map.get("name"));
            values.put("MOBILE_NO",map.get("mobileNo"));

            values.put("PICKUP_LOC",map.get("pickupLoc"));
            values.put("PICKUP_LAT",map.get("pickupLat"));
            values.put("PICKUP_LONG",map.get("pickupLon"));
            values.put("CUSTOMER_PICKUP_TIME",map.get("customerPickUpTime"));

            values.put("DROP_LOC",map.get("dropLoc"));
            values.put("DROP_LAT",map.get("dropLat"));
            values.put("DROP_LONG",map.get("dropLon"));
            values.put("PAYMENT_MODE",map.get("paymentMode"));
            values.put("JOB_STATUS",map.get("jobStatus"));
            System.out.print("inside the DB jobStatus : "+map.get("jobStatus").toString() );
            db.update("JOB_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
    public  HashMap<String,String> getRunningJobData() {
        HashMap<String,String> configData = new HashMap<String,String>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM JOB_TBL", null);
            if(c.getCount()==0)
            {
            }
            while(c.moveToNext())
            {
                configData.put("jobType",c.getString(1));
                configData.put("JobId",c.getString(2));
                configData.put("pickupLoc",c.getString(3));
                configData.put("dropLoc",c.getString(4));

                configData.put("name",c.getString(5));
                configData.put("mobileNo",c.getString(6));
                configData.put("pickupLatitude",c.getString(7));
                configData.put("pickupLongitude",c.getString(8));
                configData.put("dropLatitude",c.getString(9));
                configData.put("dropLongitude",c.getString(10));

                configData.put("waitingTime",c.getString(11));
                configData.put("totalTripDist",c.getString(12));
                configData.put("totalTripDuration",c.getString(13));
                configData.put("startTime",c.getString(14));

                configData.put("endTime",c.getString(15));
                configData.put("startDate",c.getString(16));
                configData.put("endDate",c.getString(17));

                configData.put("tripStartDateTime",c.getString(18));
                configData.put("jobStatus",c.getString(19));
                configData.put("totalBill",c.getString(20));
                configData.put("paymentMode",c.getString(21));
                configData.put("hotspotStatus",c.getString(22));
                configData.put("customerPickUpTime",c.getString(23));
                configData.put("htmlBill",c.getString(24));
                configData.put("promoCode",c.getString(26));
                configData.put("runningFare",c.getString(27));
                configData.put("startGpsOdo",c.getString(28));
            }
            c.close();
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return configData;
    }

    public int getCurrentJobDBValuesCount() {
        int cnt=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM JOB_TBL", null);
            cnt = c.getCount();
            c.close();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return cnt;
    }
    public void insertHistoryData(HashMap<String,String> map) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("JOB_TYPE",map.get("jobType"));
            values.put("JOB_ID",map.get("JobId"));
            values.put("PICKUP_LOC",map.get("pickupLoc"));
            values.put("DROP_LOC",map.get("dropLoc"));
            values.put("NAME",map.get("name"));

            values.put("MOBILE_NO",map.get("mobileNo"));
            values.put("PICKUP_LAT",map.get("pickupLatitude"));
            values.put("PICKUP_LONG",map.get("pickupLongitude"));
            values.put("DROP_LAT",map.get("dropLatitude"));
            values.put("DROP_LONG",map.get("dropLongitude"));

            values.put("WAITING_TIME",map.get("waitingTime"));
            values.put("TOTAL_TRIP_DIST",map.get("totalTripDist"));
            values.put("TOTAL_TRIP_DURATION",map.get("totalTripDuration"));
            values.put("START_TIME",map.get("startTime"));
            values.put("END_TIME",map.get("endTime"));
            values.put("START_Date",map.get("startDate"));
            values.put("END_Date",map.get("endDate"));
            values.put("START_DATE_TIME",map.get("startDateTimes"));

            values.put("TOTAL_BILL",map.get("totalBill"));
            values.put("PAYMENT_MODE",map.get("paymentMode"));
            values.put("HOTSPOT_STATUS",map.get("hotspotStatus"));
            values.put("JOB_STATUS",map.get("jobStatus"));
            db.insert("HISTORY_TBL", null, values);
            deleteHistoryData();
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
    public void insertBidHistoryData(HashMap<String,String> map) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("JOB_ID",map.get("JobId"));
            values.put("TOTAL_BILL",map.get("totalBill"));
            values.put("JOB_STATUS",map.get("jobStatus"));
            values.put("PICKUP_LOC",map.get("pickupLoc"));
            values.put("DROP_LOC",map.get("dropLoc"));
            values.put("START_TIME",map.get("startTime"));
            values.put("END_TIME",map.get("endTime"));
            values.put("START_Date",map.get("startDate"));
            values.put("END_Date",map.get("endDate"));
            values.put("TOTAL_TRIP_DIST",map.get("totalTripDist"));
            values.put("TOTAL_TRIP_DURATION",map.get("totalTripDuration"));
            values.put("JOB_STATUS",map.get("jobStatus"));
            db.insert("HISTORY_TBL", null, values);
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
       public ArrayList<HashMap<String, String>>  getHistoryData(String today) {
           HashMap<String,String> configData;
           ArrayList<HashMap<String, String>> company_list = new ArrayList<HashMap<String, String>>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=null;

            if(today.equals("Today")){
                c=db.rawQuery("SELECT * FROM HISTORY_TBL where created_date="+"'"+GetDeviceTime.getForDbDate()+"'", null);
            }
            else if(today.equals("Yest")){
                c=db.rawQuery("SELECT * FROM HISTORY_TBL where created_date="+"'"+GetDeviceTime.getYestardayDate()+"'", null);
            }
            else if(today.equals("ThisWeek")){
                c=db.rawQuery("SELECT * FROM HISTORY_TBL where created_date BETWEEN '" + GetDeviceTime.getThisWeekDate() +"' AND '" + GetDeviceTime.getForDbDate()  +"'", null);
            }
            else if(today.equals("LastWeek")){
                c= db.rawQuery("SELECT * FROM HISTORY_TBL where created_date BETWEEN '" + GetDeviceTime.getLastWeekDate() +"' AND '" + GetDeviceTime.getThisWeekDate()  +"'", null);
            }
            else{
                c=db.rawQuery("SELECT * FROM HISTORY_TBL where created_date >= "+"'"+GetDeviceTime.getFifiteenDate()+"'", null);
            }
            while(c.moveToNext()) {
                configData = new HashMap<String,String>();
                configData.put("jobType",c.getString(1));
                configData.put("JobId",c.getString(2));
                configData.put("pickupLoc",c.getString(3));
                configData.put("dropLoc",c.getString(4));

                configData.put("name",c.getString(5));
                configData.put("mobileNo",c.getString(6));
                configData.put("pickupLatitude",c.getString(7));
                configData.put("pickupLongitude",c.getString(8));
                configData.put("dropLatitude",c.getString(9));
                configData.put("dropLongitude",c.getString(10));

                configData.put("waitingTime",c.getString(11));
                configData.put("totalTripDist",c.getString(12));
                configData.put("totalTripDuration",c.getString(13));
                configData.put("startTime",c.getString(14));

                configData.put("endTime",c.getString(15));
                configData.put("startDate",c.getString(16));
                configData.put("endDate",c.getString(17));

                configData.put("tripStartDateTime",c.getString(18));
                configData.put("jobStatus",c.getString(19));
                configData.put("totalBill",c.getString(20));
                configData.put("paymentMode",c.getString(21));
                configData.put("hotspotStatus",c.getString(22));
                configData.put("createdDate",c.getString(23));
                configData.put("createTime",c.getString(24));

                company_list.add(configData);
            }
            c.close();
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return company_list;
    }

    public ArrayList<Integer> getHistoryDataCount(){
        counts =new ArrayList<Integer>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor today=db.rawQuery("SELECT * FROM HISTORY_TBL where created_date="+"'"+GetDeviceTime.getForDbDate()+"'", null);
            counts.add(today.getCount());
            today.close();

            Cursor yestarday=db.rawQuery("SELECT * FROM HISTORY_TBL where created_date="+"'"+GetDeviceTime.getYestardayDate()+"'", null);
            counts.add(yestarday.getCount());
            yestarday.close();

            Cursor lastSeven=db.rawQuery("SELECT * FROM HISTORY_TBL where created_date BETWEEN '" + GetDeviceTime.getThisWeekDate() +"' AND '" + GetDeviceTime.getForDbDate()  +"'", null);
            counts.add(lastSeven.getCount());
            lastSeven.close();

            Cursor last14=db.rawQuery("SELECT * FROM HISTORY_TBL where created_date BETWEEN '" + GetDeviceTime.getLastWeekDate() +"' AND '" + GetDeviceTime.getThisWeekDate()  +"'", null);
            counts.add(last14.getCount());
            last14.close();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        System.out.println("Inside the get history data"+counts);
        return counts;

    }
    public void insertDriverProfileData(HashMap<String,String> driverProfileData) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("DRIVER_NAME", driverProfileData.get("driverName"));
            values.put("PHONE_NO", driverProfileData.get("phoneNo"));
            values.put("DRIVER_ID", driverProfileData.get("driverId"));
            values.put("CAB_TYPE",driverProfileData.get("cabType"));
            values.put("VEHICLE_STATUS", driverProfileData.get("vehicleStatus"));
            values.put("CREDIT_BALANCE", driverProfileData.get("creditBalance"));
            values.put("WALLET", driverProfileData.get("wallet"));
            values.put("PAYABLE_AMT", driverProfileData.get("payableAmount"));
            values.put("DRIVER_EARNINGS",driverProfileData.get("driverEarnings"));
            values.put("DRIVER_RATINGS", driverProfileData.get("driverRatings"));
            values.put("INCIDENTS", driverProfileData.get("incidents"));
            values.put("PERFORMANCE",driverProfileData.get("performance"));
            db.insert("DRIVER_PROFILE_TBL", null, values);

        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }

    }

    public void updateDriverProfileData(Map<String, String> driverProfileData,int id) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("DRIVER_NAME", driverProfileData.get("driverName"));
            values.put("PHONE_NO", driverProfileData.get("phoneNo"));
            values.put("DRIVER_ID", driverProfileData.get("driverId"));
            values.put("CAB_TYPE",driverProfileData.get("cabType"));
            values.put("VEHICLE_STATUS", driverProfileData.get("vehicleStatus"));
            values.put("CREDIT_BALANCE", driverProfileData.get("creditBalance"));
            values.put("WALLET", driverProfileData.get("wallet"));
            values.put("PAYABLE_AMT", driverProfileData.get("payableAmount"));
            values.put("DRIVER_EARNINGS",driverProfileData.get("driverEarnings"));
            values.put("DRIVER_RATINGS", driverProfileData.get("driverRatings"));
            values.put("INCIDENTS", driverProfileData.get("incidents"));
            values.put("PERFORMANCE",driverProfileData.get("performance"));
            db.update("DRIVER_PROFILE_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );

        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    public HashMap<String, String> getDriverProfileData() {
        HashMap<String,String> driverProfileData=new HashMap<String, String>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM DRIVER_PROFILE_TBL", null);
            if(c.getCount()==0)
            {

            }
            while(c.moveToNext()) {

            }
            c.close();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return driverProfileData;
    }
    public int getDriverProfileDataCount() {
        int cnt=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM DRIVER_PROFILE_TBL", null);
            cnt = c.getCount();
            c.close();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return cnt;
    }

    public void insertMainScreenData(String loginDateTime,String completedJobs,
                                     String cancelledJobs,String earnedTime,
                                     String todayEarnings,String shiftID,String shiftDist,String availableCreditBal,String availableWalletBal) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("SHIFT_ID", shiftID);
            values.put("SHIFT_DIST", shiftDist);
            values.put("LOGIN_DATE_TIME", loginDateTime);
            values.put("COMPLETED_JOBS", completedJobs);
            values.put("CANCELLED_JOBS", cancelledJobs);
            values.put("EARNED_TIME",earnedTime);
            values.put("TODAY_EARNINGS", todayEarnings);
            values.put("AVAILABLE_CREDIT_BAL",availableCreditBal);
            values.put("AVAILABLE_WALLET_BAL",availableWalletBal);
            db.insert("MAIN_SCREEN_DATA_TBL", null, values);

        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }

    }

    public void updateMainScreenData(String completedJobs,String cancelledJobs,String earnedTime,
                                     String todayEarning,String availableCreditBal,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("COMPLETED_JOBS", completedJobs);
            values.put("CANCELLED_JOBS", cancelledJobs);
            values.put("EARNED_TIME", earnedTime);
            values.put("TODAY_EARNINGS", todayEarning);
            values.put("AVAILABLE_CREDIT_BAL",availableCreditBal);
            db.update("MAIN_SCREEN_DATA_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );

        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    public HashMap<String, String> getMainScreenData() {
        HashMap<String, String> main_screen_data = new HashMap<String, String>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM MAIN_SCREEN_DATA_TBL", null);
            if(c.getCount()==0)
            {

            }
            while(c.moveToNext()) {
                main_screen_data.put("shiftId", c.getString(1));
                main_screen_data.put("shiftDist", c.getString(2));
                main_screen_data.put("loginDateTime",c.getString(3));
                main_screen_data.put("completedJobs",c.getString(4));
                main_screen_data.put("cancelledJobs",c.getString(5));
                main_screen_data.put("earnedTime",c.getString(6));
                main_screen_data.put("todayEarnings",c.getString(7));
                main_screen_data.put("availableCreditBal",c.getString(8));
                main_screen_data.put("availableWallettBal",c.getString(9));
            }
            c.close();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return main_screen_data;
    }
    public int getMainScreenDataCount() {
        int cnt=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM MAIN_SCREEN_DATA_TBL", null);
            cnt = c.getCount();
            c.close();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return cnt;
    }
    public int getShiftId(){
        int val=0;
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM MAIN_SCREEN_DATA_TBL", null);
            if(c.getCount()==0)
            {

            }
            while(c.moveToNext())
            {
                val=c.getInt(1);
            }
            c.close();
        }
        catch(Exception e){
            System.out.println("Inside the exception"+e);
        }
        return val;
    }
    public void updateShitIdLoginTime(String shiftCount,String datetime,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("SHIFT_ID", shiftCount);
            values.put("LOGIN_DATE_TIME", datetime);
            db.update("MAIN_SCREEN_DATA_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );

        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    public void updateShiftData(String shiftDist,String completedJobs,String cancelledJobs,
                                String earnedTime,String todayEarnings,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("SHIFT_DIST", shiftDist);
            values.put("COMPLETED_JOBS", completedJobs);
            values.put("CANCELLED_JOBS", cancelledJobs);
            values.put("EARNED_TIME", earnedTime);
            values.put("TODAY_EARNINGS", todayEarnings);
            db.update("MAIN_SCREEN_DATA_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );

        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
    public void updateCreditBal(String availableCredit,String walletBalance,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("AVAILABLE_CREDIT_BAL", availableCredit);
            values.put("AVAILABLE_WALLET_BAL", walletBalance);
            db.update("MAIN_SCREEN_DATA_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );

        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
    public void updateConfigData(String availableCredit,String walletBalance,String availableBal,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("CREDIT_BALANCE", availableCredit);
            values.put("WALLET_BALANCE", walletBalance);
            values.put("AVAILABLE_BAL", availableBal);
            db.update("CONFIG_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );

        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    public void insetCurrentScreenVal(String val) {
        try{
            System.out.println("inside the CONFIG_TABLE insertData1");
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("CURRENT_PAGE",val);
            db.insert("CURRENT_SCREEN_TBL", null, values);

            System.out.println("Inside the Current Screen Val=>"+val);
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    public void updateCurrentScreenVal(String val, int id) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("CURRENT_PAGE",val);
            db.update("CURRENT_SCREEN_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
            System.out.println("Inside the Current Screen Val=>"+val);
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "update sqlite data");
        }
    }
    public String getCurrentScreenVal(){
        String val="";
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM CURRENT_SCREEN_TBL", null);
            if(c.getCount()==0)
            {

            }
            while(c.moveToNext())
            {
                val=c.getString(1);
            }
            c.close();
        }
        catch(Exception e){
            System.out.println("Inside the exception"+e);
        }
        return val;
    }
    public int getCurrentScreenCount() {
        int cnt=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM CURRENT_SCREEN_TBL", null);
            cnt = c.getCount();
            c.close();
            System.out.println("****DBHelper** CLIENT_TABLE ** cnt : "+cnt);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return cnt;
    }

    public void insetJobCounterVal(int val,String k) {
        try{

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("JOB_COUNT",val);
            values.put("JOB_ID",k);
            db.insert("JOB_COUNTER", null, values);

        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    public void updateJobCounterVal(int val,String k,int id) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("JOB_COUNT",val);
            values.put("JOB_ID",k);
            db.update("JOB_COUNTER", values, "id = ? ", new String[] { Integer.toString(id) } );
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "update sqlite data");
        }
    }
    public int getJobCounterCount() {
        int cnt=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM JOB_COUNTER", null);
            cnt = c.getCount();
            c.close();
            System.out.println("****DBHelper** CLIENT_TABLE ** cnt : "+cnt);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return cnt;
    }
    public int getJobCountVal(){
        int val=0;
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM JOB_COUNTER", null);
            if(c.getCount()==0)
            {

            }
            while(c.moveToNext())
            {
                val=c.getInt(1);
            }
            c.close();
        }
        catch(Exception e){
            System.out.println("Inside the exception"+e);
        }
        return val;
    }
    public String getJobCountID(){
        String val="0";
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM JOB_COUNTER", null);
            if(c.getCount()==0)
            {

            }
            while(c.moveToNext())
            {
                val=c.getString(2);
            }
            c.close();
        }
        catch(Exception e){
            System.out.println("Inside the exception"+e);
        }
        return val;
    }
    public void insertSettingsDBValues(String lang_settings,String sound_settings) {
        try{
            System.out.println("inside the SETTINGS_TBL insertData1");
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("LANGUAGE_SETTINGS",lang_settings);
            values.put("SOUND_SETTINGS",sound_settings);

            db.insert("SETTINGS_TBL", null, values);
            System.out.println("inside the SETTINGS_TBL  insertData3");
        }
        catch(Exception e){
            System.out.println("getSettings_rec_Count=>");
        }
    }

    public void updateSettingsDBValues(String langVal,String soundVal, int id) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("LANGUAGE_SETTINGS",langVal);
            values.put("SOUND_SETTINGS",soundVal);
            db.update("SETTINGS_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        }
        catch(Exception e){
            System.out.println("getSettings_rec_Count=>");
        }
    }
    public  HashMap<String, String> getSettingsInfo() {
        HashMap<String, String> config_vals= new HashMap<String, String>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM SETTINGS_TBL", null);
            if(c.getCount()==0)
            {

            }
            while(c.moveToNext())
            {
                config_vals.put("lang_settings",c.getString(1));
                config_vals.put("sound_settings",c.getString(2));
            }
            c.close();
        }
        catch(Exception e){
            System.out.println("getSettings_rec_Count=>");
        }


        return config_vals;
    }
    public int getSettings_rec_Count() {
        int cnt=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM SETTINGS_TBL", null);
            cnt = c.getCount();
            c.close();
        } catch (Exception e) {
            System.out.println("getSettings_rec_Count=>");
        }
        return cnt;
    }
    public void insertAcceptedJobValues(Map<String, String> map) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("JOBID", map.get("jobId"));
            values.put("PICUP_TIME", map.get("pickUpTime"));
            values.put("MOBILE_NO", map.get("mobileNo"));
            values.put("CUSTOMER_NAME", map.get("customerName"));

            values.put("PICKUP_LOC", map.get("pickUpLoc"));
            values.put("PICKUP_LAT", map.get("pickUpLat"));
            values.put("PICKUP_LON", map.get("pickUpLon"));
            values.put("DROP_LOC", map.get("dropLoc"));
            values.put("DROP_LAT", map.get("dropLat"));

            values.put("DROP_LON", map.get("dropLon"));
            values.put("BASE_KM", map.get("baseKm"));
            values.put("BASE_HOURS", map.get("baseHours"));
            values.put("FIXED_FARE", map.get("fixedFare"));

            values.put("EXTRA_KM_RATE", map.get("extraKmRate"));
            values.put("EXTRA_HOUR", map.get("extraHour"));
            values.put("EXTRA_HOUR_RATE", map.get("extraHourRate"));
            values.put("EXTRA_KM", map.get("extraKm"));

            values.put("WAITING_FREE_MIN", map.get("waitingFreeMin"));
            values.put("WAITING_RATE", map.get("waitingRate"));
            values.put("BILLING_TYPE", map.get("billingType"));

            values.put("TYPE_OF_TRIP", map.get("typeofTrip"));
            values.put("ETA", map.get("eta"));
            values.put("DISTANCE", map.get("distance"));
            db.insert("ACCEPTED_JOB_TBL", null, values);
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }

    }
    public void updateAceeptedJobValues(Map<String, String> map,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("JOBID", map.get("jobId"));
            values.put("PICUP_TIME", map.get("pickUpTime"));
            values.put("MOBILE_NO", map.get("mobileNo"));
            values.put("CUSTOMER_NAME", map.get("customerName"));

            values.put("PICKUP_LOC", map.get("pickUpLoc"));
            values.put("PICKUP_LAT", map.get("pickUpLat"));
            values.put("PICKUP_LON", map.get("pickUpLon"));
            values.put("DROP_LOC", map.get("dropLoc"));
            values.put("DROP_LAT", map.get("dropLat"));

            values.put("DROP_LON", map.get("dropLon"));
            values.put("BASE_KM", map.get("baseKm"));
            values.put("BASE_HOURS", map.get("baseHours"));
            values.put("FIXED_FARE", map.get("fixedFare"));

            values.put("EXTRA_KM_RATE", map.get("extraKmRate"));
            values.put("EXTRA_HOUR", map.get("extraHour"));
            values.put("EXTRA_HOUR_RATE", map.get("extraHourRate"));
            values.put("EXTRA_KM", map.get("extraKm"));

            values.put("WAITING_FREE_MIN", map.get("waitingFreeMin"));
            values.put("WAITING_RATE", map.get("waitingRate"));
            values.put("BILLING_TYPE", map.get("billingType"));
            values.put("TYPE_OF_TRIP", map.get("typeofTrip"));
            values.put("ETA", map.get("eta"));
            values.put("DISTANCE", map.get("distance"));
            db.update("ACCEPTED_JOB_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }

    }
    public int getAcceptedJbValuesCount() {
        int cnt=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM ACCEPTED_JOB_TBL", null);
            cnt = c.getCount();
            c.close();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return cnt;
    }

    public  ArrayList<HashMap<String, String>> getAcceptedJobValues() {
        ArrayList<HashMap<String, String>> jobListData = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> jobData;

        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM ACCEPTED_JOB_TBL", null);
            if(c.getCount()==0)
            {
            }
            while(c.moveToNext())
            {
                jobData = new HashMap<String,String>();
                jobData.put("jobId",c.getString(1));
                jobData.put("pickUpTime",c.getString(2));
                jobData.put("mobileNo",c.getString(3));
                jobData.put("customerName",c.getString(4));
                jobData.put("pickUpLoc",c.getString(5));

                jobData.put("pickUpLat",c.getString(6));
                jobData.put("pickUpLon",c.getString(7));
                jobData.put("dropLoc",c.getString(8));
                jobData.put("dropLat",c.getString(9));
                jobData.put("dropLon",c.getString(10));

                jobData.put("baseKm",c.getString(11));
                jobData.put("baseHours",c.getString(12));
                jobData.put("fixedFare",c.getString(13));
                jobData.put("extraKmRate",c.getString(14));
                jobData.put("extraHour",c.getString(15));
                jobData.put("extraHourRate",c.getString(16));

                jobData.put("extraKm",c.getString(17));
                jobData.put("waitingFreeMin",c.getString(18));
                jobData.put("waitingRate",c.getString(19));
                jobData.put("billingType",c.getString(20));

                jobData.put("typeofTrip",c.getString(21));
                jobData.put("eta",c.getString(22));
                jobData.put("distance",c.getString(23));
                jobListData.add(jobData);
            }
            c.close();
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        System.out.println("inside DBHelper jobListData : "+jobListData);
        return jobListData;
    }

    public  HashMap<String,String> getOnJobValues(String JobID) {
        HashMap<String,String> onJobData = new HashMap<String,String>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            String Query = "Select * from ACCEPTED_JOB_TBL where JOBID = '" + JobID + "'";
            Cursor c = db.rawQuery(Query,null);

            if(c.getCount()==0)
            {
            }
            while(c.moveToNext())
            {
                onJobData.put("jobId",c.getString(1));
                onJobData.put("pickUpTime",c.getString(2));
                onJobData.put("mobileNo",c.getString(3));
                onJobData.put("customerName",c.getString(4));
                onJobData.put("pickUpLoc",c.getString(5));

                onJobData.put("pickUpLat",c.getString(6));
                onJobData.put("pickUpLon",c.getString(7));
                onJobData.put("dropLoc",c.getString(8));
                onJobData.put("dropLat",c.getString(9));
                onJobData.put("dropLon",c.getString(10));

                onJobData.put("baseKm",c.getString(11));
                onJobData.put("baseHours",c.getString(12));
                onJobData.put("fixedFare",c.getString(13));
                onJobData.put("extraKmRate",c.getString(14));
                onJobData.put("extraHour",c.getString(15));
                onJobData.put("extraHourRate",c.getString(16));

                onJobData.put("extraKm",c.getString(17));
                onJobData.put("waitingFreeMin",c.getString(18));
                onJobData.put("waitingRate",c.getString(19));
                onJobData.put("billingType",c.getString(20));

                onJobData.put("typeofTrip",c.getString(21));
                onJobData.put("eta",c.getString(22));
                onJobData.put("distance",c.getString(23));
            }
            c.close();
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return onJobData;
    }

    public void deleteAcceptedJobDetails(String jobId) {
        try {
            System.out.println("inside deleteAcceptedJobDetails jobId: " + jobId);
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("ACCEPTED_JOB_TBL", "JOBID" + " = ?",
                    new String[]{jobId});
            db.close();
        } catch (Exception e) {

        }
    }
    public void updateAdvancedCurrentJobDBValues(Map<String, String> map, int id) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("FIXED_FARE",map.get("fixedFare"));
            values.put("BASE_KMS",map.get("baseKms"));
            values.put("BASE_HOURS",map.get("baseHours"));
            values.put("WAIT",map.get("wait"));
            values.put("WAITING_FREE_MIN",map.get("waitingFreeMin"));
            values.put("WAITING_RATE",map.get("waitingRate"));
            values.put("EXTRA_HOUR_RATE",map.get("extraHourRates"));
            values.put("EXTRA_KM_RATE",map.get("extraKmRate"));
            //  values.put("SERVICE_TAX",map.get("serviceTax"));
            db.update("CONFIG_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "update sqlite data");
        }
    }
    public void updateAdvancedCurrentJobValues(HashMap<String,String> map,int id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("JOB_ID",map.get("JobId"));
            values.put("NAME",map.get("name"));
            values.put("MOBILE_NO",map.get("mobileNo"));

            values.put("PICKUP_LOC",map.get("pickupLoc"));
            values.put("PICKUP_LAT",map.get("pickupLat"));
            values.put("PICKUP_LONG",map.get("pickupLon"));
            values.put("CUSTOMER_PICKUP_TIME",map.get("customerPickUpTime"));
            System.out.println("inside updateAdvancedCurrentJobValues PickUpTime : "+map.get("customerPickUpTime"));
            values.put("DROP_LOC",map.get("dropLoc"));
            values.put("DROP_LAT",map.get("dropLat"));
            values.put("DROP_LONG",map.get("dropLon"));
            values.put("PAYMENT_MODE",map.get("paymentMode"));
            values.put("JOB_STATUS",map.get("jobStatus"));
            values.put("TYPE_OF_TRIP",map.get("typeofTrip"));

            db.update("JOB_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    //----------------------------RSA----------------------------------------------//

    public void insertRSAJobDBValues(HashMap<String,String> map) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("RSA_JOB_ID", map.get("JobId"));
            values.put("NO_OF_SEATS", map.get("noOfSeats"));
            values.put("PICKUP_LOC", map.get("pickupLoc"));
            values.put("DROP_LOC", map.get("dropLoc"));
            values.put("NAME", map.get("name"));
            values.put("MOBILE_NO", map.get("mobileNo"));
            values.put("PICKUP_LAT", map.get("pickupLatitude"));
            values.put("PICKUP_LONG", map.get("pickupLongitude"));
            values.put("DROP_LAT", map.get("dropLatitude"));
            values.put("DROP_LONG", map.get("dropLongitude"));
            values.put("TOTAL_TRIP_DIST", map.get("totalTripDist"));
            values.put("TOTAL_TRIP_DURATION", map.get("totalTripDuration"));
            values.put("START_TIME", map.get("startTime"));
            values.put("END_TIME", map.get("endTime"));
            values.put("START_Date", map.get("startDate"));
            values.put("END_Date", map.get("endDate"));
            values.put("START_DATE_TIME", map.get("startDateTimes"));
            values.put("CUSTOMER_PICKUP_TIME", map.get("customerPickUpTime"));
            values.put("TOTAL_BILL", map.get("totalBill"));
            values.put("PAYMENT_MODE", map.get("paymentMode"));
            values.put("JOB_STATUS", map.get("jobStatus"));
            values.put("TYPE_OF_TRIP", map.get("typeOfTrip"));
            db.insert("RSA_JOB_TBL", null, values);
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }

    public void updateRSAJobDBValues(HashMap<String,String> map) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("TOTAL_TRIP_DIST", map.get("totalTripDist"));
            values.put("TOTAL_TRIP_DURATION", map.get("totalTripDuration"));
            values.put("END_Date", map.get("endDate"));
            values.put("END_TIME", map.get("endTime"));
            values.put("TOTAL_BILL", map.get("totalBill"));
            values.put("JOB_STATUS", map.get("jobStatus"));
            db.insert("RSA_JOB_TBL", null, values);
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
    public String getIMEINo() {
        String val = "0";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM CLIENT_TABLE", null);
            if (c.getCount() == 0) {

            }
            while (c.moveToNext()) {
                val = c.getString(6);
            }
            c.close();
        } catch (Exception e) {
            System.out.println("Inside the exception" + e);
        }
        return val;
    }

    public void insertConfigDBValues(Map<String, String> map) {
        try{

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("GOOGLE_API",map.get("googleAPI"));
            values.put("GOOGLE_API_KEY",map.get("googleAPIKey"));
            values.put("ARRIVE_DISTANCE",map.get("arriveDistance"));
            values.put("MIN_BALANCE",map.get("minBalance"));
            values.put("MAX_BALANCE",map.get("maxBalance"));
            values.put("ADVANCE_POPUP_TIME", map.get("advancePopupTime"));
            values.put("ADVANCE_POPUP_DURATION", map.get("advPopupDuration"));
            //values.put("MIN_AMOUNT", map.get("minAmount"));
            //values.put("MIN_DISTANCE", map.get("minDistance"));
            db.insert("CONFIG_DATA_TBL", null, values);

        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
    public void updateConfigDBValues(Map<String, String> map,int id) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("GOOGLE_API",map.get("googleAPI"));
            values.put("GOOGLE_API_KEY",map.get("googleAPIKey"));
            values.put("ARRIVE_DISTANCE",map.get("arriveDistance"));
            values.put("MIN_BALANCE",map.get("minBalance"));
            values.put("MAX_BALANCE",map.get("maxBalance"));
            values.put("ADVANCE_POPUP_TIME", map.get("advancePopupTime"));
            values.put("ADVANCE_POPUP_DURATION", map.get("advPopupDuration"));
            db.update("CONFIG_DATA_TBL", values, "id = ? ", new String[] { Integer.toString(id) } );
        }
        catch(Exception e){
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
    }
    public HashMap<String, String> getConfigDBValues() {
        HashMap<String, String> main_screen_data = new HashMap<String, String>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM CONFIG_DATA_TBL", null);
            if(c.getCount()==0)
            {

            }
            while(c.moveToNext()) {
                main_screen_data.put("googleAPI", c.getString(1));
                main_screen_data.put("googleAPIKey", c.getString(2));
                main_screen_data.put("arriveDistance",c.getString(3));
                main_screen_data.put("minBalance",c.getString(4));
                main_screen_data.put("maxBalance",c.getString(5));
                main_screen_data.put("advancePopupTime",c.getString(6));
                main_screen_data.put("advPopupDuration",c.getString(7));
            }
            c.close();
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "insert sqlite data");
        }
        return main_screen_data;
    }
    public int getConfigDBValuesCount() {
        int cnt=0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT * FROM CONFIG_DATA_TBL", null);
            cnt = c.getCount();
            c.close();
        } catch (Exception e) {
        System.out.println("Inisde the SQLite=>"+e);
        }
        return cnt;
    }
}


