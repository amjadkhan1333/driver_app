package com.infodispatch;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
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
import com.settersgetters.Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.infodispatch.GpsCheckerDialog.showGpsGsmStatusDialog;

/**
 * Created by info on 08-12-2016.
 */
public class SupportActivity extends AppCompatActivity {

    Typeface textFonts;
    WebView webview;
    SwitchCompat toolbar_switch;
    TextView toolbar_title,toolbar_date,txt_switch;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LanguageController.setLanguage(SupportActivity.this);
        setContentView(R.layout.activity_support);

        textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.support);
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

        setUiElements();
    }

    public void setUiElements() {
        webview = (WebView)findViewById(R.id.webview);
        getSupportData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager)AppController.getInstance().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGpsGsmStatusDialog(SupportActivity.this,textFonts,"Please enable the GPS","Support");
        }
        else if(!NetworkStatus.isInternetPresent(SupportActivity.this).equals("On")){
            showGpsGsmStatusDialog(SupportActivity.this,textFonts,"Please Turn on Internet","Support");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // MenuInflater inflater = getMenuInflater();
       // inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            finish();
        }
        return (super.onOptionsItemSelected(item));

    }
    public void getSupportData(){

        String url = ConfigData.getClientURL()+ APIClass.support_Request_Api;
        Map<String, String> params = new HashMap<String, String>();
        params.put("imeiNo", Utils.getImei_no());
        params.put("driverId",ConfigData.getDriverId());
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Getting Support Info...");
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response!=null){
                        if(response.getInt("status")==0){
                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_LONG).show();
                            pDialog.hide();
                        }
                        else{
                            pDialog.hide();
                            try {
                                webview.loadData(URLEncoder.encode(response.getString("content"), "utf-8").replaceAll("\\+", "%20"), "text/html", "utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                Log.e("webview", "", uee);
                            }
                        }
                    }else{
                        pDialog.hide();
                        MyLog.appendLog("Inside Support"+"Response is null");
                        Toast.makeText(getApplicationContext(),"Server Returns Null",Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    pDialog.hide();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
                Toast.makeText(getApplicationContext(),"Time out.Please Try again",Toast.LENGTH_LONG).show();
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

