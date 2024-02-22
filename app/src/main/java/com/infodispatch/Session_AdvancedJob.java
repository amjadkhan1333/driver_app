package com.infodispatch;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by AMITKUMAR on 1/3/2017.
 */
public class Session_AdvancedJob {
    private final SharedPreferences.Editor editor;
    private final SharedPreferences preferences;
    private final static String pickupTime = "time";
    private final static String jobId = "id";
    private final static String pickUpLocation= "plocation";
    private final static String Gender = "gender";
    private final static String IS_USER_LOGIN = "login";
    private final static String IS_USER_OTP = "otp";
    private final static String IS_ADDS = "adds";
    private final Context mContext;

    public Session_AdvancedJob(Context context) {
        mContext=context;
        preferences = context.getSharedPreferences("", 0);//Define sharedprefences to store the data in key /value pairs.
        editor = preferences.edit();//Editor is used to insert data in key/values
    }
    public void insertUserInfo(String picktime, String jobid, String picklocation) {
        editor.putString(pickupTime, picktime);
        editor.putString(jobId, jobid);
        editor.putString(pickUpLocation, picklocation);
        editor.commit();//for any update must use commit function.
    }
    public void insertUserInfoConfirm() {
        editor.putBoolean(IS_USER_OTP, true);
        editor.commit();//for any update must use commit function.
    }
    public HashMap<String, String> getUserInfo() {
        HashMap<String, String> hasmap = new HashMap<String, String>();
        hasmap.put(pickupTime, preferences.getString(pickupTime, null));
        hasmap.put(jobId, preferences.getString(jobId, null));
        hasmap.put(pickUpLocation, preferences.getString(pickUpLocation, null));
        return hasmap;
    }
    public  void ClearAllData()
    {
        editor.clear();//This method will delete all the data from session.
        editor.commit();


    }
}

