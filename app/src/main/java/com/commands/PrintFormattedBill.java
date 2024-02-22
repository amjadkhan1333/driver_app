package com.commands;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.connectors.BluetoothService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by AK on 11/27/2017.
 */

public class PrintFormattedBill {
    String billFormat = "";
    String my_header = "";
    String my_footer = "";
    String bill_content = "";
    String TAG = this.getClass().getSimpleName();

    final String[] itemsen = {"Print Init", "Print and Paper", "Standard ASCII font", "Compressed ASCII font", "Normal size",
            "Double high power wide", "Twice as high power wide", "Three times the high-powered wide", "Off emphasized mode", "Choose bold mode", "Cancel inverted Print", "Invert selection Print", "Cancel black and white reverse display", "Choose black and white reverse display",
            "Cancel rotated clockwise 90 °", "Select the clockwise rotation of 90 °", "Feed paper Cut", "Beep", "Standard CashBox",
            "Open CashBox", "Char Mode", "Chinese Mode", "Print SelfTest", "DisEnable Button", "Enable Button",
            "Set Underline", "Cancel Underline", "Hex Mode"};
    final byte[][] byteCommands = {
            {0x1b, 0x40, 0x0a},// 复位打印机
            {0x0a}, //打印并走纸
            {0x1b, 0x4d, 0x00},// 标准ASCII字体
            {0x1b, 0x4d, 0x01},// 压缩ASCII字体
            {0x1d, 0x21, 0x00},// 字体不放大
            {0x1d, 0x21, 0x11},// 宽高加倍
            {0x1d, 0x21, 0x22},// 宽高加倍
            {0x1d, 0x21, 0x33},// 宽高加倍
            {0x1b, 0x45, 0x00},// 取消加粗模式
            {0x1b, 0x45, 0x01},// 选择加粗模式
            {0x1b, 0x7b, 0x00},// 取消倒置打印
            {0x1b, 0x7b, 0x01},// 选择倒置打印
            {0x1d, 0x42, 0x00},// 取消黑白反显
            {0x1d, 0x42, 0x01},// 选择黑白反显
            {0x1b, 0x56, 0x00},// 取消顺时针旋转90°
            {0x1b, 0x56, 0x01},// 选择顺时针旋转90°
            {0x0a, 0x1d, 0x56, 0x42, 0x01, 0x0a},//切刀指令
            {0x1b, 0x42, 0x03, 0x03},//蜂鸣指令
            {0x1b, 0x70, 0x00, 0x50, 0x50},//钱箱指令
            {0x10, 0x14, 0x00, 0x05, 0x05},//实时弹钱箱指令
            {0x1c, 0x2e},// 进入字符模式
            {0x1c, 0x26}, //进入中文模式
            {0x1f, 0x11, 0x04}, //打印自检页
            {0x1b, 0x63, 0x35, 0x01}, //禁止按键
            {0x1b, 0x63, 0x35, 0x00}, //取消禁止按键
            {0x1b, 0x2d, 0x02, 0x1c, 0x2d, 0x02}, //设置下划线
            {0x1b, 0x2d, 0x00, 0x1c, 0x2d, 0x00}, //取消下划线
            {0x1f, 0x11, 0x03}, //打印机进入16进制模式
    };

    public String formatBillForVisionTek(ArrayList<String> labels, ArrayList<String> values) {
        my_header = "               KSA TAXI~42~4~1~1~0~2\n" +
                "                                      \n" +
                "             TRIP DETAILS~42~4~1~1~0~2\n" +
                "-----------------------------------~42~4~1~1~0~2\n";//42 is complete length of the printing paper;
        for (int i = 0; i < labels.size(); i++) {
            String val = "";
            int charLen = labels.get(i).length();
            int totalSpaceRmain = (16 - charLen);
            for (int j = 1; j < totalSpaceRmain; j++) {
                val = val + " ";
            }
            if (i == labels.size() - 1) {
                bill_content += "-----------------------------------~42~4~1~1~0~2\n";
            }
            bill_content += labels.get(i) + val + ": " + values.get(i) + "~42~4~1~1~0~2\n";
        }
//        my_footer=
//                "-----------------------------------~42~4~1~1~0~2\n"+
//                        "         Customer Declaration:~42~4~1~1~0~2\n"+
//                        "            I agree to pay~42~4~1~1~0~2\n"+
//                        "          The above mentioned~42~4~1~1~0~2\n"+
//                        "                charges.~42~4~1~1~0~2\n"+
//                        "-----------------------------------\n"+
//                        "         Thank you for choosing.~42~4~1~1~0~2\n"+
//                        "             RPMA Networks~42~4~1~1~0~2\n"+
//                        "          POWERED BY AK~42~4~1~1~0~2\n"+
//                        "      www.AKtelematics.com~42~4~1~1~0~2\n"+
//                        "\n";
        my_footer =
                "-----------------------------------~42~4~1~1~0~2\n" +
                        "               THANKS~42~4~1~1~0~2\n" +
                        "                                      \n" +
                        "       ===== CALL CENTER ====~42~4~1~1~0~2\n" +
                        "              800 1700~42~4~1~1~0~2\n" +
                        "           HAVE A NICE DAY~42~4~1~1~0~2\n" +
                        "\n";
        billFormat = my_header + bill_content + my_footer;
        System.out.println("Inside the Bill Formatter for VisionTek==>" + billFormat);

        return billFormat;

    }

