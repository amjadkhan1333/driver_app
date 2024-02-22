package com.connectors;

/**
 * Created by AK on 11/7/2017.
 */

public class Constants {
    public static String myString = "";
    public static boolean connected=true;
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int KERB_REQUEST_ENABLE_BT = 11;
    //Commands to communicate with meter.
    public static final String START_JOB="|MH";
    public static final String STOP_JOB="|MF";
    public static final String ENABLE_METER="|CE";
    public static final String DISABLE_METER="|CD";
    public static final String PRINT_BILL="|PR";
    public static final String REQUEST_FARE="|RF";
    public static final int MESSAGE_READ=1;
    public static String connectedPrinter="NONE";
   /* public static String billFormat="<table width='100%' border='0' style='border-style:none'>\n" +
            "\n" +
            "<tr bgcolor=#C6C7CA>\n" +
            "   <td style='background:#C6C7CA'>Guest Name</td>\n" +
            "   <td>gggyyuhhuuu</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#EDEEEE>\n" +
            "   <td style='background:#EDEEEE'>Invoice No</td>\n" +
            "   <td>K1</td></tr>\n" +
            "<tr bgcolor=#C6C7CA>\n" +
            "   <td style='background:#C6C7CA'>Taxi No</td>\n" +
            "   <td></td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#EDEEEE>\n" +
            "   <td style='background:#EDEEEE'>Taxi Type</td>\n" +
            "   <td></td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#C6C7CA>\n" +
            "   <td style='background:#C6C7CA'>Driver Name</td>\n" +
            "   <td>TestSky</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#EDEEEE>\n" +
            "   <td style='background:#EDEEEE'>Driver Mobile No</td>\n" +
            "   <td>1111111111</td>\n" +
            "<tr bgcolor=#C6C7CA>\n" +
            "   <td style='background:#C6C7CA'>Trip Start</td>\n" +
            "   <td>Sep 25 2018  2:59PM</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#EDEEEE>\n" +
            "   <td style='background:#EDEEEE'>Trip End</td>\n" +
            "   <td>Sep 25 2018  3:00PM</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#C6C7CA>\n" +
            "   <td style='background:#C6C7CA'>Billing Type</td>\n" +
            "   <td>NA</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#EDEEEE>\n" +
            "   <td style='background:#EDEEEE'>Trip Distance</td>\n" +
            "   <td style='text-align:right'>0&nbsp;(Km)</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#C6C7CA>\n" +
            "   <td style='background:#C6C7CA'>Trip Duration</td>\n" +
            "   <td style='text-align:right'>1</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#EDEEEE>\n" +
            "   <td style='background:#EDEEEE'>Package</td>\n" +
            "   <td style='text-align:right'>Kerb day</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#C6C7CA>\n" +
            "   <td  style='background:#C6C7CA'>Fare</td>\n" +
            "   <td style='text-align:right'>40&nbsp;(&#x20B9;)</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#EDEEEE>\n" +
            "   <td  style='background:#EDEEEE'>Wait Fare</td>\n" +
            "   <td style='text-align:right'>0&nbsp;(&#x20B9;)</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#C6C7CA>\n" +
            "   <td style='background:#C6C7CA'>Extras</td>\n" +
            "   <td style='text-align:right'>0&nbsp;(&#x20B9;)</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#EDEEEE>\n" +
            "   <td style='background:#EDEEEE'>Discount</td>\n" +
            "   <td style='text-align:right'>0&nbsp;(&#x20B9;)</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#C6C7CA>\n" +
            "   <td style='background:#C6C7CA'>CGST(@6 %)</td>\n" +
            "   <td style='text-align:right'>2.40&nbsp;(&#x20B9;)</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#EDEEEE>\n" +
            "   <td style='background:#EDEEEE'>SGST(@6 %)</td>\n" +
            "   <td style='text-align:right'>2.40&nbsp;(&#x20B9;)</td>\n" +
            "   </tr>\n" +
            "<tr bgcolor=#C6C7CA>\n" +
            "   <td style='background:#C6C7CA'>Total</td>\n" +
            "   <td style='text-align:right'>44.8&nbsp;(&#x20B9;)</td>\n" +
            "  </tr>\n" +
            "</table>";*/
     //public static String billFormat="<table width='100%' border='0' style='border-style:none'><tr><td align='center' style='color:#3b26e0;font-weight:bold'>SkyCabs<br\\/> Trip Details<tr><td><table width='100%' border='0' style='border-style:none'><tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Guest Name<td>gggyyuhhuuu<tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Invoice No<td>K1<tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Taxi No<td><tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Taxi Type<td><TaxiType><tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Driver Name<td>TestSky<tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Driver Mobile No<td>1111111111<tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Trip Start<td>Sep 25 2018  2:59PM<tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Trip End<td>Sep 25 2018  3:00PM<tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Billing Type<td>NA<tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Trip Distance<td style='text-align:right'>0&nbsp;(Km)<tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Trip Duration<td style='text-align:right'>1<tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Package<td style='text-align:right'>Kerb day<tr bgcolor=#C6C7CA><td  style='background:#C6C7CA'>Fare<td style='text-align:right'>40&nbsp;(&#x20B9;)<tr bgcolor=#EDEEEE><td  style='background:#EDEEEE'>Wait Fare<td style='text-align:right'>0&nbsp;(&#x20B9;)<tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Extras<td style='text-align:right'>0&nbsp;(&#x20B9;)<tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Discount<td style='text-align:right'>0&nbsp;(&#x20B9;)<tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>CGST(@6 %)<td style='text-align:right'>2.40&nbsp;(&#x20B9;)<tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>SGST(@6 %)<td style='text-align:right'>2.40&nbsp;(&#x20B9;)<tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Total<td style='text-align:right'>44.8&nbsp;(&#x20B9;)<tr bgcolor=#EDEEEE><td style='background:#EDEEEE' colspan='2'><table width='100%' border='0' style='border-style:none'<tr><td align='center' style='color:#3b26e0;font-weight:bold'>Thank You~ Please Visit Us Again";
       public static String billFormat="<header><h4 align='center' style='color:#3b26e0;font-weight:bold'>SkyCabs<br>TRIP DETAILS</h4></header><table width='100%' border='0' style='border-style:none'><tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Guest Name</td><td>uujjjj</td></tr><tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Invoice No</td><td>K14</td></tr><tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Taxi No</td><td></td></tr><tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Taxi Type</td><td><TaxiType></td></tr><tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Driver Name</td><td>TestSky</td></tr><tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Driver Mobile No</td><td>1111111111</td></tr><tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Trip Start</td><td>Oct 12 2018 11:47AM</td></tr><tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Trip End</td><td>Oct 12 2018 11:47AM</td></tr><tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Billing Type</td><td>NA</td></tr><tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Trip Distance</td><td style='text-align:right'>0(Km)</td></tr><tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Trip Duration</td><td style='text-align:right'>0</td></tr><tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Package</td><td style='text-align:right'>Kerb day</td></tr><tr bgcolor=#C6C7CA><td  style='background:#C6C7CA'>Fare</td><td style='text-align:right'>40(Rs.)</td></tr><tr bgcolor=#EDEEEE><td  style='background:#EDEEEE'>Wait Fare </td><td style='text-align:right'>0(Rs.)</td></tr><tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Parking Fee</td><td style='text-align:right'>50(Rs.)</td></tr><tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>Discount</td><td style='text-align:right'>0(Rs.)</td></tr><tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>CGST(@6 %)</td><td style='text-align:right'>2.40(Rs.)</td></tr><tr bgcolor=#EDEEEE><td style='background:#EDEEEE'>SGST(@6 %)</td><td style='text-align:right'>2.40(Rs.)</td></tr><tr bgcolor=#C6C7CA><td style='background:#C6C7CA'>Total</td><td style='text-align:right'>94.8(Rs.)</td></tr></table><footer align='center'>Thank You~ Please Visit Us Again</footer>";
}
