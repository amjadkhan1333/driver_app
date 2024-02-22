package com.infodispatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.maps.android.PolyUtil;
import com.google.maps.android.PolyUtil;
import com.howen.howennative.serialService;
import com.log.MyLog;
import com.infodispatch.BuildConfig;
import com.infodispatch.R;
import com.services.PollingService;
import com.session.PulseManager;
import com.settersgetters.ConfigData;
import com.settersgetters.GpsData;
import com.settersgetters.RunningJobDetails;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.Validate;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.validation.Validator;

import static com.infodispatch.GpsCheckerDialog.showGpsGsmStatusDialog;

/**
 * Created by info on 24-11-2016.
 */
public class KerbActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String TAG = getClass().getSimpleName();
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    Button btn_stop, btn_dig_promocode, dialog_btn_yes, dialog_btn_no;
    EditText edt_drop_location, edt_customer_Name, edt_parking_fare,edt_dig_promocode, edt_pickup_loc, edt_drop_loc;
    FloatingActionButton btn_emergency, btn_waitTime, btn_navigate;
    Typeface textFonts, textShadowFonts;
    TextView toolbar_title, toolbar_date, txt_switch;
    Toolbar toolbar;
    SwitchCompat toolbar_switch;
    Dialog dialogEmergency, dialogPromocode, dialogStop, dialogParking;
    TextView txt_promocode, lbl_distance, txt_distance, lbl_duration, txt_duration, lbl_startTime, txt_startTime,
            lbl_currency, txt_jobType, lbl_dig_promocode, are_you_sure, txt_fare, lblDropLocation,
            txtDropLocation, lblPickUpLocation, txtPickUpLocation,
            lbl_customer_name, lbl_customer_phone, lbl_job_id, txt_job_id, lbl_driver_name, lbl_driver_no, lbl_driver_id;

    LinearLayout layout_promocode, layout_jobType, layout_job_address;
    RelativeLayout layout_Search;
    ImageView img_jobType, imgNavigation, imgPromoCode; //imgSearch
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    DBHelper db;
    HashMap<String, String> configData;
    HashMap<String, String> map = new HashMap<String, String>();
    GoogleMap mGoogleMap;
    Double sLat = 0.0, sLng = 0.0;
    private Geocoder geocoder;
    private List<Address> addresses;
    public String DEBUG_KEY = "KerbActivity";
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    Integer[] marker_icon = {R.drawable.marker_orange, R.drawable.marker_green, R.drawable.marker_red};
    private EditText edt_phone;
    VoiceSoundPlayer voiceSoundPlayer = new VoiceSoundPlayer();
    private int count = 0;
    private PulseManager pulseManager;
    private long secondsInLong = 0;
//    ImageView emergencyImage;

    //private static double increamentDistance = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LanguageController.setLanguage(KerbActivity.this);
        setContentView(R.layout.activity_kerb);
        pulseManager = new PulseManager(this);
        try {

            textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
            textShadowFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = sdf.format(c.getTime());
            db = new DBHelper(this);
            db.updateCurrentScreenVal("KERB_SCREEN", 1);
            //LanguageController.setLanguage(KerbActivity.this);
            ConfigData.setDeviceStatus(ConfigData.Device_Status.devicestatus_hired.ordinal());

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbar_title.setText("Job Id :" + RunningJobDetails.getJobId());
            toolbar_title.setTypeface(textFonts, Typeface.BOLD);
            toolbar_title.setVisibility(View.GONE);
            toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);
            toolbar_date.setText("Date: " + strDate);
            toolbar_switch = (SwitchCompat) toolbar.findViewById(R.id.toolbar_switch);
            toolbar_switch.setVisibility(View.GONE);
            txt_switch = (TextView) toolbar.findViewById(R.id.txt_switch);
            txt_switch.setVisibility(View.GONE);
            setSupportActionBar(toolbar);

        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
        }

        setUiElements();
        // LanguageController.setLanguage(KerbActivity.this);
        startTimer();
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RunningJobDetails.getJobId().contains("K")) {
                   /* if(edt_drop_location.getText().toString().length()==0){
                        Toast.makeText(getApplicationContext(),"Please provide the drop location",Toast.LENGTH_LONG).show();
                    } else if(edt_phone.getText().toString().length()==0){
                        Toast.makeText(getApplicationContext(),"Please provide customer name",Toast.LENGTH_LONG).show();
                    }else if(edt_customer_Name.getText().toString().trim().length()==0){
                        Toast.makeText(getApplicationContext(),"Please enter the mobile number",Toast.LENGTH_LONG).show();
                    }else if(edt_phone.getText().toString().trim().length()<10){
                        Toast.makeText(getApplicationContext(),"Please enter valid mobile number",Toast.LENGTH_LONG).show();
                    }else{*/
                    showDialogSTOPJOB();
                    //}
                } else {
                    showDialogSTOPJOB();
                }
            }
        });
        btn_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmergencyAlert();
            }
        });

        btn_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sLat != 0.0 && sLng != 0.0) {
                    showNavigation(sLat, sLng);
                }
            }
        });
//        emergencyImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showEmergencyAlert();
//            }
//        });
        imgNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNavigation(sLat, sLng);
            }
        });

        layout_promocode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_phone.getText().length() == 0) {
                    Toast.makeText(KerbActivity.this, "Please provide the Mobile No.", Toast.LENGTH_LONG).show();
                } else {
                    if (txt_promocode.getText().toString().equalsIgnoreCase("Promo Code")) {
                        showDialogPromocode();
                    } else {
                        Toast.makeText(KerbActivity.this, "Promo code has already Applied.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        layout_jobType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RunningJobDetails.getTypeOfTrip().equals("Local Job")) {
                    txt_jobType.setText(R.string.lbl_triptype_airport);
                    RunningJobDetails.setTypeOfTrip("Airport Job");
                    img_jobType.setBackgroundResource(R.drawable.ic_action_airplane);

                } else if (RunningJobDetails.getTypeOfTrip().equals("Airport Job")) {
                    txt_jobType.setText(R.string.lbl_triptype_local);
                    RunningJobDetails.setTypeOfTrip("Local Job");
                    img_jobType.setBackgroundResource(R.drawable.ic_action_local_job);
                }
            }
        });

