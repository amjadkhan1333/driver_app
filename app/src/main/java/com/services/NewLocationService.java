package com.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class NewLocationService extends Service implements LocationListener {
    private String TAG =getClass().getSimpleName();
    private LocationManager locationManager;

    private Location location;

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent.getAction().equals("startListening")) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        } else {
            if (intent.getAction().equals("stopListening")) {
                locationManager.removeUpdates(this);
                locationManager = null;
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    public void onLocationChanged(final Location location) {
        this.location = location;
        // TODO this is where you'd do something like context.sendBroadcast()
        Log.e(TAG, "onLocationChanged: Lat "+location.getLatitude() + " Lon : "+location.getLongitude() );
    }

    public void onProviderDisabled(final String provider) {
    }

    public void onProviderEnabled(final String provider) {
    }

    public void onStatusChanged(final String arg0, final int arg1, final Bundle arg2) {
    }

}