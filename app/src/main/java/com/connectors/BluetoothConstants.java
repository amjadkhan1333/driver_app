package com.connectors;/*

/**
 * Defines several constants used between {@link BluetoothChatService} and the UI.
 */
public interface BluetoothConstants {

    // Message types sent from the info.bluetoothex.BluetoothChatService Handler

    public static final String UUID_STRING_WELL_KNOWN_SPP ="00001101-0000-1000-8000-00805F9B34FB";
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the info.bluetoothex.BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

}
