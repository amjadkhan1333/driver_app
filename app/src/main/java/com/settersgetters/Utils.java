package com.settersgetters;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by AK on 12/15/2016.
 */

public class Utils {

    public static String imei_no;

//    public static String getImei_no() {
//        return imei_no;
//    }
//
//    public static void setImei_no(String imei_no) {
//        Utils.imei_no = imei_no;
//    }

//    public static String getImei_no() {
//        return "352356076439231";
//    }
//    public static void setImei_no(String imei_no) {
//        Utils.imei_no = "352356076439231";
//    }

    public static String getImei_no() {
        return "351866090838011";
    }
    public static void setImei_no(String imei_no) {
        Utils.imei_no = "351866090838011";
    }

//    public static String getImei_no() {
//        return "860548040052810";
//    }
//    public static void setImei_no(String imei_no) {
//        Utils.imei_no = "860548040052810";
//    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes toConvert
     * @return hexValue
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (int idx = 0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     *
     * @param str which to be converted
     * @return array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename which to be converted to string
     * @return String value of File
     * @throws java.io.IOException if error occurs
     */
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int BUFLEN = 1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read, count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) buf.append(String.format("%02X:", aMac));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
        /*try {
        // this is so Linux hack
        return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
        return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }
    public static String millisecondToFullTime(long millisecond) {
        return timeUnitToFullTime(millisecond, TimeUnit.MILLISECONDS);
    }

    public static String secondToFullTime(long second) {
        return timeUnitToFullTime(second, TimeUnit.SECONDS);
    }

    public static String timeUnitToFullTime(long time, TimeUnit timeUnit) {
        long day = timeUnit.toDays(time)
                ;
        long hour = timeUnit.toHours(time)
                % 24;
        long minute = timeUnit.toMinutes(time)
                % 60;
        long second = timeUnit.toSeconds(time)
                % 60;

        if (day > 0) {
            return String.format("%dday %02d:%02d:%02d", day, hour, minute, second);
        } else if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else if (minute > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
    }

    public static int isOdd(int num)
    {
        return num & 0x1;
    }

    public static int HexToInt(String inHex)
    {
        return Integer.parseInt(inHex, 16);
    }

    public static byte HexToByte(String inHex)
    {
        return (byte)Integer.parseInt(inHex,16);
    }

    public static String Byte2Hex(Byte inByte)
    {
        return String.format("%02x", inByte).toUpperCase();
    }

    public static String ByteArrToHex(byte[] inBytArr)
    {
        StringBuilder strBuilder=new StringBuilder();
        int j=inBytArr.length;
        for (int i = 0; i < j; i++)
        {
            strBuilder.append(Byte2Hex(inBytArr[i]));
            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

    public static String ByteArrToHex(byte[] inBytArr,int offset,int byteCount)
    {
        StringBuilder strBuilder=new StringBuilder();
        int j=byteCount;
        for (int i = offset; i < j; i++)
        {
            strBuilder.append(Byte2Hex(inBytArr[i]));
        }
        return strBuilder.toString();
    }

    public static byte[] HexToByteArr(String inHex)
    {
        int hexlen = inHex.length();
        byte[] result;
        if (isOdd(hexlen)==1)
        {
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2)
        {
            result[j]=HexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }

    public static byte[] IntTobyteArrayLH(int n)
    {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static int ByteArrayToIntLH(byte[] b)
    {
        int res = 0;
        for(int i=0;i<b.length;i++){
            res += (b[i] & 0xff) << (i*8);
        }
        return res;
    }

    public static byte[] IntTobyteArrayHH(int n)
    {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }



    public static int ByteArrayToIntHH(byte[] b)
    {
        int res = 0;
        for(int i=0;i<b.length;i++){
            res += (b[i] & 0xff) << ((3-i)*8);
        }
        return res;
    }


    public static byte[] ShortTobyteArrayLH(short n)
    {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        return b;
    }

    public static short ByteArrayToShortLH(byte[] b)
    {
        short res = 0;
        for(int i=0;i<b.length;i++){
            res += (b[i] & 0xff) << (i*8);
        }
        return res;
    }

    public static byte[] ShortTobyteArrayHH(short n)
    {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) (n >> 8 & 0xff);
        return b;
    }

    public static short ByteArrayToShortHH(byte[] b)
    {
        short res = 0;
        for(int i=0;i<b.length;i++){
            res += (b[i] & 0xff) << ((1-i)*8);
        }
        return res;
    }
}
