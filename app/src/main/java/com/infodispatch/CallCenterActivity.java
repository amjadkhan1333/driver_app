package com.infodispatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.telecom.Call;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.APIs.APIClass;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appcontroller.AppController;
import com.appcontroller.LanguageController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.maps.android.PolyUtil;
import com.google.maps.android.PolyUtil;
import com.howen.howennative.gpio_info;
import com.log.MyLog;
import com.infodispatch.R;
import com.session.PulseManager;
import com.session.SessionManager;
import com.settersgetters.ConfigData;
import com.settersgetters.GpsData;
import com.settersgetters.RunningJobDetails;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.infodispatch.GpsCheckerDialog.showGpsGsmStatusDialog;

/**
 * Created by info on 01-12-2016.
 */
public class CallCenterActivity extends AppCompatActivity implements OnMapReadyCallback {
    public String DEBUG_KEY="CallCenterActivity";
    Button btn_accept;
    String buttonText;
    FloatingActionButton btn_emergency,btn_waitTime,btn_navigate;
    TextView mTitle;
    TextView lblCustomerName,txtCustomerName,lblPhoneNo,txtPhoneNo,lblPickUpLocation,txtPickUpLocation,
            lblDropLocation,txtDropLocation,lblPickUpTime,txtPickUpTime;
    LinearLayout layoutCustomerDetails;
    View lineView;
    DBHelper db;
    GoogleMap mGoogleMap;
    Typeface textFonts;
    SwitchCompat toolbar_switch;
    TextView toolbar_title,toolbar_date,txt_switch,txt_total_fare,lbl_currency;
    Toolbar toolbar;
    RelativeLayout layout_mobNo;
    HashMap<String,String> configDbData;
    HashMap<String,String> configData;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    VoiceSoundPlayer voiceSoundPlayer = new VoiceSoundPlayer();
    Integer[] marker_icon = {R.drawable.marker_orange,R.drawable.marker_green,R.drawable.marker_red};
    PulseManager pulseManager;
    SessionManager session;
    Dialog dialogConfirm;
    private String TAG=getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LanguageController.setLanguage(CallCenterActivity.this);
        setContentView(R.layout.callcenter_layout);

        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            db = new DBHelper(this);
            db.updateCurrentScreenVal("CALL_CENTER",1);
            configDbData= db.getConfigDBValues();
            ConfigData.setDeviceStatus(ConfigData.Device_Status.devicestatus_offered.ordinal());
            textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = sdf.format(c.getTime());

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            /*toolbar_title.setText(R.string.support);*/

