package com.infodispatch;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.log.MyLog;
import com.infodispatch.R;
import com.session.PulseManager;
import com.settersgetters.ConfigData;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.infodispatch.NetworkCheckDialog.DEBUG_KEY;


/**
 * Created by info on 02-12-2016.
 */
public class SettingsActivity  extends AppCompatActivity {
    private static String DEBUG_KEY = "SettingsActivity";
    Typeface textFonts;
    TextView lbl_language_settings,lbl_english,lbl_maynamar,lbl_sound_settings;
    TextView toolbar_title,toolbar_date,txt_switch;
    Toolbar toolbar;
    SwitchCompat toolbar_switch,settings_switch;
    RadioGroup radioGroup_language;
    Button btn_save,btn_geofence_update;
    RadioButton rbtn_english,rbtn_maynamar;
    ImageView imgSound;
    DBHelper db;
    String langVal = "MYN";
    String soundVal = "OFF";
    PulseManager pulseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LanguageController.setLanguage(SettingsActivity.this);
        setContentView(R.layout.activity_settings);
        try {
            db = new DBHelper(this);
            textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbar_title.setText(R.string.settings);
            toolbar_title.setTypeface(textFonts,Typeface.BOLD);
            toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);
            toolbar_date.setVisibility(View.GONE);
            toolbar_switch = (SwitchCompat)toolbar.findViewById(R.id.toolbar_switch);
            toolbar_switch.setVisibility(View.GONE);
            txt_switch = (TextView) toolbar.findViewById(R.id.txt_switch);
            txt_switch.setVisibility(View.GONE);
            setSupportActionBar(toolbar);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);

        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"onCreate"+e.getMessage());
        }
        setUiElements();
        radioGroup_language.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=(RadioButton)findViewById(checkedId);
                langVal = rb.getText().toString();
                System.out.println("Inside the RadioGroup"+langVal);
            }
        });

        settings_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    imgSound.setBackgroundResource(R.drawable.ic_action_sound);
                    soundVal = "OFF";
                } else {
                    imgSound.setBackgroundResource(R.drawable.mute);
                    soundVal = "ON";
                }
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    db.updateSettingsDBValues(langVal,soundVal,1);
                    if(langVal.equalsIgnoreCase("English")){
                        setLocal("en_US");
                    }else{
                        setLocal("my");
                    }
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.alt_settings_changed),Toast.LENGTH_LONG).show();
            }
        });

        btn_geofence_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpsRequest();
            }
        });
    }

    public void gpsRequest() {
        pulseManager = new PulseManager(this);
//        String url = APIClass.geofenceURL;
        String url = ConfigData.getClientURL() + APIClass.geofence_Api;
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put("sIMEINO", Utils.getImei_no());
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
        }
        System.out.println("Response driver-->" + url + "=>" + params);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.alt_getting_geofence));
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    MyLog.appendLog( DEBUG_KEY+ "onResponse: "+response);
                    if (response != null) {
                        if (response.getInt("STATUS") == 0) {
                            pDialog.dismiss();
                        } else {
                            if(response.has("geofenceList")){
                                pDialog.dismiss();
                                String stringArray = response.get("geofenceList").toString();
                                JSONArray jsonArray = new JSONArray(stringArray);
                                pulseManager.setGeoResponse(jsonArray.toString());
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


    public void setUiElements() {
        radioGroup_language = (RadioGroup) findViewById(R.id.radioGroup_language);
        rbtn_english = (RadioButton) findViewById(R.id.rbtn_english);
        rbtn_maynamar = (RadioButton) findViewById(R.id.rbtn_maynamar);
        lbl_language_settings = (TextView) findViewById(R.id.lbl_language_settings);
        lbl_sound_settings = (TextView) findViewById(R.id.lbl_sound_settings);
        lbl_english = (TextView) findViewById(R.id.lbl_english);
        lbl_maynamar = (TextView) findViewById(R.id.lbl_maynamar);
        imgSound = (ImageView) findViewById(R.id.imgSound);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_geofence_update = (Button) findViewById(R.id.btn_geofence_update);
        settings_switch = (SwitchCompat)findViewById(R.id.settings_switch);
        settings_switch.setChecked(true);

        lbl_language_settings.setTypeface(textFonts,Typeface.BOLD);
        lbl_sound_settings.setTypeface(textFonts,Typeface.BOLD);
        lbl_english.setTypeface(textFonts);
        lbl_maynamar.setTypeface(textFonts);
        lbl_maynamar.setText(R.string.lbl_maynamar);
        setDefaultSettings();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(db.getCurrentScreenVal().equalsIgnoreCase("Driver_Login")){
                Intent intent = new Intent(SettingsActivity.this, DriverLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }else  if(db.getCurrentScreenVal().equalsIgnoreCase("CLIENT_LOGIN")){
                Intent intent = new Intent(SettingsActivity.this, ClientLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }

        }
        return (super.onOptionsItemSelected(item));
    }
    private void setDefaultSettings() {
        try {
            HashMap<String, String> getSettingsInfo =db.getSettingsInfo();
            if(getSettingsInfo.get("lang_settings").equalsIgnoreCase("Maynamar")){
                rbtn_maynamar.setChecked(true);
                langVal = "Maynamar";
            }
            else{
                rbtn_english.setChecked(true);
                langVal = "English";
            }
            if(getSettingsInfo.get("sound_settings").equalsIgnoreCase("ON")){
                settings_switch.setChecked(true);
                imgSound.setBackgroundResource(R.drawable.ic_action_sound);
            }
            else{
                settings_switch.setChecked(false);
                imgSound.setBackgroundResource(R.drawable.mute);
            }

        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"setDefaultSettings"+e.getMessage());
        }
    }
    private void setLocal(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = AppController.getInstance().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, SettingsActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(db.getCurrentScreenVal().equalsIgnoreCase("Driver_Login")){
            Intent intent = new Intent(SettingsActivity.this, DriverLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }else  if(db.getCurrentScreenVal().equalsIgnoreCase("CLIENT_LOGIN")){
            Intent intent = new Intent(SettingsActivity.this, ClientLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus)
        {
//            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            sendBroadcast(closeDialog);
            try {
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
