package com.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.APIs.APIClass;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appcontroller.AppController;
import com.commands.AckCommand;
import com.commands.PollingCommands;
import com.commands.ServerConnectionEstablished;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.infodispatch.BiddingActivity;
import com.infodispatch.CallCenterActivity;
import com.infodispatch.CommandQueue;
import com.infodispatch.DriverLogin;
import com.infodispatch.EmergencyAlert;
import com.infodispatch.HardwareControls;
import com.infodispatch.MainActivity;
import com.infodispatch.MessageTopDialog;
import com.infodispatch.NetworkCheckDialog;
import com.infodispatch.NetworkStatus;
import com.infodispatch.VoiceSoundPlayer;
import com.howen.howennative.gpio_info;
import com.howen.howennative.serialService;
import com.log.MyLog;
import com.infodispatch.R;
import com.manager.ProtocolManager;
import com.session.PulseManager;
import com.session.SessionManager;
import com.settersgetters.ConfigData;
import com.settersgetters.GpsData;
import com.settersgetters.RunningJobDetails;
import com.settersgetters.UpdateCurrentJobData;
import com.settersgetters.Utils;
import com.sqlite.DBHelper;
import com.util.CRCManager;
import com.util.FrameData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.appcontroller.AppController.DEBUG_TAG;


/**
 * Created by AK on 12/14/2016.
 */

public class PollingService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    public static String DEBUG_KEY = "PollingService";
    Location location;
    public static final String BROADCAST_ACTION = "com.hellocab";
    Intent intent;
    public static String jobCancelType = "NA";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static String TAG = "PollingService";
    private Location mLastLocation;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FASTEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private CommandQueue msgQueue = new CommandQueue();
    PollingCommands pollingData = new PollingCommands();
    SessionManager session;
    PulseManager pulseManager;
    ServerConnectionEstablished sce = new ServerConnectionEstablished();
    AckCommand ack = new AckCommand();
    int startLocationSender = 0;
    public int shiftCount;
    MediaPlayer mp = null;
    VoiceSoundPlayer voiceSoundPlayer = new VoiceSoundPlayer();
    public static final String CHANNEL_ID = "InfoWFM Service";
    private String[] gpioStrings = {"P3B6", "P3B7", "P0B6", "P5C1", "P5C2", "P3B3", "P3B4", "P4D3", "P3B5", "P0C1"};
    private boolean ifdestroy = false;
    private int gpio_value;
    private int i = 0;
    private String gpioValueState = "";
    private boolean ifopensuccess = false;
    private int fd = -1;
    private int autotime = 1000;
    private boolean isAuto = false;
    private int recDataCount = 0;
    private int readsize;
    private int sendDataCout = 0;
    private String pulseReading1 = "", pulseReading2 = "";

    public static final int SERVER_PORT1 = 6000;
    public static final int SERVER_PORT2 = 3003;
    public static PrintWriter output1;
    public static PrintWriter output2;
    public static String message;
    public static Socket socket1, socket2;
    public Thread serverSendThread1, serverSendThread2;
    public ServerSocket serverSocket1, serverSocket2;

    //TODO Datahub Service integration
    private int DATAHUB_BAUD_RATE = 115200;
    private DatahubService mDatahubService = null;
    protected int mUartId;
    protected byte mUartTransmissionId;
    protected int mSendedUartTransmission = 0;
    protected int mReceivedUartTransmission = 0;
    private static final long TIME_OUT = 300;
    private static final int UPGRADE_FAIL = 0;
    private static final int UPGRADE_SUCCESS = 1;
    private static final int SEND_UPGRADE_DATA = 2;
    private static final int STOP_UPGRADE = 3;
    protected short mCurrentSendIndex = 0;
    protected short mSendTimes = 0;
    protected byte mEndUpgradeType;
    protected int mTotalSpeedInValue = 0;
    private long mStartSpeedInTime;
    private Timer mTimerSpeedInCount;

    protected short mSpeedInValue = 0;
    protected byte mSensorOutId = (byte) 0x01;
    private int mCRCValue = 0;
    private Map<Integer, String> UpgradeDataMap = new HashMap<Integer, String>();
    private final int MAX_UPDATE_BUFF = 256 * 3;
    private int mUpgradePackages = 0;
    private String PACKAGE = "packageindex";
    private Handler mTimeOutHandler = new Handler();
    protected String mUpgradeFilePath = null;
    private boolean flag;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Declaration
    private Timer timer;
    private TimerTask timerTask;
    DBHelper db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DBHelper(this);
        pulseManager = new PulseManager(this);
//        BindDatahubService();
        intent = new Intent(BROADCAST_ACTION);
        if (checkPlayServices()) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
            mLocationRequest = LocationRequest.create();
