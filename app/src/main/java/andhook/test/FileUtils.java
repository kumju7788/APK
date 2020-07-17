package andhook.test;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static String TAG = "HTTP";
    private static String logPath = "/data/user/0/com.smile.gifmaker/log";

    FileUtils() {

    }

    public static String createLogPath() {
        try {
            Log.d(TAG, "path = " + logPath);
            File dir = new File(logPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File[] files = dir.listFiles();
            for (File f: files) {
                //if(f.getName().contains("-1") || f.getName().contains("-2")) {
                    f.delete();
                //}
            }
            Log.d(TAG, "folder create success : " + logPath);
            return logPath;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return "/data/local/tmp";
    }

    public static synchronized String writeResponseData(InputStream input, long length, int identify) throws IOException {
        int count = 0;
        File file = null;
        String fileName;
        OutputStream output = null;
        StringBuilder logMsg = new StringBuilder();
        int fileNumber = 0;

        if (length <= 0)
            return "";

        while (true) {
            if (file != null)
                file = null;
            fileNumber++;
            fileName = logPath + "/" + identify + "-" + fileNumber;
            logMsg.append("saving file name :")
                    .append(fileName)
                    .append("\n");
            file = new File(fileName);
            if (!file.exists())
                break;
            logMsg.append("exist file length")
                    .append(file.length())
                    .append(" param length ")
                    .append(length)
                    .append("\n");

            if (file.length() == length) {
                return "";
            }
        }

        try {
            output = new FileOutputStream(file);
            byte[] data = new byte[(int) length];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
                total += count;
            }

            logMsg.append("saved ")
                    .append(total).append(": ")
                    .append(fileName)
                    .append("\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null)
                output.close();
        }
        return String.valueOf(logMsg);
    }
}