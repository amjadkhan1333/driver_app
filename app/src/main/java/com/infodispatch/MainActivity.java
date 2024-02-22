package com.infodispatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.APIs.APIClass;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appcontroller.AppController;
import com.appcontroller.LanguageController;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.security.ProviderInstaller;
import com.howen.howennative.gpio_info;
import com.log.MyLog;

import com.infodispatch.R;
import com.services.PollingService;
import com.session.PulseManager;
import com.session.SessionManager;
import com.settersgetters.ConfigData;
import com.settersgetters.GpsData;
import com.settersgetters.RunningJobDetails;
import com.settersgetters.ShiftDetails;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.infodispatch.GpsCheckerDialog.showGpsGsmStatusDialog;

public class MainActivity extends AppCompatActivity {

    private LayoutInflater inflater;
    private ListView listview_home;
    private FloatingActionButton fab_emergency,home_fab;
    Integer[] listImages;
    String[] title,subtitle; //= {"Street Job", "History", "Profile", "Available Credit"};
    Integer[] listImages_Hellocabs = {R.drawable.available_credit,R.drawable.streetjob_new,R.drawable.history};
    private TextView lbl_completed,txt_completed,lbl_cancelled,txt_cancelled,lbl_earntime,txt_home_earning,
            lbl_home_earning,txt_earntime,lbl_home_currency,lbl_lock_msg_off,lbl_lock_msg,lbl_login_hours,txt_login_hours;
    Dialog dialogStatus,dialogLock;
    ImageView img_dig_cancel,img_lock;
    Typeface textFonts;
    ListItem adapter;
    TextView toolbar_title,toolbar_date,txt_switch;
    Toolbar toolbar;
    SwitchCompat toolbar_switch;
    Dialog dialogEmergency;
    RelativeLayout layout_main;
    SessionManager session;
    PulseManager pulseManager;
    String currency;
    DBHelper db;
    HashMap<String, String> main_screen_data;
    HashMap<String,String> configData;
    EmergencyAlert emergencyAlert = new EmergencyAlert();
    Dialog dialogConfirm;
    DisplayMetrics displaymetrics;
    int height, width;
    Window window;
    private RelativeLayout bannerLayout;
    private Geocoder geocoder;
    private List<Address> addresses;
    public String DEBUG_KEY="MainActivity";
    HashMap<String,String> configDbData;
    HashMap<String, String> getSettingsInfo;
    Locale myLocale;
    Context context;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    VoiceSoundPlayer voiceSoundPlayer = new VoiceSoundPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        LanguageController.setLanguage(MainActivity.this);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);

