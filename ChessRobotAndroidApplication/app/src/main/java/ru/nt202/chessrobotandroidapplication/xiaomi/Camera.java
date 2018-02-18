package ru.nt202.chessrobotandroidapplication.xiaomi;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public abstract class Camera {

    private final String ADDRESS = "192.168.42.1";
    private final int PORT = 7878;
    private final String COMMAND_TO_GET_TOKEN = "{\"msg_id\":257,\"token\":0}\r\n";

    private static Socket socket = null;
    private static InputStream is = null;
    private static OutputStream os = null;
    private static OutputStreamWriter osw = null;
    protected static BufferedWriter bw = null;

    static int token = 0;

    protected synchronized void getToken() {
        try {
            bw.write(COMMAND_TO_GET_TOKEN);
            bw.flush();
            JSONObject response;
            try {
                response = getResponse(1);
                token = response.getInt("param");
            } catch (Exception e) {
                response = getResponse(1);
                token = response.getInt("param");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    protected synchronized JSONObject getResponse(int lineOfResponse) {
        JSONObject jsonResponse = null;
        try {
            byte[] data = new byte[4096];
            String response = null;
            for (int i = 0; i < lineOfResponse; i++) {
                is.read(data);
                response = new String(data, StandardCharsets.UTF_8);
            }
            assert response != null;
            jsonResponse = new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    protected String getParam(String type) {
        while (true) {
            JSONObject response = getResponse(1);
            try {
                if (response.getString("type").equals(type)) {
                    return response.getString("param");
                }
            } catch (JSONException e) {
                // NOPE
            }
        }
    }

    protected synchronized void openAllConnections() {
        try {
            socket = new Socket(ADDRESS, PORT);
            is = socket.getInputStream();
            os = socket.getOutputStream();
            osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected synchronized void closeAllConnections() {
        try {
            if (bw != null) bw.close();
            if (osw != null) osw.close();
            if (os != null) os.close();
            if (is != null) is.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
