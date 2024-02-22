package com.infodispatch;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.APIs.APIClass;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appcontroller.AppController;
import com.log.MyLog;
import com.infodispatch.R;
import com.settersgetters.ConfigData;
import com.settersgetters.GpsData;
import com.settersgetters.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by info on 02-01-2017.
 */

public class EmergencyAlert {
    Context context;
    ProgressDialog pDialog;
    public String DEBUG_KEY="EmergencyAlert";

    public void emergency_alert(Context context1,String alertType,String from){
        try{
            this.context = context1;
            String url = ConfigData.getClientURL()+ APIClass.emergency_alert_Api;
            Map<String, String> params = new HashMap<String, String>();
            params.put("imeiNo", Utils.getImei_no());
            params.put("alertType",alertType);
            params.put("curentrLocLat", String.valueOf(GpsData.getLongitude()));
            params.put("currentLocLon",String.valueOf(GpsData.getLongitude()));
            pDialog = new ProgressDialog(context);
            if(from.equalsIgnoreCase("SERVICE")){
                pDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            pDialog.setMessage(context.getResources().getString(R.string.alt_sending));
            pDialog.show();
            JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response!=null){
                            if(response.getInt("status")==0){
                                pDialog.dismiss();
                                Toast.makeText(context,response.getString("message"),Toast.LENGTH_LONG).show();
                            }
                            else{
                                pDialog.dismiss();
                                Toast.makeText(context,response.getString("message"),Toast.LENGTH_LONG).show();
                            }
                        }else{
                            pDialog.dismiss();
                            MyLog.appendLog(DEBUG_KEY+"Response is null");
                            Toast.makeText(context,"Server Returns Null",Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        pDialog.dismiss();
                        MyLog.appendLog(DEBUG_KEY+""+e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    Toast.makeText(context,"Time Out",Toast.LENGTH_LONG).show();
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
        }catch (Exception e){
            Log.e("EMERGENCY", "emergency_alert: "+e.getMessage() );
        }

    }
}
