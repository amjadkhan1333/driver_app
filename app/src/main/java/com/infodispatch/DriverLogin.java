package com.infodispatch;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.APIs.APIClass;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.appcontroller.AppController;
import com.appcontroller.LanguageController;
import com.log.MyLog;
import com.infodispatch.R;
import com.services.PollingService;
import com.session.PulseManager;
import com.settersgetters.ConfigData;
import com.settersgetters.RunningJobDetails;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.infodispatch.GpsCheckerDialog.showGpsGsmStatusDialog;

/**
 * Created by info on 23-11-2016.
 */
public class DriverLogin extends AppCompatActivity {
    public String DEBUG_KEY = "DriverLogin";
    Button btnSignIn;
    private EditText edt_userid, edt_password;
    Typeface textFonts;
    private CoordinatorLayout coordinatorLayout;
    TextView toolbar_title, toolbar_date, txt_switch;
    Toolbar toolbar;
    SwitchCompat toolbar_switch;
    DBHelper db;
    NetworkImageView clientImage;
    private ImageLoader imageLoader;
    HashMap<String, String> map = new HashMap<String, String>();
    ProgressDialog pDialog;
    public int shiftCount;
    VoiceSoundPlayer voiceSoundPlayer = new VoiceSoundPlayer();
    private PulseManager pulseManager;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LanguageController.setLanguage(DriverLogin.this);
        setContentView(R.layout.user_login);
        try {
            db = new DBHelper(this);
            db.updateCurrentScreenVal("DRIVER_LOGIN", 1);

            pulseManager = new PulseManager(this);

            clientImage = (NetworkImageView) findViewById(R.id.clientImage);
            ConfigData.setDeviceStatus(ConfigData.Device_Status.devicestatus_logout.ordinal());
            textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbar_title.setText(R.string.user_Login);
            toolbar_title.setTypeface(textFonts, Typeface.BOLD);
            toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);
            toolbar_date.setVisibility(View.GONE);
            toolbar_switch = (SwitchCompat) toolbar.findViewById(R.id.toolbar_switch);
            toolbar_switch.setVisibility(View.GONE);
            txt_switch = (TextView) toolbar.findViewById(R.id.txt_switch);
            txt_switch.setVisibility(View.GONE);
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "user_login" + e.getMessage());
        }
        setUiElements();
        //LanguageController.setLanguage(DriverLogin.this);
        map = db.getClientInfo();
        imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext()).getImageLoader();
        imageLoader.get(map.get("clientImg"), ImageLoader.getImageListener(clientImage,
                R.drawable.infotrack_logo, android.R.drawable
                        .ic_dialog_alert));
        clientImage.setImageUrl(map.get("clientImg"), imageLoader);
        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    OnLoginButtonClick();
                }
                return false;
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OnLoginButtonClick();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void setUiElements() {
        try {
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            edt_userid = (EditText) findViewById(R.id.edt_userid);
            edt_userid.setTypeface(textFonts);
            edt_password = (EditText) findViewById(R.id.edt_password);
            edt_password.setTypeface(textFonts);
            btnSignIn = (Button) findViewById(R.id.btnSignIn);
            btnSignIn.setTypeface(textFonts, Typeface.BOLD);
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "setUiElements" + e.getMessage());
        }
    }

    private void OnLoginButtonClick() {
        try {
            if (edt_userid.getText().length() == 0 && edt_password.getText().length() == 0) {
                showSnackBarMessage(getResources().getString(R.string.driver_both_validate));
            } else if (edt_userid.getText().length() == 0) {
                showSnackBarMessage(getResources().getString(R.string.driver_id_validate));
            } else if (edt_password.getText().length() == 0) {
                showSnackBarMessage(getResources().getString(R.string.driver_pass_validate));
            } else {
                if (NetworkStatus.isInternetPresent(DriverLogin.this).equals("On")) {
                    driverLoginRequest();
                } else {
                    NetworkCheckDialog.showConnectionTimeOut(DriverLogin.this);
                }

            }
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "OnLoginButtonClick" + e.getMessage());
        }

    }

    private void startSettingsActivity() {
        try {
            Intent intent = new Intent(DriverLogin.this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "startSettingsActivity" + e.getMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startSettingsActivity();
                return true;
            case R.id.action_status:
                StatusDialog.showStatusDialog(DriverLogin.this, textFonts);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void driverLoginRequest() {
        String url = ConfigData.getClientURL() + APIClass.driver_login_Api;
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put("imeiNo", Utils.getImei_no());
            params.put("loginId", edt_userid.getText().toString());
            params.put("password", edt_password.getText().toString());
            params.put("dateTime", GetDeviceTime.getDateForJobStatus());
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
        }
        System.out.println("Response driver-->" + url + "=>" + params);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.alt_login));
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println("Response driver-->" + response);
                    Log.e(TAG, "onResponse: " + response);
                    if (response != null) {
                        if (response.getInt("status") == 0) {
                            pDialog.dismiss();
                            showSnackBarMessage(response.getString("message"));
                            voiceSoundPlayer.playVoice(DriverLogin.this, R.raw.loginfailed);
                        } else {
                            pDialog.dismiss();
                            voiceSoundPlayer.playVoice(DriverLogin.this, R.raw.loginsuccessful);
                            showSnackBarMessage(response.getString("message"));
                            HashMap<String, String> config_data = new HashMap<String, String>();
                            config_data.put("fixedFare", response.optString("fixedFare"));
                            config_data.put("baseKms", response.optString("baseKms"));
                            config_data.put("baseHours", response.optString("baseHours"));
                            config_data.put("extraKmRate", response.optString("extraKmRate"));
                            config_data.put("extraHourRates", response.optString("extraHourRates"));

                            config_data.put("waitingRate", response.optString("waitingRate"));
                            config_data.put("waitingFreeMin", response.optString("waitingFreeMin"));

                            config_data.put("pollingInterval", response.optString("pollingInterval"));
                            config_data.put("clientName", response.optString("clientName"));

                            config_data.put("currencyType", response.optString("currencyType"));
                            config_data.put("nightChargesinPercentage", response.optString("nightChargesinPercentage"));
                            config_data.put("nightFareTimeFrom", response.optString("timeFrom"));
                            config_data.put("nightFareTimeTo", response.optString("timeTo"));
                            config_data.put("hotSpotStatus", response.optString("hotSpotStatus"));
                            config_data.put("intervalOfAds", response.optString("intervalOfAds"));
                            config_data.put("adsStatus", response.optString("adsStatus"));
                            config_data.put("paymentGatewayNeeded", response.optString("paymentGatewayNeeded"));
                            config_data.put("creditBalance", response.optString("creditBalance"));
                            config_data.put("walletBalance", response.optString("walletBalance"));
                            config_data.put("driverId", edt_userid.getText().toString());
                            config_data.put("availableBalance", response.optString("walletBalance"));
                            //
                            if (response.has("minDistance"))
                                config_data.put("minDistance", response.optString("minDistance"));
                            else
                                config_data.put("minDistance", "0");

                            if (response.has("minAmt"))
                                config_data.put("minAmount", response.optString("minAmt"));
                            else
                                config_data.put("minAmount", response.optString("fixedFare"));

                            HashMap<String, String> configData = new HashMap<String, String>();

                            configData.put("googleAPI", response.getString("googleAPI"));
                            configData.put("googleAPIKey", response.getString("googleAPIKey"));
                            configData.put("arriveDistance", String.valueOf(response.getInt("arriveDistance")));
                            configData.put("minBalance", String.valueOf(response.getInt("minBalance")));
                            configData.put("maxBalance", String.valueOf(response.getInt("maxBalance")));
                            configData.put("advancePopupTime", String.valueOf(response.getInt("advancePopupTime")));
                            configData.put("advPopupDuration", String.valueOf(response.getInt("advPopupDuration")));
                            //configData.put("minAmount", String.valueOf(Double.parseDouble(config_data.get("minAmount"))));
                            //configData.put("minDistance", String.valueOf(Double.parseDouble(config_data.get("minDistance"))));

                            if (db.getConfigDBValuesCount() == 0) {
                                db.insertConfigDBValues(configData);
                            } else {
                                db.updateConfigDBValues(configData, 1);
                            }
                            ConfigData.setFixedFare(config_data.get("fixedFare"));
                            ConfigData.setBaseKm(Long.parseLong(config_data.get("baseKms")));
                            ConfigData.setBaseHours(Double.parseDouble(config_data.get("baseHours")));
                            ConfigData.setExtraKmRate(Double.parseDouble(config_data.get("extraKmRate")));
                            ConfigData.setExtraHourRate(Double.parseDouble(config_data.get("extraHourRates")));
                            ConfigData.setDriverId(edt_userid.getText().toString());
                            //ADDED ON 13062020 - DHIRAJ
                            ConfigData.setWaitingRate(Double.parseDouble(config_data.get("waitingRate")));
                            ConfigData.setWaitingFreeMin(Integer.parseInt(config_data.get("waitingFreeMin")));

                            //ADDED ON 25062020 - AMJAD
                            pulseManager.setWaitingRate(Float.parseFloat(config_data.get("waitingRate")));
                            pulseManager.setWaitingMin(Integer.parseInt(config_data.get("waitingFreeMin")));

                            //pulseManager.setWaitingMin(5);

                            //ConfigData.setMinAmount(Double.parseDouble(config_data.get("minAmount")));
                            //ConfigData.setMinDistance(Double.parseDouble(config_data.get("minDistance")));

                            //Log.e("DRIVERLOGIN", "driverLoginResponse:ConfigData.getMinAmount " + Double.parseDouble(config_data.get("minAmount")));
                            //Log.e("DRIVERLOGIN", "driverLoginResponse:ConfigData.getMinDistance " + Double.parseDouble(config_data.get("minDistance")));

                            if (db.getConfigValuesCount() == 0)
                                db.insertConfigValues(config_data);
                            else
                                db.updateConfigValues(config_data, 1);
                            if (db.getMainScreenDataCount() == 0)
                                db.insertMainScreenData(getCurrentDateTime(), "0", "0", "0", "0", "1", "0", response.optString("creditBalance"), response.optString("walletBalance"));
                            else {
                                shiftCount = db.getShiftId() + 1;
                                db.updateCreditBal(response.optString("creditBalance"), response.optString("walletBalance"), 1);
                                db.updateShitIdLoginTime(String.valueOf(shiftCount), getCurrentDateTime(), 1);
                            }

                            JSONObject profileObject = new JSONObject();
                            profileObject.put("driver_no", edt_userid.getText().toString());
                            if (response.has("driverName"))
                                profileObject.put("driver_name", response.getString("driverName"));
                            if (response.has("taxiNo"))
                                profileObject.put("taxi_no", response.getString("taxiNo"));
                            if (response.has("noOfTrips"))
                                profileObject.put("no_trips", response.getString("noOfTrips"));
                            if (response.has("noOfYears"))
                                profileObject.put("no_years", response.getString("noOfYears"));
                            if (response.has("rating"))
                                profileObject.put("rating", response.getString("rating"));
                            if (response.has("driverMobileNo"))
                                profileObject.put("driverMobileNo", response.getString("driverMobileNo"));

                            pulseManager.setMinDistance((float) Double.parseDouble(config_data.get("minDistance")));
                            pulseManager.setStartingFare((float) Double.parseDouble(config_data.get("minAmount")));

                            pulseManager.setJsonProfileObject(profileObject.toString());
                            JSONObject infotainmentParams = new JSONObject();
                            try {
                                infotainmentParams.put("runningFare", "0.00");
                                infotainmentParams.put("distance", "0.00");
                                infotainmentParams.put("jobStartTime", "0");
                                infotainmentParams.put("duration", "0");
                                infotainmentParams.put("extra", "0.00");
                                infotainmentParams.put("toll", "0.00");
                                infotainmentParams.put("profile", new JSONObject(pulseManager.getJsonProfileObject()));
                                pulseManager.setJsonObject(infotainmentParams.toString());
                            } catch (JSONException ex) {
                                Log.e("DRIVERLOGIN", "infotainmentParams JSON Exception : " + ex.getMessage());
                            }

                            Intent mainActivity = new Intent(DriverLogin.this, MainActivity.class);
                            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainActivity);
                            finish();

                            if (!isMyServiceRunning(DriverLogin.class)) {
                                Intent pollingService = new Intent(DriverLogin.this, PollingService.class);
                                pollingService.putExtra("inputExtra", "Foreground service is running");
                                ContextCompat.startForegroundService(DriverLogin.this, pollingService);
                            }
                        }
                        pDialog.dismiss();
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
                showSnackBarMessage("Time Out");
                pDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        req.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(req);
    }

    public void showSnackBarMessage(String msg) {
        try {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.color_toolbar));
            textView.setTypeface(textFonts);
            snackbar.show();
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "showSnackBarMessage" + e.getMessage());
        }
    }

    public String getCurrentDateTime() {
        String strDate = null;
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy  HH:mm:ss");
            strDate = sdf.format(c.getTime());
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
        }
        return strDate;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager) AppController.getInstance().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsGsmStatusDialog(DriverLogin.this, textFonts, "Please enable the GPS", "Driver");
        } else if (!NetworkStatus.isInternetPresent(DriverLogin.this).equals("On")) {
            showGpsGsmStatusDialog(DriverLogin.this, textFonts, "Please Turn on Internet", "Driver");
        }
    }

    public String getcurrentTimeToDisplay() {
        String currentTime = null;
        try {
            Time now = new Time(Time.getCurrentTimezone());
            now.setToNow();
            currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d", now.year, (now.month + 1), now.monthDay, now.hour, now.minute, now.second);
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
        }
        return currentTime;
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