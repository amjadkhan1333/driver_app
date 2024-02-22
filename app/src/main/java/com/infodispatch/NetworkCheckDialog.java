package com.infodispatch;

import android.content.Context;
import android.content.DialogInterface;

import com.log.MyLog;
import com.infodispatch.R;

/**
 * Created by info on 30-01-2017.
 */

public class NetworkCheckDialog {
    public static String DEBUG_KEY="NetworkCheckDialog";
    public static void showConnectionTimeOut(Context context) {
        try {
            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context, R.style.AlertDialogCustom).create();
            alertDialog.setTitle("No Internet");
            alertDialog.setMessage("Unable to Connect, Please try again.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+""+e.getMessage());
        }

    }
}
