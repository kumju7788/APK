package andhook.test;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;

public class DbgLog extends Thread {
    private static String TAG = "RESP";
    byte[] mBuffer;
    int mHashCode;
    DbgLog(String tag, byte[] bytes, int hashCode) {
        mBuffer = bytes;
        mHashCode = hashCode;
        TAG = tag;
    }
    @Override
    public void run() {
        LogByteArray(mBuffer, mHashCode);
    }

    public void LogByteArray(byte[] bytes, long hashCode) {
        int length = bytes.length;
        int max_length = 1024;
        int col = 0, cur_row = 0;
        int row = (Math.min(length, max_length)) / 16 + 1;
        boolean start = false;

        if(length <= 0) {
            return;
        }

        StringBuilder allString = new StringBuilder();
        StringBuilder lineData = null;
        StringBuilder chData = null;

        for(int i = 0; i < row * 16; i ++) {
            byte b = bytes[i];
            col = i % 16;
            start = col == 0;

            if(start) {
                chData = new StringBuilder();;
                lineData = new StringBuilder(String.format("%08X: ", cur_row * 0x10));
                cur_row ++;
            }

            if(i < length) {
                lineData.append(String.format("%02X ", b));
                chData.append(b >= 0x20 && b < 0x7f ? (char)b : '.');
            } else {
                lineData.append("   ");
                chData.append(" ");
            }

            if(col == 15) {
//                allString.append("[").append(hashCode).append("]\t").append(lineData).append("    ").append(chData).append("\n");
                Log.d(TAG, "[" + hashCode + "]\t" + lineData + "    " + chData +"\n");
            }
        }
        //Log.d(TAG, String.valueOf(allString));
    }
}
