package com.infodispatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by AK on 12/19/2016.
 */

public class GetDeviceTime {
    public static String getTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    public static String getDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }
    public static String getForDbDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }
    public static String getYestardayDate(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }
    public static String getThisWeekDate(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -7);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }
    public static String getLastWeekDate(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -15);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }
    public static String getFifiteenDate(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -15);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }
    public static String getThisMonthDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }
    public static String getDateForKerbJob(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmmss");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }
    public static String getDateForJobStatus(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

}