    public void formatBillForHowenBTPrinter(ArrayList<String> labels, ArrayList<String> values, Bitmap bitmap, BluetoothService mService) {
        String line_feed = "--------------------------------\n";
        if (bitmap != null) {
            Command.ESC_Align[2] = 0x01;
            mService.write(Command.ESC_Align);
            byte[] data = PrintPicture.POS_PrintBMP(bitmap, 350, 0);
            mService.write(Command.ESC_Init);
            mService.write(Command.LF);
            mService.write(data);
            mService.write(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
            mService.write(PrinterCommand.POS_Set_Cut(1));
            mService.write(PrinterCommand.POS_Set_PrtInit());
        }

        my_header = "KSA TAXI\n" +
                "\n" +
                "TRIP DETAILS\n" +
                "--------------------------------\n";//42 is complete length of the printing paper;
        mService.write(PrinterCommand.POS_Print_Text(line_feed, "GBK", 0, 0, 0, 0));
        for (int i = 0; i < labels.size(); i++) {
            String val = "";
            int charLen = labels.get(i).length();
            Log.e(TAG, "formatBillForHowenBTPrinter: charLen " + charLen);
            int totalSpaceRmain = (13 - charLen);
            Log.e(TAG, "formatBillForHowenBTPrinter: totalSpaceRmain " + totalSpaceRmain);
            for (int j = 1; j < totalSpaceRmain; j++) {
                val = val + " ";
            }
            Log.e(TAG, "formatBillForHowenBTPrinter: val " + charLen);
            Log.e(TAG, "formatBillForHowenBTPrinter: val " + i + " >> " + labels.get(i) + " labels size >> " + labels.size());
            if (i == labels.size() - 1) {
                bill_content += "--------------------------------\n";
                mService.write(PrinterCommand.POS_Print_Text(line_feed, "GBK", 0, 0, 0, 0));
                mService.write(byteCommands[9]);
                mService.write(PrinterCommand.POS_Print_Text(labels.get(i) + val + ": " + values.get(i) + "\n", "GBK", 0, 0, 0, 0));
            } else {
                mService.write(byteCommands[4]);
                mService.write(PrinterCommand.POS_Print_Text(labels.get(i) + val + ": " + values.get(i) + "\n", "GBK", 0, 0, 0, 0));
            }
            bill_content += labels.get(i) + val + ": " + values.get(i) + "\n";
        }
        //Not using below values//
        my_footer =
                "--------------------------------\n" +
                        "THANKS\n" +
                        "\n" +
                        "===== CALL CENTER ====\n" +
                        "000 0000\n" +
                        "HAVE A NICE DAY\n" +
                        "\n";
        billFormat = my_header + bill_content + my_footer;
        //Log.e(TAG, "formatBillForHowenBTPrinter: billFormat "+billFormat );
        System.out.println("Inside the Bill Formatter for Howen==>" + billFormat);
        mService.write(byteCommands[4]);
        //mService.write(new byte[]{0x1b, 0x61, 0x00 });
        mService.write(byteCommands[4]);
        mService.write(PrinterCommand.POS_Print_Text(line_feed, "GBK", 0, 0, 0, 0));
        mService.write(PrinterCommand.POS_Print_Text("            THANKS\n", "GBK", 0, 0, 0, 0));
        mService.write(PrinterCommand.POS_Print_Text("          CALL CENTER   \n", "GBK", 0, 0, 0, 0));
        mService.write(PrinterCommand.POS_Print_Text("           0000 0000\n", "GBK", 0, 0, 0, 0));
        mService.write(PrinterCommand.POS_Print_Text("         HAVE A NICE DAY\n", "GBK", 0, 0, 0, 0));
        mService.write(PrinterCommand.POS_Print_Text("\n", "GBK", 0, 0, 0, 0));
        mService.write(PrinterCommand.POS_Print_Text("\n", "GBK", 0, 0, 0, 0));
//        mService.write(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
//        mService.write(PrinterCommand.POS_Set_Cut(1));
//        mService.write(PrinterCommand.POS_Set_PrtInit());
    }
}