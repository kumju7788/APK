package andhook.test;

import android.util.Log;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.net.ServerSocket;

public class Server extends Thread{
    public static String TAG = "JAVA";
    static List<String> param = new ArrayList<String>();
    static List<String> function = new ArrayList<String>();
    public static ServerSocket server = null;

    Server() {

    }

    @Override
    public void run() {
        try {
            create();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void create() throws IOException {
        int port = 8899;
        // ServiceSocket 8899포트에서 클리이언트 listener
        server = new ServerSocket(port);
        Log.d(TAG, "Waiting...");
        while (true) {
            Socket socket = server.accept();
            // 클라이언트가 접속할떄 마다 새 스레드가 생성된다.
            new Thread(new Task(socket)).start();
        }
//        server.close();
    }

    /**
    * 소켓요청처리를 위한 클라스
    */
    static class Task implements Runnable {
        private Socket socket;

        public Task(Socket socket) {
            this.socket = socket;
        }
        @Override
            public void run() {
            try {
                handlerSocket();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        /**
         * 클라이언트와 통신
         * 
         * @throws IOException
         */
        private void handlerSocket() throws Exception {
            // 클라이언트와의 연결을 설정 한 후 소켓의 InputStream으로 자료를 읽는다.
            /**
             * BufferedReader의 readLine을 사용하여 데이터를 읽을 때는 해당 출력 스트림에 줄바꿈기호을 붙여야 한다. 
             * 개행 문자를 쓴후 출력 스트림이 즉시 닫기지 않으면 플러시해야합니다. 
             * 이렇게하면 버퍼에서 데이터가 남아있게 된다.
             */
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String recvData;
            int index;
            while ((recvData = br.readLine()) != null) {
                if ((index = recvData.indexOf("close")) != -1) {
                    // 행의 마지막에 "close"를 수신하면 수신을 끝내고 소켓을 닫는다.
                    sb.append(recvData.substring(0, index));
                    break;
                }
                sb.append(recvData);
            }
            Log.d(TAG,"Form Cliect[port:" + socket.getPort() + "] :" + sb.toString());
            // 클라이언트에게 응답
            setParam(String.valueOf(sb));
            NativeRespose Secure = new NativeRespose(function, param);
            String result = Secure.getResult();
            Writer writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            writer.write(result);
            writer.flush();
            writer.close();
            Log.d(TAG,"To Client[port:" + socket.getPort() + "] Successful sending | " + result);
            br.close();
            socket.close();
        }
    }

    private static void setParam(String recvData) {
        int funcPos;
        String funcPrefix = "FUNC=";
        String paramPrefix = "PARAM=";

        function.clear();
        param.clear();

        int paramPos = recvData.indexOf(paramPrefix);
        if(paramPos != -1) {
            String str = recvData.substring(paramPos + paramPrefix.length());
            String[] params = str.split("&");
            for(int i = 0; i < params.length; i++) {
                param.add(params[i]);
            }
        }
        
        funcPos = recvData.indexOf(funcPrefix);
        if(funcPos != -1) {
            if(paramPos == 0)
                paramPos = recvData.length();
            String str = recvData.substring(funcPrefix.length(), paramPos != 0 ? paramPos : recvData.length());
            String[] funcs = str.split("&");
            for(int i = 0; i < funcs.length; i++) {
                function.add(funcs[i]);
            }
        }
    }
}