package com.infodispatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.commands.ServerConnectionEstablished;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.infodispatch.R;
import com.services.PollingService;
import com.session.PulseManager;
import com.session.SessionManager;
import com.settersgetters.ConfigData;
import com.settersgetters.GpsData;
import com.settersgetters.RunningJobDetails;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.infodispatch.GpsCheckerDialog.showGpsGsmStatusDialog;

public class SplashScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public String DEBUG_KEY = "SplashScreen";
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    final static int REQUEST_LOCATION = 199;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private SessionManager session;
    //  protected LocationManager locationManager;
    private Location mLastLocation;
    double latitude, longitude;
    DBHelper db = new DBHelper(this);
    boolean start = true;
    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int REQUEST_READ_PHONE_STATE = 0;
    private static final int SPLASH_DURATION = 5000;
    Typeface textFonts;
    String Imei;
    TelephonyManager telephonyManager;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    LocationManager locationManager;
    PulseManager pulseManager;
    private TextView mTextAppVersion;

    private ArrayList<String> appPermission = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.splash_screen);
        appPermission.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        appPermission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        appPermission.add(android.Manifest.permission.READ_PHONE_STATE);
        if (Build.VERSION.SDK_INT < 33) {
            appPermission.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            appPermission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        setUiElements();
        session = new SessionManager(SplashScreen.this);
        textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");

        if (db.getCurrentScreenCount() == 0) {
            db.insetCurrentScreenVal("CLIENT_LOGIN");
            HashMap<String, String> configDBData = new HashMap<String, String>();
            configDBData.put("googleAPI", "https://maps.googleapis.com/maps/api/distancematrix/json");
            configDBData.put("googleAPIKey", "AIzaSyDpsg73RSAZKWCTp3yRNUfU1rvWxZlbj4c");
            configDBData.put("arriveDistance", "200");
            configDBData.put("minBalance", "0");
            configDBData.put("maxBalance", "10000");
            configDBData.put("advancePopupTime", "70");
            configDBData.put("advPopupDuration", "10");
            db.insertConfigDBValues(configDBData);
        }
        if (db.getSettings_rec_Count() == 0) {
            db.insertSettingsDBValues("English", "ON");
        }
        int permissionCheck = ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }
    }

    private void loadPermissions(String perm, int requestCode) {
        Dexter.withActivity(this)
                .withPermissions(appPermission)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            if (!NetworkStatus.isInternetPresent(SplashScreen.this).equals("On")) {
                                showGpsGsmStatusDialog(SplashScreen.this, textFonts, "Please Turn on Internet", "Splash");
                            } else {
                                startNextActivity();
                            }
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!NetworkStatus.isInternetPresent(SplashScreen.this).equals("On")) {
                        showGpsGsmStatusDialog(SplashScreen.this, textFonts, "Please Turn on Internet", "Splash");
                    } else {
                        startNextActivity();
                    }
                } else {
                    System.out.println("Inside Splash screen OnRequest Permission");
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void setUiElements() {
        mTextAppVersion = findViewById(R.id.appversion);
        mTextAppVersion.setText("Version " + BuildConfig.VERSION_NAME);
    }

    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(SplashScreen.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                Toast.makeText(getApplicationContext(), "Location enabled by user!", Toast.LENGTH_LONG).show();
                                startNextActivity();
                            } else {
                                status.startResolutionForResult(SplashScreen.this, REQUEST_LOCATION);
                            }

                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
                        Toast.makeText(getApplicationContext(), "Location enabled by user!", Toast.LENGTH_LONG).show();
                        startNextActivity();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            Toast.makeText(getApplicationContext(), "Location enabled by user!", Toast.LENGTH_LONG).show();
                            startNextActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                            finish();

                        }

                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    private void startNextActivity() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (start == true) {
                        getCurrentScreen();
                    }
                } catch (Exception e) {
                    Log.e("Splash Screen", "run: "+e.getMessage());
                }
            }

        }, SPLASH_DURATION);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (!NetworkStatus.isInternetPresent(SplashScreen.this).equals("On")) {
            showGpsGsmStatusDialog(SplashScreen.this, textFonts, "Please Turn on Internet", "Main");
        }*/
        checkPlayServices();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void getCurrentScreen() {
        ServerConnectionEstablished ser = new ServerConnectionEstablished();

        String currentScreen = db.getCurrentScreenVal();
        System.out.println("Inside the splash screen values" + currentScreen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            loadPermissions(Manifest.permission.READ_PHONE_STATE, REQUEST_READ_PHONE_STATE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // For Android 10 (Q) and above, use the recommended method
                    Imei = Settings.Secure.getString(
                            getContentResolver(),
                            Settings.Secure.ANDROID_ID
                    );
                } else {
                    // For Android versions below 10, use the deprecated method
                    Imei = telephonyManager.getDeviceId();
                }
                Utils.setImei_no(Imei);
            } else {
                // Handle the case where permission is not granted
                // You may want to show a message to the user or request permission again
            }
        } else {
            Imei = telephonyManager.getDeviceId();
            Utils.setImei_no(Imei);
        }

        ser.frame_0001_cmd();
        start = false;
        if (currentScreen.equals("CLIENT_LOGIN")) {
//            startPollingServiceService();
            Intent clientLogin = new Intent(SplashScreen.this, ClientLogin.class);
            clientLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(clientLogin);
        } else if (currentScreen.equals("DRIVER_LOGIN")) {
            setClientInfo();
            //  new GetConfigData().execute();
            startPollingServiceService();
           /* Intent pollingService = new Intent(SplashScreen.this,PollingService.class);
            pollingService.putExtra("inputExtra", "Foreground service is running");
            ContextCompat.startForegroundService(SplashScreen.this, pollingService);
*/
            Intent driverLogin = new Intent(SplashScreen.this, DriverLogin.class);
            driverLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(driverLogin);
        } else if (currentScreen.equals("MAIN_ACTIVITY") || currentScreen.equals("BREAK")) {
            setClientInfo();
            setConfigVal();
            // new GetConfigData().execute();
            startPollingServiceService();
           /* Intent pollingService = new Intent(SplashScreen.this,PollingService.class);
            pollingService.putExtra("inputExtra", "Foreground service is running");
            ContextCompat.startForegroundService(SplashScreen.this, pollingService);*/

            Intent mainActivity = new Intent(SplashScreen.this, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(mainActivity);
        } else if (currentScreen.equals("KERB_SCREEN")) {
            setClientInfo();
            setConfigVal();
            setJobDetails();

            SessionManager session = new SessionManager(SplashScreen.this);
            HashMap<String, String> gpsOdo = session.getGPSOdoDetails();
            // HashMap<String, String> gpsStartOdo = session.getStartGPSOdoDetails();
            pulseManager = new PulseManager(this);
            if (pulseManager.isPulse()) {
                GpsData.setGpsOdometer(pulseManager.getDoubleOdometer());
            } else {
                GpsData.setGpsOdometer(Double.parseDouble(gpsOdo.get(SessionManager.KEY_GPS_ODO)));
            }
            // GpsData.setStartGPSOdo(Double.parseDouble(gpsStartOdo.get(SessionManager.KEY_START_GPS_ODO)));
            startPollingServiceService();
           /* Intent pollingService = new Intent(SplashScreen.this,PollingService.class);
            pollingService.putExtra("inputExtra", "Foreground service is running");
            ContextCompat.startForegroundService(SplashScreen.this, pollingService);
*/

            Intent kerbActivity = new Intent(SplashScreen.this, KerbActivity.class);
            kerbActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(kerbActivity);
        } else if (currentScreen.equals("BILL_ACTIVITY")) {
            setClientInfo();
            setConfigVal();
            setJobDetails();
         /*   Intent pollingService = new Intent(SplashScreen.this,PollingService.class);
            pollingService.putExtra("inputExtra", "Foreground service is running");
            ContextCompat.startForegroundService(SplashScreen.this, pollingService);*/
            startPollingServiceService();
            Intent billActivity = new Intent(SplashScreen.this, BillActivity.class);
            billActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(billActivity);
        } else if (currentScreen.equals("CALL_CENTER")) {

            setConfigVal();
            setClientInfo();
            setJobDetails();
            /*Intent pollingService = new Intent(SplashScreen.this,PollingService.class);
            pollingService.putExtra("inputExtra", "Foreground service is running");
            ContextCompat.startForegroundService(SplashScreen.this, pollingService);*/
            startPollingServiceService();

            Intent callCenter = new Intent(SplashScreen.this, CallCenterActivity.class);
            callCenter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(callCenter);
        } else if (currentScreen.equals("BIDDING")) {
            setConfigVal();
            setClientInfo();
           /* Intent pollingService = new Intent(SplashScreen.this,PollingService.class);
            pollingService.putExtra("inputExtra", "Foreground service is running");
            ContextCompat.startForegroundService(SplashScreen.this, pollingService);
*/
            startPollingServiceService();
            Intent callCenter = new Intent(SplashScreen.this, BiddingActivity.class);
            callCenter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(callCenter);
        }
    }

    private void startPollingServiceService() {
        if (!isMyServiceRunning(DriverLogin.class)) {
            Intent pollingService = new Intent(SplashScreen.this, PollingService.class);
            pollingService.putExtra("inputExtra", "Foreground service is running");
//            pollingService.putExtra("startListening", true);
            ContextCompat.startForegroundService(SplashScreen.this, pollingService);
        }
    }

    public void setConfigVal() {
        HashMap<String, String> configData = db.getConfigData();
        RunningJobDetails.setTotalTripFare(Double.parseDouble(configData.get("fixedFare")));
        ConfigData.setClientName(configData.get("clientName"));
        ConfigData.setFixedFare(configData.get("fixedFare"));
        ConfigData.setBaseKm(Long.parseLong(configData.get("baseKms")));
        ConfigData.setBaseHours(Double.parseDouble(configData.get("baseHours")));
        ConfigData.setExtraKmRate(Double.parseDouble(configData.get("extraKmRate")));
        ConfigData.setExtraHourRate(Double.parseDouble(configData.get("extraHourRates")));
        ConfigData.setCurrencyType(configData.get("currencyType"));
        ConfigData.setDriverId(configData.get("driverId"));
        //ConfigData.setMinAmount(Double.parseDouble(configData.get("minAmount")));
        //ConfigData.setMinDistance(Double.parseDouble(configData.get("minDistance")));
//        ConfigData.setWaitingRate(Double.parseDouble(configData.get("waitingRate")));
//        ConfigData.setWaitingFreeMin(Integer.parseInt(configData.get("waitingFreeMin")));
        Log.e("Splash Screen", "setConfigVal: ");
//        Log.e("Splash Screen", "setConfigVal: " + ConfigData.getWaitingRate() );
//        Log.e("Splash Screen", "setConfigVal: " + ConfigData.getWaitingFreeMin() );
    }

    public void setJobDetails() {

        HashMap<String, String> configData = db.getRunningJobData();
        RunningJobDetails.setJobType(configData.get("jobType"));
        RunningJobDetails.setJobId(configData.get("JobId"));
        RunningJobDetails.setTotalTripFare(Double.parseDouble(configData.get("totalBill")));
        RunningJobDetails.setRunningFare(Double.parseDouble(configData.get("runningFare")));
        RunningJobDetails.setTripStartDateTime(configData.get("tripStartDateTime"));
        RunningJobDetails.setTotalTripDist(configData.get("totalTripDist"));
        RunningJobDetails.setTotalTripDuration(configData.get("totalTripDuration"));
        RunningJobDetails.setStartTime(configData.get("startTime"));
        RunningJobDetails.setStartDate(configData.get("startDate"));
        RunningJobDetails.setCustomerName(configData.get("name"));
        RunningJobDetails.setMobileNo(configData.get("mobileNo"));
        RunningJobDetails.setPickupLoc(configData.get("pickupLoc"));
        RunningJobDetails.setDropLoc(configData.get("dropLoc"));
        RunningJobDetails.setCustomerPickUpTime(configData.get("customerPickUpTime"));
        RunningJobDetails.setPickupLatitude(configData.get("pickupLatitude"));
        RunningJobDetails.setPickupLongitude(configData.get("pickupLongitude"));
        RunningJobDetails.setDropLatitude(configData.get("dropLatitude"));
        RunningJobDetails.setDropLongitude(configData.get("dropLongitude"));
        GpsData.setStartGPSOdo(Double.parseDouble(configData.get("startGpsOdo")));
        if (configData.get("promoCode").equals("0")) {
            RunningJobDetails.setPromoCode("Promo Code");
        } else {
            RunningJobDetails.setPromoCode(configData.get("promoCode"));
        }
        if (RunningJobDetails.getJobId().contains("R")) {
            RunningJobDetails.setCustomerName(configData.get("name"));
            RunningJobDetails.setMobileNo(configData.get("mobileNo"));
        }

    }

    public void setClientInfo() {
        HashMap<String, String> clientInfo = db.getClientInfo();
        System.out.print("Inisde the splash screen ConfigData" + clientInfo);
        ConfigData.setPollingUrl(clientInfo.get("pollingUrl"));
        ConfigData.setPollingInterVal(clientInfo.get("PollingInterval"));
        ConfigData.setClientName(clientInfo.get("ClientName"));
        ConfigData.setClientImageURL(clientInfo.get("clientImg"));
        ConfigData.setClientURL(clientInfo.get("clientURL"));
        Utils.setImei_no(clientInfo.get("imei"));
        System.out.println("Inside Client Splash Screen PollingURL==>" + ConfigData.getPollingUrl());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
//            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            sendBroadcast(closeDialog);
            try {
                @SuppressLint("WrongConstant") Object service = getSystemService("statusbar");
                Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                Method collapse = statusbarManager.getMethod("collapse");
                collapse.setAccessible(true);
                collapse.invoke(service);
            } catch (Exception Eww) {
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
