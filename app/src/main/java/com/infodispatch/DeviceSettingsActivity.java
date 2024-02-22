package com.infodispatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appcontroller.LanguageController;
import com.log.MyLog;
import com.infodispatch.R;
import com.settersgetters.UpdateCurrentJobData;
import com.sqlite.DBHelper;

public class DeviceSettingsActivity extends AppCompatActivity {
    private static String DEBUG_KEY = "DeviceSettingsActivity";
    Button btnContinue;
    private EditText edt_pulse_count, edt_odometer_reading;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LanguageController.setLanguage(DeviceSettingsActivity.this);
        setContentView(R.layout.activity_device_settings);
        try {
            db = new DBHelper(this);
            db.updateCurrentScreenVal("DEVICE_SETTINGS", 1);
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
            MyLog.appendLog(DEBUG_KEY + "DeviceSettingsActivity" + e.getMessage());
        }
        setSupportActionBar(toolbar);
        setUiElements();
        btnContinue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    try {
                        OnContinueButtonClick();
                    } catch (Exception e) {
                        MyLog.appendLog(DEBUG_KEY + "OnContinueButtonClick" + e.getMessage());
                    }
                }
                return false;
            }
        });
    }

    private void OnContinueButtonClick() {
        SharedPreferences sh = getSharedPreferences("RaktaSharedPref",MODE_PRIVATE);
        SharedPreferences.Editor shEdit = sh.edit();
        shEdit.putString("pulse_count",edt_pulse_count.getText().toString().trim());
        shEdit.putString("initial_odo_reading",edt_odometer_reading.getText().toString().trim());
        shEdit.commit();
        Intent in = new Intent(DeviceSettingsActivity.this, DriverLogin.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
        finish();
    }

    public void setUiElements() {
        try {
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            edt_pulse_count = (EditText) findViewById(R.id.edt_pulse_count);
            edt_pulse_count.setTypeface(textFonts);
            edt_odometer_reading = (EditText) findViewById(R.id.edt_initialOdoReading);
            edt_odometer_reading.setTypeface(textFonts);
            btnContinue = (Button) findViewById(R.id.btn_accept);
            btnContinue.setTypeface(textFonts, Typeface.BOLD);
            if (db.getCurrentJobDBValuesCount() == 0) {
                db.insertCurrentJobDBValues(UpdateCurrentJobData.updateJobData());
            }
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "setUiElements" + e.getMessage());
        }
    }
}