            toolbar_title.setTypeface(textFonts,Typeface.BOLD);
            toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);
            toolbar_date.setVisibility(View.GONE);
            toolbar_switch = (SwitchCompat)toolbar.findViewById(R.id.toolbar_switch);
            toolbar_switch.setVisibility(View.GONE);
            txt_switch = (TextView) toolbar.findViewById(R.id.txt_switch);
            txt_switch.setVisibility(View.GONE);
            setSupportActionBar(toolbar);
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"onCreate"+e.getMessage());
        }
        setUiElements();
        LanguageController.setLanguage(CallCenterActivity.this);
        layout_mobNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhoneNumClick();
            }
        });
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    configData = db.getConfigData();
                    if(buttonText.equalsIgnoreCase("Accept")) {
                            /*if (Double.parseDouble(configData.get("availableBalance")) <= Double.parseDouble(configDbData.get("maxBalance"))) {
                                driverAmountAlert();
                            } else {*/
                                prepareCallCenterJobData();
                            //}
                        } else {
                            prepareCallCenterJobData();
                        }
                } catch (Exception e) {
                    MyLog.appendLog(DEBUG_KEY + "btn_accept.setOnClickList" + e.getMessage());
                }
            }
        });

        btn_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNavigationAction();
            }
        });
    }

    private void prepareCallCenterJobData() {

            voiceSoundPlayer.stopVoice(CallCenterActivity.this);
            HashMap<String,String> callcenter_configData = db.getCallCenterConfigInfo();
            ConfigData.setFixedFare(callcenter_configData.get("fixedFare"));
            ConfigData.setBaseKm(Long.parseLong(callcenter_configData.get("baseKms")));
            ConfigData.setBaseHours(Double.parseDouble(callcenter_configData.get("baseHours")));
            ConfigData.setExtraKmRate(Double.parseDouble(callcenter_configData.get("extraKmRate")));
            ConfigData.setExtraHourRate(Double.parseDouble(callcenter_configData.get("extraHourRates")));
            ConfigData.setCurrencyType(configData.get("currencyType"));
            RunningJobDetails.setTotalTripFare(Double.parseDouble(callcenter_configData.get("fixedFare")));

            if (buttonText.equalsIgnoreCase("ACCEPT")) {
                RunningJobDetails.setJobStatus(RunningJobDetails.Job_Status.jobstatus_accept.ordinal());
            } else if (buttonText.equalsIgnoreCase("ARRIVE")) {
                createPointsOnMap(RunningJobDetails.getDropLatitude(), RunningJobDetails.getDropLongitude(),2);
                RunningJobDetails.setJobStatus(RunningJobDetails.Job_Status.jobstatus_ReachedLocation.ordinal());
            } else if (buttonText.equalsIgnoreCase("PICK UP")) {
                GpsData.setStartManualOdo("0");
                GpsData.setStartGPSOdo(GpsData.getGpsOdometer());
               /* SessionManager session = new SessionManager(AppController.getInstance());
                session.createStartGPSOdoSession();*/
                db.updateStartGpsOd(String.valueOf(GpsData.getStartGPSOdo()),1);
                RunningJobDetails.setTripStartDateTime(getcurrentTimeToDisplay());
                RunningJobDetails.setStartTime(GetDeviceTime.getTime());
                RunningJobDetails.setStartDate(GetDeviceTime.getDate());
                RunningJobDetails.setTripStartDateTime(getcurrentTimeToDisplay());
                RunningJobDetails.setJobStatus(RunningJobDetails.Job_Status.jobstatus_commenced.ordinal());
        }
        if (NetworkStatus.isInternetPresent(CallCenterActivity.this).equals("On")) {
            jobStatusRequest();
        }
        else{
            NetworkCheckDialog.showConnectionTimeOut(CallCenterActivity.this);
        }
    }

    public void setUiElements() {
        try {

            ConfigData.setDeviceStatus(ConfigData.Device_Status.devicestatus_offered.ordinal());
            RunningJobDetails.setPromoCode("Promo Code");

            layout_mobNo = (RelativeLayout) findViewById(R.id.layout_mobNo);
            layoutCustomerDetails = (LinearLayout) findViewById(R.id.layoutCustomerDetails);
            layoutCustomerDetails.setVisibility(View.GONE);
            lineView = (View) findViewById(R.id.lineView);
            lineView.setVisibility(View.GONE);
            btn_accept = (Button) findViewById(R.id.btnStop);
            btn_accept.setText(R.string.btn_accept);
            btn_accept.setTypeface(textFonts,Typeface.BOLD);

            btn_emergency = (FloatingActionButton) findViewById(R.id.btn_emergency);
            btn_emergency.setVisibility(View.GONE);

            btn_waitTime = (FloatingActionButton) findViewById(R.id.btn_waitTime);
            btn_waitTime.setVisibility(View.GONE);
            btn_navigate = (FloatingActionButton) findViewById(R.id.btn_navigate);
            lblCustomerName = (TextView) findViewById(R.id.lblCustomerName);
            txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);
            lblPhoneNo = (TextView) findViewById(R.id.lblPhoneNo);
            txtPhoneNo = (TextView) findViewById(R.id.txtPhoneNo);
            lblPickUpLocation = (TextView) findViewById(R.id.lblPickUpLocation);
            txtPickUpLocation = (TextView) findViewById(R.id.txtPickUpLocation);
            lblDropLocation = (TextView) findViewById(R.id.lblDropLocation);
            txtDropLocation = (TextView) findViewById(R.id.txtDropLocation);
            lblPickUpTime = (TextView) findViewById(R.id.lblPickUpTime);
            txtPickUpTime = (TextView) findViewById(R.id.txtPickUpTime);

            lblCustomerName.setTypeface(textFonts);
            txtCustomerName.setTypeface(textFonts,Typeface.BOLD);
            lblPhoneNo.setTypeface(textFonts);
            txtPhoneNo.setTypeface(textFonts,Typeface.BOLD);
            lblPickUpLocation.setTypeface(textFonts);
            txtPickUpLocation.setTypeface(textFonts,Typeface.BOLD);
            lblDropLocation.setTypeface(textFonts);
            txtDropLocation.setTypeface(textFonts,Typeface.BOLD);
            lblPickUpTime.setTypeface(textFonts);
            txtPickUpTime.setTypeface(textFonts,Typeface.BOLD);

            HashMap<String,String> callCenterData = db.getRunningJobData();
            RunningJobDetails.setJobId(callCenterData.get("JobId"));
            RunningJobDetails.setCustomerName(callCenterData.get("name"));
            RunningJobDetails.setMobileNo(callCenterData.get("mobileNo"));
            RunningJobDetails.setPickupLoc(callCenterData.get("pickupLoc"));
            RunningJobDetails.setDropLoc(callCenterData.get("dropLoc"));
            RunningJobDetails.setCustomerPickUpTime(callCenterData.get("customerPickUpTime"));
            RunningJobDetails.setPickupLatitude(callCenterData.get("pickupLatitude"));
            RunningJobDetails.setPickupLongitude(callCenterData.get("pickupLongitude"));
            RunningJobDetails.setDropLatitude(callCenterData.get("dropLatitude"));
            RunningJobDetails.setDropLongitude(callCenterData.get("dropLongitude"));
           // createPointsOnMap(RunningJobDetails.getPickupLatitude(),RunningJobDetails.getPickupLongitude(),2);
            toolbar_title.setText("Job Id:"+callCenterData.get("JobId"));
            txtCustomerName.setText(callCenterData.get("name"));
            txtPhoneNo.setText(callCenterData.get("mobileNo"));
            txtDropLocation.setText(callCenterData.get("dropLoc"));
            txtPickUpLocation.setText(callCenterData.get("pickupLoc"));
            txtPickUpTime.setText(String.valueOf(callCenterData.get("customerPickUpTime")));
            buttonText = callCenterData.get("jobStatus");
            if(callCenterData.get("jobStatus").equalsIgnoreCase("ACCEPT")){
                btn_accept.setText(R.string.btn_accept);
                layoutCustomerDetails.setVisibility(View.GONE);
                lineView.setVisibility(View.GONE);
            }
            else if(callCenterData.get("jobStatus").equalsIgnoreCase("ARRIVE")){
                layoutCustomerDetails.setVisibility(View.VISIBLE);
                lineView.setVisibility(View.VISIBLE);
                btn_accept.setText(R.string.btn_reach);
            }
            else{
                layoutCustomerDetails.setVisibility(View.VISIBLE);
                lineView.setVisibility(View.VISIBLE);
                btn_accept.setText(R.string.btn_picup);
            }

        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"setUiElements"+e.getMessage());
        }
    }

    private void createPointsOnMap(String pickLat,String pickLon,int pos) {
        try {
            LatLng latLng = new LatLng(Double.parseDouble(pickLat),Double.parseDouble(pickLon));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(marker_icon[pos]));
            mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.5f), 4000, null);
        }catch (SecurityException e){
            MyLog.appendLog(DEBUG_KEY+"createPointsOnMap"+e.getMessage());
        }
    }
    public void onPhoneNumClick() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }else {
                String number = "tel:" + RunningJobDetails.getMobileNo().trim();
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                startActivity(callIntent);
            }
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"onPhoneNumClick"+e.getMessage());
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

    }
    @Override
    protected void onResume(){
        super.onResume();
        pulseManager = new PulseManager(this);
        session = new SessionManager(this);
        //this.stopService(new Intent(this, FloatingViewService.class));
        LocationManager locationManager = (LocationManager)AppController.getInstance().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGpsGsmStatusDialog(CallCenterActivity.this,textFonts,"Please enable the GPS","Call");
        }
        else if(!NetworkStatus.isInternetPresent(CallCenterActivity.this).equals("On")){
            showGpsGsmStatusDialog(CallCenterActivity.this,textFonts,"Please Turn on Internet","Call");
        }
        //LanguageController.setLanguage(CallCenterActivity.this);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_navigation:
                try {
                    checkNavigationAction();
                }catch (Exception e){
                    MyLog.appendLog(DEBUG_KEY+"onOptionsItemSelected"+e.getMessage());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void checkNavigationAction(){
        if (ConfigData.getClientName().equalsIgnoreCase("Hellocab")) {
            if (buttonText == null || buttonText == "") {
                Toast.makeText(getApplicationContext(), "Please Accept the Job for Navigation", Toast.LENGTH_LONG).show();
            } else {
                if (buttonText.equalsIgnoreCase("Accept")) {
                    showNavigation(RunningJobDetails.getPickupLatitude(), RunningJobDetails.getPickupLongitude());
                } else if (buttonText.equalsIgnoreCase("Arrive")) {
                    showNavigation(RunningJobDetails.getDropLatitude(), RunningJobDetails.getDropLongitude());
                }
            }
        }else{
            if (buttonText.equalsIgnoreCase("Arrive")) {
                showNavigation(RunningJobDetails.getPickupLatitude(), RunningJobDetails.getPickupLongitude());
            } else if (buttonText.equalsIgnoreCase("pick up")) {
                showNavigation(RunningJobDetails.getDropLatitude(), RunningJobDetails.getDropLongitude());
            }
        }
    }
    public void jobStatusRequest(){
        String url = ConfigData.getClientURL()+ APIClass.jobs_status_Api;
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put("imeiNo", Utils.getImei_no());
            params.put("jobId", RunningJobDetails.getJobId());
            params.put("jobStatus",String.valueOf(RunningJobDetails.getJobStatus()));
            params.put("loginId",ConfigData.getDriverId());
            params.put("shiftId","1");
            params.put("lat",String.valueOf(GpsData.getLattiude()));
            params.put("lon", String.valueOf(GpsData.getLongitude()));
            params.put("speed",GpsData.getGpsSpeed());
            params.put("dateTime",getDateForRequests());
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"jobStatusRequest"+e.getMessage());
        }
        final ProgressDialog pDialog = new ProgressDialog(this);
        System.out.println("Inside the Job Status Failed"+params);
        pDialog.setMessage(getResources().getString(R.string.alt_update_job_status));
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response!=null) {
                        if(response.getInt("status")==0){
                            pDialog.dismiss();
                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_LONG);
                        }
                        else{
                            pDialog.dismiss();
                            if(buttonText.equalsIgnoreCase("ARRIVE")){
                                btn_accept.setText(R.string.btn_picup);
                                db.updateRunningJobStatus("PICK UP",1);
                                buttonText="PICK UP";

                            }else if(buttonText.equalsIgnoreCase("ACCEPT")){
                                if (getString(R.string.app_publish_mode).equals("MDT")) {
                                    try {
                                        if (gpio_info.open_gpio() > 0) {
                                            if (gpio_info.get_gpio_data("P3B6") == 0)
                                                gpio_info.set_gpio_data("P3B6", 1);
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "GPIPO ERROR" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                layoutCustomerDetails.setVisibility(View.VISIBLE);
                                lineView.setVisibility(View.VISIBLE);
                                btn_accept.setText(R.string.btn_reach);
                                db.updateRunningJobStatus("ARRIVE",1);
                                buttonText="ARRIVE";
                                ConfigData.setDeviceStatus(ConfigData.Device_Status.devicestatus_oncall.ordinal());
                            }
                            else if(buttonText.equalsIgnoreCase("PICK UP")){
                                //TODO MINIMUM FARE and show popup for hotel pickup..
//                                GpsData.setTestLocation(25.32899691510044,55.350908052716235);
//                                checkGeofenceOrToll(GpsData.getTestLatitude(),GpsData.getTestLongitude());
                                //checkGeofenceOrToll(Double.parseDouble(RunningJobDetails.getDropLatitude()),Double.parseDouble(RunningJobDetails.getDropLongitude()));
                                showConfirmDialog();
                            }
                        }
                    }
                    else{
                        pDialog.dismiss();
                        MyLog.appendLog(DEBUG_KEY+"Response is null");
                    }
                    pDialog.dismiss();

                } catch (Exception e) {
                    pDialog.dismiss();
                    MyLog.appendLog(DEBUG_KEY+"jobStatusRequest"+e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Time out.Please Try again",Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(req);
    }

    public void showConfirmDialog() {
        try {
            LanguageController.setLanguage(CallCenterActivity.this);
            dialogConfirm = new Dialog(this);
            dialogConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogConfirm.setContentView(R.layout.dialog_confirm);
            dialogConfirm.setCanceledOnTouchOutside(false);
            dialogConfirm.setCancelable(false);
            final CheckBox checkBox = dialogConfirm.findViewById(R.id.check_hotel);
            TextView dialog_are_you_sure = (TextView) dialogConfirm.findViewById(R.id.dialog_are_you_sure);
            dialog_are_you_sure.setText(getResources().getString(R.string.lbl_pick_up_customer));
            dialog_are_you_sure.setTypeface(textFonts);
            session.resetWating();
            pulseManager.resetWating();

            Button dialog_btn_no = (Button) dialogConfirm.findViewById(R.id.dialog_btn_no);
            Button dialog_btn_yes = (Button) dialogConfirm.findViewById(R.id.dialog_btn_yes);

            dialog_btn_no.setTypeface(textFonts, Typeface.BOLD);
            dialog_btn_yes.setTypeface(textFonts, Typeface.BOLD);

            dialog_btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogConfirm.dismiss();
                }
            });

            dialog_btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogConfirm.dismiss();
                    if(checkBox.isChecked()) {
                        pulseManager.setHotelJob(true);
//                        pulseManager.setExtraFare((float) (7 + pulseManager.getExtraFare()));
                    }
                    if (NetworkStatus.isInternetPresent(CallCenterActivity.this).equals("On")) {
                        pickUpJob();
                    }
                    else{
                        NetworkCheckDialog.showConnectionTimeOut(CallCenterActivity.this);
                    }
                }
            });
        } catch (Exception Eww) {
        }
        dialogConfirm.show();

    }

    private void pickUpJob() {
        PulseManager pulseManager = new PulseManager(CallCenterActivity.this);
        if(pulseManager.isDay())
            pulseManager.setMinDistance(4.116f);
        else
            pulseManager.setMinDistance(3.528f);

        Intent intent = new Intent(getBaseContext(), KerbActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
        voiceSoundPlayer.playVoice(CallCenterActivity.this, R.raw.jobstarted);
    }

    public String getcurrentTimeToDisplay() {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d ",
                now.year,(now.month+1),now.monthDay,now.hour,now.minute,now.second);
        return currentTime;
    }
    public String getDateForRequests() {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d",now.year,(now.month+1),now.monthDay,now.hour,now.minute,now.second);
        return currentTime;
    }
    @Override
    public void onBackPressed() {

    }

    public void showNavigation(String sLat,String sLng) {
        try {
            if((!sLat.equals(null)) && !sLng.equals(null)) {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                } else {
                    startService(new Intent(CallCenterActivity.this, FloatingViewService.class));
                }*/
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + sLat + "," + sLng + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }else{
                Toast.makeText(getApplicationContext(),"Please select the Drop Location.",Toast.LENGTH_LONG);
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"Please Install the Google Map Application.",Toast.LENGTH_LONG);
            MyLog.appendLog(DEBUG_KEY+"showNavigation"+e.getMessage());
        }
    }

    public void driverAmountAlert(){
        try{
            AlertDialog.Builder driverAlert = new AlertDialog.Builder(CallCenterActivity.this,R.style.AlertDialogCustom);
            driverAlert.setTitle("Alert!");
            driverAlert.setMessage("Balance is low. Please top-up as soon as possible");
            driverAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    dialog.cancel();
                    try {
                        prepareCallCenterJobData();
                    }
                    catch (Exception e){

                    }
                }
            });
            driverAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    dialog.cancel();
                }
            });
            driverAlert.show();
        }catch (Exception e){
            System.out.println("Inside the Exception=>"+e);
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus)
        {
//            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            sendBroadcast(closeDialog);

            try
            {
                @SuppressLint("WrongConstant") Object service  = getSystemService("statusbar");
                Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                Method collapse = statusbarManager.getMethod("collapse");
                collapse .setAccessible(true);
                collapse .invoke(service);
            }
            catch(Exception Eww)
            {}
        }
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == RESULT_OK) {
                startService(new Intent(CallCenterActivity.this, FloatingViewService.class));
            } else {
                Toast.makeText(this, "Draw over other app permission not available. Closing the application", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(GpsData.getLattiude(), GpsData.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f), 4000, null);
    }


    private void checkGeofenceOrToll(double lat,double lon){
        try {
            if(pulseManager.getGeoResponse().contains(","));{
                Log.e(TAG, "checkGeofenceOrToll: "+lat );
                Log.e(TAG, "checkGeofenceOrToll: "+lon );
                JSONArray jsonArray = new JSONArray(pulseManager.getGeoResponse());
                if (!pulseManager.isEmirate1check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    //JSONObject jsonObject = new JSONObject("{\"ID\":1,\"GeofenceName\":\"Dubai-ITPL\",\"ShapeType\":2,\"ShapeCoordinates\":\"25.32899691510044,55.350908052716235|25.32838112661746,55.353119534049014|25.326834368501995,55.35498769262789|25.32024102978408,55.35329823622463|25.3167890513437,55.350393571499566|25.310233355266952,55.347660568151454|25.30701387245953,55.348317876998166|25.304725447713334,55.349833492729644|25.300264259188314,55.351263277772645|25.298596499990495,55.353723031077365|25.300344888186324,55.35794013420103|25.300715902132637,55.360869776997546|25.297728358895316,55.36415548304079|25.296874761589045,55.372336891088466|25.297805958352374,55.377428394350986|25.302229045289714,55.38632394054888|25.213969624504664,55.651524674687366|25.059419898057946,55.66335321644304|24.982548318008266,55.658289205822925|24.793334489062733,55.72785497883318|24.78904154538491,55.71663261631487|24.75385109930269,55.728187572751025|24.730639238225425,55.70581258514879|24.72039166386963,55.67232386449335|24.703281561815484,55.59591979959963|24.68713087574534,55.57825644740818|24.679054747774376,55.56976809406636|24.670510119567382,55.56007811108587|24.638666263624817,55.5232064543104|24.631861371580744,55.363682743701915|24.60536196230168,55.31001341769455|24.653282690133363,55.269445844534616|24.67848402798911,55.26564155014215|24.687963409960602,55.14906960314123|24.85579545659867,55.04211069324968|24.90868417532537,54.89328027943132|24.973455099107177,54.868035328183154|25.393516109689678,55.23169459084032|\",\"GeofenceLevel\":1,\"GeofenceType\":1}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat,lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if(inside){
                        Log.e(TAG, "INSIDE GEO FENCE " );
                        if(!pulseManager.isEmirate1()) {
                            pulseManager.setEmirate1(true);
                            pulseManager.setEmirate1check(true);
                            pulseManager.setEmirate(true);
                            Log.e(TAG, " TRUE INSIDE " );
                            int runfare = pulseManager.getExtraFare();
                            runfare = runfare + 10;
                            pulseManager.setExtraFare(runfare);
                            int emirate = GpsData.getEmirateCharges() + 17;
                            GpsData.setExtraCharges(emirate,GpsData.getTollCharges());
                        }else{
                            Log.e(TAG, "checkGeofenceOrToll: FALSE INSIDE " );
                        }
                    } else {
                        Log.e(TAG, "checkGeofenceOrToll: OUTSIDE GEO FENCE " );
                    }
                }

                if (!pulseManager.isEmirate2check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(1);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":2,\"GeofenceName\":\"Abu Dhabi-ITPL\",\"ShapeType\":2,\"ShapeCoordinates\":\"24.803311580705262,55.8148655069032|24.79592255226232,55.79156649829534|24.800409208231674,55.75918888272432|24.788143471109,55.71736989142087|24.75424818524221,55.72835378879395|24.729075440617883,55.70371795044926|24.703045551080454,55.597454696611436|24.639111591143916,55.52569051803735|24.631667321779943,55.36362273217824|24.60429204622488,55.30865219952253|24.651469727594073,55.26867119194654|24.679029187071745,55.261493600622686|24.68672324968177,55.14826146484998|24.85547314716034,55.041273511663945|24.91236132543107,54.89664880158094|24.853827915454524,54.63058439613965|24.54817708670199,54.37275973679212|24.451671516693562,53.208799041525396|24.389804628481787,51.57002297760633|24.257328484151678,51.59023610474256|24.12659417253716,51.59122315766004|22.938712865048238,52.59192315460828|22.63234180780936,55.136888898626836|22.715988685917072,55.21392194153455|22.798780244863252,55.227327622190984|23.083806740185633,55.22150722862867|23.381310607998724,55.41723338486341|23.409256027444833,55.436332724706205|23.453574400140837,55.445819027439626|23.510688080802908,55.48401770712522|23.554559551950874,55.522903032318624|23.584041530943335,55.538912467376264|23.62861565436998,55.571401394621404|23.713730091789174,55.576424501554044|23.76108803135723,55.534755713955434|23.810941568858883,55.53222572030214|23.8438199783616,55.536562181726964|23.866599233897293,55.52884865151432|23.887491009637287,55.51838853927043|23.90541244148295,55.500901542321714|23.925557698677412,55.49751324178365|23.943248667641498,55.485243476645024|23.95499281558159,55.49795203441468|23.96485354859775,55.52302021132496|23.98959151026296,55.57933637471584|23.99703118282581,55.58553864778665|24.019523786721056,55.63190968306449|24.03547201602679,55.678526978657516|24.053299345047282,55.722741014973195|24.057363914939717,55.73194916424987|24.056098897629052,55.73995568388787|24.0513742351178,55.75837198244122|24.055114352546457,55.7730117307016|24.055405843965953,55.78009837837604|24.02534011873704,55.80156728224543|24.024808531689963,55.80998430559126|24.014555540772793,55.83282088460115|24.020738089086333,55.844432293155165|24.023470965545133,55.85810363823262|24.03175864657329,55.87240006373909|24.048018881579548,55.90751604707626|24.054384755241742,55.940952716113|24.060280073293395,55.96014149086264|24.062883544069937,55.99065991649118|24.06642740197038,56.01808843733457|24.087306388890298,56.0130356584647|24.090004835286692,56.014677673296006|24.114518018512626,56.00319882454065|24.127242846179712,55.99479864882019|24.145605663640826,55.97541214497474|24.16979496896912,55.96067173005727|24.184946823574926,55.972091234938176|24.21705197745575,55.96344940894929|24.21392820941765,55.951374355421336|24.223336117789266,55.95434674592403|24.21459864652217,55.86965431989816|24.21090014271677,55.8527047705808|24.201623941299484,55.83322522761014|24.211815566997757,55.808431390390666|24.235155794128648,55.776771098093064|24.232509444861275,55.76571017102984|24.234246059852204,55.75258930744317|24.247792870689604,55.754955267251404|24.2613382390008,55.75938116358307|24.263614838629607,55.76998686948505|24.27841036214438,55.79157890351203|24.30956065807561,55.80695382849959|24.327635661522766,55.83489733347383|24.409849740823468,55.83376288478908|24.411238885711292,55.82804581913811|24.43912923375354,55.82141073601966|24.484694153606682,55.78662131948809|24.530242577490455,55.765564813112775|24.573215930458677,55.768083758369954|24.594318943347783,55.79222464533237|24.615418399194795,55.816022209540876|24.636733662833908,55.79513366864708|24.671149295213315,55.83741651447204|24.72250637497847,55.827143905402096|24.750047702641748,55.83127731522259|24.764127829762845,55.83334402013284|24.778206360895446,55.83266414301184|24.780812996388565,55.83151954174687|24.783419577136044,55.82814334258151|24.786606577848744,55.82602580142853|24.790019354454433,55.821962380499514|24.79912001509493,55.81970140402851|24.79907315104486,55.81488019618851|\",\"GeofenceLevel\":1,\"GeofenceType\":1}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat,lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if(inside){
                        Log.e(TAG, "INSIDE GEO FENCE " );
                        if(!pulseManager.isEmirate2()) {
                            pulseManager.setEmirate2(true);
                            pulseManager.setEmirate2check(true);
                            pulseManager.setEmirate(true);
                            Log.e(TAG, " TRUE INSIDE " );
                            int runfare = pulseManager.getExtraFare();
                            runfare = runfare + 10;
                            pulseManager.setExtraFare(runfare);
                            int emirate = GpsData.getEmirateCharges() + 17;
                            GpsData.setExtraCharges(emirate,GpsData.getTollCharges());
                        }else{
                            Log.e(TAG, "checkGeofenceOrToll: FALSE INSIDE " );
                        }
                    } else {
                        Log.e(TAG, "checkGeofenceOrToll: OUTSIDE GEO FENCE " );
                    }
                }

                if (!pulseManager.isEmirate3check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(2);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":23,\"GeofenceName\":\"Al Ain ITPL\",\"ShapeType\":2,\"ShapeCoordinates\":\"24.297639850271697,55.607586235013315|24.20424063011575,55.567653507199594|24.160334596032243,55.472499460187265|24.137436004150487,55.464747876134226|24.13646671288623,55.512614578214|24.12133955471748,55.57209993919403|24.100635263689583,55.59177779799373|24.086823248254326,55.61626217534811|24.07645778804611,55.63662667965562|24.076122446585345,55.65767782947094|24.051495487870575,55.7598257364896|24.056336513858376,55.780091166701624|24.024807173669362,55.80241653343708|24.021007404369623,55.81857147833378|24.01469870636824,55.83266648670704|24.02454014003782,55.85826280733616|24.042533046009368,55.89278551956684|24.060956933246516,55.961144298520395|24.063161156344933,55.9909168185857|24.065678822623283,56.01828607937367|24.0870243446171,56.01328375956089|24.09018919862808,56.01497623344929|24.12644537713602,55.99553021809132|24.14536629301806,55.97552093884022|24.170044944160335,55.9615949096349|24.18487715794911,55.97195058336527|24.217242970515553,55.963080182876894|24.214021635203707,55.95117586871655|24.223234406949654,55.95517638346226|24.2153591031518,55.871510237660715|24.211819399728128,55.85420998951466|24.201138537624534,55.83351406475575|24.212962108446636,55.80864395221741|24.234803089830955,55.776220739093134|24.232147503857604,55.76630595346958|24.23434999929868,55.752656191792795|24.247024040406117,55.75507814271421|24.261574833757862,55.76024667566688|24.262817734822367,55.767493836787054|24.278448441822352,55.79160119613678|24.3090783207141,55.806170284953424|24.312640065838494,55.811812982168505|24.320581752552833,55.82432213446171|24.375799570555,55.798827737775156|24.308709215903022,55.7420493962434|24.244713812373156,55.67771795412571|24.285251747373565,55.654928565230676|24.277195989986964,55.632375881281206|24.296366197095473,55.627332657780954|\",\"GeofenceLevel\":1,\"GeofenceType\":1}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat,lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if(inside){
                        Log.e(TAG, "INSIDE GEO FENCE " );
                        if(!pulseManager.isEmirate3()) {
                            pulseManager.setEmirate(true);
                            pulseManager.setEmirate3(true);
                            pulseManager.setEmirate3check(true);
                            Log.e(TAG, " TRUE INSIDE " );
                            int runfare = pulseManager.getExtraFare();
                            runfare = runfare + 10;
                            pulseManager.setExtraFare(runfare);
                            int emirate = GpsData.getEmirateCharges() + 17;
                            GpsData.setExtraCharges(emirate,GpsData.getTollCharges());
                        }else{
                            Log.e(TAG, "checkGeofenceOrToll: FALSE INSIDE " );
                        }
                    } else {
                        Log.e(TAG, "checkGeofenceOrToll: OUTSIDE GEO FENCE " );
                    }
                }

            }
        }catch (Exception e){
            Log.e(TAG, "checkGeofenceOrToll ex: "+e.getMessage());
        }
    }

    private List<LatLng> getPolygonObject(JSONObject jsonObject){
        List<LatLng> polygon = new ArrayList<>();
        try{
//            Log.e(TAG, "getPolygonObject: ****** "+jsonObject);
            String ShapeCoordinates = jsonObject.getString("ShapeCoordinates");

            String[] stringShapeCoordinates = ShapeCoordinates.split("\\|");
            for(int i = 0;i < stringShapeCoordinates.length; i++) {
                //Log.e(TAG, "getPolygonObject: ***** "+stringShapeCoordinates[i]);
                if(stringShapeCoordinates[i].contains(",")){
                    String[] latlng = stringShapeCoordinates[i].split(",");
                    LatLng latLng = new LatLng(Double.parseDouble(latlng[0]),Double.parseDouble(latlng[1]));
                    polygon.add(latLng);
                }
            }
        }catch (Exception e){
            Log.e(TAG, "getPolygonObject: "+e.getMessage());
        }
        return polygon;
    }


}