package com.infodispatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.appcontroller.AppController;
import com.appcontroller.LanguageController;
import com.commands.ServerConnectionEstablished;
import com.log.MyLog;
import com.infodispatch.R;
import com.settersgetters.ConfigData;
import com.settersgetters.UpdateCurrentJobData;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.infodispatch.GpsCheckerDialog.showGpsGsmStatusDialog;

/**
 * Created by info on 23-11-2016.
 */
public class ClientLogin extends AppCompatActivity {
    public String DEBUG_KEY = "ClientLogin";
    Button btnSignUp;
    private EditText edt_clientid, edt_password;
    Typeface textFonts;
    Typeface boldTypeface;
    private CoordinatorLayout coordinatorLayout;
    private static final int REQUEST_READ_PHONE_STATE = 0;
    TelephonyManager telephonyManager;
    String Imei;
    TextView toolbar_title, toolbar_date, txt_switch;
    Toolbar toolbar;
    SwitchCompat toolbar_switch;
    DBHelper db;
    VoiceSoundPlayer voiceSoundPlayer = new VoiceSoundPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LanguageController.setLanguage(ClientLogin.this);
        setContentView(R.layout.client_login);
        try {
            db = new DBHelper(this);
            db.updateCurrentScreenVal("CLIENT_LOGIN", 1);
            textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbar_title.setText(R.string.client_Login);
            toolbar_title.setTypeface(textFonts, Typeface.BOLD);
            toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);
            toolbar_date.setVisibility(View.GONE);
            toolbar_switch = (SwitchCompat) toolbar.findViewById(R.id.toolbar_switch);
            toolbar_switch.setVisibility(View.GONE);
            txt_switch = (TextView) toolbar.findViewById(R.id.txt_switch);
            txt_switch.setVisibility(View.GONE);
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "ClientLogin" + e.getMessage());
        }
        setSupportActionBar(toolbar);
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                loadPermissions(android.Manifest.permission.READ_PHONE_STATE, REQUEST_READ_PHONE_STATE);
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Imei = telephonyManager.getDeviceId();
                Utils.setImei_no(Imei);
            }
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
        }
        setUiElements();
        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    try {
                        OnLoginButtonClick();
                    } catch (Exception e) {
                        MyLog.appendLog(DEBUG_KEY + "OnLoginButtonClick" + e.getMessage());
                    }
                }
                return false;
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OnLoginButtonClick();
                } catch (Exception e) {
                    MyLog.appendLog(DEBUG_KEY + "OnLoginButtonClick" + e.getMessage());
                }
            }
        });
    }

    private void loadPermissions(String perm, int requestCode) {
        try {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                    ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
                }
            }
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "loadPermissions" + e.getMessage());
        }
    }

    public void setUiElements() {
        try {
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            edt_clientid = (EditText) findViewById(R.id.edt_clientid);
            edt_clientid.setTypeface(textFonts);
            edt_password = (EditText) findViewById(R.id.edt_password);
            edt_password.setTypeface(textFonts);
            btnSignUp = (Button) findViewById(R.id.btnSignUp);
            btnSignUp.setTypeface(textFonts, Typeface.BOLD);
            edt_clientid.setText("tdsqat");
            edt_password.setText("tdsqat");
            if (db.getCurrentJobDBValuesCount() == 0) {
                db.insertCurrentJobDBValues(UpdateCurrentJobData.updateJobData());
            }
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "setUiElements" + e.getMessage());
        }
    }

    private void OnLoginButtonClick() {

        if (edt_clientid.getText().length() == 0 && edt_password.getText().length() == 0) {
            Toast.makeText(ClientLogin.this, getResources().getString(R.string.client_both_validate), Toast.LENGTH_LONG).show();
        } else if (edt_clientid.getText().length() == 0) {
            Toast.makeText(ClientLogin.this, getResources().getString(R.string.client_id_validate), Toast.LENGTH_LONG).show();
        } else if (edt_password.getText().length() == 0) {
            Toast.makeText(ClientLogin.this, getResources().getString(R.string.client_pass_validate), Toast.LENGTH_LONG).show();
        } else {
            if (NetworkStatus.isInternetPresent(ClientLogin.this).equals("On")) {
                clientLoginRequest();
            } else {
                NetworkCheckDialog.showConnectionTimeOut(ClientLogin.this);
            }
        }
    }

    public void clientLoginRequest() {
        String url = APIClass.baseURL;
        Map<String, String> params = null;
        try {
            url = APIClass.baseURL;
            params = new HashMap<String, String>();
            params.put("imeiNo", Utils.getImei_no());
            params.put("loginId", edt_clientid.getText().toString());
            params.put("password", edt_password.getText().toString());
            params.put("dateTime", getDateForRequests());
            System.out.println("Client Login Request" + url + params);
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "clientLoginRequest" + e.getMessage());
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.alt_login));
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(
                params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println("Client Login Response" + response);
                    if (response != null) {
                        if (response.getInt("Status") == 0) {
                            pDialog.hide();
                            voiceSoundPlayer.playVoice(ClientLogin.this, R.raw.loginfailed);
                            showSnackBarMessage(response.getString("StatusMsg"));
                        } else {
                            pDialog.hide();
                            voiceSoundPlayer.playVoice(ClientLogin.this, R.raw.loginsuccessful);
                            showSnackBarMessage(response.getString("StatusMsg"));

                            HashMap<String, String> client_data = new HashMap<String, String>();
                            client_data.put("pollingUrl", response.optString("pollingUrl"));
                            client_data.put("PollingInterval", response.optString("PollingInterval"));
                            client_data.put("clientImg", response.optString("clientImg"));
                            client_data.put("ClientName", response.optString("ClientName"));
                            client_data.put("clientURL", response.optString("clientURL"));
                            client_data.put("imeino", Utils.getImei_no());

                            ConfigData.setPollingUrl(response.optString("pollingUrl"));
                            ConfigData.setPollingInterVal(response.optString("PollingInterval"));
                            ConfigData.setClientName(response.optString("ClientName"));
                            ConfigData.setClientImageURL(response.optString("clientImg"));
                            ConfigData.setClientURL(response.optString("clientURL"));
                            Log.e("CLIENT LOGIN :- ", "onResponse: "+ response.optString("clientURL"));
//                            ConfigData.setClientURL("http://ajmantaxi.AKtelematics.com/RestAPIinfodispatchTest/Infodispatch.svc");
                            System.out.println("Inside Client Login the PollingURL==>" + ConfigData.getPollingUrl());
                            db.insertClientDBValues(client_data);

                            Intent in = new Intent(ClientLogin.this, PulseActivity.class);
                            //Intent in = new Intent(ClientLogin.this, DeviceSettingsActivity.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(in);
                            finish();
                            ServerConnectionEstablished sc = new ServerConnectionEstablished();
                            sc.frame_0001_cmd();
                        }
                        pDialog.hide();
                    } else {
                        pDialog.hide();
                        MyLog.appendLog(DEBUG_KEY + "Response is null");
                        Toast.makeText(getApplicationContext(), "Server Returns Null", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
                    pDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showSnackBarMessage("Time out");
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
        req.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.client_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_status:
                StatusDialog.showStatusDialog(ClientLogin.this, textFonts);
                return true;
            case R.id.action_settings:
                try {
                    Intent intent = new Intent(ClientLogin.this, SettingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    MyLog.appendLog(DEBUG_KEY + "onOptionsItemSelected" + e.getMessage());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE: {
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Imei = telephonyManager.getDeviceId();
                        Utils.setImei_no(Imei);
                    } else {
                        finish();
                    }
                }catch (Exception e){
                    MyLog.appendLog(DEBUG_KEY+"onRequestPermissionsResult"+e.getMessage());
                }
                return;
            }
        }
    }
    public String getcurrentTimeToDisplay() {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d",now.year,(now.month+1),now.monthDay,now.hour,now.minute,now.second);
        return currentTime;
    }
    public String getDateForRequests() {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d",now.year,(now.month+1),now.monthDay,now.hour,now.minute,now.second);
        return currentTime;
    }
    public void showSnackBarMessage(String msg){
        try {
            Snackbar snackbar = Snackbar.make(coordinatorLayout,msg,Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.color_toolbar));
            textView.setTypeface(textFonts);
            snackbar.show();
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"showSnackBarMessage"+e.getMessage());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager)AppController.getInstance().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGpsGsmStatusDialog(ClientLogin.this,textFonts,"Please enable the GPS","Client");
        }
        else if(!NetworkStatus.isInternetPresent(ClientLogin.this).equals("On")){
            showGpsGsmStatusDialog(ClientLogin.this,textFonts,"Please Turn on Internet","Client");
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
}