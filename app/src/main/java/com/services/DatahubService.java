package com.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;

import com.log.MyLog;
import com.manager.ProtocolManager;
import com.manager.SerialPortManager;
import com.util.LogManager;
import com.util.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.services.PollingService.DEBUG_KEY;

public class DatahubService extends Service {
    public final static String TAG = "DatahubService";
    private Context mContext;
    private Timer mHeartbeatTimer;
    private final List<ListenerFrame> mListenerFrameList = new ArrayList<ListenerFrame>();
    private SerialPortDataManager mSerialPortDataManager = null;
    private DatahubServiceBinder mDatahubServiceBinder = new DatahubServiceBinder();
    private StringBuffer mReadBuffer = new StringBuffer();
    private byte[] headerarr = {ProtocolManager.FRAME_HEADER};
    private String mFrameHeader = null;

    private LogManager mFrameUp = new LogManager();
    private LogManager mFrameDown = new LogManager();

    @Override
    public IBinder onBind(Intent intent) {
        if (mContext == null) {
            mContext = this;
        }
        return mDatahubServiceBinder;
    }

    public class DatahubServiceBinder extends Binder {
        public DatahubService getService() {
            return DatahubService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        mSerialPortDataManager = new SerialPortDataManager();
        try {
            mFrameHeader = new String(headerarr, 0, headerarr.length, ProtocolManager.DATAHUB_CHARSETNAME);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    public void CloseDatahub() {
        MyLog.appendLog(DEBUG_KEY + "CloseDatahub: ");
        Log.e(TAG, "CloseDatahub: ");
        if (mSerialPortDataManager != null) {
            StopHeartbeat();
            mSerialPortDataManager.close();
        }
    }

    public boolean OpenDatahub(String Port, int BaudRate) {
        boolean Open = true;
        if (mReadBuffer.length() > 0) {
            mReadBuffer.delete(0, mReadBuffer.length());
        }
        mSerialPortDataManager.setPort(Port);
        mSerialPortDataManager.setBaudRate(BaudRate);
        try {
            mFrameUp.SetLogFileName("FrameUp_" + mFrameUp.GetCurrentTime() + ".log");
            mFrameDown.SetLogFileName("FrameDown_" + mFrameDown.GetCurrentTime() + ".log");
            mSerialPortDataManager.open();
            StartHeartbeat();
        } catch (SecurityException e) {
            Log.e(TAG, "OpenDatahub: " + e.getMessage());
            Open = false;
        } catch (IOException e) {
            Log.e(TAG, "OpenDatahub: " + e.getMessage());
            Open = false;
        } catch (InvalidParameterException e) {
            Log.e(TAG, "OpenDatahub: " + e.getMessage());
            Open = false;
        }
        return Open;
    }

    public void registerListener(ListenerFrame listener) {
        if (!mListenerFrameList.contains(listener)) {
            Log.e(TAG, "registerListener: YES ");
            mListenerFrameList.add(listener);
        } else {
            Log.e(TAG, "registerListener: NO ");
        }
    }

    public void unregisterListener(ListenerFrame listener) {
        if (mListenerFrameList.contains(listener)) {
            mListenerFrameList.remove(listener);
        }
    }

    public interface ListenerFrame {
        void onFrameReceived(byte[] databuf);

        void onFrameSended(byte[] databuf);
    }

    private class SerialPortDataManager extends SerialPortManager {
        protected void onDataReceived(final byte[] databuf) {
            if (databuf.length > 0) {
                try {
                    mReadBuffer.append(new String(databuf, 0, databuf.length, ProtocolManager.DATAHUB_CHARSETNAME));
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            Unpack();
        }

        protected void onDataSended(final byte[] databuf) {
            for (ListenerFrame Listener : mListenerFrameList) {
                Listener.onFrameSended(databuf);
            }
            mFrameDown.SaveHexLog(databuf);
        }
    }

    protected void FrameReceived(final byte[] databuf) {
        for (ListenerFrame Listener : mListenerFrameList) {
            Listener.onFrameReceived(databuf);
        }
        mFrameUp.SaveHexLog(databuf);
    }

    private synchronized void Unpack() {
        if (mReadBuffer.length() < ProtocolManager.FRAME_MIN_LENGTH)
            return;
        try {
            int find = mReadBuffer.indexOf(mFrameHeader);
            if (find > 0) {
                Log.d(TAG, "before delete buffer=" + Utils.ByteArrToHex(mReadBuffer.substring(0, mReadBuffer.length()).getBytes(ProtocolManager.DATAHUB_CHARSETNAME)));
                mReadBuffer = mReadBuffer.delete(0, find);
                Log.d(TAG, "after delete buffer=" + Utils.ByteArrToHex(mReadBuffer.substring(0, mReadBuffer.length()).getBytes(ProtocolManager.DATAHUB_CHARSETNAME)));
            }
            boolean flag = true;
            while (flag) {
                int startFrame = mReadBuffer.indexOf(mFrameHeader);
                int endFrame = mReadBuffer.indexOf(mFrameHeader, startFrame + 1);
                if (startFrame >= 0 && endFrame >= 0) {
                    int Length = endFrame - startFrame + 1;
                    if (Length >= ProtocolManager.FRAME_MIN_LENGTH) {
                        byte[] Frame = mReadBuffer.substring(startFrame, (endFrame + 1)).getBytes(ProtocolManager.DATAHUB_CHARSETNAME);
                        FrameReceived(Frame);
                        mReadBuffer = mReadBuffer.delete(startFrame, (endFrame + 1));
                    } else {
                        if (endFrame == startFrame + 1) {
                            Log.d(TAG, "before delete buffer=" + Utils.ByteArrToHex(mReadBuffer.substring(0, mReadBuffer.length()).getBytes(ProtocolManager.DATAHUB_CHARSETNAME)));
                            mReadBuffer = mReadBuffer.delete(startFrame, (startFrame + 1));
                            Log.d(TAG, "after delete buffer=" + Utils.ByteArrToHex(mReadBuffer.substring(0, mReadBuffer.length()).getBytes(ProtocolManager.DATAHUB_CHARSETNAME)));
                        }
                    }
                    flag = true;
                } else {
                    flag = false;
                }
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void SendCommand(byte[] sendframe) {
        if (mSerialPortDataManager != null && mSerialPortDataManager.isOpen()) {
            mSerialPortDataManager.sendData(sendframe);
        }
    }

    private void StartHeartbeat() {
        mHeartbeatTimer = new Timer();
        mHeartbeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SendCommand(ProtocolManager.heartbeat);
            }
        }, 2000, ProtocolManager.HEARTBEAT_TIME);
    }

    private void StopHeartbeat() {
        if (mHeartbeatTimer != null)
            mHeartbeatTimer.cancel();
    }

    public boolean isOpen() {
        return mSerialPortDataManager.isOpen();
    }
}
