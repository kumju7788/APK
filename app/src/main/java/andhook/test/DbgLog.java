package andhook.test;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DbgLog extends Thread {
    private static String TAG = "HTTP";
    public static final int TO_FILE = 1;

    byte[] mBuffer;
    int mHashCode;
    boolean bLogString = false;
    FileWriter mFile;
    StringBuilder allString = new StringBuilder();


    DbgLog(String tag, byte[] bytes, int hashCode) {
        mBuffer = bytes;
        mHashCode = hashCode;
        TAG = tag;
    }
    DbgLog() {};

    DbgLog(byte[] bytes, int hashcode, boolean log2string) {
        bLogString = log2string;
        mBuffer = bytes;
        mHashCode = hashcode;
    }

    DbgLog(String fileName, int flag) {
        try {
            if(flag == TO_FILE) {
                mFile = new FileWriter(fileName, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        if(bLogString)
            LogString(mBuffer, mHashCode);
        else
            LogByteArray(mBuffer, mHashCode);
    }

    public void LogByteArray(byte[] bytes, long hashCode) {
        int length = bytes.length;
        int max_length = 2048;
        int col = 0, cur_row = 0;
        int row = ((Math.min(length, max_length)) / 16) + 1;
        boolean start = false;
        int i;

        if(length <= 0) {
            return;
        }

        StringBuilder lineData = null;
        StringBuilder chData = null;
        Log.d(TAG, "[" + hashCode + "]\t Data lenth : " + length + "B\n");

        for( i = 0; i < row * 16; i ++) {
            col = i % 16;
            start = col == 0;

            if(start) {
                chData = new StringBuilder();;
                lineData = new StringBuilder(String.format("%08X: ", cur_row * 0x10));
                cur_row ++;
            }

            if(i < length) {
                byte b = bytes[i];
                lineData.append(String.format("%02X ", b));
                chData.append(b >= 0x20 && b < 0x7f ? (char)b : '.');
            } else {
                lineData.append("   ");
                chData.append(" ");
            }

            if(col == 15) {
                allString.append("[").append(hashCode).append("]\t").append(lineData).append("    ").append(chData).append("\n");
                Log.d(TAG, "[" + hashCode + "]\t" + lineData + "    " + chData);
            }
        }
//        Log.d(TAG, String.valueOf(allString));
    }

    void LogString(byte[] bytes, int hashCode) {
        Log.d(TAG, "[" + hashCode + "]\t" + bytes.toString());
    }

    void toFile(String logMsg) throws IOException {
        mFile.write(String.valueOf(logMsg));
        mFile.flush();
        mFile.close();
    }

    void toFile(byte[] bytes, long hashCode) throws IOException {
        LogByteArray(bytes, hashCode);
        mFile.write(String.valueOf(allString));
        mFile.flush();
        mFile.close();
    }

}
