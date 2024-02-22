package com.log;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AK on 1/9/2017.
 */

public class MyLog {
    public static void appendLog(String text)
    {
        File Root = Environment.getExternalStorageDirectory();
        File logFile = new File(Root,"InfoDispatchLog.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Date date = new Date();
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(String.valueOf(getCurrentDate()+" "+ date.getHours()+" : "+date.getMinutes()+" : "+date.getSeconds()+"  "+text+"\n"));
            buf.newLine();
            buf.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
