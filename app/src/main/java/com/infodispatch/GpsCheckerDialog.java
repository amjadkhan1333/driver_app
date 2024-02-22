package com.infodispatch;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.appcontroller.LanguageController;
import com.log.MyLog;
import com.infodispatch.R;

/**
 * Created by AK on 2/23/2017.
 */

public class GpsCheckerDialog {
    public static String DEBUG_KEY="GpsCheckerDialog";
    public static void showGpsGsmStatusDialog(final Context context, Typeface textFonts, final String title,final String screen){
        TextView lblGpsGsmEnableTitle;
        Button btnGpsGsmOk;
        try {
            LanguageController.setLanguage(context);

            final Dialog dialogGpsGsmStatus = new Dialog(context);
            dialogGpsGsmStatus.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogGpsGsmStatus.setContentView(R.layout.gps_gsm_check_dialog);
            dialogGpsGsmStatus.setCanceledOnTouchOutside(false);
            dialogGpsGsmStatus.setCancelable(false);
            dialogGpsGsmStatus.show();

            lblGpsGsmEnableTitle = (TextView)dialogGpsGsmStatus.findViewById(R.id.lblGpsGsmEnableTitle);
            lblGpsGsmEnableTitle.setTypeface(textFonts, Typeface.BOLD);
            lblGpsGsmEnableTitle.setText(title);
            btnGpsGsmOk =(Button)dialogGpsGsmStatus.findViewById(R.id.btnGpsGsmOk);
            btnGpsGsmOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (title.contains("GPS")){
                        dialogGpsGsmStatus.dismiss();
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }else{
                        if (screen.equalsIgnoreCase("Splash")){
                            dialogGpsGsmStatus.dismiss();
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }else {
                            dialogGpsGsmStatus.dismiss();
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                }
            });

        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY+"StatusDialog"+e.getMessage());
        }
    }
}
