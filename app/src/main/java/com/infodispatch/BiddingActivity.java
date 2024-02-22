package com.infodispatch;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.format.Time;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appcontroller.AppController;
import com.appcontroller.LanguageController;

import com.log.MyLog;
import com.infodispatch.R;
import com.services.ServiceHandler;
import com.settersgetters.ConfigData;
import com.settersgetters.GpsData;
import com.settersgetters.RunningJobDetails;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.infodispatch.GpsCheckerDialog.showGpsGsmStatusDialog;


/**
 * Created by info on 20-12-2016.
 */

public class BiddingActivity extends AppCompatActivity {

    Typeface textFonts;
    TextView toolbar_title, toolbar_date, txt_switch,lbl_address,txt_bidding_kms,lbl_bidding_kms,txt_bidding_duration,
            lbl_bidding_duration,lbl_pickuploc,lbl_droploc,txtDriverNote;
    Toolbar toolbar;
    SwitchCompat toolbar_switch;
    Button btn_nothanks,btn_bidding_accept;
    AudioManager audioManager;
    ProgressBar bidding_progressbar;
    TextView txt_bidding_progressbar;
    CountDownTimer countDownTimer;
    public String DEBUG_KEY="BiddingActivity";
    HashMap<String,String> biddingJobData;
    HashMap<String,String> configDbData;
    VoiceSoundPlayer voiceSoundPlayer = new VoiceSoundPlayer();
    DBHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.auto_bidding);
        LanguageController.setLanguage(BiddingActivity.this);
        try {
            db = new DBHelper(this);
            db.updateCurrentScreenVal("BIDDING", 1);
            configDbData= db.getConfigDBValues();
            textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbar_title.setText(R.string.autobidding);
            toolbar_title.setTypeface(textFonts, Typeface.BOLD);
            toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);
            toolbar_date.setVisibility(View.GONE);
            toolbar_switch = (SwitchCompat) toolbar.findViewById(R.id.toolbar_switch);
            toolbar_switch.setVisibility(View.GONE);
            txt_switch = (TextView) toolbar.findViewById(R.id.txt_switch);
            txt_switch.setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            setUiElements();
        }
        catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"BiddingActivity onCreatec"+e.getMessage());
        }
        btn_nothanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countDownTimer!=null) {
                    countDownTimer.cancel();
                }
                voiceSoundPlayer.stopVoice(BiddingActivity.this);
                jobBiddingRequest("Reject");
            }
        });

        btn_bidding_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countDownTimer!=null) {
                    countDownTimer.cancel();
                }
                voiceSoundPlayer.stopVoice(BiddingActivity.this);
                jobBiddingRequest("Accept");
                //new GetNearByPlaces().execute();
            }
        });
        countDownTimer = new CountDownTimer(RunningJobDetails.getTimeOutCounter()*1000, 1000) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                txt_bidding_progressbar.setText(String.format("%02d", seconds % 60));
                bidding_progressbar.setProgress((bidding_progressbar.getMax()-(int)((seconds*2) % 60)));
            }
            @Override
            public void onFinish() {
                voiceSoundPlayer.stopVoice(BiddingActivity.this);
                jobBiddingRequest("TimeOut");
            }
        }.start();
    }

    public void setUiElements() {
        biddingJobData=db.getRunningJobData();
        new GetNearByPlaces().execute();
        lbl_pickuploc = (TextView) findViewById(R.id.lbl_pickuploc);
        lbl_droploc = (TextView) findViewById(R.id.lbl_droploc);

        txt_bidding_kms = (TextView) findViewById(R.id.txt_bidding_kms);
        txt_bidding_duration = (TextView) findViewById(R.id.txt_bidding_duration);

        txtDriverNote = (TextView) findViewById(R.id.txtDriverNote);
        btn_nothanks = (Button) findViewById(R.id.btn_nothanks);
        btn_bidding_accept = (Button) findViewById(R.id.btn_bidding_accept);

        bidding_progressbar = (ProgressBar) findViewById(R.id.bidding_progressbar);
        //bidding_progressbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.color_toolbar), PorterDuff.Mode.SRC_IN);
        txt_bidding_progressbar = (TextView) findViewById(R.id.txt_bidding_progressbar);

        lbl_pickuploc.setTypeface(textFonts,Typeface.BOLD);
        lbl_droploc.setTypeface(textFonts,Typeface.BOLD);

        txt_bidding_kms.setTypeface(textFonts,Typeface.BOLD);
        txt_bidding_duration.setTypeface(textFonts,Typeface.BOLD);

        txtDriverNote.setTypeface(textFonts,Typeface.BOLD);
        btn_nothanks.setTypeface(textFonts,Typeface.BOLD);
        btn_bidding_accept.setTypeface(textFonts,Typeface.BOLD);

        toolbar_title.setText("Job Id:"+biddingJobData.get("JobId"));
        RunningJobDetails.setTimeOutCounter(Integer.parseInt(biddingJobData.get("paymentMode")));//Payment mode==Timeout
        lbl_pickuploc.setText(String.valueOf(biddingJobData.get("pickupLoc")));
        lbl_droploc.setText(String.valueOf(biddingJobData.get("dropLoc")));
        txtDriverNote.setText("Note :"+String.valueOf(biddingJobData.get("dropLongitude")));//Note As Drop Lon
    }

    public void jobBiddingRequest(final String messageReply){
        String url = ConfigData.getClientURL()+"/BiddingRequest";
        Map<String, String> params=null;
        try {
         //  string distance, string ETA
            params = new HashMap<String, String>();
            params.put("imeiNo", Utils.getImei_no());
            params.put("messageId",biddingJobData.get("JobId"));
            params.put("messageReply",messageReply);
            params.put("lat",String.valueOf(GpsData.getLatitude()));
            params.put("lon",String.valueOf(GpsData.getLongitude()));
            params.put("datetime",getDateForRequests());
            params.put("distance",String.valueOf(RunningJobDetails.getDistance()));
            params.put("ETA",String.valueOf(RunningJobDetails.getEta()));

        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+"jobBiddingRequest"+e.getMessage());
        }
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.alt_loading));
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(
                params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response!=null) {
                        if(response.getInt("status")==0){
                            countDownTimer.cancel();
                            pDialog.dismiss();
                            HashMap<String,String> mainScreenData = db.getMainScreenData();
                            double shiftDist=Double.parseDouble(mainScreenData.get("shiftDist"));
                            int completedJobs=Integer.parseInt(mainScreenData.get("completedJobs"));
                            int cancelledJobs=Integer.parseInt(mainScreenData.get("cancelledJobs"))+1;
                            double earnedTime=Double.parseDouble(mainScreenData.get("earnedTime"));
                            double todayEarnings=Double.parseDouble(mainScreenData.get("todayEarnings"));
                            db.updateShiftData(String.valueOf(shiftDist),
                                    String.valueOf(completedJobs),String.valueOf(cancelledJobs),
                                    String.valueOf(earnedTime),String.valueOf(todayEarnings),1);

                            db.updateCurrentScreenVal("MAIN_ACTIVITY",1);
                            HashMap<String,String> bidHistorydata = new HashMap<String, String>();
                            bidHistorydata.put("JobId",biddingJobData.get("JobId"));
                            bidHistorydata.put("totalBill","0.0");
                            bidHistorydata.put("jobStatus",messageReply);
                            bidHistorydata.put("pickupLoc",biddingJobData.get("pickupLoc"));
                            bidHistorydata.put("dropLoc",biddingJobData.get("dropLoc"));
                            bidHistorydata.put("startTime",GetDeviceTime.getTime());
                            bidHistorydata.put("endTime","");
                            bidHistorydata.put("startDate",GetDeviceTime.getDate());
                            bidHistorydata.put("endDate","");
                            bidHistorydata.put("totalTripDist",RunningJobDetails.getDistance());
                            bidHistorydata.put("totalTripDuration",RunningJobDetails.getEta());

                            db.insertBidHistoryData(bidHistorydata);
                            Intent intent = new Intent(BiddingActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_LONG).show();
                        }
                        else if(response.getInt("status")==1){
                            pDialog.dismiss();
                            countDownTimer.cancel();
                            db.updateCurrentScreenVal("MAIN_ACTIVITY",1);
                            if(messageReply.equals("Reject")){
                                HashMap<String,String> mainScreenData = db.getMainScreenData();
                                double shiftDist=Double.parseDouble(mainScreenData.get("shiftDist"));
                                int completedJobs=Integer.parseInt(mainScreenData.get("completedJobs"));
                                int cancelledJobs=Integer.parseInt(mainScreenData.get("cancelledJobs"))+1;
                                double earnedTime=Double.parseDouble(mainScreenData.get("earnedTime"));
                                double todayEarnings=Double.parseDouble(mainScreenData.get("todayEarnings"));
                                db.updateShiftData(String.valueOf(shiftDist),
                                        String.valueOf(completedJobs),String.valueOf(cancelledJobs),
                                        String.valueOf(earnedTime),String.valueOf(todayEarnings),1);

                                HashMap<String,String> bidHistorydata = new HashMap<String, String>();
                                bidHistorydata.put("JobId",biddingJobData.get("JobId"));
                                bidHistorydata.put("totalBill","0.0");
                                bidHistorydata.put("jobStatus",messageReply);
                                bidHistorydata.put("pickupLoc",biddingJobData.get("pickupLoc"));
                                bidHistorydata.put("dropLoc",biddingJobData.get("dropLoc"));
                                bidHistorydata.put("startTime",GetDeviceTime.getTime());
                                bidHistorydata.put("endTime","");
                                bidHistorydata.put("startDate",GetDeviceTime.getDate());
                                bidHistorydata.put("endDate","");
                                bidHistorydata.put("totalTripDist",RunningJobDetails.getDistance());
                                bidHistorydata.put("totalTripDuration",RunningJobDetails.getEta());

                                db.insertBidHistoryData(bidHistorydata);
                            }
                            Intent intent = new Intent(BiddingActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(intent);

                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_LONG).show();
                        }
                    }else{
                        pDialog.dismiss();
                        MyLog.appendLog(DEBUG_KEY+"Response is null");
                        Toast.makeText(getApplicationContext(),"Server Returns Null",Toast.LENGTH_LONG).show();
                    }
                    pDialog.dismiss();
                } catch (Exception e) {
                    MyLog.appendLog(DEBUG_KEY+""+e.getMessage());
                    Toast.makeText(getApplicationContext(),"Time Out..!",Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Time Out..!",Toast.LENGTH_LONG).show();
                pDialog.dismiss();
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
    public String getDateForRequests() {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d",now.year,(now.month+1),now.monthDay,now.hour,now.minute,now.second);
        return currentTime;
    }
    public String getcurrentTimeToDisplay() {
        String currentTime=null;
        try {
            Time now = new Time(Time.getCurrentTimezone());
            now.setToNow();
            currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d",now.year,(now.month+1),now.monthDay,now.hour,now.minute,now.second);
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+""+e.getMessage());
        }
        return currentTime;
    }

    @Override
    public void onBackPressed() {

    }
    private class GetNearByPlaces extends AsyncTask<String, Void, String> {
        public int noOfAttempts=0;
        String response="";
        @Override
        protected String doInBackground(String... params) {
            try{
                StringBuilder sb = new StringBuilder(configDbData.get("googleAPI")+"?");
                sb.append("origins="+GpsData.getLatitude()+","+GpsData.getLongitude());
                sb.append("&destinations="+biddingJobData.get("pickupLatitude")+","+biddingJobData.get("pickupLongitude"));
                sb.append("&key="+configDbData.get("googleAPIKey"));
                ServiceHandler sh = new ServiceHandler(null);
                response = sh.makeServiceCall(sb.toString(),ServiceHandler.GET,null);
                if(response == null)
                    return "SENDING_ERROR";
                else {
                    return response;
                }
            }
            catch (Exception e){
                MyLog.appendLog(DEBUG_KEY+""+e.getMessage());
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response){
            try{
                JSONObject json= new JSONObject(response);
                if(!response.contains("SENDING_ERROR")){
                    if (json.getString("status").equals("OK")) {
                        JSONArray array = json.getJSONArray("rows");
                        JSONObject routes = array.getJSONObject(0);

                        JSONArray elements = routes.getJSONArray("elements");
                        JSONObject content = elements.getJSONObject(0);

                        JSONObject distance = content.getJSONObject("distance");
                        JSONObject eta = content.getJSONObject("duration");

                        RunningJobDetails.setDistance(distance.getString("text"));
                        RunningJobDetails.setEta(eta.getString("text"));
                        txt_bidding_kms.setText(String.valueOf(RunningJobDetails.getDistance()));//dropLat as Distance KM
                        txt_bidding_duration.setText(String.valueOf(RunningJobDetails.getEta()));

                    } else {
                        if(noOfAttempts<2){
                            noOfAttempts++;
                            new GetNearByPlaces().execute();
                        }else{
                            System.out.println("Tried 2 Times");
                        }
                    }
                }
                else {
                    if(noOfAttempts<2){
                        noOfAttempts++;
                        new GetNearByPlaces().execute();
                    }else{
                        System.out.println("Tried 2 Times");
                    }
                }
            }
            catch(Exception e){
                MyLog.appendLog(DEBUG_KEY+"GetNearByPlaces onPostExecute"+e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager)AppController.getInstance().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGpsGsmStatusDialog(BiddingActivity.this,textFonts,"Please enable the GPS","Bidd");
        } else if(!NetworkStatus.isInternetPresent(BiddingActivity.this).equals("On")){
            showGpsGsmStatusDialog(BiddingActivity.this,textFonts,"Please Turn on Internet","Bidd");
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