//        imgSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    findPlace(v);
//                } catch (Exception e) {
//                    MyLog.appendLog(DEBUG_KEY + "imgSearch" + e.getMessage());
//                }
//            }
//        });

        edt_drop_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    findPlace(v);
                } catch (Exception e) {
                    MyLog.appendLog(DEBUG_KEY + "edt_drop_location" + e.getMessage());
                }
            }
        });

    }

    public void setUiElements() {
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (SecurityException e) {
            MyLog.appendLog(DEBUG_KEY + "setUiElements" + e.getMessage());
        }
        try {
            edt_drop_location = (EditText) findViewById(R.id.edt_drop_location);
            edt_customer_Name = (EditText) findViewById(R.id.edt_customer_Name);
            edt_phone = (EditText) findViewById(R.id.edt_phone);
            btn_emergency = (FloatingActionButton) findViewById(R.id.btn_emergency);
            btn_waitTime = (FloatingActionButton) findViewById(R.id.btn_waitTime);
            btn_navigate = (FloatingActionButton) findViewById(R.id.btn_navigate);
            btn_stop = (Button) findViewById(R.id.btnStop);
            btn_stop.setBackgroundColor(Color.parseColor("#d5971a"));
            layout_Search = (RelativeLayout) findViewById(R.id.layout_Search);
            layout_promocode = (LinearLayout) findViewById(R.id.layout_promocode);
            layout_jobType = (LinearLayout) findViewById(R.id.layout_jobType);
            layout_job_address = (LinearLayout) findViewById(R.id.layout_job_address);

            View line_view = (View) findViewById(R.id.line_view);

            imgNavigation = findViewById(R.id.imgNavigation);
            lbl_distance = (TextView) findViewById(R.id.lbl_distance);
            txt_distance = (TextView) findViewById(R.id.txt_distance);
            lbl_duration = (TextView) findViewById(R.id.lbl_duration);
            txt_duration = (TextView) findViewById(R.id.txt_duration);
            lbl_startTime = (TextView) findViewById(R.id.lbl_startTime);
            txt_startTime = (TextView) findViewById(R.id.txt_startTime);
            txt_startTime.setText(String.valueOf(RunningJobDetails.getStartTime()));
            txt_promocode = (TextView) findViewById(R.id.txt_promocode);
            lbl_currency = (TextView) findViewById(R.id.lbl_currency);
            lbl_customer_name = (TextView) findViewById(R.id.lbl_customer_name);
            lbl_customer_phone = (TextView) findViewById(R.id.lbl_customer_phone);

            lblDropLocation = (TextView) findViewById(R.id.lblDropLocation);
            txtDropLocation = (TextView) findViewById(R.id.txtDropLocation);

            lblPickUpLocation = (TextView) findViewById(R.id.lblPickUpLocation);
            txtPickUpLocation = (TextView) findViewById(R.id.txtPickUpLocation);

            lbl_currency.setText(String.valueOf(ConfigData.getCurrencyType()));
            lbl_customer_name = (TextView) findViewById(R.id.lbl_customer_name);
            lbl_customer_phone = (TextView) findViewById(R.id.lbl_customer_phone);
            lbl_job_id = (TextView) findViewById(R.id.lbl_job_id);
            txt_job_id = (TextView) findViewById(R.id.txt_job_id);
            txt_fare = (TextView) findViewById(R.id.txt_fare);

            lbl_driver_name = (TextView) findViewById(R.id.txt_driver_name);
            lbl_driver_no = (TextView) findViewById(R.id.txt_driver_number);
            lbl_driver_id = (TextView) findViewById(R.id.txt_driver_id);

//            JSONObject jsonObject = new JSONObject(pulseManager.getJsonProfileObject());
//            if (jsonObject != null) {
//                lbl_driver_name.setText(jsonObject.getString("driver_name"));
//                lbl_driver_no.setText(jsonObject.getString("driverMobileNo"));
//                lbl_driver_id.setText(jsonObject.getString("driver_no"));
//            }
            lbl_driver_name.setText("Demo");
            lbl_driver_no.setText("1231231234");
            lbl_driver_id.setText("1122");

            txt_jobType = (TextView) findViewById(R.id.txt_jobType);
            RunningJobDetails.setTypeOfTrip("Local Job");
            img_jobType = (ImageView) findViewById(R.id.img_jobType);
            imgPromoCode = (ImageView) findViewById(R.id.imgPromoCode);

//            imgSearch = (ImageView) findViewById(R.id.imgSearch);
//            emergencyImage = (ImageView) findViewById(R.id.image_emergency);

            lblDropLocation.setTypeface(textFonts);
            txtDropLocation.setTypeface(textFonts);
            lblPickUpLocation.setTypeface(textFonts);
            txtPickUpLocation.setTypeface(textFonts);

            txt_promocode.setTypeface(textFonts);
            lbl_currency.setTypeface(textFonts);
            txt_jobType.setTypeface(textFonts);

            lbl_job_id.setTypeface(textFonts, Typeface.BOLD);
            txt_job_id.setTypeface(textFonts, Typeface.BOLD);
            txt_job_id.setText("" + RunningJobDetails.getJobId());

            edt_drop_location.setTypeface(textFonts, Typeface.BOLD);
            lbl_customer_name.setTypeface(textFonts, Typeface.BOLD);
            lbl_customer_phone.setTypeface(textFonts, Typeface.BOLD);

            txt_distance.setTypeface(textFonts);
            txt_duration.setTypeface(textFonts);
            txt_startTime.setTypeface(textFonts);

            txt_distance.setTypeface(textShadowFonts, Typeface.NORMAL);
            txt_duration.setTypeface(textShadowFonts, Typeface.NORMAL);
            txt_startTime.setTypeface(textShadowFonts, Typeface.NORMAL);
            txt_fare.setTypeface(textShadowFonts, Typeface.NORMAL);

            lbl_distance.setTypeface(textFonts, Typeface.BOLD);
            lbl_duration.setTypeface(textFonts, Typeface.BOLD);
            lbl_startTime.setTypeface(textFonts, Typeface.BOLD);
            lbl_currency.setTypeface(textFonts, Typeface.BOLD);

            edt_drop_location.setTypeface(textFonts);
            edt_customer_Name.setTypeface(textFonts);
            edt_phone.setTypeface(textFonts);
            btn_stop.setTypeface(textFonts, Typeface.BOLD);
            btn_stop.setText(R.string.btn_stop);
            btn_emergency.setVisibility(View.VISIBLE);
            if (RunningJobDetails.getJobId().startsWith("K")) {
                layout_job_address.setVisibility(View.GONE);
            } else {
                // else if(RunningJobDetails.getJobId().contains("R")){
                layout_Search.setVisibility(View.GONE);
                HashMap<String, String> callcenter_configData = db.getCallCenterConfigInfo();
                ConfigData.setFixedFare(callcenter_configData.get("fixedFare"));
                ConfigData.setBaseKm(Long.parseLong(callcenter_configData.get("baseKms")));
                ConfigData.setBaseHours(Double.parseDouble(callcenter_configData.get("baseHours")));
                ConfigData.setExtraKmRate(Double.parseDouble(callcenter_configData.get("extraKmRate")));
                ConfigData.setExtraHourRate(Double.parseDouble(callcenter_configData.get("extraHourRates")));
                ConfigData.setCurrencyType(ConfigData.getCurrencyType());
                RunningJobDetails.setTotalTripFare(Double.parseDouble(callcenter_configData.get("fixedFare")));

                edt_customer_Name.setFocusable(false);
                edt_phone.setFocusable(false);
                txtPickUpLocation.setText(String.valueOf(RunningJobDetails.getPickupLoc()));
                txtDropLocation.setText(String.valueOf(RunningJobDetails.getDropLoc()));
                edt_customer_Name.setText(String.valueOf(RunningJobDetails.getCustomerName()));
                edt_phone.setText(String.valueOf(RunningJobDetails.getMobileNo()));
                edt_drop_location.setText(String.valueOf(RunningJobDetails.getDropLoc()));
                sLat = Double.parseDouble(RunningJobDetails.getDropLatitude());
                sLng = Double.parseDouble(RunningJobDetails.getDropLongitude());
                //  createPointsOnMap(RunningJobDetails.getDropLatitude(),RunningJobDetails.getDropLongitude(),2);
            }
            if (RunningJobDetails.getPromoCode().equalsIgnoreCase("Promo Code")) {
                txt_promocode.setText("Promo Code");
            } else {
                txt_promocode.setText(RunningJobDetails.getPromoCode());
            }
            //txt_fare.setText(String.valueOf(Integer.toString((int) Math.round(RunningJobDetails.getTotalTripFare()))));
            txt_fare.setText(new DecimalFormat("0.00").format(Double.valueOf(RunningJobDetails.getTotalTripFare())));
            txt_distance.setText(String.valueOf(RunningJobDetails.getTotalTripDist()));
            txt_duration.setText(String.valueOf(RunningJobDetails.getTotalTripDuration()));

        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "SetUiElements" + e.getMessage());
        }
    }

    public void findPlace(View view) {
        try {
            if (!Places.isInitialized()) {
                Places.initialize(KerbActivity.this, BuildConfig.G_API_KEY);
            }
            List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(KerbActivity.this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "findPlace" + e.getMessage());
        }
    }

    public void showDialogPromocode() {
        try {
            LanguageController.setLanguage(KerbActivity.this);
            dialogPromocode = new Dialog(this);
            dialogPromocode.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogPromocode.setContentView(R.layout.dialog_promocode);
            dialogPromocode.setCanceledOnTouchOutside(false);
            dialogPromocode.setCancelable(true);
            dialogPromocode.show();

            lbl_dig_promocode = (TextView) dialogPromocode.findViewById(R.id.lbl_dig_promocode);
            edt_dig_promocode = (EditText) dialogPromocode.findViewById(R.id.edt_dig_promocode);
            btn_dig_promocode = (Button) dialogPromocode.findViewById(R.id.btn_dig_promocode);
            //Button btn_dig_promocode_cancel = (Button) dialogPromocode.findViewById(R.id.btn_dig_promocode_cancel);
            edt_dig_promocode.setTypeface(textFonts);
            btn_dig_promocode.setTypeface(textFonts);
            lbl_dig_promocode.setTypeface(textFonts);

            btn_dig_promocode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edt_dig_promocode.getText().toString().length() != 0) {
                        txt_promocode.setText(edt_dig_promocode.getText().toString());
                        dialogPromocode.dismiss();
                        if (NetworkStatus.isInternetPresent(KerbActivity.this).equals("On")) {
                            validatePromoCode();
                        } else {
                            NetworkCheckDialog.showConnectionTimeOut(KerbActivity.this);
                        }
                    } else {
                        Toast.makeText(KerbActivity.this, "Please enter the promo code.", Toast.LENGTH_LONG).show();
                    }
                }
            });
//            btn_dig_promocode_cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialogPromocode.dismiss();
//                }
//            });
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "showDialogPromocode" + e.getMessage());
        }
    }

    public void showEmergencyAlert() {
        if (NetworkStatus.isInternetPresent(KerbActivity.this).equals("On")) {
            EmergencyAlert em = new EmergencyAlert();
            //em.emergency_alert(getApplicationContext(), "108", "ACTIVITY");
            em.emergency_alert(KerbActivity.this, "108", "ACTIVITY");
        } else {
            NetworkCheckDialog.showConnectionTimeOut(KerbActivity.this);
        }
    }

    public void startBillActivity(String parkingFee) {
        if (NetworkStatus.isInternetPresent(KerbActivity.this).equals("On")) {
            getBillRequestV2(parkingFee);
        } else {
            NetworkCheckDialog.showConnectionTimeOut(KerbActivity.this);
        }
        stoptimertask();
    }

    public void showDialogSTOPJOB() {
        try {
            dialogStop = new Dialog(this);
            dialogStop.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogStop.setContentView(R.layout.dialog_stop);
            dialogStop.setCanceledOnTouchOutside(false);
            dialogStop.setCancelable(false);
            dialogStop.show();

            are_you_sure = (TextView) dialogStop.findViewById(R.id.are_you_sure);
            are_you_sure.setTypeface(textFonts);

            dialog_btn_no = (Button) dialogStop.findViewById(R.id.dialog_btn_no);
            dialog_btn_no.setTypeface(textFonts);
            // if button is clicked, close the custom dialog
            dialog_btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogStop.dismiss();
                }
            });

            dialog_btn_yes = (Button) dialogStop.findViewById(R.id.dialog_btn_yes);
            dialog_btn_yes.setTypeface(textFonts);
            dialog_btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogStop.dismiss();
