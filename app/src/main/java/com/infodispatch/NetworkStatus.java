package com.infodispatch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.log.MyLog;

public class NetworkStatus {
	private Context context;
	private Context _context;
	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;
	public static String DEBUG_KEY="NetworkStatus";

	public NetworkStatus(Context context){
		this._context = context;
	}
	public static int getConnectivityStatus(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			if (null != activeNetwork) {
				if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
					return TYPE_WIFI;
				if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
					return TYPE_MOBILE;
			}
		}catch (Exception e){
			MyLog.appendLog(DEBUG_KEY+"getConnectivityStatus"+e.getMessage());
		}
		return TYPE_NOT_CONNECTED;
	}
	public static String isInternetPresent(Context context) {
		String status = null;
		try {
			int conn = NetworkStatus.getConnectivityStatus(context);
			if (conn == NetworkStatus.TYPE_WIFI) {
				status = "On";
			} else if (conn == NetworkStatus.TYPE_MOBILE) {
				status = "On";
			} else if (conn == NetworkStatus.TYPE_NOT_CONNECTED) {
				status = "Off";
			}
		}catch (Exception e){
			MyLog.appendLog(DEBUG_KEY+"isInternetPresent"+e.getMessage());
		}
		return status;
	}
}