package com.connectors;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

/*
 * 
 * @author Sreekanth <sreekanth.reddy@visiontek.co.in>
 *
 */

public class BTConnector {
	public static BluetoothSocket mSocket;
	public static OutputStream mOutputStream;
	public static InputStream mInputStream;

	private ConnectTask mConnectTask;
	private P25ConnectionListener mListener;

	private boolean mIsConnecting = false;

	private static final String TAG = "BTCONNECT";

	public BTConnector(P25ConnectionListener listener) {
		mListener = listener;
	}

	public boolean isConnecting() {
		return mIsConnecting;
	}

	public boolean isConnected() {
		return (mSocket == null) ? false : true;
	}

	public void connect(BluetoothDevice device) throws BTConnectionException {
		if (mIsConnecting && mConnectTask != null) {
			throw new BTConnectionException("Connection in progress");
		}

		if (mSocket != null) {
			throw new BTConnectionException("Socket already connected");
		}

		(mConnectTask = new ConnectTask(device)).execute();
	}

	public void disconnect() throws BTConnectionException {
		if (mSocket == null) {
			throw new BTConnectionException("Socket is not connected");
		}

		try {
			mSocket.close();
			mSocket = null;
			mListener.onDisconnected();
		} catch (IOException e) {
			throw new BTConnectionException(e.getMessage());
		}
	}

	public void cancel() throws BTConnectionException {
		if (mIsConnecting && mConnectTask != null) {
			mConnectTask.cancel(true);
		} else {
			throw new BTConnectionException("No connection is in progress");
		}
	}

	public interface P25ConnectionListener {
		public abstract void onStartConnecting();

		public abstract void onConnectionCancelled();

		public abstract void onConnectionSuccess();

		public abstract void onConnectionFailed(String error);

		public abstract void onDisconnected();
	}

	public class ConnectTask extends AsyncTask<URL, Integer, Long> {
		BluetoothDevice device;
		String error = "";

		public ConnectTask(BluetoothDevice device) {
			this.device = device;
		}

		protected void onCancelled() {
			mIsConnecting = false;
			mListener.onConnectionCancelled();
		}

		protected void onPreExecute() {
			mListener.onStartConnecting();

			mIsConnecting = true;
		}

		protected Long doInBackground(URL... urls) {
			long result = 0;

			try {

				UUID SERIAL_UUID = UUID
						.fromString("00001101-0000-1000-8000-00805f9b34fb");

				try {
					mSocket = device
							.createRfcommSocketToServiceRecord(SERIAL_UUID);
				} catch (Exception e) {
					Log.d(TAG, "ERROR IN SOCKET CREATION");
				}

				try {
					result = 1;
					mSocket.connect();
					Log.d(TAG, "CONNECTED");
				} catch (IOException e) {
					Log.d(TAG, e.getMessage());
					try {
						result = 1;
						Log.d(TAG, "TRYING TO RE-CONNECT...");

						mSocket = (BluetoothSocket) device
								.getClass()
								.getMethod("createRfcommSocket",
										new Class[] { int.class })
								.invoke(device, 1);

						mSocket.connect();

						Log.d(TAG, "CONNECTED 2 WAY");

					} catch (Exception e2) {
						Log.d(TAG, e2.getMessage());
						Log.d(TAG,
								"COULD NOT ESTABLISHING BLUETOOTH CONNECTION IN 2 WAY ALSO");
						result = -1;
					}
				}

				mOutputStream = mSocket.getOutputStream();
				mInputStream = mSocket.getInputStream();

			} catch (IOException e) {
				e.printStackTrace();

				error = e.getMessage();
			}

			return result;
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(Long result) {
			mIsConnecting = false;

			if (mSocket != null && result == 1) {

				mListener.onConnectionSuccess();
			} else {
				mListener.onConnectionFailed("Connection failed " + error);
				try {
					mSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mSocket = null;
			}
		}
	}
}