//                    GpsData.setEndGPSOdo(GpsData.getGpsOdometer());
//                    startBillActivity();
                    showDialogParkingFareJOB();
                }
            });
        } catch (Exception Eww) {

        }
    }

    public void showDialogParkingFareJOB() {
        try {
            dialogParking = new Dialog(this);
            dialogParking.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogParking.setContentView(R.layout.dialog_parking);
            dialogParking.setCanceledOnTouchOutside(false);
            dialogParking.setCancelable(false);
            dialogParking.show();

            are_you_sure = (TextView) dialogParking.findViewById(R.id.are_you_sure);
            are_you_sure.setTypeface(textFonts);

            edt_parking_fare = (EditText) dialogParking.findViewById(R.id.edt_parking_fare);
            edt_parking_fare.setTypeface(textFonts);

            dialog_btn_no = (Button) dialogParking.findViewById(R.id.dialog_btn_no);
            dialog_btn_no.setTypeface(textFonts);
            // if button is clicked, close the custom dialog
            dialog_btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogParking.dismiss();
                }
            });

            dialog_btn_yes = (Button) dialogParking.findViewById(R.id.dialog_btn_yes);
            dialog_btn_yes.setTypeface(textFonts);
            dialog_btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogParking.dismiss();
                    GpsData.setEndGPSOdo(GpsData.getGpsOdometer());
                    if(edt_parking_fare.equals(""))
                        startBillActivity("0");
                    else
                        startBillActivity(edt_parking_fare.getText().toString());
                }
            });
        } catch (Exception Eww) {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng latlng = place.getLatLng();
                sLat = latlng.latitude;
                sLng = latlng.longitude;
                edt_drop_location.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

            } else if (resultCode == RESULT_CANCELED) {

            }
        }/* else if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == RESULT_OK) {
                startService(new Intent(KerbActivity.this, FloatingViewService.class));
            } else {
                Toast.makeText(this, "Draw over other app permission not available. Closing the application", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // this.stopService(new Intent(this, FloatingViewService.class));
        LocationManager locationManager = (LocationManager) AppController.getInstance().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsGsmStatusDialog(KerbActivity.this, textFonts, "Please enable the GPS", "Kerb");
        } else if (!NetworkStatus.isInternetPresent(KerbActivity.this).equals("On")) {
            showGpsGsmStatusDialog(KerbActivity.this, textFonts, "Please Turn on Internet", "Kerb");
        }
        //LanguageController.setLanguage(KerbActivity.this);
    }

    public void initializeTimerTask() {
        try {
            timerTask = new TimerTask() {
                public void run() {
                    android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                    handler.post(new Runnable() {
                        public void run() {
                            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                            calculateFare();
                        }
                    });
                }
            };
        } catch (Exception e) {
            System.out.println("Exception Inside the startTimerTask");
        }
    }

    private void calculateFare() {
        long diffMinutes = 0;
        double totaltripfare = 0.0;
        Double getbaseHours;
        double tripdist = 0.0, additionalKms = 0.0, additionalKmFare = 0, additionalHours, additionalRateHours, waitingTimeInMin;
        if (RunningJobDetails.getJobId().startsWith("K")) {
            getbaseHours = ConfigData.getBaseHours() * 60;
        } else {
            getbaseHours = ConfigData.getBaseHours();
        }
        //GpsData.setEndGPSOdo(GpsData.getGpsOdometer());

        try {
            java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date date1 = df.parse(RunningJobDetails.getTripStartDateTime());
            Calendar cal = Calendar.getInstance();
            String dateInString = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date2 = formatter.parse(dateInString);
            long diff = date2.getTime() - date1.getTime();
            int seconds = (int) ((diff / 1000) % 60);
            diffMinutes = (diff / 1000 - seconds) / 60;
            secondsInLong = seconds;
            secondsInLong = secondsInLong + (diffMinutes * 60);

            int speedValue = 0;
            double speedValuedouble = 0.0;
            try {
                Double newData = Double.valueOf(GpsData.getGpsSpeed());
                speedValuedouble = newData;
                //Toast.makeText(getApplicationContext(), "Speed : " + speedValuedouble, Toast.LENGTH_SHORT).show();
                MyLog.appendLog(DEBUG_KEY + " Speed " + speedValuedouble);
                speedValue = newData.intValue();
            } catch (Exception ex) {
                //Setting speed value to 0
            }

            if (speedValuedouble > 4.88) {
                if (!pulseManager.isPulse()) {
                    System.out.println("calculateFare --- Speed is greater than 4 : " + GpsData.getGpsSpeed());
                    MyLog.appendLog("calculateFare > 3 " + "Lat -> " + GpsData.getLatitude() + " Lon -> " + GpsData.getLongitude() + " Speed -> " + GpsData.getGpsSpeed());
                    GpsData.setEndGPSOdo(GpsData.getGpsOdometer());
                    //CALCULATE TRIP DISTANCE
                    tripdist = getDistance(GpsData.getLatitude(), GpsData.getLongitude(), GpsData.getGpsLatitude_Prev(), GpsData.getGpsLongitude_Prev());
//                    MyLog.appendLog("calculateFare getDistance 1 " + tripdist);
                    //CALCULATE TRIP DISTANCE
                    tripdist = getDistance(GpsData.getGpsLatitude_Prev(), GpsData.getGpsLongitude_Prev(), GpsData.getLatitude(), GpsData.getLongitude());
                    //BELOW CODE MAY BE A CRASH POINT
//                    MyLog.appendLog("calculateFare getDistance 2 " + tripdist);
                    //CALCULATE TRIP DISTANCE
                    tripdist = Math.abs(GpsData.getEndGPSOdo() - GpsData.getStartGPSOdo());
//                    MyLog.appendLog("calculateFare getDistance 3 " + tripdist);
                    MyLog.appendLog("calculateFare " + "EndGPSOdo -> " + GpsData.getEndGPSOdo() + " StartGPSOdo -> " + GpsData.getStartGPSOdo() + " tripdist -> " + tripdist);
                }
            } else {
                if (speedValuedouble < 3.0) {
                    if (!pulseManager.isPulse()) {
                        System.out.println("calculateFare --- Speed is less than 3 :" + GpsData.getGpsSpeed());
                        int waittime = Integer.valueOf(RunningJobDetails.getWaitingTime());
                        RunningJobDetails.setWaitingTime(String.valueOf(waittime + 1));
                        //CALCULATE TRIP DISTANCE -- commented on 9 may 2020 - dhiraj
                        tripdist = Double.valueOf(RunningJobDetails.getTotalTripDist());
                        MyLog.appendLog("calculateFare < 2 " + "Lat -> " + GpsData.getLatitude() + " Lon -> " + GpsData.getLongitude() + " Speed -> " + GpsData.getGpsSpeed());
                    }
                } else {
                    if (!pulseManager.isPulse()) {
                        MyLog.appendLog("calculateFare speed low ");
                        tripdist = Double.valueOf(RunningJobDetails.getTotalTripDist());
                        //GpsData.setEndGPSOdo(GpsData.getGpsOdometer());
                    }
                }
            }

            if (pulseManager.isPulse()) {
                tripdist = pulseManager.getTripDistance();
                GpsData.setGpsOdometer(pulseManager.getDoubleOdometer());
                RunningJobDetails.setWaitingTime("" + pulseManager.getTripWating());
                MyLog.appendLog("Pulse mode enabled getDistance 2 " + tripdist);
            }
            //increamentDistance =increamentDistance + 0.100;
            //tripdist = tripdist + increamentDistance;
            RunningJobDetails.setTotalTripDist(String.valueOf(round(tripdist, 2, BigDecimal.ROUND_HALF_UP)));

            additionalKms = (tripdist > (float) ConfigData.getBaseKm()) ? (tripdist - (float) ConfigData.getBaseKm()) : 0;

        } catch (Exception ex) {
            if (Double.valueOf(txt_distance.getText().toString()) > 0) {
                // DO NOTHING
                tripdist = Double.valueOf(txt_distance.getText().toString());
            } else {
                tripdist = 0.0;
            }
        }

        try {

            if (additionalKms > 0) {
                if (tripdist > (float) ConfigData.getBaseKm()) {
                    additionalKmFare = additionalKms * ConfigData.getExtraKmRate();
                    totaltripfare = Double.parseDouble(ConfigData.getFixedFare()) + additionalKmFare;
                    RunningJobDetails.setTotalTripFare(round(totaltripfare, 2, BigDecimal.ROUND_HALF_UP));
                    RunningJobDetails.setRunningFare(round(totaltripfare, 2, BigDecimal.ROUND_HALF_UP));
                    RunningJobDetails.setTotalTripDuration(String.valueOf(diffMinutes));
                    RunningJobDetails.setTotalTripDist(String.valueOf(round(tripdist, 2, BigDecimal.ROUND_HALF_UP)));
                }
            } else {
                RunningJobDetails.setTotalTripFare(Double.parseDouble(ConfigData.getFixedFare()));
                RunningJobDetails.setRunningFare(Double.parseDouble(ConfigData.getFixedFare()));
                RunningJobDetails.setTotalTripDuration(String.valueOf(diffMinutes));
                RunningJobDetails.setTotalTripDist(RunningJobDetails.getTotalTripDist());
            }



            if (diffMinutes > getbaseHours) {
                additionalKmFare = additionalKms * ConfigData.getExtraKmRate();
                additionalHours = diffMinutes - getbaseHours;

                additionalRateHours = additionalHours * ConfigData.getExtraHourRate();


                totaltripfare = Double.parseDouble(ConfigData.getFixedFare()) + additionalKmFare + additionalRateHours;

                RunningJobDetails.setTotalTripFare(round(totaltripfare, 2, BigDecimal.ROUND_HALF_UP));
                RunningJobDetails.setRunningFare(round(totaltripfare, 2, BigDecimal.ROUND_HALF_UP));

                RunningJobDetails.setTotalTripDuration(String.valueOf(diffMinutes));
                RunningJobDetails.setTotalTripDist(String.valueOf(round(tripdist, 2, BigDecimal.ROUND_HALF_UP)));
            }

        } catch (Exception ex) {
            Log.e(TAG, "743 calculateFare: " + ex.getMessage());
        }

        // calculate waiting time to show in running fare
        double waitingTime = 0;

        if (pulseManager.isPulse()) {
            //IN PULSE WAITING DIVIDE BY MINUTE
            waitingTime = pulseManager.getTripWating();
            waitingTime = waitingTime / 60;
            MyLog.appendLog(DEBUG_KEY + "W : " + waitingTime);
        } else {
            // IN GPS WAITING DIVIDE BY TIME
            waitingTime = Double.valueOf(RunningJobDetails.getWaitingTime());
            waitingTime = waitingTime / 12; // As thread is running every 5 seconds, so divide by 12 to get actual waiting time
        }
        long waitingValue = (int) waitingTime;

        MyLog.appendLog("Waiting value " + waitingValue);

        int chargeableWaitingTime = 0;
        Log.e(TAG, "calculateFare: " + pulseManager.getWaitingMin());

//        if(pulseManager.isHotelJob() || pulseManager.isEmirate()) {
//            chargeableWaitingTime = (int) waitingValue;
//        }else{
//            chargeableWaitingTime = (int) waitingValue - pulseManager.getWaitingMin();
//        }

        chargeableWaitingTime = (int) waitingValue - pulseManager.getWaitingMin();

        Log.e(TAG, "calculateFare: " + chargeableWaitingTime);
        MyLog.appendLog("Chargeable Waiting Time " + chargeableWaitingTime);

        //SET RUNNING FARE TO SHOW ON METER
        if (tripdist <= pulseManager.getMinDistance() && !pulseManager.isHotelJob()) {
            if (chargeableWaitingTime > 0) {
                RunningJobDetails.setRunningFare(pulseManager.getStartingFare() + (chargeableWaitingTime * pulseManager.getWaitingRate()));
            } else
                RunningJobDetails.setRunningFare(pulseManager.getStartingFare());
            MyLog.appendLog("cal tripdist < pulsemanager.getMinDistance dis:" + tripdist + " mind:" + pulseManager.getMinDistance() + " minf:" + pulseManager.getStartingFare());
        } else {
            double startDistance = pulseManager.getMinDistance();
            Log.e(TAG, "calculateFare: 1"+startDistance );
            double distance = pulseManager.getTripDistance();

            if(pulseManager.isHotelJob())
                startDistance = 0.01;

            Log.e(TAG, "calculateFare: 2 "+startDistance );
            startDistance = startDistance - 0.147;

            distance = distance - startDistance;
            distance = distance * 1000;

            int valueDistance = (int) (distance / 147);
            double runFare = valueDistance * 0.25;
            Log.e(TAG, "calculateFare: d 3 "+startDistance );
            runFare = runFare + pulseManager.getStartingFare();
            RunningJobDetails.setRunningFare(runFare);
            MyLog.appendLog(DEBUG_KEY + " DISTANCE " + distance);
            MyLog.appendLog(DEBUG_KEY + " RUNNINGF " + runFare);
            if (chargeableWaitingTime > 0) {
                MyLog.appendLog(DEBUG_KEY + " WAITTIME " + chargeableWaitingTime);
                RunningJobDetails.setRunningFare(RunningJobDetails.getRunningFare() + (chargeableWaitingTime * pulseManager.getWaitingRate()));
            }
        }
        MyLog.appendLog(DEBUG_KEY + " DISTANCE " + RunningJobDetails.getTotalTripDist());
        MyLog.appendLog(DEBUG_KEY + " TIMEWAIT " + RunningJobDetails.getWaitingTime());
        MyLog.appendLog(DEBUG_KEY + " RUN-FARE : " + RunningJobDetails.getRunningFare());

//        Emirate and Toll check
//        PASS RUN TIME GEO FENCE
//        if(pulseManager.getGpsCounter() == 35) //Dubai
//            GpsData.setTestLocation(25.32899691510044,55.350908052716235);
//        else if(pulseManager.getGpsCounter() == 70) //abu dhabi
//            GpsData.setTestLocation(24.79592255226232,55.79156649829534);
//        else if(pulseManager.getGpsCounter() == 80) // alain
//            GpsData.setTestLocation(24.20424063011575,55.567653507199594);
//        else if(pulseManager.getGpsCounter() == 38)//toll
//            GpsData.setTestLocation(25.114463599270294,55.19057996945783);
//        else if(pulseManager.getGpsCounter() == 12)//toll
//            GpsData.setTestLocation(25.250803116762004,55.320657787044695);
//        else if(pulseManager.getGpsCounter() == 16)//toll
//            GpsData.setTestLocation(25.285852431430758,55.359194345902765);
//        else if(pulseManager.getGpsCounter() == 105)//toll
//            GpsData.setTestLocation(25.291604429896555,55.35863879089489);
//        else if(pulseManager.getGpsCounter() == 120)//toll
//            GpsData.setTestLocation(25.249001724630737,55.387172453205174);
//        else if(pulseManager.getGpsCounter() == 135)//toll
//            GpsData.setTestLocation(25.191232144166143,55.25972458659494);
//        else if(pulseManager.getGpsCounter() == 150)//toll
//            GpsData.setTestLocation(25.232651932152038,55.33815347632495);
//        else if(pulseManager.getGpsCounter() == 150)//toll
//            GpsData.setTestLocation(25.23009388376831,55.33377913154634);
//        pulseManager.setGpsCounter((pulseManager.getGpsCounter() + 1));
//        Log.e(TAG, "calculateFare: GPS "+pulseManager.getGpsCounter() );
//        checkGeofenceOrToll(GpsData.getTestLatitude(),GpsData.getTestLongitude());

        if(pulseManager.getGpsCounter() % 2 == 0) {
            pulseManager.setGpsCounter((pulseManager.getGpsCounter() + 1));
            checkGeofenceOrToll(GpsData.getLatitude(), GpsData.getLongitude());
        }else{
            pulseManager.setGpsCounter((pulseManager.getGpsCounter() + 1));
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //txt_fare.setText(String.valueOf(Integer.toString((int) Math.round(RunningJobDetails.getTotalTripFare()))));
                //txt_fare.setText(String.valueOf(Integer.toString((int) Math.round(RunningJobDetails.getTotalTripFare()))));
                //txt_fare.setText(new DecimalFormat("0.00").format(Double.valueOf(RunningJobDetails.getTotalTripFare())));
                txt_fare.setText(new DecimalFormat("0.00").format(Double.valueOf((RunningJobDetails.getRunningFare() + pulseManager.getExtraFare() + pulseManager.getExtraTollFare()))));
                txt_distance.setText(String.valueOf(RunningJobDetails.getTotalTripDist()));
                txt_duration.setText(String.valueOf(RunningJobDetails.getTotalTripDuration()));

                if (pulseManager.isEmirate1check()) {
                    if (pulseManager.isEmirate1()) {
                        pulseManager.setEmirate1(true);
                        pulseManager.setEmirate1check(false);
                        voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.dubai);
                    }
                } else if (pulseManager.isEmirate2check()) {
                    if (pulseManager.isEmirate2()) {
                        pulseManager.setEmirate2(true);
                        pulseManager.setEmirate2check(false);
                        voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.abudhabi);
                    }
                } else if (pulseManager.isEmirate3check()) {
                    if (pulseManager.isEmirate3()) {
                        pulseManager.setEmirate3(true);
                        pulseManager.setEmirate3check(false);
                        voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.alain);
                    }
                }

                if (pulseManager.isToll1check()) {
                    if (pulseManager.isToll1()) {
                        pulseManager.setToll1(true);
                        pulseManager.setToll1check(false);
                        voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.tollgate);
                    }
                }
                if (pulseManager.isToll2check()) {
                    if (pulseManager.isToll2()) {
                        pulseManager.setToll2(true);
                        pulseManager.setToll2check(false);
                        voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.tollgate);
                    }
                }
                if (pulseManager.isToll3check()) {
                    if (pulseManager.isToll3()) {
                        pulseManager.setToll3(true);
                        pulseManager.setToll3check(false);
                        voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.tollgate);
                    }
                }
                if (pulseManager.isToll4check()) {
                    if (pulseManager.isToll4()) {
                        pulseManager.setToll4(true);
                        pulseManager.setToll4check(false);
                        voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.tollgate);
                    }
                }
                if (pulseManager.isToll5check()) {
                    if (pulseManager.isToll5()) {
                        pulseManager.setToll5(true);
                        pulseManager.setToll5check(false);
                        voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.tollgate);
                    }
                }
                if (pulseManager.isToll6check()) {
                    if (pulseManager.isToll6()) {
                        pulseManager.setToll6(true);
                        pulseManager.setToll6check(false);
                        voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.tollgate);
                    }
                }
                if (pulseManager.isToll7check()) {
                    if (pulseManager.isToll7()) {
                        pulseManager.setToll7(true);
                        pulseManager.setToll7check(false);
                        voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.tollgate);
                    }
                }

                if (pulseManager.isToll8check()) {
                    if (pulseManager.isToll8()) {
                        pulseManager.setToll8(true);
                        pulseManager.setToll8check(false);
                        voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.tollgate);
                    }
                }
            }
        });

        JSONObject infotainmentParams = new JSONObject();
        try {
            infotainmentParams.put("runningFare", String.valueOf(new DecimalFormat("0.00").format(Double.valueOf(RunningJobDetails.getRunningFare()))));
            infotainmentParams.put("distance", RunningJobDetails.getTotalTripDist());
            infotainmentParams.put("jobStartTime", RunningJobDetails.getTripStartDateTime());
            infotainmentParams.put("duration", Utils.secondToFullTime(secondsInLong));
            infotainmentParams.put("extra", "0.00");
            infotainmentParams.put("toll", "0.00");
            infotainmentParams.put("lat", String.valueOf(GpsData.getLatitude()));
            infotainmentParams.put("lon", String.valueOf(GpsData.getLongitude()));
            infotainmentParams.put("profile", new JSONObject(pulseManager.getJsonProfileObject()));
            pulseManager.setLatitude(String.valueOf(GpsData.getLatitude()));
            pulseManager.setLongitude(String.valueOf(GpsData.getLongitude()));
            pulseManager.setJsonObject(infotainmentParams.toString());
        } catch (JSONException ex) {
            Log.e(TAG, "calculateFare: infotainmentParams JSON Exception : " + ex.getMessage());
        }

        map.put("jobType", RunningJobDetails.getTypeOfTrip());
        map.put("JobId", RunningJobDetails.getJobId());
        map.put("pickupLoc", RunningJobDetails.getPickupLoc());
        map.put("dropLoc", RunningJobDetails.getDropLoc());
        map.put("pickupLatitude", RunningJobDetails.getPickupLatitude());
        map.put("pickupLongitude", RunningJobDetails.getPickupLongitude());
        map.put("waitingTime", RunningJobDetails.getWaitingTime());
        map.put("totalTripDist", RunningJobDetails.getTotalTripDist());
        map.put("totalTripDuration", RunningJobDetails.getTotalTripDuration());
        map.put("startTime", RunningJobDetails.getStartTime());
        map.put("startDate", RunningJobDetails.getStartDate());
        map.put("startDateTimes", RunningJobDetails.getTripStartDateTime());

        map.put("totalBill", String.valueOf(RunningJobDetails.getTotalTripFare()));

//        map.put("runningFare", String.valueOf(RunningJobDetails.getRunningFare()));

        db.updateRunningCurrentJobValues(map, 1);
    }

    public void startTimer() {
        try {
            timer = new Timer();
            if (pulseManager.isPulse()) {
                pulseManager.setStartOdometer();
                pulseManager.setEndOdometer();
                pulseManager.setTrip(true);
            } else {
                GpsData.setGpsSpeed("0");
            }
            initializeTimerTask();
            timer.schedule(timerTask, 0, 15000); //
        } catch (Exception e) {
            System.out.println("Exception Inside the initializeTimerTask");
        }
    }

    public void stoptimertask() {
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            System.out.println("Inside the stop timer task Exception" + e);
        }
    }

    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    public void getBillRequest() {
        String url = ConfigData.getClientURL() + APIClass.final_bill_Api;
//        String url = APIClass.billURL + APIClass.final_bill_Api;
        MyLog.appendLog(DEBUG_KEY + "getBillRequest 1");
        Log.e(TAG, "getBillRequest: "+url);
        final String status = "Status";
        Map<String, String> params = new HashMap<String, String>();
        try {
            if (RunningJobDetails.getJobId().startsWith("K")) {
                RunningJobDetails.setPickupLoc(txtPickUpLocation.getText().toString());
                RunningJobDetails.setCustomerName(edt_customer_Name.getText().toString());
                RunningJobDetails.setMobileNo(edt_phone.getText().toString());
            }
            params.put("imeiNo", Utils.getImei_no());
            params.put("jobId", RunningJobDetails.getJobId());
            params.put("customerName", RunningJobDetails.getCustomerName());
            params.put("mobileNo", RunningJobDetails.getMobileNo());
            params.put("getLat", String.valueOf(GpsData.getLattiude()));
            params.put("getLon", String.valueOf(GpsData.getLongitude()));
            params.put("tripDuration", RunningJobDetails.getTotalTripDuration());
            params.put("tripDistance", RunningJobDetails.getTotalTripDist());

            double wait;
            if (pulseManager.isPulse()) {
                wait = pulseManager.getTripWating();
                wait = wait / 60;
            } else {
                wait = Double.valueOf(RunningJobDetails.getWaitingTime());
                wait = wait / 12;
            }
            long waitingValue = Math.round(wait);

            params.put("waitingTime", String.valueOf(waitingValue));
            params.put("tripStartDateTime", RunningJobDetails.getTripStartDateTime());
            params.put("tripEndDateTime", getcurrentTimeToDisplay());
            params.put("jobType", RunningJobDetails.getTypeOfTrip());
            params.put("promoCode", txt_promocode.getText().toString());
//            params.put("hotelPickup", pulseManager.isHotelJob() ? "1" : "0");
            Log.e(TAG, "getBillRequest: "+params.toString());
            MyLog.appendLog(DEBUG_KEY + "getBillRequest" + params.toString());
            MyLog.appendLog(DEBUG_KEY + "getBillRequest 2");
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "getBillRequest" + e.getMessage());
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.alt_billing));
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        System.out.println(
                                "JsonObjet response:" + response);
                        MyLog.appendLog(DEBUG_KEY + "getBillresponse" + response.toString());
                        String convertedStatus = status.toLowerCase();
                        if (response.getInt(convertedStatus) == 0) {
                            pDialog.dismiss();
                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        } else {
                            pulseManager.setGpsCounter(0);

                            MyLog.appendLog(DEBUG_KEY + "getBillRequest 3");
                            voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.jobclosed);
                            GpsData.setEndGPSOdo(GpsData.getGpsOdometer());
                            RunningJobDetails.setTotalTripFare(Double.parseDouble(response.getString("totalTripFare")));
                            map.put("totalBill", String.valueOf(RunningJobDetails.getTotalTripFare()));
                            RunningJobDetails.setBillData(response.getString("billinHTML"));
                            Log.e(TAG, "onResponse: ----------- " + response.getString("billinHTML"));
                            db.updateCreditBal(response.getString("creditBalance"), response.optString("wallet"), 1);
                            db.updateConfigData(response.getString("creditBalance"), response.optString("wallet"), response.optString("availableBalance"), 1);

                            map.put("jobType", RunningJobDetails.getJobType());
                            map.put("JobId", RunningJobDetails.getJobId());
                            map.put("pickupLoc", RunningJobDetails.getPickupLoc());
                            map.put("pickupLatitude", RunningJobDetails.getPickupLatitude());
                            map.put("pickupLongitude", RunningJobDetails.getPickupLongitude());
                            map.put("waitingTime", RunningJobDetails.getWaitingTime());
                            map.put("totalTripDist", RunningJobDetails.getTotalTripDist());
                            map.put("totalTripDuration", RunningJobDetails.getTotalTripDuration());
                            map.put("startTime", RunningJobDetails.getStartTime());
                            map.put("startDate", RunningJobDetails.getStartDate());
                            map.put("dropLoc", "" + edt_drop_location.getText().toString());
                            map.put("name", "" + edt_customer_Name.getText().toString());
                            map.put("mobileNo", "" + edt_phone.getText().toString());
                            map.put("endTime", GetDeviceTime.getTime());
                            map.put("endDate", GetDeviceTime.getDate());
                            map.put("hotspotStatus", "0");
                            ///Added at first
                            //  map.put("totalBill",String.valueOf(RunningJobDetails.getTotalTripFare()));
                            map.put("paymentMode", "0");
                            map.put("jobStatus", "Complete");
                            map.put("customerPickUpTime", "0");
                            map.put("htmlBill", RunningJobDetails.getBillData());
                            Log.e(TAG, "onResponse: ------------ " + RunningJobDetails.getRunningFare());
                            map.put("runningFare", String.valueOf(RunningJobDetails.getRunningFare()));
                            // map.put("promoCode",txt_promocode.getText().toString());
                            db.updateCurrentJobValues(map, 1);
                            MyLog.appendLog(DEBUG_KEY + "getBillRequest 4");
                            pDialog.dismiss();
                            MyLog.appendLog(DEBUG_KEY + "pDialog dissmiss 0 ");
                            Intent intent = new Intent(KerbActivity.this, BillActivity.class);
                            MyLog.appendLog(DEBUG_KEY + "pDialog dissmiss 1 ");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            MyLog.appendLog(DEBUG_KEY + "pDialog dissmiss 2 ");
                            finish();
                            startActivity(intent);
                        }
                    } else {
                        pDialog.dismiss();
                        MyLog.appendLog(DEBUG_KEY + "Response is null");
                        Toast.makeText(getApplicationContext(), "Server Returns Null", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    pDialog.dismiss();
                    MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Time out.Please Try again", Toast.LENGTH_LONG).show();
                pDialog.hide();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        System.out.println("JsonObjet Request:" + req);
        AppController.getInstance().addToRequestQueue(req);
    }
    public void getBillRequestV2(String parkingFee) {
        String url = ConfigData.getClientURL() + APIClass.final_bill_V2_Api;
//        String url = APIClass.billURL + APIClass.final_bill_Api;
        MyLog.appendLog(DEBUG_KEY + "getBillRequest 1");
        Log.e(TAG, "getBillRequest: "+url);
        final String status = "Status";
        Map<String, String> params = new HashMap<String, String>();
        try {
            if (RunningJobDetails.getJobId().startsWith("K")) {
                RunningJobDetails.setPickupLoc(txtPickUpLocation.getText().toString());
                RunningJobDetails.setCustomerName(edt_customer_Name.getText().toString());
                RunningJobDetails.setMobileNo(edt_phone.getText().toString());
            }
            params.put("imeiNo", Utils.getImei_no());
            params.put("jobId", RunningJobDetails.getJobId());
            params.put("customerName", RunningJobDetails.getCustomerName());
            params.put("mobileNo", RunningJobDetails.getMobileNo());
            params.put("getLat", String.valueOf(GpsData.getLattiude()));
            params.put("getLon", String.valueOf(GpsData.getLongitude()));
            params.put("tripDuration", RunningJobDetails.getTotalTripDuration());
            params.put("tripDistance", RunningJobDetails.getTotalTripDist());

            double wait;
            if (pulseManager.isPulse()) {
                wait = pulseManager.getTripWating();
                wait = wait / 60;
            } else {
                wait = Double.valueOf(RunningJobDetails.getWaitingTime());
                wait = wait / 12;
            }
            long waitingValue = Math.round(wait);

            params.put("waitingTime", String.valueOf(waitingValue));
            params.put("tripStartDateTime", RunningJobDetails.getTripStartDateTime());
            params.put("tripEndDateTime", getcurrentTimeToDisplay());
            params.put("jobType", RunningJobDetails.getTypeOfTrip());
            params.put("promoCode", txt_promocode.getText().toString());
            params.put("parkingFee", parkingFee);
//            params.put("hotelPickup", pulseManager.isHotelJob() ? "1" : "0");
            Log.e(TAG, "getBillRequest: "+params.toString());
            MyLog.appendLog(DEBUG_KEY + "getBillRequest" + params.toString());
            MyLog.appendLog(DEBUG_KEY + "getBillRequest 2");
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "getBillRequest" + e.getMessage());
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.alt_billing));
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        System.out.println(
                                "JsonObjet response:" + response);
                        MyLog.appendLog(DEBUG_KEY + "getBillresponse" + response.toString());
                        String convertedStatus = status.toLowerCase();
                        if (response.getInt(convertedStatus) == 0) {
                            pDialog.dismiss();
                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        } else {
                            pulseManager.setGpsCounter(0);

                            MyLog.appendLog(DEBUG_KEY + "getBillRequest 3");
                            voiceSoundPlayer.playVoice(KerbActivity.this, R.raw.jobclosed);
                            GpsData.setEndGPSOdo(GpsData.getGpsOdometer());
                            RunningJobDetails.setTotalTripFare(Double.parseDouble(response.getString("totalTripFare")));
                            map.put("totalBill", String.valueOf(RunningJobDetails.getTotalTripFare()));
                            RunningJobDetails.setBillData(response.getString("billinHTML"));
                            Log.e(TAG, "onResponse: ----------- " + response.getString("billinHTML"));
                            db.updateCreditBal(response.getString("creditBalance"), response.optString("wallet"), 1);
                            db.updateConfigData(response.getString("creditBalance"), response.optString("wallet"), response.optString("availableBalance"), 1);

                            map.put("jobType", RunningJobDetails.getJobType());
                            map.put("JobId", RunningJobDetails.getJobId());
                            map.put("pickupLoc", RunningJobDetails.getPickupLoc());
                            map.put("pickupLatitude", RunningJobDetails.getPickupLatitude());
                            map.put("pickupLongitude", RunningJobDetails.getPickupLongitude());
                            map.put("waitingTime", RunningJobDetails.getWaitingTime());
                            map.put("totalTripDist", RunningJobDetails.getTotalTripDist());
                            map.put("totalTripDuration", RunningJobDetails.getTotalTripDuration());
                            map.put("startTime", RunningJobDetails.getStartTime());
                            map.put("startDate", RunningJobDetails.getStartDate());
                            map.put("dropLoc", "" + edt_drop_location.getText().toString());
                            map.put("name", "" + edt_customer_Name.getText().toString());
                            map.put("mobileNo", "" + edt_phone.getText().toString());
                            map.put("endTime", GetDeviceTime.getTime());
                            map.put("endDate", GetDeviceTime.getDate());
                            map.put("hotspotStatus", "0");
                            ///Added at first
                            //  map.put("totalBill",String.valueOf(RunningJobDetails.getTotalTripFare()));
                            map.put("paymentMode", "0");
                            map.put("jobStatus", "Complete");
                            map.put("customerPickUpTime", "0");
                            map.put("htmlBill", RunningJobDetails.getBillData());
                            Log.e(TAG, "onResponse: ------------ " + RunningJobDetails.getRunningFare());
                            map.put("runningFare", String.valueOf(RunningJobDetails.getRunningFare()));
                            // map.put("promoCode",txt_promocode.getText().toString());
                            db.updateCurrentJobValues(map, 1);
                            MyLog.appendLog(DEBUG_KEY + "getBillRequest 4");
                            pDialog.dismiss();
                            MyLog.appendLog(DEBUG_KEY + "pDialog dissmiss 0 ");
                            Intent intent = new Intent(KerbActivity.this, BillActivity.class);
                            MyLog.appendLog(DEBUG_KEY + "pDialog dissmiss 1 ");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            MyLog.appendLog(DEBUG_KEY + "pDialog dissmiss 2 ");
                            finish();
                            startActivity(intent);
                        }
                    } else {
                        pDialog.dismiss();
                        MyLog.appendLog(DEBUG_KEY + "Response is null");
                        Toast.makeText(getApplicationContext(), "Server Returns Null", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    pDialog.dismiss();
                    MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Time out.Please Try again", Toast.LENGTH_LONG).show();
                pDialog.hide();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        System.out.println("JsonObjet Request:" + req);
        AppController.getInstance().addToRequestQueue(req);
    }

    public String getcurrentTimeToDisplay() {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d", now.year, (now.month + 1), now.monthDay, now.hour, now.minute, now.second);
        return currentTime;
    }

    public void showNavigation(double sLat, double sLng) {
        try {
            if ((sLat != 0.0) && (sLng != 0.0)) {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                } else {
                    startService(new Intent(KerbActivity.this, FloatingViewService.class));
                }*/
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + sLat + "," + sLng + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } else {
                Toast.makeText(getApplicationContext(), "Please select the Drop Location.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "showNavigation" + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {

    }

    public class GetLocationAsync extends AsyncTask<String, Void, String> {
        // boolean duplicateResponse;
        StringBuilder str;
        private double xLat, yLong;
        LatLng geoLatLong;

        public GetLocationAsync(double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            xLat = latitude;
            yLong = longitude;
            geoLatLong = new LatLng(xLat, yLong);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                geocoder = new Geocoder(KerbActivity.this, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(xLat, yLong, 1);
                str = new StringBuilder();
                if (Geocoder.isPresent() && addresses.size() != 0) {
                    android.location.Address returnAddress = addresses.get(0);
                    String localityString = returnAddress.getLocality();
                    String city = returnAddress.getCountryName();
                    String region_code = returnAddress.getCountryCode();
                    String zipcode = returnAddress.getPostalCode();
                    str.append(localityString + "");
                    str.append(city + "" + region_code + "");
                    str.append(zipcode + "");
                } else {
                }
            } catch (IOException e) {
                MyLog.appendLog(DEBUG_KEY + "GetLocationAsync" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                mGoogleMap.clear();
                edt_drop_location.setText(addresses.get(0).getAddressLine(0) + " , "
                        + addresses.get(0).getAddressLine(1) + " " + addresses.get(0).getAddressLine(2));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(geoLatLong));
                mGoogleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
                        .position(geoLatLong));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(geoLatLong, 10.5f), 4000, null);

            } catch (Exception e) {
                MyLog.appendLog(DEBUG_KEY + "GetLocationAsync onPostExecute" + e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_navigation:
                showNavigation(sLat, sLng);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void validatePromoCode() {
        String url = ConfigData.getClientURL() + APIClass.validate_Promo_APi;
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put("imeiNO", Utils.getImei_no());
            params.put("driverId", ConfigData.getDriverId());
            params.put("jobNo", RunningJobDetails.getJobId());
            params.put("erId", edt_phone.getText().toString());
            params.put("dateTime", getcurrentTimeToDisplay());
            params.put("promoCode", edt_dig_promocode.getText().toString());
            params.put("requestFrom", "Driver");
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "validatePromoCode" + e.getMessage());
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Validating Promo code...");
        pDialog.setCancelable(false);
        System.out.println("Inside the Validate promoCode=>" + params);
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(
                params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 0) {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        txt_promocode.setText("Promo Code");
                        RunningJobDetails.setPromoCode(txt_promocode.getText().toString());
                    } else if (response.getInt("status") == -1) {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        txt_promocode.setText("Promo Code");
                        RunningJobDetails.setPromoCode(txt_promocode.getText().toString());
                    } else {
                        db.updatePromo(edt_dig_promocode.getText().toString(), 1);
                        imgPromoCode.setBackgroundResource(R.drawable.green_tickmark);
                        txt_promocode.setText(String.valueOf(edt_dig_promocode.getText().toString()));
                        RunningJobDetails.setPromoCode(txt_promocode.getText().toString());
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                    pDialog.hide();
                } catch (Exception e) {
                    pDialog.hide();
                    MyLog.appendLog(DEBUG_KEY + "validatePromoCode onResponse" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Time out.Please Try again", Toast.LENGTH_LONG).show();
                pDialog.hide();
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

    private void createPointsOnMap(String pickLat, String pickLon, int pos) {
        try {
            LatLng latLng = new LatLng(Double.parseDouble(pickLat), Double.parseDouble(pickLon));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(marker_icon[pos]));
            mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.5f), 4000, null);
        } catch (SecurityException e) {
            MyLog.appendLog(DEBUG_KEY + "createPointsOnMap" + e.getMessage());
        }
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(GpsData.getLattiude(), GpsData.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f), 4000, null);
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
//        Log.e(TAG, "getDistance: Lat Lng values 1 " + lat1 );
//        Log.e(TAG, "getDistance: Lat Lng values 2 " + lon1 );
//        Log.e(TAG, "getDistance: Lat Lng values 3 " + lat2 );
//        Log.e(TAG, "getDistance: Lat Lng values 4 " + lon2 );
        double R = 6371000; // for haversine use R = 6372.8 km instead of 6371 km
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        //double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        // simplify haversine:
        //return 2  R  1000 * Math.asin(Math.sqrt(a));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void checkGeofenceOrToll(double lat, double lon) {
        try {
            MyLog.appendLog(DEBUG_KEY + "checkGeofenceOrToll:= " + lat + " lon:= " + lon);
            if (pulseManager.getGeoResponse().contains(",")) ;
            {
                JSONArray jsonArray = new JSONArray(pulseManager.getGeoResponse());
                if (!pulseManager.isEmirate1check()) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONObject jsonObject = new JSONObject("{\"ID\":1,\"GeofenceName\":\"Dubai-ITPL\",\"ShapeType\":2,\"ShapeCoordinates\":\"25.32899691510044,55.350908052716235|25.32838112661746,55.353119534049014|25.326834368501995,55.35498769262789|25.32024102978408,55.35329823622463|25.3167890513437,55.350393571499566|25.310233355266952,55.347660568151454|25.30701387245953,55.348317876998166|25.304725447713334,55.349833492729644|25.300264259188314,55.351263277772645|25.298596499990495,55.353723031077365|25.300344888186324,55.35794013420103|25.300715902132637,55.360869776997546|25.297728358895316,55.36415548304079|25.296874761589045,55.372336891088466|25.297805958352374,55.377428394350986|25.302229045289714,55.38632394054888|25.213969624504664,55.651524674687366|25.059419898057946,55.66335321644304|24.982548318008266,55.658289205822925|24.793334489062733,55.72785497883318|24.78904154538491,55.71663261631487|24.75385109930269,55.728187572751025|24.730639238225425,55.70581258514879|24.72039166386963,55.67232386449335|24.703281561815484,55.59591979959963|24.68713087574534,55.57825644740818|24.679054747774376,55.56976809406636|24.670510119567382,55.56007811108587|24.638666263624817,55.5232064543104|24.631861371580744,55.363682743701915|24.60536196230168,55.31001341769455|24.653282690133363,55.269445844534616|24.67848402798911,55.26564155014215|24.687963409960602,55.14906960314123|24.85579545659867,55.04211069324968|24.90868417532537,54.89328027943132|24.973455099107177,54.868035328183154|25.393516109689678,55.23169459084032|\",\"GeofenceLevel\":1,\"GeofenceType\":1}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat, lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if (inside) {
                        MyLog.appendLog(DEBUG_KEY + "INSIDE GEO FENCE ");
                        if (!pulseManager.isEmirate1()) {
                            pulseManager.setEmirate(true);
                            pulseManager.setEmirate1(true);
                            pulseManager.setEmirate1check(true);
                            MyLog.appendLog(DEBUG_KEY + " TRUE INSIDE ");
                            int runfare = pulseManager.getExtraFare();
                            runfare = runfare + 20;
                            pulseManager.setExtraFare(runfare);
//                            int emirate = GpsData.getEmirateCharges() + 17;
//                            GpsData.setExtraCharges(emirate,GpsData.getTollCharges());
                            GpsData.setExtraCharges(pulseManager.getExtraFare(),GpsData.getTollCharges());
                            if(jsonObject.has("ID"))
                                GpsData.setExtraID(jsonObject.getInt("ID"),GpsData.getTollID());
                        }
                    }
                }

                if (!pulseManager.isEmirate2check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(1);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":2,\"GeofenceName\":\"Abu Dhabi-ITPL\",\"ShapeType\":2,\"ShapeCoordinates\":\"24.803311580705262,55.8148655069032|24.79592255226232,55.79156649829534|24.800409208231674,55.75918888272432|24.788143471109,55.71736989142087|24.75424818524221,55.72835378879395|24.729075440617883,55.70371795044926|24.703045551080454,55.597454696611436|24.639111591143916,55.52569051803735|24.631667321779943,55.36362273217824|24.60429204622488,55.30865219952253|24.651469727594073,55.26867119194654|24.679029187071745,55.261493600622686|24.68672324968177,55.14826146484998|24.85547314716034,55.041273511663945|24.91236132543107,54.89664880158094|24.853827915454524,54.63058439613965|24.54817708670199,54.37275973679212|24.451671516693562,53.208799041525396|24.389804628481787,51.57002297760633|24.257328484151678,51.59023610474256|24.12659417253716,51.59122315766004|22.938712865048238,52.59192315460828|22.63234180780936,55.136888898626836|22.715988685917072,55.21392194153455|22.798780244863252,55.227327622190984|23.083806740185633,55.22150722862867|23.381310607998724,55.41723338486341|23.409256027444833,55.436332724706205|23.453574400140837,55.445819027439626|23.510688080802908,55.48401770712522|23.554559551950874,55.522903032318624|23.584041530943335,55.538912467376264|23.62861565436998,55.571401394621404|23.713730091789174,55.576424501554044|23.76108803135723,55.534755713955434|23.810941568858883,55.53222572030214|23.8438199783616,55.536562181726964|23.866599233897293,55.52884865151432|23.887491009637287,55.51838853927043|23.90541244148295,55.500901542321714|23.925557698677412,55.49751324178365|23.943248667641498,55.485243476645024|23.95499281558159,55.49795203441468|23.96485354859775,55.52302021132496|23.98959151026296,55.57933637471584|23.99703118282581,55.58553864778665|24.019523786721056,55.63190968306449|24.03547201602679,55.678526978657516|24.053299345047282,55.722741014973195|24.057363914939717,55.73194916424987|24.056098897629052,55.73995568388787|24.0513742351178,55.75837198244122|24.055114352546457,55.7730117307016|24.055405843965953,55.78009837837604|24.02534011873704,55.80156728224543|24.024808531689963,55.80998430559126|24.014555540772793,55.83282088460115|24.020738089086333,55.844432293155165|24.023470965545133,55.85810363823262|24.03175864657329,55.87240006373909|24.048018881579548,55.90751604707626|24.054384755241742,55.940952716113|24.060280073293395,55.96014149086264|24.062883544069937,55.99065991649118|24.06642740197038,56.01808843733457|24.087306388890298,56.0130356584647|24.090004835286692,56.014677673296006|24.114518018512626,56.00319882454065|24.127242846179712,55.99479864882019|24.145605663640826,55.97541214497474|24.16979496896912,55.96067173005727|24.184946823574926,55.972091234938176|24.21705197745575,55.96344940894929|24.21392820941765,55.951374355421336|24.223336117789266,55.95434674592403|24.21459864652217,55.86965431989816|24.21090014271677,55.8527047705808|24.201623941299484,55.83322522761014|24.211815566997757,55.808431390390666|24.235155794128648,55.776771098093064|24.232509444861275,55.76571017102984|24.234246059852204,55.75258930744317|24.247792870689604,55.754955267251404|24.2613382390008,55.75938116358307|24.263614838629607,55.76998686948505|24.27841036214438,55.79157890351203|24.30956065807561,55.80695382849959|24.327635661522766,55.83489733347383|24.409849740823468,55.83376288478908|24.411238885711292,55.82804581913811|24.43912923375354,55.82141073601966|24.484694153606682,55.78662131948809|24.530242577490455,55.765564813112775|24.573215930458677,55.768083758369954|24.594318943347783,55.79222464533237|24.615418399194795,55.816022209540876|24.636733662833908,55.79513366864708|24.671149295213315,55.83741651447204|24.72250637497847,55.827143905402096|24.750047702641748,55.83127731522259|24.764127829762845,55.83334402013284|24.778206360895446,55.83266414301184|24.780812996388565,55.83151954174687|24.783419577136044,55.82814334258151|24.786606577848744,55.82602580142853|24.790019354454433,55.821962380499514|24.79912001509493,55.81970140402851|24.79907315104486,55.81488019618851|\",\"GeofenceLevel\":1,\"GeofenceType\":1}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat, lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if (inside) {
                        Log.e(TAG, "INSIDE GEO FENCE ");
                        if (!pulseManager.isEmirate2()) {
                            pulseManager.setEmirate(true);
                            pulseManager.setEmirate2(true);
                            pulseManager.setEmirate2check(true);
                            Log.e(TAG, " TRUE INSIDE ");
                            int runfare = pulseManager.getExtraFare();
                            runfare = runfare + 20;
                            pulseManager.setExtraFare(runfare);
//                            int emirate = GpsData.getEmirateCharges() + 17;
//                            GpsData.setExtraCharges(emirate,GpsData.getTollCharges());
                            GpsData.setExtraCharges(pulseManager.getExtraFare(),GpsData.getTollCharges());
                            if(jsonObject.has("ID"))
                                GpsData.setExtraID(jsonObject.getInt("ID"),GpsData.getTollID());
                        }
                    }
                }
                if (!pulseManager.isEmirate3check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(2);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":23,\"GeofenceName\":\"Al Ain ITPL\",\"ShapeType\":2,\"ShapeCoordinates\":\"24.297639850271697,55.607586235013315|24.20424063011575,55.567653507199594|24.160334596032243,55.472499460187265|24.137436004150487,55.464747876134226|24.13646671288623,55.512614578214|24.12133955471748,55.57209993919403|24.100635263689583,55.59177779799373|24.086823248254326,55.61626217534811|24.07645778804611,55.63662667965562|24.076122446585345,55.65767782947094|24.051495487870575,55.7598257364896|24.056336513858376,55.780091166701624|24.024807173669362,55.80241653343708|24.021007404369623,55.81857147833378|24.01469870636824,55.83266648670704|24.02454014003782,55.85826280733616|24.042533046009368,55.89278551956684|24.060956933246516,55.961144298520395|24.063161156344933,55.9909168185857|24.065678822623283,56.01828607937367|24.0870243446171,56.01328375956089|24.09018919862808,56.01497623344929|24.12644537713602,55.99553021809132|24.14536629301806,55.97552093884022|24.170044944160335,55.9615949096349|24.18487715794911,55.97195058336527|24.217242970515553,55.963080182876894|24.214021635203707,55.95117586871655|24.223234406949654,55.95517638346226|24.2153591031518,55.871510237660715|24.211819399728128,55.85420998951466|24.201138537624534,55.83351406475575|24.212962108446636,55.80864395221741|24.234803089830955,55.776220739093134|24.232147503857604,55.76630595346958|24.23434999929868,55.752656191792795|24.247024040406117,55.75507814271421|24.261574833757862,55.76024667566688|24.262817734822367,55.767493836787054|24.278448441822352,55.79160119613678|24.3090783207141,55.806170284953424|24.312640065838494,55.811812982168505|24.320581752552833,55.82432213446171|24.375799570555,55.798827737775156|24.308709215903022,55.7420493962434|24.244713812373156,55.67771795412571|24.285251747373565,55.654928565230676|24.277195989986964,55.632375881281206|24.296366197095473,55.627332657780954|\",\"GeofenceLevel\":1,\"GeofenceType\":1}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat, lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if (inside) {
                        Log.e(TAG, "INSIDE GEO FENCE ");
                        if (!pulseManager.isEmirate3()) {
                            pulseManager.setEmirate(true);
                            pulseManager.setEmirate3(true);
                            pulseManager.setEmirate3check(true);
                            Log.e(TAG, " TRUE INSIDE ");
                            int runfare = pulseManager.getExtraFare();
                            runfare = runfare + 20;
                            pulseManager.setExtraFare(runfare);
//                            int emirate = GpsData.getEmirateCharges() + 17;
//                            GpsData.setExtraCharges(emirate,GpsData.getTollCharges());
                            GpsData.setExtraCharges(pulseManager.getExtraFare(),GpsData.getTollCharges());
                            if(jsonObject.has("ID"))
                                GpsData.setExtraID(jsonObject.getInt("ID"),GpsData.getTollID());
                        }
                    }
                }

                if (!pulseManager.isToll1check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(3);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":15,\"GeofenceName\":\"Al Brasha Toll Gate\",\"ShapeType\":2,\"ShapeCoordinates\":\"25.11524003326005,55.190062427731355|25.114463599270294,55.19057996945783|25.11574389662674,55.19214516433857|25.11694848227207,55.19386714252613|25.117453627557833,55.19291764053486|25.1161540662464,55.19140215145107|\",\"GeofenceLevel\":1,\"GeofenceType\":2},{\"ID\":16,\"GeofenceName\":\"Al Maktoum Toll Gate\",\"ShapeType\":2,\"ShapeCoordinates\":\"25.248974902030188,55.318950263622156|25.25004622683155,55.3200998875696|25.250803116762004,55.320657787044695|25.25384326008959,55.32319464734352|25.25433813472398,55.32388129285133|25.25411495621691,55.324809337170485|25.254420613633677,55.325034642727736|25.255395801202376,55.32344141057289|25.253981987822446,55.32246097956411|25.252822422057413,55.321409553630275|25.250390390496566,55.319131869060804|25.249755575291076,55.31834416212113|\",\"GeofenceLevel\":1,\"GeofenceType\":2}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat, lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if (inside) {
                        Log.e(TAG, "INSIDE GEO FENCE ");
                        if (!pulseManager.isToll1()) {
                            pulseManager.setToll1(true);
                            pulseManager.setToll1check(true);
                            pulseManager.setToll(true);
                            Log.e(TAG, "TOLL 1 INSIDE ");
                            int runfare = pulseManager.getExtraTollFare();
                            runfare = runfare + 4;
                            pulseManager.setExtraTollFare(runfare);
                            int toll = GpsData.getTollCharges() + 4;
                            GpsData.setExtraCharges(GpsData.getEmirateCharges(),toll);
                            if(jsonObject.has("ID"))
                                GpsData.setExtraID(GpsData.getEmirateID(),jsonObject.getInt("ID"));
                        }
                    }
                }
                if (!pulseManager.isToll2check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(4);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":17,\"GeofenceName\":\"Al Mamzar South Toll Gate\",\"ShapeType\":2,\"ShapeCoordinates\":\"25.288804850953138,55.359725305980994|25.285852431430758,55.359194345902765|25.284506299434373,55.3586715636257|25.283274276681563,55.357951683241396|25.282587936931897,55.35740252082134|25.28192574906837,55.356767583069924|25.2813146877572,55.356106950950874|25.281183116753404,55.35595731558669|25.281520235026335,55.35561399283279|25.281849597492375,55.3557423857307|25.28224007038314,55.35647194658275|25.282885427475875,55.35717569175853|25.284651018940917,55.358237846528425|25.28672529418078,55.35884939018382|25.288655643036407,55.35896733507487|\",\"GeofenceLevel\":1,\"GeofenceType\":2}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat, lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if (inside) {
                        Log.e(TAG, "INSIDE GEO FENCE ");
                        if (!pulseManager.isToll2()) {
                            pulseManager.setToll2(true);
                            pulseManager.setToll2check(true);
                            pulseManager.setToll(true);
                            Log.e(TAG, "TOLL 2 INSIDE ");
                            int runfare = pulseManager.getExtraTollFare();
                            runfare = runfare + 4;
                            pulseManager.setExtraTollFare(runfare);
                            int toll = GpsData.getTollCharges() + 4;
                            GpsData.setExtraCharges(GpsData.getEmirateCharges(),toll);
                            if(jsonObject.has("ID"))
                                GpsData.setExtraID(GpsData.getEmirateID(),jsonObject.getInt("ID"));
                        }
                    }
                }
                if (!pulseManager.isToll3check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(5);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":18,\"GeofenceName\":\"Al Mamzar North Toll Gate\",\"ShapeType\":2,\"ShapeCoordinates\":\"25.29118807790804,55.36145601168382|25.291604429896555,55.35863879089489|25.29428171769495,55.36024811630382|25.29658426788846,55.3607720258628|25.297544566736562,55.3612226369773|25.297258417875447,55.36200584200965|25.29563366153666,55.361405027190315|25.29394300966886,55.361114777181406|25.292145679895405,55.36091784531303|25.291549104940668,55.361127057616194|\",\"GeofenceLevel\":1,\"GeofenceType\":2}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat, lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if (inside) {
                        Log.e(TAG, "INSIDE GEO FENCE ");
                        if (!pulseManager.isToll3()) {
                            pulseManager.setToll3(true);
                            pulseManager.setToll3check(true);
                            pulseManager.setToll(true);
                            Log.e(TAG, "TOLL 3 INSIDE ");
                            int runfare = pulseManager.getExtraTollFare();
                            runfare = runfare + 4;
                            pulseManager.setExtraTollFare(runfare);
                            int toll = GpsData.getTollCharges() + 4;
                            GpsData.setExtraCharges(GpsData.getEmirateCharges(),toll);
                            if(jsonObject.has("ID"))
                                GpsData.setExtraID(GpsData.getEmirateID(),jsonObject.getInt("ID"));
                        }
                    }
                }
                if (!pulseManager.isToll4check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(6);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":19,\"GeofenceName\":\"Airport Tunnel Toll Gate\",\"ShapeType\":2,\"ShapeCoordinates\":\"25.25421789674929,55.38695054840884|25.253622063386572,55.386958595035885|25.2530601009966,55.38699260099793|25.251798276954755,55.38706384602037|25.250588521320616,55.38712982166208|25.249803036739625,55.38717541921533|25.249001724630737,55.387172453205174|25.248235119585548,55.38705080405834|25.247229227944366,55.38672598441508|25.246481631086763,55.38639096155766|25.246961979721043,55.38622734680775|25.248537031486446,55.38687004459526|25.249621543076973,55.38690759552147|25.251968666388574,55.38670374763634|25.25430023583081,55.38647844207909|\",\"GeofenceLevel\":1,\"GeofenceType\":2}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat, lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if (inside) {
                        Log.e(TAG, "INSIDE GEO FENCE ");
                        if (!pulseManager.isToll4()) {
                            pulseManager.setToll4(true);
                            pulseManager.setToll4check(true);
                            pulseManager.setToll(true);
                            Log.e(TAG, "TOLL 4 INSIDE ");
                            int runfare = pulseManager.getExtraTollFare();
                            runfare = runfare + 4;
                            pulseManager.setExtraTollFare(runfare);
                            int toll = GpsData.getTollCharges() + 4;
                            GpsData.setExtraCharges(GpsData.getEmirateCharges(),toll);
                            if(jsonObject.has("ID"))
                                GpsData.setExtraID(GpsData.getEmirateID(),jsonObject.getInt("ID"));
                        }
                    }
                }
                if (!pulseManager.isToll5check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(7);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":20,\"GeofenceName\":\"Jebel Ali Toll Gate\",\"ShapeType\":2,\"ShapeCoordinates\":\"25.058728418676537,55.126562591673355|25.058218171427644,55.127324339033585|25.060356336172877,55.12923745012273|25.062902651042542,55.13152991966065|25.063544079860613,55.13037656978425|\",\"GeofenceLevel\":1,\"GeofenceType\":2}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat, lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if (inside) {
                        Log.e(TAG, "INSIDE GEO FENCE ");
                        if (!pulseManager.isToll5()) {
                            pulseManager.setToll5(true);
                            pulseManager.setToll5check(true);
                            pulseManager.setToll(true);
                            Log.e(TAG, "TOLL 5 INSIDE ");
                            int runfare = pulseManager.getExtraTollFare();
                            runfare = runfare + 4;
                            pulseManager.setExtraTollFare(runfare);
                            int toll = GpsData.getTollCharges() + 4;
                            GpsData.setExtraCharges(GpsData.getEmirateCharges(),toll);
                            if(jsonObject.has("ID"))
                                GpsData.setExtraID(GpsData.getEmirateID(),jsonObject.getInt("ID"));
                        }
                    }
                }
                if (!pulseManager.isToll6check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(8);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":21,\"GeofenceName\":\"Al Safa Toll Gate\",\"ShapeType\":2,\"ShapeCoordinates\":\"25.20010930032325,55.26709737588339|25.199861752895558,55.26736023236685|25.198176997712956,55.26605075756665|25.19638102547604,55.26445752541181|25.19480318792851,55.26303028904888|25.193039939168834,55.261394824914674|25.191232144166143,55.25972458659494|25.19153310543571,55.259391992677095|25.193144691839546,55.26093158065164|25.195024370633273,55.2627279220928|25.196601932803517,55.26419240821493|25.19801059455277,55.26544583544854|\",\"GeofenceLevel\":1,\"GeofenceType\":2}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat, lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if (inside) {
                        Log.e(TAG, "INSIDE GEO FENCE ");
                        if (!pulseManager.isToll6()) {
                            pulseManager.setToll6(true);
                            pulseManager.setToll6check(true);
                            pulseManager.setToll(true);
                            Log.e(TAG, "TOLL 6 INSIDE ");
                            int runfare = pulseManager.getExtraTollFare();
                            runfare = runfare + 4;
                            pulseManager.setExtraTollFare(runfare);
                            int toll = GpsData.getTollCharges() + 4;
                            GpsData.setExtraCharges(GpsData.getEmirateCharges(),toll);
                            if(jsonObject.has("ID"))
                                GpsData.setExtraID(GpsData.getEmirateID(),jsonObject.getInt("ID"));
                        }
                    }
                }
                if (!pulseManager.isToll7check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(9);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":22,\"GeofenceName\":\"AL Garhoud Toll gate\",\"ShapeType\":2,\"ShapeCoordinates\":\"25.236242779528617,55.341418468903484|25.232651932152038,55.33815347632495|25.22719195235496,55.33268479026827|25.228987469926317,55.33210543312105|25.23009388376831,55.33377913154634|25.233421227085703,55.337049152369026|25.236963527788912,55.339570428843025|\",\"GeofenceLevel\":1,\"GeofenceType\":2}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat, lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if (inside) {
                        Log.e(TAG, "INSIDE GEO FENCE ");
                        if (!pulseManager.isToll7()) {
                            pulseManager.setToll7(true);
                            pulseManager.setToll7check(true);
                            pulseManager.setToll(true);
                            Log.e(TAG, "TOLL 7 INSIDE ");
                            int runfare = pulseManager.getExtraTollFare();
                            runfare = runfare + 4;
                            pulseManager.setExtraTollFare(runfare);
                            int toll = GpsData.getTollCharges() + 4;
                            GpsData.setExtraCharges(GpsData.getEmirateCharges(),toll);
                        }
                    }
                }

                if (!pulseManager.isToll8check()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(10);
//                    JSONObject jsonObject = new JSONObject("{\"ID\":22,\"GeofenceName\":\"AL Garhoud Toll gate\",\"ShapeType\":2,\"ShapeCoordinates\":\"25.236242779528617,55.341418468903484|25.232651932152038,55.33815347632495|25.22719195235496,55.33268479026827|25.228987469926317,55.33210543312105|25.23009388376831,55.33377913154634|25.233421227085703,55.337049152369026|25.236963527788912,55.339570428843025|\",\"GeofenceLevel\":1,\"GeofenceType\":2}");
                    List<LatLng> polygons = getPolygonObject(jsonObject);
                    LatLng latLng = new LatLng(lat, lon);
                    boolean inside = PolyUtil.containsLocation(latLng, polygons, false);
                    if (inside) {
                        Log.e(TAG, "INSIDE GEO FENCE ");
                        if (!pulseManager.isToll8()) {
                            pulseManager.setToll8(true);
                            pulseManager.setToll8check(true);
                            pulseManager.setToll(true);
                            Log.e(TAG, "TOLL 8 INSIDE ");
                            int runfare = pulseManager.getExtraTollFare();
                            runfare = runfare + 4;
                            pulseManager.setExtraTollFare(runfare);
                            int toll = GpsData.getTollCharges() + 4;
                            GpsData.setExtraCharges(GpsData.getEmirateCharges(),toll);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "checkGeofenceOrToll ex: " + e.getMessage());
        }
    }

    private List<LatLng> getPolygonObject(JSONObject jsonObject) {
        List<LatLng> polygon = new ArrayList<>();
        try {
            String ShapeCoordinates = jsonObject.getString("ShapeCoordinates");
            String[] stringShapeCoordinates = ShapeCoordinates.split("\\|");
            MyLog.appendLog(DEBUG_KEY + ShapeCoordinates);
            for (int i = 0; i < stringShapeCoordinates.length; i++) {
                //Log.e(TAG, "getPolygonObject: ***** "+stringShapeCoordinates[i]);
                if (stringShapeCoordinates[i].contains(",")) {
                    String[] latlng = stringShapeCoordinates[i].split(",");
                    LatLng latLng = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
                    polygon.add(latLng);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getPolygonObject: " + e.getMessage());
        }
        return polygon;
    }

}
