package com.settersgetters;

import android.util.Log;

/**
 * Created by AK on 12/19/2016.
 */

public class ConfigData {
    private static String TAG = "Config Data class";

    public static String clientName;
    public static String currencyType;

    public static String fixedFare = "0";

    public static double baseHours = 0.0;
    public static long baseKm = 0;

    public static double extraHourRate = 0.0;
    public static double extraKmRate = 0.0;

    public static int waitingFreeMin = 0;
    //public static int waitingRate = 0;
    public static double waitingRate = 0; // changed from int to double - Dhiraj - 13062020

    public static double startGPSOdo = 0.0;
    public static double endGPSOdo = 0.0;

    public static String startManualOdo = "0";
    public static String endManualOdo = "0";

    public static String pollingUrl;
    public static String pollingInterVal;
    public static String clientURL;
    public static String clientImageURL;
    public static String firmwareVersion = "ZITAndroTDS_6.0M";
    public static String buildDate = "14-06-2020";

    public static int deviceStatus;
    public static String driverId = "0";

    //ADDED TO HANDLE RUNNING FARE ON METER START FOR AJMAN TAXI
    public static double minDistance = 0;
    public static double minAmount = 0;

    public static String getDriverId() {
        return driverId;
    }
    public static void setDriverId(String driverId) {
        ConfigData.driverId = driverId;
    }
    public static int getDeviceStatus() {
        return deviceStatus;
    }

    public static void setDeviceStatus(int deviceStatus) {
        ConfigData.deviceStatus = deviceStatus;
    }

    public static String getPollingUrl() {
        return pollingUrl;
    }

    public static void setPollingUrl(String pollingUrl) {
        ConfigData.pollingUrl = pollingUrl;
    }

    public static String getPollingInterVal() {
        return pollingInterVal;
    }

    public static void setPollingInterVal(String pollingInterVal) {
        ConfigData.pollingInterVal = pollingInterVal;
    }

    public static String getClientURL() {
        return clientURL;
    }

    public static void setClientURL(String clientURL) {
        ConfigData.clientURL = clientURL;
    }

    public static String getClientImageURL() {
        return clientImageURL;
    }

    public static void setClientImageURL(String clientImageURL) {
        ConfigData.clientImageURL = clientImageURL;
    }

    public static String getClientName() {
        return clientName;
    }

    public static void setClientName(String clientName) {
        ConfigData.clientName = clientName;
    }
    public static String getCurrencyType() {
        return currencyType;
    }

    public static void setCurrencyType(String currencyType) {
        ConfigData.currencyType = currencyType;
    }
    public static String getFixedFare() {
        return fixedFare;
    }

    public static void setFixedFare(String fixedFare) {
        ConfigData.fixedFare = fixedFare;
    }
    
    public static double getBaseHours() {
        return baseHours;
    }

    public static void setBaseHours(double baseHours) {
        ConfigData.baseHours = baseHours;
    }

    public static long getBaseKm() {
        return baseKm;
    }

    public static void setBaseKm(long baseKm) {
        ConfigData.baseKm = baseKm;
    }

    public static double getExtraHourRate() {
        return extraHourRate;
    }

    public static void setExtraHourRate(double extraHourRate) {
        ConfigData.extraHourRate = extraHourRate;
    }

    public static double getExtraKmRate() {
        return extraKmRate;
    }

    public static void setExtraKmRate(double extraKmRate) {
        ConfigData.extraKmRate = extraKmRate;
    }

    public static int getWaitingFreeMin() {
        return waitingFreeMin;
    }

    public static void setWaitingFreeMin(int waitingFreeMin) {
        Log.e(TAG, "setWaitingFreeMin: "+waitingFreeMin);
        ConfigData.waitingFreeMin = waitingFreeMin;
    }

    public static double getWaitingRate() {
        return waitingRate;
    }

    public static void setWaitingRate(double waitingRate) {
        Log.e(TAG, "setWaitingRate: "+waitingRate );
        ConfigData.waitingRate = waitingRate;
    }

    public static double getStartGPSOdo() {
        return startGPSOdo;
    }

    public static void setStartGPSOdo(double startGPSOdo) {
        ConfigData.startGPSOdo = startGPSOdo;
    }

    public static double getEndGPSOdo() {
        return endGPSOdo;
    }

    public static void setEndGPSOdo(double endGPSOdo) {
        ConfigData.endGPSOdo = endGPSOdo;
    }

    public static String getStartManualOdo() {
        return startManualOdo;
    }

    public static void setStartManualOdo(String startManualOdo) {
        ConfigData.startManualOdo = startManualOdo;
    }

    public static String getEndManualOdo() {
        return endManualOdo;
    }

    public static void setEndManualOdo(String endManualOdo) {
        ConfigData.endManualOdo = endManualOdo;
    }

    public static double getMinDistance() {
        return minDistance;
    }

    public static void setMinDistance(double minDistance) {
        ConfigData.minDistance = minDistance;
    }

    public static double getMinAmount() {
        return minAmount;
    }

    public static void setMinAmount(double minAmount) {
        ConfigData.minAmount = minAmount;
    }

    public enum Device_Status{
        devicestatus_hired,//0
        devicestatus_free,
        devicestatus_oncall,
        devicestatus_logout,
        devicestatus_offered,
        devicestatus_download,
        devicestatus_downloadfail,
        devicestatus_Break,//
        devicestatus_paymentmode,//8
        devicestatus_RSA;//9
    }
}
