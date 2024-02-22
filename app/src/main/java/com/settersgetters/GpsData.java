package com.settersgetters;

import android.location.Location;

/**
 * Created by AK on 12/20/2016.
 */

public class GpsData {
    public static Location oldLocation;
    public static boolean canGetLocation = false;

    public static double latitude=0.0;
    public static double longitude=0.0;

    private static double gpsLatitude_Prev=0.0;
    private static double gpsLongitude_Prev=0.0;
    public static Double gpsOdometer=0.0;

    public static String startManualOdo = "0";
    public static String endManualOdo = "0";

    public static double startGPSOdo = 0.0;
    public static double endGPSOdo = 0.0;

    public static String gpsSpeed = "0";
    public static String gpsTime = "0";
    public static String gpsDirection = "0";
    public static String gsmStatus="";
    public static String gpsStatus="";
    public static int seatSensor;

    public static double testlatitude = 0.0;
    public static double testlongitude = 0.0;

    public static int emirateCharges = 0;
    public static int tollCharges = 0;

    public static int emirateID = 0;
    public static int tollID = 0;

    public static String getGpsStatus() {
        return gpsStatus;
    }

    public static void setGpsStatus(String gpsStatus) {
        GpsData.gpsStatus = gpsStatus;
    }

    public static String getGsmStatus() {
        return gsmStatus;
    }

    public static void setGsmStatus(String gsmStatus) {
        GpsData.gsmStatus = gsmStatus;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static String getGpsSpeed() {
        return gpsSpeed;
    }

    public static void setGpsSpeed(String gpsSpeed) {
        GpsData.gpsSpeed = gpsSpeed;
    }

    public static String getGpsTime() {
        return gpsTime;
    }

    public static void setGpsTime(String gpsTime) {
        GpsData.gpsTime = gpsTime;
    }

    public static String getGpsDirection() {
        return gpsDirection;
    }

    public static void setGpsDirection(String gpsDirection) {
        GpsData.gpsDirection = gpsDirection;
    }

    public static String getStartManualOdo() {
        return startManualOdo;
    }

    public static void setStartManualOdo(String startManualOdo) {
        GpsData.startManualOdo = startManualOdo;
    }

    public static String getEndManualOdo() {
        return endManualOdo;
    }

    public static void setEndManualOdo(String endManualOdo) {
        GpsData.endManualOdo = endManualOdo;
    }

    public static double getStartGPSOdo() {
        return startGPSOdo;
    }

    public static void setStartGPSOdo(double startGPSOdo) {
        GpsData.startGPSOdo = startGPSOdo;
    }

    public static double getEndGPSOdo() {
        return endGPSOdo;
    }

    public static void setEndGPSOdo(double endGPSOdo) {
        GpsData.endGPSOdo = endGPSOdo;
    }

    public static boolean isCanGetLocation() {
        return canGetLocation;
    }

    public static void setCanGetLocation(boolean canGetLocation) {
        GpsData.canGetLocation = canGetLocation;
    }

    public static double getLattiude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        GpsData.latitude = latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        GpsData.longitude = longitude;
    }

    public static double getGpsLatitude_Prev() {
        return gpsLatitude_Prev;
    }

    public static void setGpsLatitude_Prev(double gpsLatitude_Prev) {
        GpsData.gpsLatitude_Prev = gpsLatitude_Prev;
    }

    public static double getGpsLongitude_Prev() {
        return gpsLongitude_Prev;
    }

    public static void setGpsLongitude_Prev(double gpsLongitude_Prev) {
        GpsData.gpsLongitude_Prev = gpsLongitude_Prev;
    }

    public static Double getGpsOdometer() {
        return gpsOdometer;
    }

    public static void setGpsOdometer(Double gpsOdometer) {
        GpsData.gpsOdometer = gpsOdometer;
    }

    public static Location getOldLocation() {
        return oldLocation;
    }
    public static void setOldLocation(Location oldLocation) {

        GpsData.oldLocation = oldLocation;
    }

    public static int getSeatSensor() {
        return seatSensor;
    }

    public static void setSeatSensor(int seatSensor) {
        GpsData.seatSensor = seatSensor;
    }

    public static double getTestLatitude() {
        return testlatitude;
    }
    public static double getTestLongitude() {
        return testlongitude;
    }

    public static void setTestLocation(double lat,double lon){
        testlatitude = lat;
        testlongitude = lon;
    }


    public static int getEmirateCharges() {
        return emirateCharges;
    }

    public static int getTollCharges() {
        return tollCharges;
    }

    public static void setExtraCharges(int emirateCharge,int tollCharge){
        emirateCharges = emirateCharge;
        tollCharges = tollCharge;
    }


    public static int getEmirateID() {
        return emirateID;
    }

    public static int getTollID() {
        return tollID;
    }

    public static void setExtraID(int emirateIDs,int tollIDs){
        emirateID = emirateIDs;
        tollID = tollIDs;
    }
}
