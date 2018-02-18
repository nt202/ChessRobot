package ru.nt202.chessrobotandroidapplication.xiaomi;

import android.annotation.SuppressLint;
import java.net.URL;

public class Capture extends Camera {

    private final String COMMAND_TO_TAKE_PICTURE = "{\"msg_id\":769,\"token\":%d}\r\n";
    private final String ADDRESS_OF_STORAGE_PHOTOS = "http://192.168.42.1/DCIM/100MEDIA/";

    @SuppressLint("DefaultLocale")
    public URL receivePictureURL() {
        URL url = null;
        try {
            bw.write(String.format(COMMAND_TO_TAKE_PICTURE, token));
            bw.flush();
            String cameraPathToPicture = getParam("photo_taken");
            String pictureName = cameraPathToPicture.substring(cameraPathToPicture.length() - 12, cameraPathToPicture.length());
            url = new URL(ADDRESS_OF_STORAGE_PHOTOS + pictureName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}
