package com.infodispatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.APIs.APIClass;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appcontroller.AppController;
import com.appcontroller.LanguageController;
import com.log.MyLog;
import com.infodispatch.R;
import com.settersgetters.ConfigData;
import com.settersgetters.RunningJobDetails;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.infodispatch.GpsCheckerDialog.showGpsGsmStatusDialog;

/**
 * Created by info on 02-12-2016.
 */
public class ProfileActivity extends AppCompatActivity {

    Typeface textFonts;
    TextView lbl_driver_name,txt_driver_name,lbl_driver_id,txt_driver_id,lbl_phoneNo,txt_phoneNo,lbl_cab_type,
            txt_cab_type,lbl_vechicle_status,txt_vechicle_status,lbl_wallet,lbl_creditcard,lbl_payable,lbl_earnings,lbl_currency,lbl_currency1,lbl_currency2,lbl_currency3,
            lbl_ratings,lbl_performance,lbl_incident,txt_wallet,txt_credit,txt_payable,txt_earnings,txtActiveInactive,txtDriverRatings,txtPerformance,txtIncidence;
    LinearLayout layout_ratings,layout_performance,layout_incident;
    TextView toolbar_title,toolbar_date,txt_switch;
    Toolbar toolbar;
    SwitchCompat toolbar_switch;
    DBHelper db;
    public String DEBUG_KEY="ProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LanguageController.setLanguage(ProfileActivity.this);
        setContentView(R.layout.activity_profile);
        try {
            textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
            db = new DBHelper(this);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbar_title.setText(R.string.profile);
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

        setConfigVal();
        setUiElements();
        layout_ratings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layout_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layout_incident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setConfigVal(){
        try {
            HashMap<String,String> configData = db.getConfigData();
            RunningJobDetails.setTotalTripFare(Double.parseDouble(configData.get("fixedFare")));
            ConfigData.setClientName(configData.get("clientName"));
            ConfigData.setFixedFare(configData.get("fixedFare"));
            ConfigData.setBaseKm(Long.parseLong(configData.get("baseKms")));
            ConfigData.setBaseHours(Double.parseDouble(configData.get("baseHours")));
            ConfigData.setExtraKmRate(Double.parseDouble(configData.get("extraKmRate")));
            ConfigData.setExtraHourRate(Double.parseDouble(configData.get("extraHourRates")));
            ConfigData.setCurrencyType(configData.get("currencyType"));
            ConfigData.setDriverId(configData.get("driverId"));
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"setConfigVal"+e.getMessage());
        }
    }


