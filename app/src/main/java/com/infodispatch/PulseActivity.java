package com.infodispatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.APIs.APIClass;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.appcontroller.AppController;
import com.infodispatch.R;
import com.log.MyLog;
import com.services.PollingService;
import com.session.PulseManager;
import com.settersgetters.ConfigData;
import com.settersgetters.GpsData;
import com.settersgetters.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.infodispatch.NetworkCheckDialog.DEBUG_KEY;

public class PulseActivity extends AppCompatActivity {

    private EditText mEditPulse, mEditKilo, mEditMeter;
    private Button mButtonSubmit;
    private PulseManager pulseManager;
    private String TAG = getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulse);
        mEditPulse = findViewById(R.id.edt_pulse_count);
        mEditKilo = findViewById(R.id.edt_kilometer);
        mEditMeter = findViewById(R.id.edt_meter);
        mButtonSubmit = findViewById(R.id.btnSubmit);
        pulseManager = new PulseManager(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (NetworkStatus.isInternetPresent(PulseActivity.this).equals("On")) {
//                        gpsRequest();
                        startActivity(new Intent(PulseActivity.this, DriverLogin.class));
                        finish();
                    } else {
                        NetworkCheckDialog.showConnectionTimeOut(PulseActivity.this);
                    }
                }
            }
        });
    }

    private boolean validation() {
        if (AppController.validateString(mEditPulse.getText().toString()) && mEditPulse.getText().toString().length() > 0 && Integer.parseInt(mEditPulse.getText().toString()) > 0) {
            if (AppController.validateString(mEditKilo.getText().toString()) && mEditKilo.getText().toString().length() > 0) {
                if (AppController.validateString(mEditMeter.getText().toString())) {
                    pulseManager.setOdometer(Integer.parseInt(mEditPulse.getText().toString()), Integer.parseInt(mEditKilo.getText().toString()), Integer.parseInt(mEditMeter.getText().toString()));
                    pulseManager.setPulseCount(Integer.parseInt(mEditPulse.getText().toString()));
                    pulseManager.setPulse(true);
                    GpsData.setGpsOdometer(pulseManager.getDoubleOdometer());
                    return true;
                } else {
                    Toast.makeText(PulseActivity.this, "Enter proper Meter", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(PulseActivity.this, "Enter proper kilometer", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(PulseActivity.this, "You are going with GPS TRACKING", Toast.LENGTH_LONG).show();
            pulseManager.setOdometer(0, 0, 0);
            pulseManager.setPulseCount(0);
            pulseManager.setPulse(false);
            return true;
        }
        return false;
    }

    public void gpsRequest() {
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
                    Log.e(TAG, "onResponse: " + response);
                    if (response != null) {
                        if (response.getInt("STATUS") == 0) {
                            pDialog.dismiss();
                            Log.e(TAG, "onResponse: "+response.getString("MESSAGE"));
                        } else {
                            if(response.has("geofenceList")){
                                pDialog.dismiss();
                                String stringArray = response.get("geofenceList").toString();
                                JSONArray jsonArray = new JSONArray(stringArray);
                                pulseManager.setGeoResponse(jsonArray.toString());
                                startActivity(new Intent(PulseActivity.this, DriverLogin.class));
                                finish();
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
                    Log.e(TAG, "onResponse: "+e.getMessage());
                    MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: TIME OUT ");
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

}