//            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
            System.out.println("inside 3");
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }
        try {
            if (startLocationSender == 0) {
//                performOnBackgroundThread();
            }
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "onCreate" + e.getMessage());
        }
    }

    private void BindDatahubService() {
        Intent intent = new Intent(PollingService.this, DatahubService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unBindDatahubService() {
        if (flag) {
            MyLog.appendLog(DEBUG_TAG + "UnBindDataHubService onDestroy");
            unbindService(mConnection);
            mDatahubService = null;
            flag = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DatahubService.DatahubServiceBinder binder = (DatahubService.DatahubServiceBinder) service;
            mDatahubService = binder.getService();
            mDatahubService.registerListener(mListenerFrame);
            if (null != mDatahubService) {
                MyLog.appendLog(DEBUG_KEY + "onServiceConnected");
                //TESTING
//              ifopensuccess = true;
//              new TestThread().start();

             //Open port for sending pulse
//                OpenDatahub();
            }
            flag = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MyLog.appendLog(DEBUG_KEY + "onServiceDisconnected");
        }
    };

    private DatahubService.ListenerFrame mListenerFrame = new DatahubService.ListenerFrame() {
        @Override
        public void onFrameReceived(byte[] databuf) {
            Message msg = mHandler.obtainMessage(ProtocolManager.FRAME_RECEIVED);
            Bundle bundle = new Bundle();
            bundle.putByteArray(ProtocolManager.KEY_FRAME_BUF, databuf);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        @Override
        public void onFrameSended(byte[] databuf) {
            Message msg = mHandler.obtainMessage(ProtocolManager.FRAME_SENDED);
            Bundle bundle = new Bundle();
            bundle.putByteArray(ProtocolManager.KEY_FRAME_BUF, databuf);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    };


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ProtocolManager.FRAME_RECEIVED) {
                Bundle b = msg.getData();
                byte[] Receivedbuf = b.getByteArray(ProtocolManager.KEY_FRAME_BUF);
                ParsingReceivedFrame(Receivedbuf);
            } else if (msg.what == ProtocolManager.FRAME_SENDED) {
                Bundle b = msg.getData();
                byte[] Sendedbuf = b.getByteArray(ProtocolManager.KEY_FRAME_BUF);
                ParsingSendedFrame(Sendedbuf);
            }
        }
    };

    private void ParsingSendedFrame(byte[] Frame) {
        FrameData data = ProtocolManager.CreateFrameData(Frame);
        if (data == null)
            return;
        byte cmd = data.getcmd();
        switch (cmd) {
            case ProtocolManager.UART1_TRANSMISSION:
            case ProtocolManager.UART2_TRANSMISSION:
            case ProtocolManager.UART3_TRANSMISSION:
            case ProtocolManager.UART4_TRANSMISSION:
                if (data.getDataLength() > 0) {
                    ParsingSendedUartTransmissionFrame(cmd, data.getdatabuffer());
                }
                break;
            default:
                break;
        }
    }

    private void ParsingReceivedFrame(byte[] Frame) {
        FrameData data = ProtocolManager.CreateFrameData(Frame);

        if (data == null)
            return;
        byte cmd = data.getcmd();
        switch (cmd) {
            case ProtocolManager.VERSION:
                if (data.getDataLength() > 0) {
                    Log.e(TAG, "ParsingReceivedFrame: Verison " + new String(data.getdatabuffer()));
                }
                break;
            case ProtocolManager.UART_CONFIG:
                if (data.getDataLength() > 0) {
                    ParsingUARTConfig(data.getdatabuffer());
                }
                break;
            case ProtocolManager.UART_GET:
                if (data.getDataLength() > 0) {
                    ParsingUARTGet(data.getdatabuffer());
                }
                break;
            case ProtocolManager.UART_RESTORE:
                if (data.getDataLength() > 0) {
                    ParsingUARTRestore(data.getdatabuffer());
                }
                break;
            case ProtocolManager.SENSOR_IN:
                if (data.getDataLength() > 0) {
                    ParsingSensorIn(data.getdatabuffer());
                }
                break;
            case ProtocolManager.VOLTAGE:
                if (data.getDataLength() > 0) {
                    ParsingVoltage(data.getdatabuffer());
                }
                break;
            case ProtocolManager.SPEED_IN:
                if (data.getDataLength() > 0) {
                    if(pulseManager.isTrip()){
                        ParsingSpeedin(data.getdatabuffer());
                    }else{
                        MyLog.appendLog(DEBUG_KEY +" Trip Not Started");
                    }
                }
                break;
            case ProtocolManager.SENSOR_OUT:
                if (data.getDataLength() > 0) {
                    ParsingSensorOutFrame(data.getdatabuffer());
                }
                break;
            case ProtocolManager.SENSOR_OUT_GET:
                if (data.getDataLength() > 0) {
                    ParsingSensorOutGet(data.getdatabuffer());
                }
                break;
            case ProtocolManager.UPGRADE_START:
                if (data.getDataLength() > 0) {
                    ParsingStartUpgradeFrame(data.getdatabuffer());
                }
                break;
            case ProtocolManager.UPGRADE_DATE_SEND:
                if (data.getDataLength() > 0) {
                    ParsingUpgradeDataFrame(data.getdatabuffer());
                }
                break;
            case ProtocolManager.UPGRADE_END:
                if (data.getDataLength() > 0) {
                    ParsingUpgradeEndFrame(data.getdatabuffer());
                }
                break;
            case ProtocolManager.ACC_OFF_SETTINGS:
                if (data.getDataLength() > 0) {
                    ParsingAccSettingFrame(data.getdatabuffer());
                }
                break;
            case ProtocolManager.ACC_OFF_GET:
                if (data.getDataLength() > 0) {
                    ParsingGetAccSettingFrame(data.getdatabuffer());
                }
                break;
            case ProtocolManager.UART1_TRANSMISSION:
            case ProtocolManager.UART2_TRANSMISSION:
            case ProtocolManager.UART3_TRANSMISSION:
            case ProtocolManager.UART4_TRANSMISSION:
                if (data.getDataLength() > 0) {
                    ParsingReceivedUartTransmissionFrame(cmd, data.getdatabuffer());
                }
                break;
            case ProtocolManager.UART_DEBUG:
                if (data.getDataLength() > 0) {
                    ParsingUartDebug(data.getdatabuffer());
                }
                break;
            default:
                break;
        }
    }

    private void ParsingSendedUartTransmissionFrame(byte UartTransmissionId, byte[] data) {
        if (UartTransmissionId == mUartTransmissionId) {
            mSendedUartTransmission = mSendedUartTransmission + data.length;
            MyLog.appendLog(DEBUG_KEY + "ParsingSendedUartTransmissionFrame: Sended   :" + mSendedUartTransmission + "\r\n" + "Received:" + mReceivedUartTransmission);
            Log.e(TAG, "ParsingSendedUartTransmissionFrame: Sended   :" + mSendedUartTransmission + "\r\n" + "Received:" + mReceivedUartTransmission);
        }
    }

    private void ParsingReceivedUartTransmissionFrame(byte UartTransmissionId, byte[] data) {
        if (UartTransmissionId == mUartTransmissionId) {
            mReceivedUartTransmission = mReceivedUartTransmission + data.length;
            MyLog.appendLog(DEBUG_KEY + "ParsingReceivedUartTransmissionFrame: Sended   :" + mSendedUartTransmission + "\r\n" + "Received:" + mReceivedUartTransmission);
            Log.e(TAG, "ParsingReceivedUartTransmissionFrame: Sended   :" + mSendedUartTransmission + "\r\n" + "Received:" + mReceivedUartTransmission);
        }
    }

    private void ParsingAccSettingFrame(byte[] data) {
        if (data.length == 5) {
            if (data[0] == 1) {
                Log.e(TAG, "ParsingAccSettingFrame: " + getString(R.string.setting_acc_time_successful));

            } else {
                Log.e(TAG, "ParsingAccSettingFrame: " + getString(R.string.setting_acc_time_failed));

            }
        }
    }

    public static int ByteArrayToIntHH(byte[] b) {
        int res = 0;
        for (int i = 0; i < b.length; i++) {
            res += (b[i] & 0xff) << ((3 - i) * 8);
        }
        return res;
    }


    private void ParsingGetAccSettingFrame(byte[] data) {
        if (data.length == 4) {
            int time = ByteArrayToIntHH(data);
//            Log.e(TAG, "Acc power off time:" + time + " (s) ");
        }
    }

    private void ParsingUartDebug(byte[] data) {
        if (data.length == 1) {
            if (data[0] == 1) {
                MyLog.appendLog(DEBUG_KEY + "ParsingUartDebug: " + "RS232_2 debug" + " config successful!");
//                Log.e(TAG, "ParsingUartDebug: " + "RS232_2 debug" + " config successful!");
            } else {
                MyLog.appendLog(DEBUG_KEY + "ParsingUartDebug: " + "RS232_2 debug" + " config fail!");
//                Log.e(TAG, "ParsingUartDebug: " + "RS232_2 debug" + " config fail!");
            }
        }
    }

    private void ParsingStartUpgradeFrame(byte[] data) {
        if (data.length == 1) {
            if (data[0] == 1) {
                Message msg = mUpgradeHandler.obtainMessage(SEND_UPGRADE_DATA);
                Bundle bundle = new Bundle();
                bundle.putShort(PACKAGE, (short) 1);
                msg.setData(bundle);
                mUpgradeHandler.sendMessage(msg);

                //MyLog.appendLog(DEBUG_KEY + "ParsingStartUpgradeFrame: " + msg);
//                Log.e(TAG, "ParsingStartUpgradeFrame: " + msg);
                //mUpgradeProgressBar.setMax(UpgradeDataMap.size());
            } else {
                MyLog.appendLog(DEBUG_KEY + "EMPTY MESSAGE");
                mUpgradeHandler.sendEmptyMessage(UPGRADE_FAIL);
            }
        }
    }


    private void LoadUpgradePackages(byte[] data) {
        UpgradeDataMap.clear();
        StringBuffer StringBuffer = new StringBuffer();
        try {
            StringBuffer.append(new String(data, 0, data.length, ProtocolManager.DATAHUB_CHARSETNAME));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int index = 1;
        while (StringBuffer.length() > 0) {
            if (StringBuffer.length() > MAX_UPDATE_BUFF) {
                UpgradeDataMap.put(index++, StringBuffer.substring(0, MAX_UPDATE_BUFF));
                StringBuffer = StringBuffer.delete(0, MAX_UPDATE_BUFF);

            } else {
                UpgradeDataMap.put(index++, StringBuffer.substring(0, StringBuffer.length()));
                StringBuffer = StringBuffer.delete(0, StringBuffer.length());
            }
        }
        mUpgradePackages = index - 1;
    }

    private void SendCRC() {
        byte[] crc = Utils.IntTobyteArrayHH(mCRCValue);
        byte[] data = new byte[crc.length + 1];
        data[0] = (byte) 0x00;
        mEndUpgradeType = data[0];
        System.arraycopy(crc, 0, data, 1, crc.length);
        byte[] end = ProtocolManager.CreateFrameArray(ProtocolManager.UPGRADE_END, data);
        SendToDatahub(end);
    }

    private void SendUpgradeData(short Counter, byte[] data) {
        byte[] CounterBuf = Utils.ShortTobyteArrayHH(Counter);
        byte[] SendDate = new byte[data.length + CounterBuf.length];
        System.arraycopy(CounterBuf, 0, SendDate, 0, CounterBuf.length);
        System.arraycopy(data, 0, SendDate, CounterBuf.length, data.length);
        byte[] send = ProtocolManager.CreateFrameArray(ProtocolManager.UPGRADE_DATE_SEND, SendDate);
        SendToDatahub(send);
    }

    private void StopUpgrade() {
        byte[] data = new byte[5];
        data[0] = (byte) 0x01;
        mEndUpgradeType = data[0];
        byte[] Stop = ProtocolManager.CreateFrameArray(ProtocolManager.UPGRADE_END, data);
        SendToDatahub(Stop);
        UpgradeDataMap.clear();
    }

    private void StartUpgrade() {
        if (mUpgradeFilePath != null) {
            byte[] filebuff = getByte(mUpgradeFilePath);
            mCRCValue = CRCManager.CRCManager(filebuff, 0);
            LoadUpgradePackages(filebuff);
            int length = filebuff.length;
            byte[] FileLengthData = Utils.IntTobyteArrayHH(length);
            byte[] DatahubFile = ProtocolManager.CreateFrameArray(ProtocolManager.UPGRADE_START, FileLengthData);
            SendToDatahub(DatahubFile);
        } else {
//            MyLog.appendLog(DEBUG_KEY + "StartUpgrade: " + getString(R.string.select_upgrade_message));
//            Log.e(TAG, "StartUpgrade: " + getString(R.string.select_upgrade_message));
            return;
        }
    }

    private byte[] getByte(String path) {
        File f = new File(path);
        InputStream in;
        byte bytes[] = null;
        try {
            in = new FileInputStream(f);
            bytes = new byte[(int) f.length()];
            in.read(bytes);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private void SendToDatahub(byte[] FrameData) {
        mDatahubService.SendCommand(FrameData);
    }

    private Handler mUpgradeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SEND_UPGRADE_DATA) {
                Bundle b = msg.getData();
                short index = b.getShort(PACKAGE);
                //mUpgradeProgressBar.setProgress(index);
                if (index <= mUpgradePackages) {
                    String UpgradeData = UpgradeDataMap.get((int) index);
                    if (UpgradeData != null) {
                        try {
                            if (mCurrentSendIndex == index) {
                                mSendTimes++;
                            } else {
                                mSendTimes = 1;
                            }
                            if (mSendTimes <= ProtocolManager.MAX_UPGRADE_SEND_TIMES) {
                                byte[] data = UpgradeData.getBytes(ProtocolManager.DATAHUB_CHARSETNAME);
                                SendUpgradeData(index, data);
                                mCurrentSendIndex = index;
                                MyLog.appendLog(DEBUG_KEY + "AddSendFramesQueue package==" + mCurrentSendIndex + "  mSendTimes==" + mSendTimes);
                                Log.d(TAG, "AddSendFramesQueue package==" + mCurrentSendIndex + "  mSendTimes==" + mSendTimes);
                                mTimeOutHandler.removeCallbacks(mTimeOutRunnable);
                                mTimeOutHandler.postDelayed(mTimeOutRunnable, TIME_OUT);
                            } else {
                                MyLog.appendLog(DEBUG_KEY + "times out upgrade fail !");
                                Log.d(TAG, "times out upgrade fail !");
                                mUpgradeHandler.sendEmptyMessage(UPGRADE_FAIL);
                            }
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            } else if (msg.what == UPGRADE_FAIL) {
                MyLog.appendLog(DEBUG_KEY + "StopUpgrade upgrade fail");
                Log.d(TAG, "StopUpgrade upgrade fail");
                StopUpgrade();
                mTimeOutHandler.removeCallbacks(mTimeOutRunnable);
//                MyLog.appendLog(DEBUG_KEY + "handleMessage: " + getString(R.string.upgrade_failed));
//                Log.e(TAG, "handleMessage: " + getString(R.string.upgrade_failed));
                UpgradeDataMap.clear();
            } else if (msg.what == UPGRADE_SUCCESS) {
//                MyLog.appendLog(DEBUG_KEY + "handleMessage: " + getString(R.string.upgrade_successful));
//                Log.e(TAG, "handleMessage: " + getString(R.string.upgrade_successful));
                UpgradeDataMap.clear();
            } else if (msg.what == STOP_UPGRADE) {
//                MyLog.appendLog(DEBUG_KEY + "handleMessage: " + getString(R.string.stop_upgrade));
//                Log.e(TAG, "handleMessage: " + getString(R.string.stop_upgrade));
                UpgradeDataMap.clear();
            }

        }
    };


    private void ParsingUpgradeEndFrame(byte[] data) {
        if (data.length == 1) {
            if (data[0] == 1) {
                if (mEndUpgradeType == 0) {
                    mUpgradeHandler.sendEmptyMessage(UPGRADE_SUCCESS);
                } else if (mEndUpgradeType == 1) {
                    mUpgradeHandler.sendEmptyMessage(STOP_UPGRADE);
                }
            } else {
                mUpgradeHandler.sendEmptyMessage(UPGRADE_FAIL);
            }
        }
    }


    private final Runnable mTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = mUpgradeHandler.obtainMessage(SEND_UPGRADE_DATA);
            Bundle bundle = new Bundle();
            bundle.putShort(PACKAGE, mCurrentSendIndex);
            msg.setData(bundle);
            Log.d(TAG, "TimeOutRunnable send package==" + bundle.getShort(PACKAGE));
            mHandler.sendMessage(msg);
        }
    };

    private void ParsingUpgradeDataFrame(byte[] data) {
        if (data.length == 3) {
            byte[] index = new byte[2];
            System.arraycopy(data, 0, index, 0, 2);
            short ReceivedIndex = Utils.ByteArrayToShortHH(index);
            if (mCurrentSendIndex == ReceivedIndex) {
                mTimeOutHandler.removeCallbacks(mTimeOutRunnable);
                if (ReceivedIndex <= mUpgradePackages) {
                    Message msg = mUpgradeHandler.obtainMessage(SEND_UPGRADE_DATA);
                    Bundle bundle = new Bundle();
                    if (data[2] == (byte) 0x01) {
                        if (ReceivedIndex == mUpgradePackages) {
                            SendCRC();
                            Log.d(TAG, "SendCRC");
                            return;
                        } else {
                            bundle.putShort(PACKAGE, (short) (ReceivedIndex + 1));
                        }
                    } else {
                        bundle.putShort(PACKAGE, ReceivedIndex);
                    }
                    msg.setData(bundle);
                    mUpgradeHandler.sendMessage(msg);
                }
            }
        }
    }

    private void ParsingSensorOutFrame(byte[] data) {
        if (data.length == 3) {
            if (data[2] == (byte) 0x01) {
                if (data[0] == mSensorOutId) {
//                    String device=mSensorOutDevices.getSelectedItem().toString();
                    String device = "AK- RS_232_2";
                    if (data[1] == 1) {
//                        MyLog.appendLog(DEBUG_KEY + "ParsingSensorOutFrame: " + device + " State is Hight");
//                        Log.e(TAG, "ParsingSensorOutFrame: " + device + " State is Hight");
                    } else if (data[1] == 0) {
//                        MyLog.appendLog(DEBUG_KEY + "ParsingSensorOutFrame: " + device + " State is Low");
//                        Log.e(TAG, "ParsingSensorOutFrame: " + device + " State is Low");
                    }
                }
            }
        }
    }

    private void ParsingSensorOutGet(byte[] data) {
        if (data.length == 2) {
            if (data[0] == mSensorOutId) {
//                String device=mSensorOutDevices.getSelectedItem().toString();
                String device = "AK- RS_232_2";
                if (data[1] == 1) {
//                    MyLog.appendLog(DEBUG_KEY + "ParsingSensorOutFrame: " + device + " State is Hight");
//                    Log.e(TAG, "ParsingSensorOutFrame: " + device + " State is Hight");
                } else if (data[1] == 0) {
//                    MyLog.appendLog(DEBUG_KEY + "ParsingSensorOutFrame: " + device + " State is Low");
//                    Log.e(TAG, "ParsingSensorOutFrame: " + device + " State is Low");
                }
            }
        }
    }

    private void StartSpeedInCount() {
        if (!mDatahubService.isOpen()) return;
        if (mTimerSpeedInCount != null) {
            mTimerSpeedInCount.cancel();
            mTimerSpeedInCount = null;
        }
        mTotalSpeedInValue = 0;
        mStartSpeedInTime = System.currentTimeMillis();
        mTimerSpeedInCount = new Timer();
        mTimerSpeedInCount.schedule(new TimerTask() {
            @Override
            public void run() {
                mTotalSpeedInValue = mTotalSpeedInValue + (int) mSpeedInValue;
                String totalSpeedin = "Total speed in" + " : " + mTotalSpeedInValue;
                long lasttime = (System.currentTimeMillis() - mStartSpeedInTime) / 1000;
                String time = "Time" + " : " + DateUtils.formatElapsedTime(lasttime);
//                Log.e(TAG, totalSpeedin + "    " + time);
            }
        }, 10, 1000);
    }

    private void StopSpeedInCount() {
        if (mTimerSpeedInCount != null) {
            mTimerSpeedInCount.cancel();
            mTimerSpeedInCount = null;
            String totalSpeedin = "Total speed in:" + " : " + mTotalSpeedInValue;
            long lasttime = (System.currentTimeMillis() - mStartSpeedInTime) / 1000;
            String time = "Time" + " : " + DateUtils.formatElapsedTime(lasttime);
//            Log.e(TAG, "StopSpeedInCount: " + totalSpeedin + "    " + time);
        }
    }


    private void ParsingSpeedin(byte[] databuffer) {
        if (databuffer.length == 2) {
            mSpeedInValue = Utils.ByteArrayToShortHH(databuffer);
            MyLog.appendLog(DEBUG_KEY +" Speed Int " + Utils.ByteArrayToIntHH(databuffer));
            MyLog.appendLog(DEBUG_KEY + "Speed value " + mSpeedInValue);
            pulseReading1 = "" + mSpeedInValue; //333

            next = pulseManager.getNextCount(); // 0
            MyLog.appendLog(DEBUG_KEY + " next raw : "+next);
            if (next == 0) {
                next = Integer.parseInt(pulseReading1); //333
                pulseManager.setNextCount(next); // 333
            }
            prev = next; // 333
            pulseManager.setPrevCount(prev); // 333

            next = next + Integer.parseInt(pulseReading1); //333
            MyLog.appendLog(DEBUG_KEY + "next cumulative : " + next);
            MyLog.appendLog(DEBUG_KEY + "prev old value: " + prev);

            pulseManager.setNextCount(next); // 333

            if (next > prev) {
                int diffVal = (next - prev);
                MyLog.appendLog(DEBUG_KEY + "diffVal calculation : " + diffVal);
                if (diffVal > 0) {
                    MyLog.appendLog(DEBUG_KEY + " ParsingSpeedin: P " + prev + " N " + next + " digg " + diffVal);
                    double pulseUnit = pulseManager.getPulseCount();
                    int odoKiloMeter = pulseManager.getOdoKiloMeter();
                    int odometer = pulseManager.getOdoMeter();
                    double diff = diffVal / pulseUnit;

                    if (pulseManager.isTrip()) {
                        MyLog.appendLog(DEBUG_KEY + "diff trip : " + diff);
                        pulseManager.setTripDistatnce((float) (diff + pulseManager.getTripDistance()));
                    }

                    MyLog.appendLog(DEBUG_KEY + "ParsingSpeedin: D " + diff + " P " + pulseUnit + " K " + odoKiloMeter + " M " + odometer);

                    int killdiff = (int) diff;
                    if (odoKiloMeter + killdiff > 99999) {
                        odoKiloMeter = odoKiloMeter + killdiff - 99999;
//                        Log.e(TAG, "ParsingSpeedin: extra kilometer " + odoKiloMeter);
                    } else {
                        odoKiloMeter = odoKiloMeter + killdiff;
//                        Log.e(TAG, "ParsingSpeedin: kilometer " + odoKiloMeter);
                    }

                    diff = diff - killdiff;
                    diff = diff * 10;
                    killdiff = (int) diff;
                    int odom = killdiff;

                    if (odometer + odom > 9) {
                        if (odoKiloMeter + 1 > 99999) {
                            odoKiloMeter = 0;
                        } else {
                            odoKiloMeter = odoKiloMeter + 1;
                        }
                        odom = odometer + odom - 10;
                    } else {
                        odom = odometer + odom;
                    }
                    pulseManager.setOdometer(odoKiloMeter, odom);
//                    MyLog.appendLog(DEBUG_KEY + " KM : "+ odoKiloMeter);
//                    MyLog.appendLog(DEBUG_KEY + " M : "+ odom);
//                    Log.e(TAG, "ParsingSpeedin: " + odoKiloMeter);
//                    Log.e(TAG, "ParsingSpeedin: " + odom);
//                    MyLog.appendLog(DEBUG_KEY + " ODO DOUB : "+ + pulseManager.getDoubleOdometer());
//                    MyLog.appendLog(DEBUG_KEY + "ParsingSpeedin: D after " + diff + " P " + pulseUnit + " K " + odoKiloMeter + " M " + odometer);
                    if (pulseManager.isTrip()) {
                        pulseManager.setEndOdometer();
                    }

                }
            } else if (next == prev) {
                int wait =0;
                if (pulseManager.isTrip()) {
                    wait = pulseManager.getTripWating();
                    wait = wait + 1;
                    MyLog.appendLog(DEBUG_KEY + "W : "+ wait);
//                    Log.e(TAG, "ParsingSpeedin: W " + wait);
                    pulseManager.setTripWating(wait);
                }
            } else {
                if (prev > 99000 && next < 1000) {
                    prev = 99999 - prev;
                } else {
                    prev = 0;
                }
                int diffVal = (prev + next);
                if (diffVal > 0) {

                    double pulseUnit = pulseManager.getPulseCount();
                    int odoKiloMeter = pulseManager.getOdoKiloMeter();
                    int odometer = pulseManager.getOdoMeter();
                    double diff = diffVal / pulseUnit;

                    int killdiff = (int) diff;
                    if (odoKiloMeter + killdiff > 99999) {
                        odoKiloMeter = odoKiloMeter + killdiff - 99999;
//                        Log.e(TAG, "ParsingSpeedin: extra kilometer " + odoKiloMeter);
//                        MyLog.appendLog(DEBUG_KEY + " extra KM : "+ odoKiloMeter);
                    } else {
                        odoKiloMeter = odoKiloMeter + killdiff;
                    }

                    if (pulseManager.isTrip()) {
                        pulseManager.setTripDistatnce((float) (diff + pulseManager.getTripDistance()));
                        int wait = 0;
                        if (pulseManager.isTrip()) {
                            wait = pulseManager.getTripWating();
                            wait = wait + 1;
                            MyLog.appendLog(DEBUG_KEY + "W : "+ wait);
//                            Log.e(TAG, "handleMessage: W " + wait);
                            pulseManager.setTripWating(wait);
                        }
                    }

                    diff = diff - killdiff;
                    diff = diff * 10;
                    int odom = (int) diff;

                    if (odometer + odom > 9) {
                        if (odoKiloMeter + 1 > 99999) {
                            odoKiloMeter = 0;
                        } else {
                            odoKiloMeter = odoKiloMeter + 1;
                        }
                        odom = odometer + odom - 10;
                    } else {
                        odom = odometer + odom;
                    }
                    pulseManager.setOdometer(odoKiloMeter, odom);
                    if (pulseManager.isTrip()) {
                        pulseManager.setEndOdometer();
                    }

                }
            }

        }else{
            MyLog.appendLog(DEBUG_KEY +"" + databuffer);
        }
    }

    private void ParsingVoltage(byte[] databuffer) {
        if (databuffer.length == 8) {
            byte[] power = new byte[2];
            byte[] acc = new byte[2];
            byte[] ad1 = new byte[2];
            byte[] ad2 = new byte[2];
            power[0] = databuffer[0];
            power[1] = databuffer[1];
            acc[0] = databuffer[2];
            acc[1] = databuffer[3];
            ad1[0] = databuffer[4];
            ad1[1] = databuffer[5];
            ad2[0] = databuffer[6];
            ad2[1] = databuffer[7];
            short powerVoltage = (short) (Utils.ByteArrayToShortHH(power) * 10);
            short accVoltage = (short) (Utils.ByteArrayToShortHH(acc) * 10);
            short ad1Voltage = (short) (Utils.ByteArrayToShortHH(ad1) * 10);
            short ad2Voltage = (short) (Utils.ByteArrayToShortHH(ad2) * 10);

//            Log.e(TAG, "Power :" + powerVoltage + " mV");
//            Log.e(TAG, "ACC :" + accVoltage + " mV");
//            Log.e(TAG, "AD1 :" + ad1Voltage + " mV");
//            Log.e(TAG, "AD2 :" + ad2Voltage + " mV");
        }
    }

    private void ParsingUARTConfig(byte[] databuffer) {
        if (databuffer.length == 2) {
            if (databuffer[0] == mUartId) {
                //String uart=mUARTDevice.getSelectedItem().toString();
                if (databuffer[0] == 1) {
//                    Log.e(TAG, "ParsingUARTConfig: config successful!");
                } else if (databuffer[0] == 0) {
//                    Log.e(TAG, "ParsingUARTConfig:  config fail!");
                }
            }
        }
    }

    private void ParsingUARTRestore(byte[] databuffer) {
        if (databuffer.length == 2) {
            if (databuffer[0] == mUartId) {
                //String uart=mUARTDevice.getSelectedItem().toString();
                if (databuffer[0] == 1) {
//                    Log.e(TAG, "ParsingUARTRestore: Restore successful!");
                } else if (databuffer[0] == 0) {
//                    Log.e(TAG, "ParsingUARTRestore: Restore fail!");
                }
            }
        }
    }

    private void ParsingUARTGet(byte[] databuffer) {
        if (databuffer.length == 8) {
            if (databuffer[0] == mUartId) {
                byte[] baudrate = new byte[4];
                System.arraycopy(databuffer, 1, baudrate, 0, 4);
                int Baudrate = Utils.ByteArrayToIntHH(baudrate);
                String ParityBit = null;
                if (databuffer[5] == (byte) 0x01) {
                    ParityBit = "Odd parity";
                } else if (databuffer[5] == (byte) 0x02) {
                    ParityBit = "Even parity";
                } else if (databuffer[5] == (byte) 0x00) {
                    ParityBit = "None parity";
                }
                int BitWidth = 0;
                if (databuffer[6] == (byte) 0x07) {
                    BitWidth = 7;
                } else if (databuffer[6] == (byte) 0x09) {
                    BitWidth = 9;
                } else if (databuffer[6] == (byte) 0x08) {
                    BitWidth = 8;
                }
                double StopBit = 0;
                if (databuffer[7] == (byte) 0x01) {
                    StopBit = 0.5;
                } else if (databuffer[7] == (byte) 0x03) {
                    StopBit = 1.5;
                } else if (databuffer[7] == (byte) 0x04) {
                    StopBit = 2;
                } else if (databuffer[7] == (byte) 0x02) {
                    StopBit = 1;
                }
                //String uart=mUARTDevice.getSelectedItem().toString();
                String uartconfig = "uart Baud rate:" + Baudrate + " Parity Bit:" + ParityBit + " Bit Width:" + BitWidth + " Stop Bit:" + StopBit;
//                Log.e(TAG, uartconfig);
            }
        }
    }

    private void ParsingSensorIn(byte[] databuffer) {
        if (databuffer.length == 4) {
            //boolean SensorInState[]=new boolean[mSensorInView.length];
            int value = Utils.ByteArrayToIntHH(databuffer);
            String strSensors = Integer.toBinaryString(value);
            StringBuilder sensorsBuilder = new StringBuilder(strSensors);
            char sensors[] = sensorsBuilder.reverse().toString().toCharArray();
            MyLog.appendLog(DEBUG_KEY + " ParsingSensorIn: " + strSensors);

//            for(int i=0;i<sensors.length;i++)
//            {
//                if(sensors[i]=='1')
//                {
//                    SensorInState[i]=true;
//                }else if(sensors[i]=='0')
//                {
//                    SensorInState[i]=false;
//                }
//            }
//            for(int j=0;j<mSensorInView.length;j++)
//            {
//                int SensorinId=j+1;
//                if(SensorInState[j])
//                {
//                    mSensorInView[j].setText("Sensor in"+SensorinId+"  "+getString(R.string.sensor_in_state_hight));
//                }else
//                {
//                    mSensorInView[j].setText("Sensor in"+SensorinId+"  "+getString(R.string.sensor_in_state_low));
//                }
//            }
        }
    }

    private void OpenDatahub() {
        if (null == mDatahubService) {
            BindDatahubService();
            return;
        }
        if (mDatahubService.isOpen()) {
            MyLog.appendLog(DEBUG_TAG + "Data Hub already open");
            return;
        }
        String address = "/dev/ttyS4"; //mDatahubAddress.getSelectedItem().toString();
        boolean open = mDatahubService.OpenDatahub(address, DATAHUB_BAUD_RATE);
        if (open) {
            ifopensuccess = true;
        } else {
            ifopensuccess = false;
        }
    }

    class TestThread extends Thread {
        public void run() {
            while (ifopensuccess) {
                try {
                    Message msg = new Message();
                    msg.obj = "23";
                    msg.what = 2;
                    serialPorthandler.sendMessage(msg);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    int next, prev;
    @SuppressLint("HandlerLeak")
    Handler serialPorthandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                pulseReading1 = "" + Integer.parseInt(msg.obj.toString());
                next = pulseManager.getNextCount();
                if (next == 0) {
                    next = Integer.parseInt(pulseReading1);
                    pulseManager.setNextCount(next);
                }
                prev = next;
                pulseManager.setPrevCount(prev);
                next = next + Integer.parseInt(pulseReading1);
                pulseManager.setNextCount(next);
                if (next > prev) {
                    int diffVal = (next - prev);
                    if (diffVal > 0) {
                        double pulseUnit = pulseManager.getPulseCount();
                        int odoKiloMeter = pulseManager.getOdoKiloMeter();
                        int odometer = pulseManager.getOdoMeter();
                        double diff = diffVal / pulseUnit;

                        if (pulseManager.isTrip()) {
                            pulseManager.setTripDistatnce((float) (diff + pulseManager.getTripDistance()));
                             //CALCULATING PULSE WAITING TIME
//                            if(pulseManager.isTrip()){
//                                int wait = 0;
//                                if (pulseManager.isTrip()) {
//                                    wait = pulseManager.getTripWating();
//                                    wait = wait + 6;
////                                    Log.e(TAG, "handleMessage: W " + wait);
//                                    pulseManager.setTripWating(wait);
//                                }
//                            }
                        }

//                        Log.e(TAG, "handleMessage: D " + diff + " P " + pulseUnit + " K " + odoKiloMeter + " M " + odometer);
                        int killdiff = (int) diff;
                        if (odoKiloMeter + killdiff > 99999) {
                            odoKiloMeter = odoKiloMeter + killdiff - 99999;
//                            Log.e(TAG, "handleMessage: extra kilometer " + odoKiloMeter);
                        } else {
                            odoKiloMeter = odoKiloMeter + killdiff;
//                            Log.e(TAG, "handleMessage: kilometer " + odoKiloMeter);
                        }

                        diff = diff - killdiff;
                        diff = diff * 10;
                        killdiff = (int) diff;
                        int odom = killdiff;

                        if (odometer + odom > 9) {
                            if (odoKiloMeter + 1 > 99999) {
                                odoKiloMeter = 0;
                            } else {
                                odoKiloMeter = odoKiloMeter + 1;
                            }
                            odom = odometer + odom - 10;
                        } else {
                            odom = odometer + odom;
                        }
                        pulseManager.setOdometer(odoKiloMeter, odom);
//                        Log.e(TAG, "handleMessage: " + odoKiloMeter);
//                        Log.e(TAG, "handleMessage: " + odom);
//                        Log.e(TAG, "handleMessage: " + pulseManager.getDoubleOdometer());
                        if (pulseManager.isTrip()) {
                            pulseManager.setEndOdometer();
                        }
                    }
                } else if (next == prev) {
                    int wait = 0;
                    if (pulseManager.isTrip()) {
                        wait = pulseManager.getTripWating();
                        wait = wait + 1;
//                        Log.e(TAG, "handleMessage: W " + wait);
                        pulseManager.setTripWating(wait);
                    }
                } else {
                    if (prev > 99000 && next < 1000) {
                        prev = 99999 - prev;
                    } else {
                        prev = 0;
                    }
                    int diffVal = (prev + next);
                    if (diffVal > 0) {
                        double pulseUnit = pulseManager.getPulseCount();
                        int odoKiloMeter = pulseManager.getOdoKiloMeter();
                        int odometer = pulseManager.getOdoMeter();
                        double diff = diffVal / pulseUnit;

                        int killdiff = (int) diff;
                        if (odoKiloMeter + killdiff > 99999) {
                            odoKiloMeter = odoKiloMeter + killdiff - 99999;
//                            Log.e(TAG, "handleMessage: extra kilometer " + odoKiloMeter);
                        } else {
                            odoKiloMeter = odoKiloMeter + killdiff;
                        }


                        if (pulseManager.isTrip()) {
                            pulseManager.setTripDistatnce((float) (diff + pulseManager.getTripDistance()));
                            // CALCULATING PULSE WAITING TIME
                            double currentPulse = pulseManager.getPulseCount() * 20; // GET HOUR VALUE OF PULSE COUNT
                            currentPulse = currentPulse / 60;  // GET MIN VALUE OF PULSE COUNT FROM HOUR VALUE OF 20 KM
                            currentPulse = currentPulse / 5;  // MAKE DIV BY REPEAT INTERVAL VALUE OF PULSE COUNT GETTING
                            if(diffVal < currentPulse){
                                int wait = 0;
                                if (pulseManager.isTrip()) {
                                    wait = pulseManager.getTripWating();
                                    wait = wait + 1;
//                                    Log.e(TAG, "handleMessage: W " + wait);
                                    pulseManager.setTripWating(wait);
                                }
                            }
                        }
                        diff = diff - killdiff;
                        diff = diff * 10;
                        int odom = (int) diff;

                        if (odometer + odom > 9) {
                            if (odoKiloMeter + 1 > 99999) {
                                odoKiloMeter = 0;
                            } else {
                                odoKiloMeter = odoKiloMeter + 1;
                            }
                            odom = odometer + odom - 10;
                        } else {
                            odom = odometer + odom;
                        }
                        pulseManager.setOdometer(odoKiloMeter, odom);
                        if (pulseManager.isTrip()) {
                            pulseManager.setEndOdometer();
                        }

                    }
                }
            }
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        try {
            pulseManager = new PulseManager(AppController.getInstance());
            intent = new Intent(BROADCAST_ACTION);

            if (startLocationSender == 0) {
                performOnBackgroundThread();
            }
            if (getString(R.string.app_publish_mode).equals("MDT")) {
                new gpioThread().start();
            }
            try {
                if (pulseManager.isPulse()) {
                    //OLD Code
                    //open_serialPort();
//                    new recDataThread().start();
                }
            } catch (Exception ex) {
                System.out.println("open_serialPort error the OnStartCommand" + ex.getMessage());
            }
            String input = intent.getStringExtra("inputExtra");
            createNotificationChannel();
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getApplicationContext().getResources().getString(R.string.alt_app_name) + " is running")
                    .setContentText(input)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(null)
                    .build();
            startForeground(1, notification);
        } catch (Exception Eww) {
            System.out.println("Inside the OnStartComman" + Eww);
        }

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Service is running", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void performOnBackgroundThread() {
        startLocationSender = 1;

        serverSendThread1 = new Thread(new ServerSendThread1());
        serverSendThread1.start();

        serverSendThread2 = new Thread(new ServerSendThread2());
        serverSendThread2.start();


        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    int threadCntr = 0, pollCounter = 1;
                    while (true) {
                        try {
//                            if (threadCntr % 2 == 0)
//                                sendDataToServer();
//                            else if (threadCntr % 5 == 0)
//                                recvDataFromServer();
//                            else if (threadCntr % 3 == 0)
//                                sendDataToInfotainment();
                            if (Integer.parseInt(ConfigData.getPollingInterVal()) == pollCounter) {
//                                if(null!= pulseManager)
//                                    GpsData.setExtraCharges(pulseManager.getExtraFare(),pulseManager.getExtraTollFare());

                                pollingData.frame_09_cmd();

                                GpsData.setExtraCharges(0,0);
                                pollCounter = 0;
                            }
                            if (threadCntr > 9999)
                                threadCntr = 1;
                            else
                                threadCntr++;
                            pollCounter++;
                            Thread.sleep(1000);
                        } catch (Exception Eww) {
                        }
                    }
                } finally {
                }
            }
        };
        t.start();
    }

    private void recvDataFromServer() {
        try {
            if (ConfigData.getPollingUrl() != null) {
                WebService webService = new WebService(ConfigData.getPollingUrl() + "/");
                Map<String, String> params = new HashMap<String, String>();
                String response = webService.webGet("GetForward/" + Utils.getImei_no(), params);
                System.out.println("Command response in Service" + response);
                if (response != "" && response != null) {
                    processInCommands(response);
                }
            } else {
                setClientInfo();
            }
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "recvDataFromServer" + e.getMessage());
        }
    }

    private void sendDataToServer() {
        try {
            if (ConfigData.getPollingUrl() != null) {
                if (db.getCurrentScreenVal().equalsIgnoreCase("MAIN_ACTIVITY"))
                    ConfigData.setDeviceStatus(1);
                String command = "";
                if (msgQueue.priorQueue_Size() > 0) {
                    command = msgQueue.fetchCmdFromPriorQueue();
                } else {
                    command = msgQueue.fetchCmdFromQueue();
                    if (command.startsWith("^0624"))
                        msgQueue.deleteCmdFromQueue();
                }
                if (command != "" && command != null) {
                    System.out.println("Command Framed in Service" + command);
                    WebService webService = new WebService(ConfigData.getPollingUrl() + "/");
                    String[] parsedData = command.split("\\||\\^");
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("Cmdid", parsedData[1]);
                    params.put("Cmdstr", command);
                    String response = webService.webInvoke("SubmitReturn/" + Utils.getImei_no(), params);
                    System.out.println("Command response in Service" + command);
                    System.out.println("Command response in Service params" + params);

                    if (response != "" && response != null) {
                        processInCommands(response);
                    }
                }
            } else {
                setClientInfo();
            }

        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "sendDataToServer" + e.getMessage());
        }
    }

    private void sendDataToInfotainment() {
        message = pulseManager.getJsonObject();
        if (socket1 != null && socket1.isConnected()) {
//            MyLog.appendLog(DEBUG_KEY + "infotainmentParams run: JSON 1 " + message);
//            Log.e(TAG, "infotainmentParams run: JSON 1 " + message);
            output1.write(message + "\n");
            output1.flush();
        }

        if (socket2 != null && socket2.isConnected()) {
//            MyLog.appendLog(DEBUG_KEY + "infotainmentParams run: JSON 2 " + message);
//            Log.e(TAG, "infotainmentParams run: JSON 2 " + message);
            output2.write(message + "\n");
            output2.flush();
        }
    }

    public void processInCommands(String recvData) {
        System.out.println("Inside the processCommand:" + recvData);
        try {
            int count = 0;
            //String packets1 = "0632|799|CMCG297587|Bid Job|Niton Buildings A, Cunningham Rd, Vasanth Nagar, Bengaluru, Karnataka 560051, India|Kindly Bid|3, Thimakka Layout, Coconut Garden, Cholanayakanahalli, Hebbal, Bengaluru, Karnataka 560032, India|12.9907|77.5891|Accept|Reject|20|0.00|0|!";
            String[] packets = recvData.split("\\^|\\!");
            while (count < packets.length) {
                try {
                    if (!packets[count].startsWith("{") && packets[count].length() > 3) {
                        packets[count] = "^" + packets[count] + "!";
                        if (!packets[count].startsWith("^0623")) {
                            String[] parsedData = packets[count].split("\\|");
                            //ack.frame_24_cmd(parsedData[1]);
                            updateACK(parsedData[1]);
                        } else {
                            if (msgQueue.priorQueue_Size() > 0) {
                                msgQueue.deleteCmdFromPriorQueue();
                            } else {
                                msgQueue.deleteCmdFromQueue();
                            }
                        }
                        if (packets[count].startsWith("^0640") && packets[count].endsWith("|!")) {
                            try {
                                if (db.getCurrentScreenVal().equalsIgnoreCase("MAIN_ACTIVITY")) {
                                    db.updateCurrentScreenVal("CALL_CENTER", 1);
                                    voiceSoundPlayer.playVoice(PollingService.this, R.raw.jobreceived);
                                    HardwareControls.backlightON(PollingService.this);

                                    String[] parsedData = packets[count].split("(\\|)|(\\:)|(\\~)");
                                    String stringPickup = parsedData[14].toString();
                                    String stringDrop = parsedData[16].toString();

                                    HashMap<String, String> callCenterData = new HashMap<String, String>();
                                    callCenterData.put("JobId", parsedData[2]);
                                    callCenterData.put("mobileNo", parsedData[4]);
                                    callCenterData.put("name", parsedData[5]);
                                    callCenterData.put("customerPickUpTime", parsedData[11] + ":" + parsedData[12]);
                                    callCenterData.put("pickupLoc", stringPickup);
                                    callCenterData.put("dropLoc", stringDrop);
                                    callCenterData.put("pickupLat", parsedData[35]);
                                    callCenterData.put("pickupLon", parsedData[36]);
                                    callCenterData.put("dropLat", parsedData[37]);
                                    callCenterData.put("dropLon", parsedData[38]);
                                    callCenterData.put("paymentMode", "0");
                                    callCenterData.put("jobStatus", "ACCEPT");

                                    RunningJobDetails.setJobId(parsedData[2]);
                                    RunningJobDetails.setMobileNo(parsedData[4]);
                                    RunningJobDetails.setCustomerName(parsedData[5]);
                                    RunningJobDetails.setPickupLoc(stringPickup);
                                    RunningJobDetails.setDropLoc(stringDrop);
                                    RunningJobDetails.setPickupLatitude(parsedData[35]);
                                    RunningJobDetails.setPickupLongitude(parsedData[36]);
                                    RunningJobDetails.setDropLatitude(parsedData[37]);
                                    RunningJobDetails.setDropLongitude(parsedData[38]);

                                    String[] slittedConfig = parsedData[34].split("\\ ");

                                    db.updateCallCenterJobValues(callCenterData, 1);
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("fixedFare", slittedConfig[0]);
                                    map.put("baseKms", slittedConfig[1]);
                                    map.put("baseHours", slittedConfig[2]);
                                    map.put("wait", slittedConfig[3]);
                                    map.put("waitingFreeMin", slittedConfig[4]);
                                    map.put("waitingRate", slittedConfig[5]);
                                    map.put("extraHourRates", slittedConfig[6]);
                                    map.put("extraKmRate", slittedConfig[7]);
                                    map.put("serviceTax", slittedConfig[8]);
                                    int cnt = db.getCallCenterRecordsCount();
                                    if (cnt == 0) {
                                        db.insetCallCenterDBValues(map);
                                    } else {
                                        db.updateCallCenterDBValues(map, 1);
                                    }
                                    Intent i = new Intent(getApplicationContext(), CallCenterActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.addFlags(Intent.FLAG_FROM_BACKGROUND);
                                    i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                    AppController.getInstance().startActivity(i);
                                }
                            } catch (Exception e) {
                                MyLog.appendLog(DEBUG_KEY + "Exception 0640" + e.getMessage());
                            }
                        } else if (packets[count].startsWith("^0641") && packets[count].endsWith("|!")) {
                            if (!db.getCurrentScreenVal().equalsIgnoreCase("BID")) {
                                HashMap<String, String> callCenterData = db.getRunningJobData();
                                if (!callCenterData.get("jobStatus").equalsIgnoreCase("pick up")) {
                                    jobStatusRequest("0641");
                                } else {
                                    System.out.println("Inside the Else case in 41 cmd");
                                }
                            }
                        } else if (packets[count].startsWith("^0632") && packets[count].endsWith("|!")) {
                            try {
                                String currentScreen = db.getCurrentScreenVal();
                                if (db.getCurrentScreenVal().equalsIgnoreCase("MAIN_ACTIVITY")) {
                                    String[] parsedData = null;
                                    String[] parsedData1 = packets[count].split("(\\|)|(\\~)");
                                    if (!parsedData1[3].equalsIgnoreCase("URL")) {
                                        parsedData = packets[count].split("(\\|)|(\\:)|(\\~)");
                                    } else {
                                        parsedData = packets[count].split("(\\|)|(\\~)");
                                    }
                                    RunningJobDetails.setJobMessageTitle(parsedData[3]);//Bidding
                                    RunningJobDetails.setMessageContent(parsedData[6]);
                                    RunningJobDetails.setDriverTopUpMessage(parsedData[2]);
                                    if (RunningJobDetails.getJobMessageTitle().toLowerCase().contains("bid") && !currentScreen.equalsIgnoreCase("BIDDING") && !currentScreen.equalsIgnoreCase("BILL_ACTIVITY")) {
                                        db.updateCurrentScreenVal("BIDDING", 1);
                                        voiceSoundPlayer.playVoice(AppController.getInstance(), R.raw.alarm);
                                        // RunningJobDetails.setJobId(parsedData[2]);
                                        HashMap<String, String> callCenterData = new HashMap<String, String>();
                                        RunningJobDetails.setPickupLoc(parsedData[4]);//Pickup address
                                        RunningJobDetails.setDriverNote(parsedData[5]);//Driver note
                                        RunningJobDetails.setDropLoc(parsedData[6]);//Drop address
                                        RunningJobDetails.setPickupLatitude(parsedData[7]);//Pick up lat
                                        RunningJobDetails.setPickupLongitude(parsedData[8]);//pick up lon
                                        RunningJobDetails.setTimeOutCounter(Integer.parseInt(parsedData[11]));
                                        RunningJobDetails.setDistance(parsedData[12]);
                                        RunningJobDetails.setEta(parsedData[13]);

                                        callCenterData.put("JobId", parsedData[2]);
                                        callCenterData.put("mobileNo", "0");
                                        callCenterData.put("name", RunningJobDetails.getCustomerName());
                                        callCenterData.put("customerPickUpTime", RunningJobDetails.getEta());//Customer PickUp time = ETA for Bidding Jobs
                                        callCenterData.put("pickupLoc", RunningJobDetails.getPickupLoc());
                                        callCenterData.put("dropLoc", RunningJobDetails.getDropLoc());
                                        callCenterData.put("pickupLat", RunningJobDetails.getPickupLatitude());
                                        callCenterData.put("pickupLon", RunningJobDetails.getPickupLongitude());
                                        callCenterData.put("dropLat", RunningJobDetails.getDistance());//Drop Lat = Distance for Bidding Jobs
                                        callCenterData.put("dropLon", RunningJobDetails.getDriverNote());
                                        callCenterData.put("paymentMode", String.valueOf(RunningJobDetails.getTimeOutCounter()));//TimeOut
                                        callCenterData.put("jobStatus", "0");
                                        db.updateCallCenterJobValues(callCenterData, 1);

                                        Intent i = new Intent(PollingService.this, BiddingActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        i.addFlags(Intent.FLAG_FROM_BACKGROUND);
                                        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                        AppController.getInstance().startActivity(i);

                                    } else if (RunningJobDetails.getDriverTopUpMessage().equalsIgnoreCase("DRIVERTOPUP")) {
                                        String[] parseDriverData = parsedData[4].split("\\-");
                                        db.updateCreditBal(parseDriverData[1], parseDriverData[2], 1);
                                        db.updateConfigData(parseDriverData[1], parseDriverData[2], parseDriverData[4], 1);
                                        intent.putExtra("notifyDataChanged", parsedData[4]);
                                        RunningJobDetails.setJobMessageTitle(parsedData[3]);
                                        RunningJobDetails.setMessageContent(parsedData[3]);
                                        sendBroadcast(intent);
                                        Intent i = new Intent(PollingService.this, MessageTopDialog.class);
                                        i.putExtra("notifyDataChanged", RunningJobDetails.getMessageContent());
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_FROM_BACKGROUND);
                                        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                        AppController.getInstance().startActivity(i);

                                    } else if (RunningJobDetails.getJobMessageTitle().equalsIgnoreCase("MESSAGE")) {
                                        Intent i = new Intent(PollingService.this, MessageTopDialog.class);
                                        i.putExtra("notifyDataChanged", RunningJobDetails.getMessageContent());
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_FROM_BACKGROUND);
                                        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                        AppController.getInstance().startActivity(i);
                                    } else if (RunningJobDetails.getJobMessageTitle().equalsIgnoreCase("URL")) {

                                        RunningJobDetails.setJobMessageTitle(parsedData[3]);
                                        RunningJobDetails.setMessageContent(parsedData[4]);
                                        Intent i = new Intent(PollingService.this, MessageTopDialog.class);
                                        i.putExtra("notifyDataChanged", RunningJobDetails.getMessageContent());
                                        i.putExtra("buttonVal", parsedData[5]);
                                        i.putExtra("url", parsedData[6]);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_FROM_BACKGROUND);
                                        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                        AppController.getInstance().startActivity(i);
                                    }
                                } else {
                                    String[] parsedData = packets[count].split("(\\|)|(\\:)|(\\~)");
                                    RunningJobDetails.setJobMessageTitle(parsedData[3]);//Bidding
                                    RunningJobDetails.setMessageContent(parsedData[6]);
                                    RunningJobDetails.setDriverTopUpMessage(parsedData[2]);
                                    if (RunningJobDetails.getDriverTopUpMessage().equalsIgnoreCase("DRIVERTOPUP")) {
                                        String[] parseDriverData = parsedData[4].split("\\-");
                                        db.updateCreditBal(parseDriverData[1], parseDriverData[2], 1);
                                        db.updateConfigData(parseDriverData[1], parseDriverData[2], parseDriverData[3], 1);
                                        intent.putExtra("notifyDataChanged", parsedData[4]);
                                        RunningJobDetails.setJobMessageTitle(parsedData[3]);
                                        RunningJobDetails.setMessageContent(parsedData[3]);
                                        sendBroadcast(intent);

                                        Intent i = new Intent(PollingService.this, MessageTopDialog.class);
                                        i.putExtra("notifyDataChanged", RunningJobDetails.getMessageContent());
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_FROM_BACKGROUND);
                                        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                        AppController.getInstance().startActivity(i);

                                    } else if (RunningJobDetails.getJobMessageTitle().equalsIgnoreCase("MESSAGE")) {
                                        Intent i = new Intent(PollingService.this, MessageTopDialog.class);
                                        i.putExtra("notifyDataChanged", RunningJobDetails.getMessageContent());
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_FROM_BACKGROUND);
                                        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                        AppController.getInstance().startActivity(i);
                                    }
                                }
                            } catch (Exception e) {
                                MyLog.appendLog(DEBUG_KEY + "Exception 0632" + e.getMessage());
                            }
                        } else if (packets[count].startsWith("^0655") && packets[count].endsWith("|!")) {
                            //This is for Driver Deactivation
                            try {
                                if (db.getCurrentScreenVal().equalsIgnoreCase("MAIN_ACTIVITY")) {
                                    if (NetworkStatus.isInternetPresent(AppController.getInstance()).equals("On")) {
                                        String[] driverLoginCredentials = packets[count].split("\\|");
                                        logoutRequest("Deactivation", "Device-Deactivated");
                                    } else {
                                        NetworkCheckDialog.showConnectionTimeOut(AppController.getInstance());
                                    }
                                }
                            } catch (Exception e) {
                                MyLog.appendLog(DEBUG_KEY + "Exception 0655" + e.getMessage());
                            }
                        } else if (packets[count].startsWith("^0656") && packets[count].endsWith("|!")) {
                            //This is for Made device free
                            try {
                                String currentScreenVal = db.getCurrentScreenVal();
                                if (currentScreenVal.equalsIgnoreCase("CALL_CENTER") || currentScreenVal.equalsIgnoreCase("KERB_SCREEN")
                                        || currentScreenVal.equalsIgnoreCase("BILL_ACTIVITY")) {
                                    RunningJobDetails.setDefaultValues();
                                    db.updateCurrentJobValues(UpdateCurrentJobData.updateJobData(), 1);
                                    Intent mainActivity = new Intent(AppController.getInstance(), MainActivity.class);
                                    mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    mainActivity.addFlags(Intent.FLAG_FROM_BACKGROUND);
                                    mainActivity.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                    AppController.getInstance().startActivity(mainActivity);

                                    Intent i = new Intent(PollingService.this, MessageTopDialog.class);
                                    i.putExtra("notifyDataChanged", "We have manually closed the Job.");
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_FROM_BACKGROUND);
                                    i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                    AppController.getInstance().startActivity(i);

                                }
                            } catch (Exception e) {
                                MyLog.appendLog(DEBUG_KEY + "Exception 0655" + e.getMessage());
                            }
                        } else if (packets[count].startsWith("^0657") && packets[count].endsWith("|!")) {
                            //This is for Driver login Request
                            try {
                                if (db.getCurrentScreenVal().equalsIgnoreCase("DRIVER_LOGIN")) {
                                    if (NetworkStatus.isInternetPresent(AppController.getInstance()).equals("On")) {
                                        String[] driverLoginCredentials = packets[count].split("\\|");
                                        driverLoginRequest(driverLoginCredentials[2], driverLoginCredentials[3]);
                                    } else {
                                        NetworkCheckDialog.showConnectionTimeOut(AppController.getInstance());
                                    }
                                }
                            } catch (Exception e) {
                                MyLog.appendLog(DEBUG_KEY + "Exception 0655" + e.getMessage());
                            }
                        } else if (packets[count].startsWith("^0658") && packets[count].endsWith("|!")) {
                            //This is for Driver logout
                            try {
                                String currentVal = db.getCurrentScreenVal();
                                if (!currentVal.equalsIgnoreCase("CALL_CENTER") || !currentVal.equalsIgnoreCase("KERB_SCREEN")
                                        || !currentVal.equalsIgnoreCase("BILL_ACTIVITY")) {
                                    if (NetworkStatus.isInternetPresent(AppController.getInstance()).equals("On")) {
                                        logoutRequest("Logout", "Logout from Web App");
                                    } else {
                                        NetworkCheckDialog.showConnectionTimeOut(AppController.getInstance());
                                    }
                                }
                            } catch (Exception e) {
                                MyLog.appendLog(DEBUG_KEY + "Exception 0658" + e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.print("processInCommands while " + e);
                }
                count++;
            }
        } catch (Exception e) {
            System.out.print("processInCommands" + e);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        GpsData.setGpsStatus("On");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        GpsData.setGpsStatus("On");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;

    }

    protected void startLocationUpdates() {
        System.out.println("inside 8");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        System.out.println("inside 9");

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (session == null)
                session = new SessionManager(AppController.getInstance());
            session.setLatitude(location.getLatitude(), location.getLongitude());
            CLocation cLocation = new CLocation(location);

            MyLog.appendLog(DEBUG_KEY + "getDistance: Lat values 0 " + cLocation.getLatitude());
            Log.e(TAG, "getDistance: Lat values 0 " + cLocation.getLatitude());
            Log.e(TAG, "getDistance: Lng values 0 " + cLocation.getLongitude());


            if (GpsData.getGpsLatitude_Prev() != location.getLatitude() && GpsData.getGpsLongitude_Prev() != location.getLongitude()) {
                findLatLonDistance(GpsData.getGpsLatitude_Prev(), GpsData.getGpsLongitude_Prev(), GpsData.getLattiude(), GpsData.getLongitude());
                GpsData.setGpsLatitude_Prev(GpsData.getLattiude());
                GpsData.setGpsLongitude_Prev(GpsData.getLongitude());
            } else {
                GpsData.setGpsLatitude_Prev(location.getLatitude());
                GpsData.setGpsLongitude_Prev(location.getLongitude());
            }

            GpsData.setOldLocation(location);
            GpsData.setLatitude(location.getLatitude());
            GpsData.setLongitude(location.getLongitude());
            if (location.hasSpeed() && location.getAccuracy() < 100.0f) {
                //GpsData.setGpsSpeed(String.valueOf(location.getSpeed()));
                //calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph
                //speed = location.getSpeed() * 18 / 5;
                GpsData.setGpsSpeed(String.valueOf(location.getSpeed() * 18 / 5));
            } else {
                GpsData.setGpsSpeed("0");
            }
            System.out.println("Inside the onLocation Changed==>" + GpsData.getLatitude() + "<===>" + GpsData.getLongitude() + "Speed" + GpsData.getGpsSpeed());

        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "onLocationChanged" + e.getMessage());
        }
    }

    private void findLatLonDistance(double prevlat, double prevlon, double lat, double lon) {
        try {
            double lat1 = prevlat;
            double lon1 = prevlon;
            double lat2 = lat;
            double lon2 = lon;

            if (lat1 != 0.0 && lon1 != 0.0 && lat2 != 0.0 && lon2 != 0.0) {
                float[] distance = new float[2];
                Location.distanceBetween(lat1, lon1, lat2, lon2, distance);
                try {
                    if (pulseManager.isPulse()) {
                        GpsData.setGpsOdometer(pulseManager.getDoubleOdometer());
                    } else {
                        GpsData.setGpsOdometer((GpsData.getGpsOdometer() * 1000 + (distance[0])) / 1000);
                    }
                    System.out.println("Running Polling Inside the getStartGPSOdo==>" + GpsData.getGpsOdometer());
                } catch (Exception Eww) {
                    if (pulseManager.isPulse()) {
                        GpsData.setGpsOdometer(pulseManager.getDoubleOdometer());
                    }
                }
                session = new SessionManager(AppController.getInstance());
                session.createGPSOdoSession();
            }
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "findLatLonDistance" + e.getMessage());
        }
    }

    public String getcurrentTimeToDisplay() {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d ",
                now.year, (now.month + 1), now.monthDay, now.hour, now.minute, now.second);
        return currentTime;
    }

    public void setClientInfo() {
        try {
            HashMap<String, String> clientInfo = db.getClientInfo();
            ConfigData.setPollingUrl(clientInfo.get("pollingUrl"));
            ConfigData.setPollingInterVal(clientInfo.get("PollingInterval"));
            ConfigData.setClientName(clientInfo.get("ClientName"));
            ConfigData.setClientImageURL(clientInfo.get("clientImg"));
            ConfigData.setClientURL(clientInfo.get("clientURL"));
            Utils.setImei_no(clientInfo.get("imei"));
            System.out.println("Inside Service the PollingURL==>" + ConfigData.getPollingUrl());
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "setClientInfo" + e.getMessage());
        }
    }

    public void logoutRequest(final String title, final String content) {
        HashMap<String, String> main_screen_data = db.getMainScreenData();
        String url = ConfigData.getClientURL() + APIClass.logtout_Api;
        Map<String, String> params = new HashMap<String, String>();
        params.put("imeiNo", Utils.getImei_no());
        params.put("driverId", ConfigData.getDriverId());
        params.put("shiftId", main_screen_data.get("shiftId"));
        params.put("shiftDist", main_screen_data.get("shiftDist"));
        params.put("noOfTrips", main_screen_data.get("completedJobs"));
        params.put("totlaShiftAMT", main_screen_data.get("todayEarnings"));
        params.put("hiredKM", "");
        params.put("lat", String.valueOf(GpsData.getLattiude()));
        params.put("lon", String.valueOf(GpsData.getLongitude()));
        params.put("dateTime", getDateForRequests());
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 0) {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        CreateNotification(title, content);
                        db.updateMainScreenData("0", "0", "0", "0", "0", 1);
                        Intent i = new Intent(AppController.getInstance(), DriverLogin.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_FROM_BACKGROUND);
                        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        AppController.getInstance().startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Time out.Please Try again", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(req);
    }

    public String getDateForRequests() {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        String currentTime = String.format("%02d-%02d-%02d %02d:%02d:%02d", now.year, (now.month + 1), now.monthDay, now.hour, now.minute, now.second);
        return currentTime;
    }

    public void CreateNotification(String Title, String Text) {
        try {
            int notificationId = 37;
            androidx.core.app.NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(AppController.getInstance())
                            .setSmallIcon(R.drawable.infodispatch_appicon)
                            .setContentTitle(Title)
                            .setContentText(Text);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationId, mBuilder.build());
            HardwareControls.backlightON(AppController.getInstance());
        } catch (Exception Eww) {
            Log.e(DEBUG_TAG, "!! Exception !! Notification:" + Eww.toString());
        }
    }

    public void driverLoginRequest(String driverId, String Password) {
        String url = ConfigData.getClientURL() + APIClass.driver_login_Api;
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put("imeiNo", Utils.getImei_no());
            params.put("loginId", driverId);
            params.put("password", Password);
            params.put("dateTime", getDateForRequests());
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
        }
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println("Response driver-->" + response);
                    if (response.getInt("status") == 0) {
                        voiceSoundPlayer.playVoice(AppController.getInstance(), R.raw.loginfailed);
                    } else {
                        voiceSoundPlayer.playVoice(AppController.getInstance(), R.raw.loginsuccessful);
                        HashMap<String, String> config_data = new HashMap<String, String>();
                        config_data.put("fixedFare", response.optString("fixedFare"));
                        config_data.put("baseKms", response.optString("baseKms"));
                        config_data.put("baseHours", response.optString("baseHours"));
                        config_data.put("extraKmRate", response.optString("extraKmRate"));
                        config_data.put("extraHourRates", response.optString("extraHourRates"));
                        config_data.put("waitingFreeMinutes", response.optString("waitingFreeMin"));

                        config_data.put("pollingInterval", response.optString("pollingInterval"));
                        config_data.put("clientName", response.optString("clientName"));

                        config_data.put("waitingRate", response.optString("waitingRate"));
                        config_data.put("currencyType", response.optString("currencyType"));
                        config_data.put("nightChargesinPercentage", response.optString("nightChargesinPercentage"));
                        config_data.put("nightFareTimeFrom", response.optString("timeFrom"));
                        config_data.put("nightFareTimeTo", response.optString("timeTo"));
                        config_data.put("hotSpotStatus", response.optString("hotSpotStatus"));
                        config_data.put("intervalOfAds", response.optString("intervalOfAds"));
                        config_data.put("adsStatus", response.optString("adsStatus"));
                        config_data.put("paymentGatewayNeeded", response.optString("paymentGatewayNeeded"));
                        config_data.put("creditBalance", response.optString("creditBalance"));
                        config_data.put("walletBalance", response.optString("walletBalance"));
                        config_data.put("driverId", ConfigData.getDriverId());
                        config_data.put("availableBalance", response.optString("walletBalance"));

                        if (response.has("minDistance"))
                            config_data.put("minDistance", response.optString("minDistance"));
                        else
                            config_data.put("minDistance", "0");
                        if (response.has("minAmt"))
                            config_data.put("minAmount", response.optString("minAmt"));
                        else
                            config_data.put("minAmount", response.optString("fixedFare"));

                        ConfigData.setFixedFare(config_data.get("fixedFare"));
                        ConfigData.setBaseKm(Long.parseLong(config_data.get("baseKms")));
                        ConfigData.setBaseHours(Double.parseDouble(config_data.get("baseHours")));
                        ConfigData.setExtraKmRate(Double.parseDouble(config_data.get("extraKmRate")));
                        ConfigData.setExtraHourRate(Double.parseDouble(config_data.get("extraHourRates")));
                        ConfigData.setDriverId(ConfigData.getDriverId());

                        ConfigData.setMinDistance(Double.parseDouble(config_data.get("minDistance")));
                        ConfigData.setMinAmount(Double.parseDouble(config_data.get("minAmount")));

                        if (db.getConfigValuesCount() == 0)
                            db.insertConfigValues(config_data);
                        else
                            db.updateConfigValues(config_data, 1);
                        if (db.getMainScreenDataCount() == 0)
                            db.insertMainScreenData(getCurrentDateTime(), "0", "0", "0", "0", "1", "0", response.optString("creditBalance"), response.optString("walletBalance"));
                        else {
                            shiftCount = db.getShiftId() + 1;
                            db.updateCreditBal(response.optString("creditBalance"), response.optString("walletBalance"), 1);
                            db.updateShitIdLoginTime(String.valueOf(shiftCount), getCurrentDateTime(), 1);
                        }
                        Intent mainActivity = new Intent(AppController.getInstance(), MainActivity.class);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mainActivity.addFlags(Intent.FLAG_FROM_BACKGROUND);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        AppController.getInstance().startActivity(mainActivity);
                    }
                } catch (Exception e) {
                    MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        AppController.getInstance().addToRequestQueue(req);
    }

    public String getCurrentDateTime() {
        String strDate = null;
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy  HH:mm:ss");
            strDate = sdf.format(c.getTime());
        } catch (Exception e) {
            MyLog.appendLog(DEBUG_KEY + "" + e.getMessage());
        }
        return strDate;
    }

    public void jobStatusRequest(final String cmd) {

        HashMap<String, String> main_screen_data = db.getMainScreenData();
        String url = ConfigData.getClientURL() + APIClass.jobs_status_Api;
        Map<String, String> params = new HashMap<String, String>();
        params.put("imeiNo", Utils.getImei_no());
        params.put("jobId", RunningJobDetails.getJobId());
        if (cmd.equals("0641")) {
            params.put("jobStatus", String.valueOf(RunningJobDetails.Job_Status.jobstatus_undefined_7.ordinal()));
        } else {
            params.put("jobStatus", String.valueOf(RunningJobDetails.Job_Status.jobstatus_completed.ordinal()));
        }
        params.put("loginId", ConfigData.getDriverId());
        params.put("shiftId", main_screen_data.get("shiftId"));
        params.put("lat", String.valueOf(GpsData.getLattiude()));
        params.put("lon", String.valueOf(GpsData.getLongitude()));
        params.put("speed", "0");
        params.put("dateTime", getDateForRequests());

        Log.e(TAG,"Request URL :" + url);
        Log.e(TAG,"Request params :" + params);

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.getInt("status") == 0) {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        int cancelledJobs = 0, completedJobs = 0;
                        db.updateCurrentScreenVal("MAIN_ACTIVITY", 1);
                        HashMap<String, String> mainScreenData = db.getMainScreenData();
                        double shiftDist = Double.parseDouble(mainScreenData.get("shiftDist")) + Double.parseDouble(RunningJobDetails.getTotalTripDist());
                        double earnedTime = Double.parseDouble(mainScreenData.get("earnedTime")) + Double.parseDouble(RunningJobDetails.getTotalTripDuration());
                        double todayEarnings = Double.parseDouble(mainScreenData.get("todayEarnings")) + RunningJobDetails.getTotalTripFare();
                        if (cmd.equals("0641")) {
                            jobCancelType = "Job has been cancelled";
                            RunningJobDetails.setMessageContent(jobCancelType);
                            cancelledJobs = Integer.parseInt(mainScreenData.get("cancelledJobs")) + 1;
                            db.updateShiftData(String.valueOf(shiftDist),
                                    String.valueOf(mainScreenData.get("completedJobs")), String.valueOf(cancelledJobs),
                                    String.valueOf(earnedTime), String.valueOf(todayEarnings), 1);
                        } else {
                            jobCancelType = "Job has been closed";
                            RunningJobDetails.setMessageContent(jobCancelType);
                            completedJobs = Integer.parseInt(mainScreenData.get("completedJobs")) + 1;
                            db.updateShiftData(String.valueOf(shiftDist),
                                    String.valueOf(completedJobs), String.valueOf(mainScreenData.get("cancelledJobs")),
                                    String.valueOf(earnedTime), String.valueOf(todayEarnings), 1);
                        }
                        HashMap<String, String> runningJobData = db.getRunningJobData();
                        db.insertHistoryData(runningJobData);
                        db.updatePromo("Promo Code", 1);
                        RunningJobDetails.setPromoCode("Promo Code");
                        RunningJobDetails.setTotalTripFare(0.0);

                        Intent i = new Intent(AppController.getInstance(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_FROM_BACKGROUND);
                        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        AppController.getInstance().startActivity(i);

                        Intent i2 = new Intent(PollingService.this, MessageTopDialog.class);
                        i2.putExtra("notifyDataChanged", RunningJobDetails.getMessageContent());
                        i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i2.addFlags(Intent.FLAG_FROM_BACKGROUND);
                        i2.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        AppController.getInstance().startActivity(i2);
                        RunningJobDetails.setDefaultValues();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Time out.Please Try again", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        AppController.getInstance().addToRequestQueue(req);
    }

    public void updateACK(final String cmd) {

        HashMap<String, String> main_screen_data = db.getMainScreenData();
        String url = ConfigData.getClientURL() + "/SubmitDeviceReturnMessage";
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobileID", Utils.getImei_no());
        params.put("cmdId", "0624");
        params.put("webServiceMessage", ack.frame_24_cmd(cmd));
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("status") == 0) {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Time out.Please Try again", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        AppController.getInstance().addToRequestQueue(req);
    }

    class gpioThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.i(TAG, "------gpioThread-start------");

            if (gpio_info.open_gpio() < 0) {
                MyLog.appendLog(DEBUG_KEY + "open gpio fail");
                Log.e(TAG, "open gpio fail");
            } else {
                // IF VEHICLE IS NOT ON TRIP.STOP OUTPUT VOLTAGE.
                try {
                    if (ConfigData.getDeviceStatus() == 0) {
                        if (gpio_info.get_gpio_data("P3B6") == 0)
                            gpio_info.set_gpio_data("P3B6", 1);
                    } else {
                        if (gpio_info.get_gpio_data("P3B6") == 1)
                            gpio_info.set_gpio_data("P3B6", 0);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "GPIO ERROR" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                while (true) {
                    if (ifdestroy) {
                        break;
                    }
                    try {
                        //Log.d(TAG,"get gpio : "+ gpioStrings[i]+" value");
                        //if (i == 8) {
                        //	gpio_value = mAcGpio;
                        //} else {
                        gpio_value = gpio_info.get_gpio_data(gpioStrings[i]);
                        //}

                        if (gpio_value == 0) {
                            gpioValueState = "low";
                        } else if (gpio_value == 1) {
                            gpioValueState = "high";
                        } else {
                            gpioValueState = "XX";
                        }
                        Message msg = handler.obtainMessage();
                        msg.obj = gpioValueState;
                        msg.what = i;
                        handler.sendMessage(msg);
                        i++;
                        if (10 == i)
                            i = 0;

                        sleep(500);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        EmergencyAlert emergencyAlert;

        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 5:
                    //Panic Button-108
                    String msg1 = (String) msg.obj;
//                    Log.i(TAG, "handleMessage: Message 1" + msg1);
                    if (msg1.equalsIgnoreCase("high")) {
                        emergencyAlert = new EmergencyAlert();
                        emergencyAlert.emergency_alert(PollingService.this, "108", "SERVICE");
                    }
                    break;
                case 6:
                    //Customer Panic-108
                    String msg2 = (String) msg.obj;
//                    Log.i(TAG, "handleMessage: Message 2" + msg2);
                    if (msg2.equalsIgnoreCase("high")) {
                        emergencyAlert = new EmergencyAlert();
                        emergencyAlert.emergency_alert(getApplicationContext(), "109", "SERVICE");
                    }
                    //Panic Button1
                    break;
                case 7:
                    String msg3 = (String) msg.obj;
//                    Log.i(TAG, "handleMessage: Message 3" + msg3);
                    if (msg3.equalsIgnoreCase("high")) {
                        GpsData.setSeatSensor(1);
                    } else {
                        GpsData.setSeatSensor(0);
                    }
                    break;
                default:
                    break;
            }
        }
    };


//    class recDataThread extends Thread {
//        byte[] readdata = new byte[1024];
//        int readlen = 1024;
//
//        public void run() {
//            while (ifopensuccess) {
//                if (ifopensuccess == false) {
//                    System.out.println("------Close Rece Thread------");
//                    break;
//                }
//                try {
//                    readsize = serialService.serialRead(fd, readdata, readlen);
//                    if (readsize > 0) {
//                        System.out.println("------readSize:" + String.valueOf(readsize) + "------");
//
//                        byte[] tempBytes = new byte[readsize];
//                        for (int i = 0; i < tempBytes.length; i++) {
//                            tempBytes[i] = readdata[i];
//                        }
//
//                        String recvdataString = new String(tempBytes, "GBK");
////                        recvdataString = hex2DebugHexString(tempBytes);
//                        recvdataString.trim();
//                        if (recvdataString.length() > 5)
//                            recvdataString = recvdataString.substring(0, 5);
//                        Log.e(TAG, "run: " + recvdataString);
//                        Message msg = new Message();
//                        msg.obj = recvdataString;
//                        msg.what = 2;
//                        serialPorthandler.sendMessage(msg);
//                    }
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    public static String hex2DebugHexString(byte[] b) {
//        int len = b.length;
//        int[] x = new int[len];
//        String[] y = new String[len];
//        StringBuilder str = new StringBuilder();
//        int j = 0;
//        for (; j < len; j++) {
//            x[j] = b[j] & 0xff;
//            y[j] = Integer.toHexString(x[j]);
//            while (y[j].length() < 2) {
//                y[j] = "0" + y[j];
//            }
//            str.append(y[j]);
//            str.append(" ");
//        }
//        return new String(str).toUpperCase();
//    }

//    private void open_serialPort() {
//        try {
//            // TODO Auto-generated method stub
//            if ((fd = serialService.serialOpen("//dev//ttyS2")) < 0) {
//                ifopensuccess = false;
//                System.out.println("----open serialPort error-----");
//                Toast.makeText(PollingService.this, "open serialPort fail!!!",
//                        Toast.LENGTH_SHORT).show();
//            } else {
//                String baudRate = "115200";
//                String databits = "8";
//                String parity = "0";
//                String stopbits = "1";
//                System.out.println("------fd：" + fd + "------" + baudRate);
//                if (serialService.serialPortSetting(
//                        fd,
//                        Integer.parseInt(baudRate),
//                        Integer.parseInt(databits),
//                        Integer.parseInt(parity),
//                        Integer.parseInt(stopbits)
//                ) < 0) {
//                    System.out.println("----open serialPort error-----");
//                    Toast.makeText(PollingService.this, "open serialPort fail!!!",
//                            Toast.LENGTH_SHORT).show();
//                    ifopensuccess = false;
//                } else {
//                    System.out.println("----open serialPort success-----");
//                    //openComButton.setText("close serialport");
//                    ifopensuccess = true;
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "open_serialPort: " + e.getMessage());
//        }
//    }


    @Override
    public void onDestroy() {
//        unBindDatahubService();
//
//        if (ifopensuccess = true) {
//            MyLog.appendLog(DEBUG_TAG + "ifOpenSuccess onDestroy");
//            isAuto = false;
//            ifopensuccess = false;
//            serialService.serialClose(fd);
//        }

        try {
            if (socket1 != null && !socket1.isClosed()) {
                socket1.close();
            }
            if (socket2 != null && !socket2.isClosed()) {
                socket2.close();
            }
            if (serverSocket1 != null && !serverSocket1.isClosed()) {
                serverSocket1.close();
            }
            if (serverSocket2 != null && !serverSocket2.isClosed()) {
                serverSocket2.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: ");
        }
        super.onDestroy();
    }

    class dataThread extends Thread {
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
            message = pulseManager.getJsonObject();
            if (socket1 != null && socket1.isConnected()) {
//                Log.e(TAG, "infotainmentParams run: JSON 1 " + message);
                output1.write(message + "\n");
                output1.flush();
            }

            if (socket2 != null && socket2.isConnected()) {
//                Log.e(TAG, "infotainmentParams run: JSON 2 " + message);
                output2.write(message + "\n");
                output2.flush();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "run: " + e.getMessage());
            }
        }
    }

    class ServerSendThread1 implements Runnable {
        @Override
        public void run() {
            try {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                try {
                    serverSocket1 = new ServerSocket(SERVER_PORT1);
                } catch (Exception e) {
                    Log.e(TAG, "run: 1 " + e.getMessage());
                }
                if (null != serverSocket1) {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            socket1 = serverSocket1.accept();
                            output1 = new PrintWriter(socket1.getOutputStream());
                            new Thread(new dataThread()).start();
                        } catch (IOException e) {
                            //Log.e(TAG, "run: 1 " + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "run: 1 " + e.getMessage());
            }
        }
    }

    class ServerSendThread2 implements Runnable {

        @Override
        public void run() {
            try {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                serverSocket2 = new ServerSocket(SERVER_PORT2);
            } catch (Exception e) {
                Log.e(TAG, "run: 2 " + e.getMessage());
            }
            if (null != serverSocket2) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        socket2 = serverSocket2.accept();
                        output2 = new PrintWriter(socket2.getOutputStream());
                        new Thread(new dataThread()).start();
                    } catch (IOException e) {
                        //Log.e(TAG, "run: 2 " + e.getMessage());
                    }
                }
            }

        }
    }

}


