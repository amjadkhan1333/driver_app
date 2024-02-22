package com.infodispatch;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.APIs.APIClass;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appcontroller.AppController;
import com.appcontroller.LanguageController;
import com.commands.PrintFormattedBill;
import com.connectors.BTConnectionException;
import com.connectors.BTConnector;
import com.connectors.BluetoothService;
import com.connectors.Constants;
import com.howen.howennative.gpio_info;
import com.log.MyLog;
import com.session.PulseManager;
import com.settersgetters.ConfigData;
import com.settersgetters.GpsData;
import com.settersgetters.RunningJobDetails;
import com.settersgetters.UpdateCurrentJobData;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;
import com.visiontek.app.bt.library.vtekbt.VisionTekBT;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.infodispatch.BuildConfig.DEBUG;
import static com.infodispatch.GpsCheckerDialog.showGpsGsmStatusDialog;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by info on 24-11-2016.
 */
public class BillActivity extends AppCompatActivity {
    private Button btn_finish, btn_print;
    private WebView webviewBill;
    private DBHelper db;
    private Typeface textFonts;
    private SwitchCompat toolbar_switch;
    private TextView toolbar_title, toolbar_date, txt_switch, txt_total_fare, lbl_currency;
    private Toolbar toolbar;
    public String DEBUG_KEY = "BillActivity";
    private HashMap<String, String> main_screen_data;
    private VoiceSoundPlayer voiceSoundPlayer = new VoiceSoundPlayer();
    private BTConnector mConnector;
    private ProgressDialog mConnectingDlg;

    private ArrayList<String> labels;
    private ArrayList<String> values;

    private ArrayList<BluetoothDevice> pairedDeviceArrayList;
    private ArrayAdapter<String> pairedDeviceAdapter;
    private ListView listViewPairedDevice;
    private BluetoothAdapter bluetoothAdapter;
    private AlertDialog alertDialog;
    private VisionTekBT bt = new VisionTekBT();

    // HOWEN PRINTER
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Member object for the services
    private BluetoothService mBluetoothService = null;
    private String TAG = this.getClass().getSimpleName();
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private PulseManager pulseManager;
    SharedPreferences sharedPref;
    public static final String bt_printer_pref = "btprinterpref";
    public static final String bt_device_mac_add = "btdevicemacadd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LanguageController.setLanguage(BillActivity.this);
        setContentView(R.layout.activity_bill);
        try {
            sharedPref = getApplicationContext().getSharedPreferences(bt_printer_pref, 0);
            pulseManager = new PulseManager(this);
            db = new DBHelper(this);
            db.updateCurrentScreenVal("BILL_ACTIVITY", 1);
            MyLog.appendLog(DEBUG_KEY + "oncreate 2 ");
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            // If the adapter is null, then Bluetooth is not supported
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth is not available",
                        Toast.LENGTH_LONG).show();
                //finish();
            } else {
                mBluetoothService = new BluetoothService(this, mHandler);
            }
            MyLog.appendLog(DEBUG_KEY + "oncreate 2_1 ");
            //  LanguageController.setLanguage(BillActivity.this);
            textFonts = Typeface.createFromAsset(this.getAssets(), "truenorg.otf");
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbar_title.setText(R.string.bill);
            toolbar_title.setTypeface(textFonts, Typeface.BOLD);
            toolbar_date = (TextView) toolbar.findViewById(R.id.toolbar_date);
            toolbar_date.setVisibility(View.GONE);
            toolbar_switch = (SwitchCompat) toolbar.findViewById(R.id.toolbar_switch);
            toolbar_switch.setVisibility(View.GONE);
            txt_switch = (TextView) toolbar.findViewById(R.id.txt_switch);
            txt_switch.setVisibility(View.GONE);
            setSupportActionBar(toolbar);
            main_screen_data = db.getMainScreenData();
            MyLog.appendLog(DEBUG_KEY + "oncreate 3 ");
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "onCreate" + e.getMessage());
        }

        setUiElements();
        //  LanguageController.setLanguage(BillActivity.this);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkStatus.isInternetPresent(BillActivity.this).equals("On")) {
                    getCompleteRequest();
                } else {
                    NetworkCheckDialog.showConnectionTimeOut(BillActivity.this);
                }
            }
        });