    public void setUiElements() {
        try {
            lbl_driver_name = (TextView) findViewById(R.id.lbl_driver_name);
            txt_driver_name = (TextView) findViewById(R.id.txt_driver_name);
            lbl_driver_id = (TextView) findViewById(R.id.lbl_driver_id);
            txt_driver_id = (TextView) findViewById(R.id.txt_driver_id);
            lbl_phoneNo = (TextView) findViewById(R.id.lbl_phoneNo);
            txt_phoneNo = (TextView) findViewById(R.id.txt_phoneNo);

            txtDriverRatings=(TextView) findViewById(R.id.txtDriverRatings);
            txtDriverRatings.setTypeface(textFonts,Typeface.BOLD);
            txtPerformance=(TextView) findViewById(R.id.txtPerformance);
            txtPerformance.setTypeface(textFonts,Typeface.BOLD);
            txtIncidence=(TextView) findViewById(R.id.txtIncidence);
            txtIncidence.setTypeface(textFonts,Typeface.BOLD);

            lbl_cab_type = (TextView) findViewById(R.id.lbl_cab_type);
            txt_cab_type = (TextView) findViewById(R.id.txt_cab_type);
            lbl_vechicle_status = (TextView) findViewById(R.id.lbl_vechicle_status);
            txt_vechicle_status = (TextView) findViewById(R.id.txt_vechicle_status);
            lbl_wallet = (TextView) findViewById(R.id.lbl_wallet);
            lbl_creditcard = (TextView) findViewById(R.id.lbl_creditcard);
            lbl_payable = (TextView) findViewById(R.id.lbl_payable);

            txt_wallet = (TextView) findViewById(R.id.txt_wallet);
            txt_credit = (TextView) findViewById(R.id.txt_credit);
            txt_payable = (TextView) findViewById(R.id.txt_payable);
            txt_earnings = (TextView) findViewById(R.id.txt_earnings);


            lbl_earnings = (TextView) findViewById(R.id.lbl_earnings);
            lbl_currency = (TextView) findViewById(R.id.lbl_currency);
            lbl_currency1 = (TextView) findViewById(R.id.lbl_currency1);
            lbl_currency2 = (TextView) findViewById(R.id.lbl_currency2);
            lbl_currency3 = (TextView) findViewById(R.id.lbl_currency3);
            lbl_ratings = (TextView) findViewById(R.id.lbl_ratings);

            lbl_performance = (TextView) findViewById(R.id.lbl_performance);
            lbl_incident = (TextView) findViewById(R.id.lbl_incident);

            layout_ratings  =  (LinearLayout) findViewById(R.id.layout_ratings);
            layout_performance  =  (LinearLayout) findViewById(R.id.layout_performance);
            layout_incident  =  (LinearLayout) findViewById(R.id.layout_incident);

            lbl_driver_name.setTypeface(textFonts);
            txt_driver_name.setTypeface(textFonts,Typeface.BOLD);
            lbl_driver_id.setTypeface(textFonts);
            txt_driver_id.setTypeface(textFonts,Typeface.BOLD);

            lbl_phoneNo.setTypeface(textFonts);
            txt_phoneNo.setTypeface(textFonts,Typeface.BOLD);
            lbl_cab_type.setTypeface(textFonts);

            txt_cab_type.setTypeface(textFonts,Typeface.BOLD);
            lbl_vechicle_status.setTypeface(textFonts);
            txt_vechicle_status.setTypeface(textFonts,Typeface.BOLD);
            lbl_wallet.setTypeface(textFonts);
            lbl_creditcard.setTypeface(textFonts);

            lbl_payable.setTypeface(textFonts);
            lbl_earnings.setTypeface(textFonts);
            lbl_currency.setTypeface(textFonts);
            lbl_currency1.setTypeface(textFonts);
            lbl_currency2.setTypeface(textFonts);
            lbl_currency3.setTypeface(textFonts);
            lbl_ratings.setTypeface(textFonts);
            lbl_performance.setTypeface(textFonts);
            lbl_incident.setTypeface(textFonts);

            lbl_currency.setText((ConfigData.getCurrencyType()));
            lbl_currency1.setText(String.valueOf(ConfigData.getCurrencyType()));
            lbl_currency2.setText(String.valueOf(ConfigData.getCurrencyType()));
            lbl_currency3.setText(String.valueOf(ConfigData.getCurrencyType()));

            if (NetworkStatus.isInternetPresent(ProfileActivity.this).equals("On")) {
                driverProfileRequest();
            }
            else{
                NetworkCheckDialog.showConnectionTimeOut(ProfileActivity.this);
            }
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"setUiElements"+e.getMessage());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager)AppController.getInstance().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGpsGsmStatusDialog(ProfileActivity.this,textFonts,"Please enable the GPS","Profile");
        }
        else if(!NetworkStatus.isInternetPresent(ProfileActivity.this).equals("On")){
            showGpsGsmStatusDialog(ProfileActivity.this,textFonts,"Please Turn on Internet","Profile");
        }
        //LanguageController.setLanguage(ProfileActivity.this);
    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }else if (item.getItemId() == R.id.action_refresh) {
                driverProfileRequest();
            }
            return (super.onOptionsItemSelected(item));
    }
    public void driverProfileRequest(){
        final HashMap<String,String> driverProfileData=new HashMap<String, String>();
        String url = ConfigData.getClientURL()+ APIClass.driver_Profile_Api;
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put("imeiNo", Utils.getImei_no());
            params.put("driverId",ConfigData.getDriverId());
            System.out.println("Inside driverProfileRequest params : "+params);
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+""+e.getMessage());
        }
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.alt_get_profile));
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response!=null){
                        if(response.getInt("status")==0){
                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_LONG).show();
                           pDialog.dismiss();
                        }
                        else{
                            toolbar_title.setText(String.valueOf(getResources().getString(R.string.profile)+"("+response.getString("CabNo")+")"));
                            txt_driver_name.setText(String.valueOf(response.getString("driverName")));
                            txt_phoneNo.setText(String.valueOf(response.getString("phoneNo")));
                            txt_driver_id.setText(String.valueOf(response.getString("driverId")));
                            txt_cab_type.setText(String.valueOf(response.getString("cabType")));
                            txt_credit.setText(String.valueOf(response.getString("creditBalance")));
                            txt_wallet.setText(String.valueOf(response.getString("wallet")));
                            txt_payable.setText(String.valueOf(response.getString("payableAmount")));
                            txt_earnings.setText(String.valueOf(response.getString("driverEarnings")));
                            txt_vechicle_status.setText(String.valueOf(response.getString("vehicleStatus")));

                            txtDriverRatings.setText(String.valueOf(response.getString("driverRatings")));
                            txtPerformance.setText(String.valueOf(response.getString("performance")));
                            txtIncidence.setText(String.valueOf(response.getString("incidents")));

                            db.updateCreditBal(response.getString("creditBalance"),response.getString("wallet"),1);
                            db.updateConfigData(response.getString("creditBalance"),response.optString("wallet"),response.optString("availableBalance"),1);
                           pDialog.dismiss();
                        }
                    }else{
                       pDialog.dismiss();
                        MyLog.appendLog(DEBUG_KEY+"Response is null");
                        Toast.makeText(getApplicationContext(),"Server Returns Null",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                   pDialog.dismiss();
                    MyLog.appendLog(DEBUG_KEY+"driverProfileRequest"+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               pDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Time Out",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(req);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
//            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            sendBroadcast(closeDialog);
            try {
                Object service  = getSystemService("statusbar");
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

