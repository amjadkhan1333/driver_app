package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.infodispatch.SplashScreen;

public class StartUpBootReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i =new Intent(context, SplashScreen.class);
        context.startActivity(i);
    }
}
