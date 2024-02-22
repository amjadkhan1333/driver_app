package com.infodispatch;

import android.content.Context;
import android.media.MediaPlayer;

import com.appcontroller.AppController;
import com.log.MyLog;
import com.sqlite.DBHelper;

import java.util.HashMap;

/**
 * Created by AK on 1/25/2017.
 */

public class VoiceSoundPlayer {
    public  MediaPlayer myPlayer;
    public  String DEBUG_KEY="VoiceSoundPlayer";
    MediaPlayer mediaPlayer;
    public void playVoice(Context c,int id){
        try {
           /* AudioManager audioManager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);*/
            DBHelper db = new DBHelper(AppController.getInstance());
            HashMap<String, String> getSettingsInfo =db.getSettingsInfo();
            if(getSettingsInfo.get("sound_settings").equalsIgnoreCase("ON")) {
                mediaPlayer = MediaPlayer.create(c, id);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY+"playVoice"+e.getMessage());
        }
    }
    public void stopVoice(Context c){
        try {
            if(myPlayer!=null) {
                myPlayer.stop();
            }
        }catch (Exception e){
            MyLog.appendLog(DEBUG_KEY+" stopVoice "+e);
        }
    }
}
