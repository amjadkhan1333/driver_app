package com.infodispatch;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.appcontroller.LanguageController;
import com.log.MyLog;
import com.infodispatch.R;
import com.settersgetters.ConfigData;
import com.settersgetters.GpsData;
import com.settersgetters.Utils;

import static android.content.Context.LOCATION_SERVICE;
import static com.services.PollingService.DEBUG_KEY;

/**
 * Created by AK on 1/30/2017.
 */

public class StatusDialog {
    public static void showStatusDialog(Context context, Typeface textFonts){
        TextView lbl_device_status,lbl_imei,txt_imei,lbl_ip,txt_ip,lbl_version,txt_version,lbl_build_date,txt_build_date,
                lbl_gps,txt_gps,lbl_gsm,txt_gsm,lbl_latitude,txt_latitude,lbl_longitude,txt_longitude;
        try {
            LanguageController.setLanguage(context);
            LocationManager locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                GpsData.setGpsStatus("On");
            }else{
                GpsData.setGpsStatus("Off");
            }
            final Dialog dialogStatus = new Dialog(context);
            dialogStatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogStatus.setContentView(R.layout.dialog_device_status);
            dialogStatus.setCanceledOnTouchOutside(true);
            dialogStatus.setCancelable(true);
            dialogStatus.show();

            lbl_device_status = (TextView)dialogStatus.findViewById(R.id.lbl_device_status);
            lbl_imei = (TextView)dialogStatus.findViewById(R.id.lbl_imei);
            txt_imei = (TextView)dialogStatus.findViewById(R.id.txt_imei);
            lbl_ip = (TextView)dialogStatus.findViewById(R.id.lbl_ip);
            txt_ip = (TextView)dialogStatus.findViewById(R.id.txt_ip);
            lbl_version = (TextView)dialogStatus.findViewById(R.id.lbl_version);
            txt_version = (TextView)dialogStatus.findViewById(R.id.txt_version);
            lbl_build_date = (TextView)dialogStatus.findViewById(R.id.lbl_build_date);
            txt_build_date = (TextView)dialogStatus.findViewById(R.id.txt_build_date);
            lbl_gps = (TextView)dialogStatus.findViewById(R.id.lbl_gps);
            txt_gps = (TextView)dialogStatus.findViewById(R.id.txt_gps);
            lbl_gsm = (TextView)dialogStatus.findViewById(R.id.lbl_gsm);
            txt_gsm = (TextView)dialogStatus.findViewById(R.id.txt_gsm);
            lbl_latitude = (TextView)dialogStatus.findViewById(R.id.lbl_latitude);
            txt_latitude = (TextView)dialogStatus.findViewById(R.id.txt_latitude);
            lbl_longitude = (TextView)dialogStatus.findViewById(R.id.lbl_longitude);
            txt_longitude = (TextView)dialogStatus.findViewById(R.id.txt_longitude);

            lbl_device_status.setTypeface(textFonts, Typeface.BOLD);
            lbl_imei.setTypeface(textFonts);
            txt_imei.setTypeface(textFonts);
            lbl_ip.setTypeface(textFonts);
            txt_ip.setTypeface(textFonts);
            lbl_version.setTypeface(textFonts);
            txt_version.setTypeface(textFonts);
            lbl_build_date.setTypeface(textFonts);
            txt_build_date.setTypeface(textFonts);
            lbl_gps.setTypeface(textFonts);
            txt_gps.setTypeface(textFonts);
            lbl_gsm.setTypeface(textFonts);
            txt_gsm.setTypeface(textFonts);
            lbl_latitude.setTypeface(textFonts);
            txt_latitude.setTypeface(textFonts);
            lbl_longitude.setTypeface(textFonts);
            txt_longitude.setTypeface(textFonts);

            String networkStatus = NetworkStatus.isInternetPresent(context);
            txt_imei.setText(Utils.getImei_no());
            String ipv4 = Utils.getIPAddress(true); // IPv4
            txt_ip.setText(ipv4);
            txt_version.setText(ConfigData.firmwareVersion);
            txt_build_date.setText(ConfigData.buildDate);
            txt_gps.setText(GpsData.getGpsStatus());
            txt_gsm.setText(networkStatus);
            txt_latitude.setText(String.valueOf(GpsData.getLattiude()));
            txt_longitude.setText(String.valueOf(GpsData.getLongitude()));
            ImageView img_dig_cancel = (ImageView) dialogStatus.findViewById(R.id.img_dig_cancel);
            img_dig_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogStatus.dismiss();
                }
            });
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY+"StatusDialog"+e.getMessage());
        }
    }
}