//        btn_print.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!mConnector.isConnected()) {
//                    if (!bluetoothAdapter.isEnabled()) {
//                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
//                    }else{
//                        setup();
//                    }
//                } else {
//                    labels = new ArrayList<String>();
//                    values = new ArrayList<String>();
//                    Document doc = Jsoup.parse(RunningJobDetails.getBillData().toString());
//                    //Document doc = Jsoup.parse(Constants.billFormat);
//                    Element table = doc.select("table").first();
//                    Iterator<Element> iterator = table.select("td").iterator();
//                    while(iterator.hasNext()){
//                        try{
//                            //String value=iterator.next().text();
//                            labels.add(iterator.next().text());
//                            values.add(iterator.next().text());
//                        }catch(Exception ex){
//                            Log.e("Bill Acitivity", "onClick: " + ex.getMessage() );
//                        }
//
//                    }
//                            /*Constants.myString="";
//                            PrintFormattedBill printFormattedBill = new PrintFormattedBill();
//                            mChatService.sendMessage(printFormattedBill.formatBill(labels,values));*/
//
//                        PrintFormattedBill printFormattedBill = new PrintFormattedBill();
//                        String myBill =printFormattedBill.formatBillForVisionTek(labels,values);
//                        bt.printerLineFeed(BTConnector.mOutputStream,
//                                BTConnector.mInputStream, 1);
//                        showToast(bt.printBillString(BTConnector.mOutputStream,
//                                BTConnector.mInputStream, myBill));
//                        bt.printerLineFeed(BTConnector.mOutputStream,
//                                BTConnector.mInputStream, 2);
//                    //getCompleteRequest();
//                }
//            }
//        });

        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
                }else{
                    sharedPref = getSharedPreferences(bt_printer_pref,Context.MODE_PRIVATE);
                    String bt_device_add = sharedPref.getString(bt_device_mac_add, "");
                    if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
//                        if (!bluetoothAdapter.isEnabled()) {
//                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
//                        } else {
//                            setup();
//                        }
                        setup();
                    } else {
                        //BluetoothPrintTest();
//                        try{
//                            mBluetoothService.connect(bluetoothAdapter.getRemoteDevice(bt_device_add));
//                        }catch (Exception ex){
//                            setup();
//                        }
                        ProgressDialog pDialog = new ProgressDialog(BillActivity.this);
                        pDialog.setMessage(getResources().getString(R.string.alt_please_wait));
                        pDialog.setCancelable(false);
                        pDialog.show();

                        labels = new ArrayList<String>();
                        values = new ArrayList<String>();
                        Document doc = Jsoup.parse(RunningJobDetails.getBillData().toString());
                        //Document doc = Jsoup.parse(Constants.billFormat);
                        Element table = doc.select("table").first();
                        Iterator<Element> iterator = table.select("td").iterator();
                        while (iterator.hasNext()) {
                            try {
                                //String value=iterator.next().text();
                                labels.add(iterator.next().text());
                                values.add(iterator.next().text());
                            } catch (Exception ex) {
                                Log.e("Bill Acitivity", "onClick: " + ex.getMessage());
                            }

                        }
                        try{
                            PrintFormattedBill printFormattedBill = new PrintFormattedBill();
                            Bitmap  bitmap = getImageFromAssetsFile("ajmantaxi.png");
                            printFormattedBill.formatBillForHowenBTPrinter(labels,values,bitmap,mBluetoothService);
                            pDialog.dismiss();
                        }
                        catch (Exception ex){
                            pDialog.dismiss();
                        }
                    }
                }

            }
        });

        MyLog.appendLog(DEBUG_KEY + "oncreate 7 ");
    }

    public void setUiElements() {
        try {
            HashMap<String, String> getJobDetails = db.getRunningJobData();
            RunningJobDetails.setJobId(getJobDetails.get("JobId"));
            RunningJobDetails.setTotalTripFare(Double.parseDouble(getJobDetails.get("totalBill")));
            RunningJobDetails.setTripStartDateTime(getJobDetails.get("tripStartDateTime"));
            RunningJobDetails.setTotalTripDist(getJobDetails.get("totalTripDist"));
            RunningJobDetails.setTotalTripDuration(getJobDetails.get("totalTripDuration"));
            RunningJobDetails.setBillData(getJobDetails.get("htmlBill"));
            RunningJobDetails.setPromoCode(getJobDetails.get("promoCode"));
            webviewBill = (WebView) findViewById(R.id.webviewBill);
            try {
                webviewBill.loadData(URLEncoder.encode(RunningJobDetails.getBillData(), "utf-8").replaceAll("\\+", "%20"), "text/html", "utf-8");
            } catch (UnsupportedEncodingException uee) {
                Log.e("webview", "", uee);
            }
            MyLog.appendLog(DEBUG_KEY + "oncreate 5 ");
            txt_total_fare = (TextView) findViewById(R.id.txt_total_fare);
            if (ConfigData.getClientName().equalsIgnoreCase("Hellocab")) {
                RunningJobDetails.setTotalTripFare(Double.parseDouble(getJobDetails.get("totalBill")));
                txt_total_fare.setText(String.valueOf(Integer.toString((int) Math.round(RunningJobDetails.getTotalTripFare()))));
            } else {
                ConfigData.setFixedFare(getJobDetails.get("totalBill"));
                // Get Fare calculated
                double calculatedFare  = Double.parseDouble(ConfigData.getFixedFare());
                // Getting Decimal point
                double pointValue = calculatedFare - (int) calculatedFare;

                //Setting it to another variable
                double calculatePointValue = pointValue;
                //Checking how many 0.25 value to round counter
                int count = 0;

                //Checking if pointvalue is greater than 0
                if(pointValue > 0){
                    // while loop till value is below 0.25
                    while(pointValue / 0.25 > 0) {
                        pointValue = pointValue - 0.25;
                        count = count + 1;
                    }
                }
                MyLog.appendLog(DEBUG_KEY + "oncreate 6 ");
                //Adding value of point value into fare
                calculatePointValue = (int) calculatedFare  + count * 0.25;

                MyLog.appendLog(DEBUG_KEY + " Billing Activity : ------ " + calculatePointValue);
                ConfigData.setFixedFare(""+calculatePointValue);

                txt_total_fare.setText(new DecimalFormat("0.00").format(Double.valueOf(ConfigData.getFixedFare())));
            }
            JSONObject infotainmentParams = new JSONObject();
            try {
                infotainmentParams.put("runningFare", txt_total_fare.getText().toString());
                infotainmentParams.put("distance", RunningJobDetails.getTotalTripDist());
                infotainmentParams.put("jobStartTime", RunningJobDetails.getTripStartDateTime());
                infotainmentParams.put("duration", RunningJobDetails.getTotalTripDuration());
                infotainmentParams.put("extra", "0.00");
                infotainmentParams.put("toll", "0.00");
                infotainmentParams.put("lat", "" + pulseManager.getLatitude());
                infotainmentParams.put("lon", "" + pulseManager.getLongitude());
                infotainmentParams.put("profile", new JSONObject(pulseManager.getJsonProfileObject()));
                pulseManager.setJsonObject(infotainmentParams.toString());
            } catch (JSONException ex) {
                Log.e(TAG, "calculateFare: infotainmentParams JSON Exception : " + ex.getMessage());
            }
            lbl_currency = (TextView) findViewById(R.id.lbl_currency);
            lbl_currency.setTypeface(textFonts, Typeface.BOLD);
            lbl_currency.setText(String.valueOf(ConfigData.getCurrencyType()));
            btn_finish = (Button) findViewById(R.id.btn_finish);
            btn_finish.setTypeface(textFonts, Typeface.BOLD);
            btn_print = findViewById(R.id.btn_print);
            btn_print.setTypeface(textFonts, Typeface.BOLD);
            mConnectingDlg = new ProgressDialog(this);
            mConnectingDlg.setMessage("Connecting...");
            mConnectingDlg.setCancelable(false);

            autoConnectBTPrinter();

            mConnector = new BTConnector(new BTConnector.P25ConnectionListener() {
                @Override
                public void onStartConnecting() {
                    if (mConnectingDlg != null)
                        mConnectingDlg.show();
                }

                @Override
                public void onConnectionSuccess() {
                    if (mConnectingDlg != null)
                        mConnectingDlg.dismiss();
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onConnectionFailed(String error) {
                    if (mConnectingDlg != null)
                        mConnectingDlg.dismiss();
                    Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onConnectionCancelled() {
                    if (mConnectingDlg != null)
                        mConnectingDlg.dismiss();
                }

                @Override
                public void onDisconnected() {
                    Toast.makeText(getApplicationContext(), "Disconnectd", Toast.LENGTH_SHORT).show();
                }
            });
            MyLog.appendLog(DEBUG_KEY + "oncreate 8 ");
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "setUiElements" + e.getMessage());
        }

    }

    public void getCompleteRequest() {
        if (getString(R.string.app_publish_mode).equals("MDT")) {
            try {
                if (gpio_info.open_gpio() > 0) {
                    if (gpio_info.get_gpio_data("P3B6") == 1)
                        gpio_info.set_gpio_data("P3B6", 0);
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "GPIO ERROR" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        String url = ConfigData.getClientURL() + APIClass.job_completion_APi;
        final String status = "Status";
        Map<String, String> jobData = new HashMap<String, String>();
        try {
            jobData.put("imeiNo", Utils.getImei_no());
            jobData.put("jobId", RunningJobDetails.getJobId());
            jobData.put("loginId", ConfigData.getDriverId());
            jobData.put("tripStartTime", RunningJobDetails.getTripStartDateTime());
            jobData.put("tripEndTime", getcurrentTimeToDisplay());
            jobData.put("totalTripDist", RunningJobDetails.getTotalTripDist());
            jobData.put("tripDuration", RunningJobDetails.getTotalTripDuration());
            jobData.put("waitingTime", "0");
            jobData.put("waitingFare", "0");
            jobData.put("runningFare", String.valueOf(RunningJobDetails.getRunningFare()));
            jobData.put("lat", String.valueOf(GpsData.getLattiude()));
            jobData.put("lon", String.valueOf(GpsData.getLongitude()));
            jobData.put("totalTripFare", String.valueOf(RunningJobDetails.getTotalTripFare()));
            jobData.put("gpsOdometer", String.valueOf((int)Double.parseDouble(String.valueOf(GpsData.getGpsOdometer()))));
            jobData.put("billingType", RunningJobDetails.getPaymentMode());
            jobData.put("speed", String.valueOf((int)Double.parseDouble(String.valueOf(GpsData.getGpsSpeed()))));
            jobData.put("transactionId", "123456789");
            jobData.put("shiftId", main_screen_data.get("shiftId"));
            jobData.put("strdriverpromoCode", RunningJobDetails.getPromoCode());
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "getCompleteRequest" + e.getMessage());
        }
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.alt_complete));
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(jobData), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    System.out.println("getCompleteRequest=>" + response);
                    if (response != null) {
                        String convertedStatus = status.toLowerCase();
                        if (response.getInt(convertedStatus) == 0) {
                            pDialog.dismiss();
                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        } else {
                            pDialog.dismiss();
                            pulseManager.setTrip(false);
                            pulseManager.setTripDistatnce(0.0f);
                            pulseManager.setTripWating(0);
                            pulseManager.setPrevCount(0);
                            pulseManager.setNextCount(0);
                            GpsData.setTestLocation(0,0);
                            GpsData.setExtraID(0,0);

                            pulseManager.setGpsCounter(0);

                            pulseManager.setHotelJob(false);
                            pulseManager.setExtraFare(0);
                            pulseManager.setExtraTollFare(0);

                            pulseManager.setEmirate(false);
                            pulseManager.setToll(false);

                            pulseManager.setEmirate1(false);
                            pulseManager.setEmirate2(false);
                            pulseManager.setEmirate3(false);
                            pulseManager.setToll1(false);
                            pulseManager.setToll2(false);
                            pulseManager.setToll3(false);
                            pulseManager.setToll4(false);
                            pulseManager.setToll5(false);
                            pulseManager.setToll6(false);
                            pulseManager.setToll7(false);
                            pulseManager.setToll8(false);

                            pulseManager.setEmirate1check(false); ;
                            pulseManager.setEmirate2check(false);
                            pulseManager.setEmirate3check(false);
                            pulseManager.setToll1check(false);
                            pulseManager.setToll2check(false);
                            pulseManager.setToll3check(false);
                            pulseManager.setToll4check(false);
                            pulseManager.setToll5check(false);
                            pulseManager.setToll6check(false);
                            pulseManager.setToll7check(false);
                            pulseManager.setToll8check(false);

                            voiceSoundPlayer.playVoice(BillActivity.this, R.raw.jobcompleted);
                            db.updateCurrentScreenVal("MAIN_ACTIVITY", 1);
                            HashMap<String, String> mainScreenData = db.getMainScreenData();
                            double shiftDist = Double.parseDouble(mainScreenData.get("shiftDist")) + Double.parseDouble(RunningJobDetails.getTotalTripDist());
                            int completedJobs = Integer.parseInt(mainScreenData.get("completedJobs")) + 1;
                            int cancelledJobs = Integer.parseInt(mainScreenData.get("cancelledJobs"));
                            double earnedTime = Double.parseDouble(mainScreenData.get("earnedTime")) + Double.parseDouble(RunningJobDetails.getTotalTripDuration());
                            double todayEarnings = Double.parseDouble(mainScreenData.get("todayEarnings")) + RunningJobDetails.getTotalTripFare();
                            db.updateShiftData(String.valueOf(shiftDist),
                                    String.valueOf(completedJobs), String.valueOf(cancelledJobs),
                                    String.valueOf(earnedTime), String.valueOf(todayEarnings), 1);
                            HashMap<String, String> runningJobData = db.getRunningJobData();
                            db.insertHistoryData(runningJobData);
                            db.updatePromo("Promo Code", 1);
                            db.updateCreditBal(response.getString("creditBalance"), response.optString("wallet"), 1);
                            db.updateConfigData(response.getString("creditBalance"), response.optString("wallet"), response.optString("availableBalance"), 1);
                            RunningJobDetails.setPromoCode("Promo Code");
                            RunningJobDetails.setTotalTripFare(0.0);
                            Thread.sleep(1000);
                            RunningJobDetails.setDefaultValues();
                            db.updateCurrentJobValues(UpdateCurrentJobData.updateJobData(), 1);
                            JSONObject infotainmentParams = new JSONObject();
                            //JOB STOP
                            //if (pulseManager.isPulse()) {

                            //}

                            try {
                                infotainmentParams.put("runningFare", "0.00");
                                infotainmentParams.put("distance", "0.00");
                                infotainmentParams.put("jobStartTime", "00:00:00");
                                infotainmentParams.put("duration", "0");
                                infotainmentParams.put("extra", "0.00");
                                infotainmentParams.put("toll", "0.00");
                                infotainmentParams.put("profile", pulseManager.getJsonProfileObject());
                                pulseManager.setJsonObject(infotainmentParams.toString());
                                Log.e(TAG, "calculateFare: infotainmentParams Put JSON : " + infotainmentParams.toString());
                            } catch (JSONException ex) {
                                Log.e(TAG, "calculateFare: infotainmentParams JSON Exception : " + ex.getMessage());
                            }

                            Intent intent = new Intent(BillActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                        pDialog.dismiss();
                    } else {
                        pDialog.dismiss();
                        MyLog.appendLog(DEBUG_KEY + "Response is null");
                        Toast.makeText(getApplicationContext(), "Server Returns Null", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Time out.Please Try again", Toast.LENGTH_LONG).show();
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
        System.out.println("JsonObjet response:" + req);
        AppController.getInstance().addToRequestQueue(req);
    }

    public String getcurrentTimeToDisplay() {
        String currentTime = null;
        try {
            Time now = new Time(Time.getCurrentTimezone());
            now.setToNow();
            currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d", now.year, (now.month + 1), now.monthDay, now.hour, now.minute, now.second);
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "getcurrentTimeToDisplay" + e.getMessage());
        }
        return currentTime;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.appendLog(DEBUG_KEY + "onResume 1 ");
        LocationManager locationManager = (LocationManager) AppController.getInstance().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsGsmStatusDialog(BillActivity.this, textFonts, "Please enable the GPS", "Bill");
        } else if (!NetworkStatus.isInternetPresent(BillActivity.this).equals("On")) {
            showGpsGsmStatusDialog(BillActivity.this, textFonts, "Please Turn on Internet", "Bill");
        }
        //TODO Enable if bluetooth is there
//        if (mBluetoothService != null) {
//            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
//                // Start the Bluetooth services
//                MyLog.appendLog(DEBUG_KEY + "onResume 3 ");
//                mBluetoothService.start();
//            }
//        }
        MyLog.appendLog(DEBUG_KEY + "onResume 4 ");
        //LanguageController.setLanguage(BillActivity.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth services
//        if (mBluetoothService != null)
//            mBluetoothService.stop();
        if (DEBUG)
            Log.e(TAG, "--- ON DESTROY ---");
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

    private void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        final ArrayList<String> mDeviceName = new ArrayList<String>();
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                mDeviceName.add(device.getName());
                pairedDeviceArrayList.add(device);
            }
            alertDialog = new AlertDialog.Builder(BillActivity.this).create();
            LayoutInflater inflater = getLayoutInflater();
            MyLog.appendLog(DEBUG_KEY + "custom 1_1 ");
            View convertView = (View) inflater.inflate(R.layout.custom, null);
            MyLog.appendLog(DEBUG_KEY + "custom 1_2 ");
            alertDialog.setView(convertView);
            alertDialog.setTitle("List of Paired Devices");
            listViewPairedDevice = (ListView) convertView.findViewById(R.id.listViewPairedDevice);
            MyLog.appendLog(DEBUG_KEY + "spinner layout 1_1 ");
            pairedDeviceAdapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, mDeviceName);
            MyLog.appendLog(DEBUG_KEY + "spinner layout 1_2 ");
            //pairedDeviceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            listViewPairedDevice.setAdapter(pairedDeviceAdapter);
            listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                    final BluetoothDevice device = (BluetoothDevice) pairedDeviceArrayList.get(position);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(bt_device_mac_add, device.getAddress());
                    editor.commit();
                    try {
                        //mConnector.connect(device);
                        mBluetoothService.connect(device);
//                    } catch (BTConnectionException e) {
//                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.i("Exception", "onItemClick: " + e);
                    }
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(BillActivity.this);
                alert.setTitle("BT PRINTER");
                alert.setMessage("PLEASE PAIR THE DEVICE");
                alert.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alert.show();
            }
        }
    }

    private void autoConnectBTPrinter() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        final ArrayList<String> mDeviceName = new ArrayList<String>();
        sharedPref = getSharedPreferences(bt_printer_pref,Context.MODE_PRIVATE);
        String bt_device_add = sharedPref.getString(bt_device_mac_add, "");
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                mDeviceName.add(device.getName());
                if (device.getAddress().equals(bt_device_add)) {
                    BluetoothDevice pairedDevice = bluetoothAdapter.getRemoteDevice(bt_device_add);
                    try {
                        //mConnector.connect(device);
                        mBluetoothService.connect(pairedDevice);
//                    } catch (BTConnectionException e) {
//                        e.printStackTrace();
                    } catch (Exception e) {
                        setup();
                        Log.i("Exception", "onItemClick: " + e);
                        break;
                    }
                }
            }
        } else {
            setup();
        }
    }

    public void showToast(final String toast) {
        AlertDialog.Builder alert = new AlertDialog.Builder(BillActivity.this);
        alert.setTitle("BT PRINTER");
        alert.setMessage(toast);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (toast.equalsIgnoreCase("BROKEN PIPE ERROR")) {
                    try {
                        mConnector.disconnect();
                    } catch (BTConnectionException e) {
                        e.printStackTrace();
                    }
                }
                //dialog.dismiss();
            }
        });
        // alert.show();
    }

    /****************************************************************************************************/
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (DEBUG)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            //mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            //mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:

                    break;
                case MESSAGE_READ:

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_CONNECTION_LOST:    //è“�ç‰™å·²æ–­å¼€è¿žæŽ¥
                    Toast.makeText(getApplicationContext(), "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_UNABLE_CONNECT:     //æ— æ³•è¿žæŽ¥è®¾å¤‡
                    Toast.makeText(getApplicationContext(), "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void BluetoothPrintTest() {
        String msg = "";
        //String lang = getString(R.string.strLang);
        String lang = "en";
        if ((lang.compareTo("en")) == 0) {
            msg = "Division I is a research and development, production and services in one high-tech research and development, production-oriented enterprises, specializing in POS terminals finance, retail, restaurants, bars, songs and other areas, computer terminals, self-service terminal peripheral equipment R & D, manufacturing and sales! \n company's organizational structure concise and practical, pragmatic style of rigorous, efficient operation. Integrity, dedication, unity, and efficient is the company's corporate philosophy, and constantly strive for today, vibrant, the company will be strong scientific and technological strength, eternal spirit of entrepreneurship, the pioneering and innovative attitude, confidence towards the international information industry, with friends to create brilliant information industry !!! \n\n\n";
            SendDataString(msg);
        } else if ((lang.compareTo("cn")) == 0) {
            msg = "æˆ‘å�¸æ˜¯ä¸€å®¶é›†ç§‘ç ”å¼€å�‘ã€�ç”Ÿäº§ç»�è�¥å’Œæœ�åŠ¡äºŽä¸€ä½“çš„é«˜æŠ€æœ¯ç ”å�‘ã€�ç”Ÿäº§åž‹ä¼�ä¸šï¼Œä¸“ä¸šä»Žäº‹é‡‘èž�ã€�å•†ä¸šé›¶å”®ã€�é¤�é¥®ã€�é…’å�§ã€�æ­Œå�§ç­‰é¢†åŸŸçš„POSç»ˆç«¯ã€�è®¡ç®—æœºç»ˆç«¯ã€�è‡ªåŠ©ç»ˆç«¯å‘¨è¾¹é…�å¥—è®¾å¤‡çš„ç ”å�‘ã€�åˆ¶é€ å�Šé”€å”®ï¼�\nå…¬å�¸çš„ç»„ç»‡æœºæž„ç®€ç»ƒå®žç”¨ï¼Œä½œé£ŽåŠ¡å®žä¸¥è°¨ï¼Œè¿�è¡Œé«˜æ•ˆã€‚è¯šä¿¡ã€�æ•¬ä¸šã€�å›¢ç»“ã€�é«˜æ•ˆæ˜¯å…¬å�¸çš„ä¼�ä¸šç�†å¿µå’Œä¸�æ–­è¿½æ±‚ä»Šå¤©ï¼Œæœ�æ°”è“¬å‹ƒï¼Œå…¬å�¸å°†ä»¥é›„åŽšçš„ç§‘æŠ€åŠ›é‡�ï¼Œæ°¸æ�’çš„åˆ›ä¸šç²¾ç¥žï¼Œä¸�æ–­å¼€æ‹“åˆ›æ–°çš„å§¿æ€�ï¼Œå……æ»¡ä¿¡å¿ƒçš„æœ�ç�€å›½é™…åŒ–ä¿¡æ�¯äº§ä¸šé¢†åŸŸï¼Œä¸Žæœ‹å�‹ä»¬æ�ºæ‰‹å…±åˆ›ä¿¡æ�¯äº§ä¸šçš„è¾‰ç…Œ!!!\n\n\n";
            SendDataString(msg);
        } else if ((lang.compareTo("hk")) == 0) {
            msg = "æˆ‘å�¸æ˜¯ä¸€å®¶é›†ç§‘ç ”é–‹ç™¼ã€�ç”Ÿç”¢ç¶“ç‡Ÿå’Œæœ�å‹™æ–¼ä¸€é«”çš„é«˜æŠ€è¡“ç ”ç™¼ã€�ç”Ÿç”¢åž‹ä¼�æ¥­ï¼Œå°ˆæ¥­å¾žäº‹é‡‘èž�ã€�å•†æ¥­é›¶å”®ã€�é¤�é£²ã€�é…’å�§ã€�æ­Œå�§ç­‰é ˜åŸŸçš„POSçµ‚ç«¯ã€�è¨ˆç®—æ©Ÿçµ‚ç«¯ã€�è‡ªåŠ©çµ‚ç«¯å‘¨é‚Šé…�å¥—è¨­å‚™çš„ç ”ç™¼ã€�è£½é€ å�ŠéŠ·å”®ï¼� \nå…¬å�¸çš„çµ„ç¹”æ©Ÿæ§‹ç°¡ç·´å¯¦ç”¨ï¼Œä½œé¢¨å‹™å¯¦åš´è¬¹ï¼Œé�‹è¡Œé«˜æ•ˆã€‚èª ä¿¡ã€�æ•¬æ¥­ã€�åœ˜çµ�ã€�é«˜æ•ˆæ˜¯å…¬å�¸çš„ä¼�æ¥­ç�†å¿µå’Œä¸�æ–·è¿½æ±‚ä»Šå¤©ï¼Œæœ�æ°£è“¬å‹ƒï¼Œå…¬å�¸å°‡ä»¥é›„åŽšçš„ç§‘æŠ€åŠ›é‡�ï¼Œæ°¸æ�†çš„å‰µæ¥­ç²¾ç¥žï¼Œä¸�æ–·é–‹æ‹“å‰µæ–°çš„å§¿æ…‹ï¼Œå……æ»¿ä¿¡å¿ƒçš„æœ�è‘—åœ‹éš›åŒ–ä¿¡æ�¯ç”¢æ¥­é ˜åŸŸï¼Œèˆ‡æœ‹å�‹å€‘æ”œæ‰‹å…±å‰µä¿¡æ�¯ç”¢æ¥­çš„è¼�ç…Œ!!!\n\n\n";
            //SendDataByte(PrinterCommand.POS_Print_Text(msg, BIG5, 0, 0, 0, 0));
        } else if ((lang.compareTo("kor")) == 0) {
            msg = "ë¶€ë¬¸ IëŠ” ê¸ˆìœµ, ì†Œë§¤, ë ˆìŠ¤í† ëž‘, ë°”, ë…¸ëž˜ ë°� ê¸°íƒ€ ë¶„ì•¼, ì»´í“¨í„° ë‹¨ë§�ê¸°, ì…€í”„ ì„œë¹„ìŠ¤ í„°ë¯¸ë„� ì£¼ë³€ ìž¥ì¹˜ POS í„°ë¯¸ë„�ì�„ ì „ë¬¸ìœ¼ë¡œ í•œ ì²¨ë‹¨ ê¸°ìˆ  ì—°êµ¬ ë°� ê°œë°œ, ìƒ�ì‚° ì§€í–¥ì � ì�¸ ê¸°ì—…ì�˜ ì—°êµ¬ ë°� ê°œë°œ, ìƒ�ì‚° ë°� ì„œë¹„ìŠ¤ìž…ë‹ˆë‹¤ R & D, ì œì¡° ë°� íŒ�ë§¤! \n íšŒì‚¬ì�˜ ì¡°ì§� êµ¬ì¡°ì�˜ ê°„ê²°í•˜ê³  ì—„ê²©í•œ, íš¨ìœ¨ì �ì�¸ ìš´ì˜�ì�˜ ì‹¤ì œ, ì‹¤ìš©ì �ì�¸ ìŠ¤íƒ€ì�¼. ë¬´ê²°ì„±, í—Œì‹ , ë‹¨ê²°, íš¨ìœ¨ì �ì�¸ íšŒì‚¬ì�˜ ê¸°ì—… ì² í•™ì�´ë©°, ì§€ì†�ì �ìœ¼ë¡œ, í™œê¸°ì°¬,ì�´ íšŒì‚¬ëŠ” ê°•ë ¥í•œ ê³¼í•™ ê¸°ìˆ  ê°•ë�„, ê¸°ì—…ê°€ ì •ì‹ ì�˜ ì˜�ì›�í•œ ì •ì‹ ì�´ ë�  ê²ƒìž…ë‹ˆë‹¤ ì˜¤ëŠ˜ì�„ ìœ„í•´ ë…¸ë ¥, ê°œì²™ê³¼ í˜�ì‹ ì �ì�¸ íƒœë�„, êµ­ì œ ì •ë³´ ì‚°ì—…ì�„ í–¥í•´ ìž�ì‹ ê°�, ì¹œêµ¬ì™€ í•¨ê»˜ í™”ë ¤í•œ ì •ë³´ ì‚°ì—…ì�„ ë§Œë“¤ ìˆ˜ ìžˆìŠµë‹ˆë‹¤!!!\n\n\n";
            //SendDataByte(PrinterCommand.POS_Print_Text(msg, KOREAN, 0, 0, 0, 0));
        } else if ((lang.compareTo("thai")) == 0) {
            msg = "à¸ªà¹ˆà¸§à¸™à¸‰à¸±à¸™à¸„à¸·à¸­à¸�à¸²à¸£à¸§à¸´à¸ˆà¸±à¸¢à¹�à¸¥à¸°à¸�à¸²à¸£à¸žà¸±à¸’à¸™à¸²à¸�à¸²à¸£à¸œà¸¥à¸´à¸•à¹�à¸¥à¸°à¸�à¸²à¸£à¸šà¸£à¸´à¸�à¸²à¸£à¹ƒà¸™à¸�à¸²à¸£à¸§à¸´à¸ˆà¸±à¸¢à¸«à¸™à¸¶à¹ˆà¸‡à¸—à¸µà¹ˆà¸¡à¸µà¹€à¸—à¸„à¹‚à¸™à¹‚à¸¥à¸¢à¸µà¸ªà¸¹à¸‡à¹�à¸¥à¸°à¸�à¸²à¸£à¸žà¸±à¸’à¸™à¸²à¸ªà¸–à¸²à¸™à¸›à¸£à¸°à¸�à¸­à¸šà¸�à¸²à¸£à¸œà¸¥à¸´à¸•à¸—à¸µà¹ˆà¸¡à¸¸à¹ˆà¸‡à¹€à¸™à¹‰à¸™à¸„à¸§à¸²à¸¡à¹€à¸Šà¸µà¹ˆà¸¢à¸§à¸Šà¸²à¸�à¹ƒà¸™à¸‚à¸±à¹‰à¸§ POS à¸�à¸²à¸£à¹€à¸‡à¸´à¸™, à¸„à¹‰à¸²à¸›à¸¥à¸µà¸�, à¸£à¹‰à¸²à¸™à¸­à¸²à¸«à¸²à¸£, à¸šà¸²à¸£à¹Œ, à¹€à¸žà¸¥à¸‡à¹�à¸¥à¸°à¸žà¸·à¹‰à¸™à¸—à¸µà¹ˆà¸­à¸·à¹ˆà¸™ à¹† , à¹€à¸„à¸£à¸·à¹ˆà¸­à¸‡à¸„à¸­à¸¡à¸žà¸´à¸§à¹€à¸•à¸­à¸£à¹Œ, à¸šà¸£à¸´à¸�à¸²à¸£à¸•à¸™à¹€à¸­à¸‡à¸‚à¸±à¹‰à¸§à¸­à¸¸à¸›à¸�à¸£à¸“à¹Œà¸•à¹ˆà¸­à¸žà¹ˆà¸§à¸‡ R & D, à¸�à¸²à¸£à¸œà¸¥à¸´à¸•à¹�à¸¥à¸°à¸¢à¸­à¸”à¸‚à¸²à¸¢! \n à¸�à¸£à¸°à¸Šà¸±à¸šà¹‚à¸„à¸£à¸‡à¸ªà¸£à¹‰à¸²à¸‡ à¸šà¸£à¸´à¸©à¸±à¸—  à¸‚à¸­à¸‡à¸­à¸‡à¸„à¹Œà¸�à¸£à¹�à¸¥à¸°à¸�à¸²à¸£à¸›à¸�à¸´à¸šà¸±à¸•à¸´à¹ƒà¸™à¸—à¸²à¸‡à¸›à¸�à¸´à¸šà¸±à¸•à¸´à¸‚à¸­à¸‡à¸ªà¹„à¸•à¸¥à¹Œà¸­à¸¢à¹ˆà¸²à¸‡à¹€à¸‚à¹‰à¸¡à¸‡à¸§à¸”à¸”à¸³à¹€à¸™à¸´à¸™à¸‡à¸²à¸™à¸¡à¸µà¸›à¸£à¸°à¸ªà¸´à¸—à¸˜à¸´à¸ à¸²à¸ž à¸„à¸§à¸²à¸¡à¸‹à¸·à¹ˆà¸­à¸ªà¸±à¸•à¸¢à¹Œà¸—à¸¸à¹ˆà¸¡à¹€à¸—à¸„à¸§à¸²à¸¡à¸ªà¸²à¸¡à¸±à¸„à¸„à¸µà¹�à¸¥à¸°à¸¡à¸µà¸›à¸£à¸°à¸ªà¸´à¸—à¸˜à¸´à¸ à¸²à¸žà¸„à¸·à¸­à¸›à¸£à¸±à¸Šà¸�à¸²à¸‚à¸­à¸‡à¸­à¸‡à¸„à¹Œà¸�à¸£à¸‚à¸­à¸‡ à¸šà¸£à¸´à¸©à¸±à¸— à¸­à¸¢à¹ˆà¸²à¸‡à¸•à¹ˆà¸­à¹€à¸™à¸·à¹ˆà¸­à¸‡à¹�à¸¥à¸°à¸¡à¸¸à¹ˆà¸‡à¸¡à¸±à¹ˆà¸™à¹€à¸žà¸·à¹ˆà¸­à¸§à¸±à¸™à¸™à¸µà¹‰à¸—à¸µà¹ˆà¸ªà¸”à¹ƒà¸ªà¸‚à¸­à¸‡ à¸šà¸£à¸´à¸©à¸±à¸— à¸ˆà¸°à¸¡à¸µà¸�à¸³à¸¥à¸±à¸‡à¹�à¸£à¸‡à¸‚à¸¶à¹‰à¸™à¸—à¸²à¸‡à¸§à¸´à¸—à¸¢à¸²à¸¨à¸²à¸ªà¸•à¸£à¹Œà¹�à¸¥à¸°à¹€à¸—à¸„à¹‚à¸™à¹‚à¸¥à¸¢à¸µà¸—à¸µà¹ˆà¹�à¸‚à¹‡à¸‡à¹�à¸�à¸£à¹ˆà¸‡à¸ˆà¸´à¸•à¸§à¸´à¸�à¸�à¸²à¸“à¸™à¸´à¸£à¸±à¸™à¸”à¸£à¹Œà¸‚à¸­à¸‡à¸œà¸¹à¹‰à¸›à¸£à¸°à¸�à¸­à¸šà¸�à¸²à¸£à¸—à¸µà¹ˆà¸¡à¸µà¸—à¸±à¸¨à¸™à¸„à¸•à¸´à¸—à¸µà¹ˆà¹€à¸›à¹‡à¸™à¸œà¸¹à¹‰à¸šà¸¸à¸�à¹€à¸šà¸´à¸�à¹�à¸¥à¸°à¸™à¸§à¸±à¸•à¸�à¸£à¸£à¸¡à¸„à¸§à¸²à¸¡à¹€à¸Šà¸·à¹ˆà¸­à¸¡à¸±à¹ˆà¸™à¸—à¸µà¹ˆà¸¡à¸µà¸•à¹ˆà¸­à¸­à¸¸à¸•à¸ªà¸²à¸«à¸�à¸£à¸£à¸¡à¸‚à¹‰à¸­à¸¡à¸¹à¸¥à¸£à¸°à¸«à¸§à¹ˆà¸²à¸‡à¸›à¸£à¸°à¹€à¸—à¸¨ à¸�à¸±à¸šà¹€à¸žà¸·à¹ˆà¸­à¸™ à¹† à¹ƒà¸™à¸�à¸²à¸£à¸ªà¸£à¹‰à¸²à¸‡à¸­à¸¸à¸•à¸ªà¸²à¸«à¸�à¸£à¸£à¸¡à¸‚à¹‰à¸­à¸¡à¸¹à¸¥à¸—à¸µà¹ˆà¸¢à¸­à¸”à¹€à¸¢à¸µà¹ˆà¸¢à¸¡!!!\n\n\n";
            //SendDataByte(PrinterCommand.POS_Print_Text(msg, THAI, 255, 0, 0, 0));
        }
    }


    /*****************************************************************************************************/
    /*
     * SendDataString
     */
    private void SendDataString(String data) {

        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "NOT CONNECTED", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (data.length() > 0) {
            try {
                mBluetoothService.write(data.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
     *SendDataByte
     */
    private void SendDataByte(byte[] data) {

        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "NOT CONNECTED", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mBluetoothService.write(data);
    }

    /****************************************************************************************************/


//    String input = "563";
//        System.out.println("double : " + input);
//        System.out.println("double : " +  df2.format(Double.valueOf(input)));    //3.14
//
//    // DecimalFormat, default is RoundingMode.HALF_EVEN
//        df2.setRoundingMode(RoundingMode.DOWN);
//    //System.out.println("\ndouble : " + df2.format(input));  //3.14
//
//        df2.setRoundingMode(RoundingMode.UP);
//    //System.out.println("double : " + df2.format(input));    //3.15
//        System.out.println(new BigDecimal(1.03).subtract(new BigDecimal(0.41)));
//        System.out.println(new BigDecimal("1.0").subtract(new BigDecimal("0.4")));
//        System.out.println(round(Double.valueOf(input),2));
//        System.out.println(new DecimalFormat("0.00").format(Double.valueOf(input))); // this works for formatting upto 2 decimal place with zeros.
    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager assetMgr = getAssets();
        try {
            InputStream is = assetMgr.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}