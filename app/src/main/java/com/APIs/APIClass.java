package com.APIs;

import com.settersgetters.ConfigData;

public class APIClass {
   //public static String baseURL="http://api.AKtelematics.com/Rest_Android_App_Masters/Infodispatch.svc/ClientLogInRequest";
    //public static String baseURL="http://qatlimousine.AKtelematics.com/RestAPIinfodispatch/Infodispatch.svc/ClientLogInRequest";
//    public static String baseURL="http://tdsdemo.AKtelematics.com/RestAPIinfodispatch/Infodispatch.svc/ClientLogInRequest";
//    public static String baseURL="http://180.179.236.125/TDSdemotaxiAPI/InfoCabRestAPI.svc/ClientLogInRequest";
//    public static String baseURL="http://103.211.202.112/TDSdemotaxiAPI/InfoCabRestAPI.svc/ClientLogInRequest";
//    http://103.211.202.112/QATTDSdemotaxi/index.aspx
//    public static String baseURL="http://103.211.202.112/RestAPIinfodispatch/Infodispatch.svc/ClientLogInRequest";
    public static String baseURL="http://103.185.74.197/QATRestAPIinfodispatch/Infodispatch.svc/ClientLogInRequest";
//    public static String baseURL="http://103.211.202.112/QATRestAPIinfodispatch/Infodispatch.svc/ClientLogInRequest";
//    public static String baseURL="http://ajmantaxi.AKtelematics.com/RestAPIinfodispatch/Infodispatch.svc/ClientLogInRequest";
    public static String geofenceURL="http://ajmantaxi.infotracktelematics.com/RestAPIinfodispatchTest/Infodispatch.svc/GeofenceList";
//    public static String billURL="http://ajmantaxi.AKtelematics.com/RestAPIinfodispatchTest/Infodispatch.svc/";
//   public static String baseURL="http://demotds.AKtelematics.com/RestAPIinfodispatch/Infodispatch.svc/ClientLogInRequest";
    //public static String baseURL="http://ksademo.AKtelematics.com/RestAPIinfodispatch/Infodispatch.svc/ClientLogInRequest";
   // public static String baseURL="http://stagingnano.AKtelematics.com/RestAPIInfoDispatch/Infodispatch.svc/ClientLogInRequest";
    public static String clientURL=ConfigData.getClientURL();
    //public static String baseURL="http://192.168.1.100/Infodispatch.svc/ClientLogInRequest";
    //http://stagingnano.infotracktelematics.com/RestAPIInfoDispatch/Infodispatch.svc
    public static String driver_login_Api="/LoginRequest";
    public static String jobs_status_Api="/JobStatusRequest";
    public static String final_bill_Api="/FinalBillRequest";
    public static String final_bill_V2_Api="/FinalBillRequestV2";
    public static String driver_Profile_Api="/DriverProfileRequest";
    public static String support_Request_Api="/SupportRequest";
    public static String emergency_alert_Api="/EmergencyAlertRequest";
    public static String job_completion_APi="/JobCompleteRequest";
    public static String validate_Promo_APi="/ValidatePromocode";
    public static String logtout_Api="/LogOutRequest";
    public static String geofence_Api="/GeofenceList";
    public static String rsaRouteRequest_APi="/RideShareRouteRequest";
    public static String rsaRegisterRouteRequest_APi="/RegisterRideShareRequest";
    public static String onbreak_Api="/OnBreakRequest";
    public static String config_APi="/ConfigManager";

    // SCREEN TYPES
   //CLIENT_LOGIN, DRIVER_LOGIN, MAIN_ACTIVITY, BREAK, KERB_SCREEN, BILL_ACTIVITY, CALL_CENTER, BIDDING
}