//        boolean inside = PolyUtil.containsLocation(new LatLng(12.22,19.22), poly, true);

        
        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        ConfigData.setDeviceStatus(ConfigData.Device_Status.devicestatus_free.ordinal());
        textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
        title = getResources().getStringArray(R.array.home_title_hellocabs);
        subtitle = getResources().getStringArray(R.array.home_subtitle_hellocabs);
        listImages = listImages_Hellocabs;
        currency = "Rs";

        session = new SessionManager(this);
        pulseManager = new PulseManager(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getResources().getString(R.string.home));
        toolbar_title.setTypeface(textFonts, Typeface.BOLD);
        toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);
        toolbar_date.setVisibility(View.GONE);
        toolbar_switch = (SwitchCompat)toolbar.findViewById(R.id.toolbar_switch);
        txt_switch = (TextView) toolbar.findViewById(R.id.txt_switch);
        txt_switch.setTypeface(textFonts, Typeface.BOLD);
        toolbar_switch.setChecked(true);
        setSupportActionBar(toolbar);
        configDbData= db.getConfigDBValues();
        setUiElements();
        //updateAndroidSecurityProvider(MainActivity.this);
       /* try {
            new GetVersionCode().execute();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/
        if(db.getCurrentScreenVal().equalsIgnoreCase("BREAK")){
            ConfigData.setDeviceStatus(ConfigData.Device_Status.devicestatus_Break.ordinal());
            txt_switch.setText(getResources().getText(R.string.txt_switch_off));
            txt_switch.setTextColor(getResources().getColor(R.color.color_red));
            layout_main.setAlpha(0.9f);
            showLockDialog();
            toolbar_switch.setChecked(false);
        }else{
            db.updateCurrentScreenVal("MAIN_ACTIVITY",1);
        }

        adapter = new ListItem(MainActivity.this,title);
        listview_home.setAdapter(adapter);

        toolbar_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    txt_switch.setText(getResources().getText(R.string.txt_switch_on));

                } else {
                    onBreakRequest(ConfigData.Device_Status.devicestatus_Break.ordinal());
                    //showLockDialog();
                }
            }
        });
                fab_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkStatus.isInternetPresent(MainActivity.this).equals("On")) {
                    emergencyAlert.emergency_alert(MainActivity.this,"108","ACTIVITY");
                }
                else{
                    NetworkCheckDialog.showConnectionTimeOut(MainActivity.this);
                }
            }
        });

    }

    public void setUiElements() {
        main_screen_data=db.getMainScreenData();
        bannerLayout = (RelativeLayout)findViewById(R.id.bannerLayout);
        layout_main = (RelativeLayout)findViewById(R.id.layout_main);
        listview_home = (ListView)findViewById(R.id.listview_home);
        fab_emergency = (FloatingActionButton) findViewById(R.id.btn_emergency);

        lbl_completed = (TextView) findViewById(R.id.lbl_completed);
        txt_completed = (TextView) findViewById(R.id.txt_completed);
        lbl_cancelled = (TextView) findViewById(R.id.lbl_cancelled);
        txt_cancelled = (TextView) findViewById(R.id.txt_cancelled);
        lbl_earntime = (TextView) findViewById(R.id.lbl_earntime);
        txt_earntime = (TextView) findViewById(R.id.txt_earntime);

        lbl_home_earning = (TextView) findViewById(R.id.lbl_home_earning);
        txt_home_earning = (TextView) findViewById(R.id.txt_home_earning);
        txt_home_earning.setText(String.valueOf(Integer.toString((int)Math.round(Double.parseDouble(main_screen_data.get("todayEarnings"))))));

        lbl_home_currency = (TextView) findViewById(R.id.lbl_home_currency);
        lbl_home_currency.setText("in "+configDbData.get("currencyType"));

        lbl_login_hours = (TextView) findViewById(R.id.lbl_login_hours);
        txt_login_hours = (TextView) findViewById(R.id.txt_login_hours);

        txt_login_hours.setText(String.valueOf(" "+main_screen_data.get("loginDateTime")));

        lbl_completed.setTypeface(textFonts,Typeface.BOLD);
        lbl_cancelled.setTypeface(textFonts,Typeface.BOLD);
        lbl_earntime.setTypeface(textFonts,Typeface.BOLD);
        lbl_home_earning.setTypeface(textFonts,Typeface.BOLD);
        lbl_home_currency.setTypeface(textFonts,Typeface.BOLD);
        lbl_login_hours.setTypeface(textFonts,Typeface.BOLD);

        txt_completed.setTypeface(textFonts);
        txt_completed.setText(String.valueOf(main_screen_data.get("completedJobs")));
        txt_cancelled.setTypeface(textFonts);
        txt_cancelled.setText(String.valueOf(main_screen_data.get("cancelledJobs")));
        txt_earntime.setTypeface(textFonts);
        txt_earntime.setText(String.valueOf(String.valueOf(Integer.toString((int)Math.round(Double.parseDouble(main_screen_data.get("earnedTime")))))));
        try{
            System.out.println("Inside the getClientName=> "+ConfigData.getClientName());
            bannerLayout.setBackgroundResource(getImageId(getApplicationContext(), ConfigData.getClientName().toLowerCase()));
        } catch (Exception e) {
            bannerLayout.setBackgroundResource(getImageId(getApplicationContext(), "AK_logo"));
        }
        //setLanguage();
        ShiftDetails.setShiftId(main_screen_data.get("shiftId"));
       // LanguageController.setLanguage(MainActivity.this);
    }
    public static int getImageId(Context context, String clientName) {
        System.out.println("Inside the getImageId  getClientName=> "+ConfigData.getClientName());
        return context.getResources().getIdentifier("drawable/ban_qatdemo", null, context.getPackageName());
        //return context.getResources().getIdentifier("drawable/dashboard_banner", null, context.getPackageName());
        //return context.getResources().getIdentifier("drawable/dashboard_banner", null, context.getPackageName());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            case R.id.action_status:
                 StatusDialog.showStatusDialog(MainActivity.this,textFonts);
                return true;
            case R.id.action_logout:
                try{
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom);
                    alertDialog.setTitle(getResources().getString(R.string.log_out));
                    alertDialog.setMessage(getResources().getString(R.string.are_sure));
                    alertDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            dialog.cancel();
                            try {
                                if (NetworkStatus.isInternetPresent(MainActivity.this).equals("On")) {
                                    logoutRequest();
                                }
                                else{
                                    NetworkCheckDialog.showConnectionTimeOut(MainActivity.this);
                                }
                            }
                            catch (Exception e){
                            }
                        }
                    });
                    alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }catch (Exception e){
                    System.out.println("Inside the Exception=>"+e);
                }

                return true;
            case R.id.action_user:

                Intent profileAct = new Intent(getBaseContext(), ProfileActivity.class);
                profileAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(profileAct);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showLockDialog() {
        try
        {
            LanguageController.setLanguage(MainActivity.this);
            dialogLock = new Dialog(this);
            dialogLock.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogLock.setContentView(R.layout.dialog_lock);
            dialogLock.setCanceledOnTouchOutside(false);
            dialogLock.setCancelable(false);
            dialogLock.show();
            dialogLock.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            lbl_lock_msg_off = (TextView)dialogLock.findViewById(R.id.lbl_lock_msg_off);
            lbl_lock_msg_off.setTypeface(textFonts);
            lbl_lock_msg = (TextView)dialogLock.findViewById(R.id.lbl_lock_msg);
            lbl_lock_msg.setTypeface(textFonts);

            img_lock = (ImageView)dialogLock.findViewById(R.id.img_lock);
            img_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   onBreakRequest(ConfigData.Device_Status.devicestatus_free.ordinal());
                }
            });
        }
        catch(Exception Eww)
        {}
    }
    public class ListItem extends BaseAdapter {

        Context context;
        String[] data;
        String currencyType;
        public ListItem(Context context, String[] items) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            data = items;
            configData = db.getConfigData();
            currencyType=configData.get("currencyType");
            lbl_home_currency.setText(configData.get("currencyType"));
            ConfigData.setCurrencyType(currencyType);
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class Holder
        {
            TextView txtHeading,txtSubHeading,txtAmount,txtCurrency;;
            ImageView home_fab;
            View viewLine;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            System.out.println("List Item getView(): getView()");

            Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.listitem_home, null);
            holder.txtHeading=(TextView) rowView.findViewById(R.id.txtHeading);
            holder.txtSubHeading=(TextView) rowView.findViewById(R.id.txtSubHeading);
            holder.home_fab= rowView.findViewById(R.id.home_fab);

            holder.txtHeading.setTypeface(textFonts,Typeface.BOLD);
            holder.txtSubHeading.setTypeface(textFonts);

            holder.txtAmount = (TextView)rowView.findViewById(R.id.txtAmount);
            holder.txtCurrency = (TextView)rowView.findViewById(R.id.txtCurrency);
            holder.viewLine = (View)rowView.findViewById(R.id.viewLine);
            holder.txtCurrency.setTypeface(textFonts,Typeface.BOLD);
            holder.txtAmount.setTypeface(textFonts,Typeface.BOLD);
            if(position == 0){
                holder.home_fab.setImageDrawable(getApplicationContext().getResources().getDrawable(listImages_Hellocabs[position]));
                holder.txtHeading.setText(title[position]);
                holder.txtSubHeading.setText(subtitle[position]);
                holder.txtCurrency.setVisibility(View.VISIBLE);
                holder.txtAmount.setVisibility(View.VISIBLE);
                holder.viewLine.setVisibility(View.GONE);
                holder.txtHeading.setText(R.string.credit_title);
                holder.txtSubHeading.setText(R.string.credit_sub_title);
                holder.txtAmount.setText(String.valueOf(configData.get("availableBalance")));
                holder.txtCurrency.setText(currencyType);
               /* if(main_screen_data.get("todayEarnings").equals("0")){
                    if(!configData.get("creditBalance").equals("0") && (!configData.get("creditBalance").equals("0.0"))){
                        holder.txtAmount.setText(String.valueOf(configData.get("creditBalance")));
                        holder.txtHeading.setText(R.string.credit_title);
                        holder.txtSubHeading.setText(R.string.credit_sub_title);
                    }
                    else {
                        holder.txtAmount.setText(String.valueOf(configData.get("walletBalance")));
                        holder.txtHeading.setText(R.string.wallet_title);
                        holder.txtSubHeading.setText(R.string.wallet_sub_title);
                    }
                }
                else{
                    if((!main_screen_data.get("availableCreditBal").equals("0.0")) && (!main_screen_data.get("availableCreditBal").equals("0"))){
                        holder.txtAmount.setText(String.valueOf(main_screen_data.get("availableCreditBal")));
                        holder.txtHeading.setText(R.string.credit_title);
                        holder.txtSubHeading.setText(R.string.credit_sub_title);
                    } else
                    {
                        holder.txtAmount.setText(String.valueOf(main_screen_data.get("availableWallettBal")));
                        holder.txtHeading.setText(R.string.wallet_title);
                        holder.txtSubHeading.setText(R.string.wallet_sub_title);
                    }
                }*/

            }else{
                holder.home_fab.setImageDrawable(getApplicationContext().getResources().getDrawable(listImages_Hellocabs[position]));
                holder.txtHeading.setText(title[position]);
                holder.txtSubHeading.setText(subtitle[position]);
                holder.txtCurrency.setVisibility(View.GONE);
                holder.txtAmount.setVisibility(View.GONE);
                holder.viewLine.setVisibility(View.GONE);
            }
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                            if(position==0){

                            }
                            else if(position==1){
                                if (Double.parseDouble(configData.get("availableBalance")) <= Double.parseDouble(configDbData.get("maxBalance"))) {
                                    driverAmountAlert();
                                } else {
                                    showConfirmDialog("KERB");
                                }
                            }
                            else if(position==2){
                                //History Activity
                                Intent intent = new Intent(getBaseContext(), HistorySunlight.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else if(position==3){
                                //RSA Activity
                                showConfirmDialog("RSA");
                            }
                        }
            });

            return rowView;
        }
    }
    public void showConfirmDialog(final String action) {
        createKerbJobDetails();
        new GetLocationAsync(GpsData.getLattiude(), GpsData.getLongitude()).execute();
        RunningJobDetails.setJobStatus(RunningJobDetails.Job_Status.jobstatus_commenced.ordinal());
        if (NetworkStatus.isInternetPresent(MainActivity.this).equals("On")) {
            jobStatusRequest();
        }
        else{
            NetworkCheckDialog.showConnectionTimeOut(MainActivity.this);
        }
        return;
//        try {
//            LanguageController.setLanguage(MainActivity.this);
//            dialogConfirm = new Dialog(this);
//            dialogConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialogConfirm.setContentView(R.layout.dialog_confirm);
//            dialogConfirm.setCanceledOnTouchOutside(false);
//            dialogConfirm.setCancelable(false);
//            final CheckBox checkBox = dialogConfirm.findViewById(R.id.check_hotel);
//            if(action.equals("KERB")){
//                TextView dialog_are_you_sure = (TextView) dialogConfirm.findViewById(R.id.dialog_are_you_sure);
//                dialog_are_you_sure.setText(getResources().getString(R.string.lbl_are_you_sure));
//                dialog_are_you_sure.setTypeface(textFonts);
//                session.resetWating();
//                pulseManager.resetWating();
//            }else{
//                TextView dialog_are_you_sure = (TextView) dialogConfirm.findViewById(R.id.dialog_are_you_sure);
//                dialog_are_you_sure.setText(getResources().getString(R.string.lbl_are_you_sure_rsa));
//                dialog_are_you_sure.setTypeface(textFonts);
//            }
//            Button dialog_btn_no = (Button) dialogConfirm.findViewById(R.id.dialog_btn_no);
//            Button dialog_btn_yes = (Button) dialogConfirm.findViewById(R.id.dialog_btn_yes);
//
//            dialog_btn_no.setTypeface(textFonts, Typeface.BOLD);
//            dialog_btn_yes.setTypeface(textFonts, Typeface.BOLD);
//
//            dialog_btn_no.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialogConfirm.dismiss();
//                }
//            });
//
//            dialog_btn_yes.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialogConfirm.dismiss();
//                    if(action.equals("KERB")){
//                        dialogConfirm.dismiss();
//                        if(checkBox.isChecked()) {
//                            pulseManager.setHotelJob(true);
////                            pulseManager.setExtraFare((float) (7 + pulseManager.getExtraFare()));
//                        }
//                        createKerbJobDetails();
//                        new GetLocationAsync(GpsData.getLattiude(), GpsData.getLongitude()).execute();
//                        RunningJobDetails.setJobStatus(RunningJobDetails.Job_Status.jobstatus_commenced.ordinal());
//                        if (NetworkStatus.isInternetPresent(MainActivity.this).equals("On")) {
//                            jobStatusRequest();
//                        }
//                        else{
//                            NetworkCheckDialog.showConnectionTimeOut(MainActivity.this);
//                        }
//                    }
//                    else {
//                            dialogConfirm.dismiss();
//                            Toast.makeText(getApplicationContext(),"Coming Soon",Toast.LENGTH_LONG).show();
//                           /* Intent in  = new Intent(MainActivity.this,RSARoutes.class);
//                            startActivity(in);*/
//                }
//                }
//            });
//        } catch (Exception Eww) {
//        }
//        dialogConfirm.show();

    }
    @Override
    public void onBackPressed() {
        try{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom);
            alertDialog.setTitle(getResources().getString(R.string.exit));
            alertDialog.setMessage(getResources().getString(R.string.are_sure));
            alertDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    dialog.cancel();
                    try {
                        finish();
                    }
                    catch (Exception e){

                    }
                }
            });
            alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }catch (Exception e){
            System.out.println("Inside the Exception=>"+e);
        }
    }
    public String getcurrentTimeToDisplay() {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d",
                now.year,(now.month+1),now.monthDay,now.hour,now.minute,now.second);
        return currentTime;
    }
    public String getDateForRequests() {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d",now.year,(now.month+1),now.monthDay,now.hour,now.minute,now.second);
        return currentTime;
    }
    public String getDateKerbJob() {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String currentTime = String.format("%02d%02d%02d%02d%02d%02d",now.monthDay,(now.month+1),now.year,now.hour,now.minute,now.second);
        return currentTime;
    }
    public void jobStatusRequest(){
        if (getString(R.string.app_publish_mode).equals("MDT")) {
            try {
                if (gpio_info.open_gpio() > 0) {
                    if (gpio_info.get_gpio_data("P3B6") == 0)
                        gpio_info.set_gpio_data("P3B6", 1);
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "GPIPO ERROR" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        String url = ConfigData.getClientURL()+APIClass.jobs_status_Api;
        Map<String, String> params = new HashMap<String, String>();
        params.put("imeiNo", Utils.getImei_no());
        params.put("jobId",RunningJobDetails.getJobId());
        params.put("jobStatus",String.valueOf(RunningJobDetails.getJobStatus()));
        params.put("loginId",ConfigData.getDriverId());
        params.put("shiftId",main_screen_data.get("shiftId"));
        params.put("lat",String.valueOf(GpsData.getLattiude()));
        params.put("lon", String.valueOf(GpsData.getLongitude()));
        params.put("speed", "0");
        params.put("dateTime",getDateForRequests());
        final ProgressDialog pDialog = new ProgressDialog(this);
        System.out.println("jobStatusRequest"+params);
        pDialog.setMessage(getResources().getString(R.string.alt_starting_trip));
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getInt("status")==0){
                        Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_LONG).show();
                    }
                    else{
                        //TODO MINIMUM FARE
                        if(pulseManager.isDay())
                            pulseManager.setMinDistance(4.116f);
                        else
                            pulseManager.setMinDistance(3.528f);
                        // createKerbJobDetails();
                        voiceSoundPlayer.playVoice(MainActivity.this,R.raw.jobstarted);
                        Intent intent = new Intent(MainActivity.this, KerbActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(intent);
                    }
                    pDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
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
    public void createKerbJobDetails() {
        if (db.getJobCounterCount() == 0) {
            db.insetJobCounterVal(0, "K"+GetDeviceTime.getDateForKerbJob());
            RunningJobDetails.setJobId("K"+GetDeviceTime.getDateForKerbJob());
            db.updateJobCounterVal(0,RunningJobDetails.getJobId(), 1);
        } else {
            RunningJobDetails.setJobId("K"+GetDeviceTime.getDateForKerbJob());
            db.updateJobCounterVal(0,RunningJobDetails.getJobId(), 1);
        }
        ShiftDetails.setShiftId(main_screen_data.get("shiftId"));
        GpsData.setStartManualOdo("0");
        GpsData.setStartGPSOdo(GpsData.getGpsOdometer());
        /*SessionManager session = new SessionManager(AppController.getInstance());
        session.createStartGPSOdoSession();*/
        db.updateStartGpsOd(String.valueOf(GpsData.getStartGPSOdo()),1);
        System.out.println("Gps ODO Inside the Main while Starting the Job:"+GpsData.getStartGPSOdo());
        RunningJobDetails.setTripStartDateTime(getcurrentTimeToDisplay());
        RunningJobDetails.setStartTime(GetDeviceTime.getTime());
        RunningJobDetails.setStartDate(GetDeviceTime.getDate());
        RunningJobDetails.setPromoCode("Promo Code");

        ConfigData.setCurrencyType(configData.get("currencyType"));
        ConfigData.setFixedFare(configData.get("fixedFare"));
        ConfigData.setBaseKm(Long.parseLong(configData.get("baseKms")));
        ConfigData.setBaseHours(Double.parseDouble(configData.get("baseHours")));
        ConfigData.setExtraKmRate(Double.parseDouble(configData.get("extraKmRate")));
        ConfigData.setExtraHourRate(Double.parseDouble(configData.get("extraHourRates")));
        RunningJobDetails.setTotalTripFare(Double.parseDouble(configData.get("fixedFare")));
    }
    public void showLogout() {
        Intent intent = new Intent(getBaseContext(), DriverLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void setConfigVal(){
        ConfigData.setClientName(configData.get("clientName"));
        ConfigData.setFixedFare(configData.get("fixedFare"));
        ConfigData.setBaseKm(Long.parseLong(configData.get("baseKms")));
        ConfigData.setBaseHours(Double.parseDouble(configData.get("baseHours")));
        ConfigData.setExtraKmRate(Double.parseDouble(configData.get("extraKmRate")));
        ConfigData.setExtraHourRate(Double.parseDouble(configData.get("extraHourRates")));
        ConfigData.setCurrencyType(configData.get("currencyType"));
        ConfigData.setDriverId(configData.get("driverId"));
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
                geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
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
                Log.e("tag", e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                RunningJobDetails.setPickupLoc(addresses.get(0).getAddressLine(0) + " , "
                        + addresses.get(0).getAddressLine(1) + " " + addresses.get(0).getAddressLine(2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void logoutRequest(){
        String url = ConfigData.getClientURL()+APIClass.logtout_Api;
        Map<String, String> params = new HashMap<String, String>();
        params.put("imeiNo", Utils.getImei_no());
        params.put("driverId",ConfigData.getDriverId());
        params.put("shiftId",main_screen_data.get("shiftId"));
        params.put("shiftDist",main_screen_data.get("shiftDist"));
        params.put("noOfTrips",main_screen_data.get("completedJobs"));
        params.put("totlaShiftAMT",main_screen_data.get("todayEarnings"));
        params.put("hiredKM","");
        params.put("lat",String.valueOf(GpsData.getLattiude()));
        params.put("lon", String.valueOf(GpsData.getLongitude()));
        params.put("dateTime",getDateForRequests());
        final ProgressDialog pDialog = new ProgressDialog(this);
        System.out.println("logoutRequest"+params);
        pDialog.setMessage(getResources().getString(R.string.alt_logging_out));
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response!=null){
                        pDialog.dismiss();
                        if(response.getInt("status")==0){
                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_LONG).show();
                            db.updateMainScreenData("0","0","0","0","0",1);
                            Intent intent = new Intent(MainActivity.this, DriverLogin.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            stopService(new Intent(MainActivity.this, PollingService.class));
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        pDialog.dismiss();
                        MyLog.appendLog(DEBUG_KEY+"Response is null");
                        Toast.makeText(getApplicationContext(),"Server Returns Null",Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Time out.Please Try again",Toast.LENGTH_LONG).show();
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
public void driverAmountAlert(){

    try{
        AlertDialog.Builder driverAlert = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom);
        driverAlert.setTitle(getResources().getString(R.string.alt_alert));
        driverAlert.setMessage(getResources().getString(R.string.alt_low_balance));
        driverAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
                try {
                    if (Double.parseDouble(configData.get("availableBalance")) < Double.parseDouble(configDbData.get("minBalance"))) {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.alt_dont_sufficient_bal),Toast.LENGTH_LONG).show();
                    } else {
                        showConfirmDialog("KERB");
                    }
                } catch (Exception e) {

                }
            }
        });
        driverAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
            }
        });
        driverAlert.show();
    }catch (Exception e){
        System.out.println("Inside the Exception=>"+e);
    }
 }

    @Override
    protected void onResume() {
        super.onResume();
     //   setLanguage();
        LocationManager locationManager = (LocationManager)AppController.getInstance().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGpsGsmStatusDialog(MainActivity.this,textFonts,"Please enable the GPS","Main");
        }
        else if(!NetworkStatus.isInternetPresent(MainActivity.this).equals("On")){
            showGpsGsmStatusDialog(MainActivity.this,textFonts,"Please Turn on Internet","Main");
        }
        try {
            main_screen_data=db.getMainScreenData();
            //txt_home_earning.setText(String.valueOf(Integer.toString((int)Math.round(Double.parseDouble(main_screen_data.get("todayEarnings"))))));
            txt_home_earning.setText(new DecimalFormat("0.00").format(Double.valueOf(main_screen_data.get("todayEarnings"))));
            txt_completed.setText(String.valueOf(main_screen_data.get("completedJobs")));
            txt_cancelled.setText(String.valueOf(main_screen_data.get("cancelledJobs")));
            txt_earntime.setText(String.valueOf(String.valueOf(Integer.toString((int)Math.round(Double.parseDouble(main_screen_data.get("earnedTime")))))));
            registerReceiver(broadcastReceiver, new IntentFilter(PollingService.BROADCAST_ACTION));
        } catch (Exception e) {
            MyLog.appendLog("MainActivity"+"onResume()"+e.getMessage());
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                updateUI(intent);
            } catch (Exception e) {
                MyLog.appendLog("MainActivity"+"broadcastReceiver"+e.getMessage());
            }

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(broadcastReceiver!=null){
                unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception e) {
            MyLog.appendLog("MainActivity"+"onPause"+e.getMessage());
        }
    }

    private void updateUI(Intent intent) {
        try {
            String val =intent.getStringExtra("notifyDataChanged");
            System.out.println("Inside the UpdateUi"+val);
            main_screen_data=db.getMainScreenData();
            adapter = new ListItem(MainActivity.this,title);
            listview_home.setAdapter(adapter);
        } catch (Exception e) {
            MyLog.appendLog("MainActivity"+"updateUI"+e.getMessage());
        }
    }
    public void onBreakRequest(final int breakStatus){
        //  {"imeiNO":"357422071316827","status":"0","driverId":"262","lat":"12.22","lon":"23.23","dateTime":"2017-01-06 17:59:30.247"}

        String url = ConfigData.getClientURL()+APIClass.onbreak_Api;
        Map<String, String> params = new HashMap<String, String>();
        params.put("imeiNO", Utils.getImei_no());
        params.put("status",String.valueOf(breakStatus));
        params.put("driverId",ConfigData.getDriverId());
        params.put("lat",String.valueOf(GpsData.getLattiude()));
        params.put("lon", String.valueOf(GpsData.getLongitude()));
        params.put("dateTime",getDateForRequests());
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Updating status...");
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response!=null){
                        if(response.getString("status").equals("0")){
                            Toast.makeText(AppController.getInstance(),response.getString("message"),Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(AppController.getInstance(),response.getString("message"),Toast.LENGTH_LONG).show();
                            if(breakStatus==1){ //Free
                                //showLockDialog();
                                if(dialogLock!=null){
                                    dialogLock.hide();
                                }
                                db.updateCurrentScreenVal("MAIN_ACTIVITY",1);
                                ConfigData.setDeviceStatus(ConfigData.Device_Status.devicestatus_free.ordinal());
                                toolbar_switch.setChecked(true);
                                txt_switch.setTextColor(getResources().getColor(R.color.textColorPrimary));
                                txt_switch.setText(getResources().getText(R.string.txt_switch_on));
                            }
                            else{
                                //Break
                                db.updateCurrentScreenVal("BREAK",1);
                                ConfigData.setDeviceStatus(ConfigData.Device_Status.devicestatus_Break.ordinal());
                                txt_switch.setText(getResources().getText(R.string.txt_switch_off));
                                txt_switch.setTextColor(getResources().getColor(R.color.color_red));
                                layout_main.setAlpha(0.9f);
                                toolbar_switch.setChecked(false);
                                showLockDialog();
                            }
                        }
                        pDialog.dismiss();
                    }else{
                        pDialog.dismiss();
                        MyLog.appendLog(DEBUG_KEY+"Response is null");
                        Toast.makeText(getApplicationContext(),"Server Returns Null",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    toolbar_switch.setChecked(true);
                    txt_switch.setTextColor(getResources().getColor(R.color.textColorPrimary));
                    txt_switch.setText(getResources().getText(R.string.txt_switch_on));
                    Toast.makeText(AppController.getInstance(),"Time Out.Please try again",Toast.LENGTH_LONG).show();
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
    public void setLanguage()
    {
        String myLang;
        try {
            getSettingsInfo = db.getSettingsInfo();
            if (getSettingsInfo.get("lang_settings").equalsIgnoreCase("Maynamar")) {
                myLang = "my";
            } else {
                myLang = "en";
            }
            myLocale = new Locale(myLang);
            Locale.setDefault(myLocale);
            Configuration conf = new Configuration();
            conf.locale = myLocale;
            getBaseContext().getResources().updateConfiguration(conf,
                    getBaseContext().getResources().getDisplayMetrics());
            //this.setContentView(R.layout.activity_main);
        }
        catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+" setLanguage "+e);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus){
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
            {

            }
        }
    }

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }
    private class GetVersionCode extends AsyncTask<Void, String, String> {
        String newVersion;
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        String currentVersion = pInfo.versionName;

        private GetVersionCode() throws PackageManager.NameNotFoundException {
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=com.info.hellotaxidispatch&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);

            if (!currentVersion.equalsIgnoreCase(onlineVersion)) {
                //show dialog
                new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom)
                        .setTitle("Updated app available!")
                        .setMessage("Want to update app?")
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    //Toast.makeText(getApplicationContext(), "App is in BETA version cannot update", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        })
                        .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }
}
