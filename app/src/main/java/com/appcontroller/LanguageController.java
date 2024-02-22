package com.appcontroller;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.log.MyLog;
import com.sqlite.DBHelper;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by AK on 2/19/2017.
 */

public class LanguageController {
    public static DBHelper db;
    public static String currentLanguage;
    public static String DEBUG_KEY="LanguageController";
    public static void setLanguage(Context context)
    {
        try {
            db = new DBHelper(AppController.getInstance());
            HashMap<String, String> getSettingsInfo = db.getSettingsInfo();
            if (getSettingsInfo.get("lang_settings").equalsIgnoreCase("Maynamar")) {
                currentLanguage = "my";
            } else {
                currentLanguage = "en_us";
            }
            Locale myLocale = new Locale(currentLanguage);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
           /* Locale myLocale = new Locale(currentLanguage);
            Locale.setDefault(myLocale);
            Configuration config = new Configuration();
            config.locale = myLocale;
            context.getResources().updateConfiguration(config,
                    context.getResources().getDisplayMetrics());*/
        }
        catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+" setLanguage "+e);
        }
    }
}
