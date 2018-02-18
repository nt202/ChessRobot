package ru.nt202.chessrobotandroidapplication.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import ru.nt202.chessrobotandroidapplication.R;
import ru.nt202.chessrobotandroidapplication.cameratasks.StreamOffTask;
import ru.nt202.chessrobotandroidapplication.logic.GameHandler;

public class CalibrationActivity extends AppCompatActivity implements View.OnClickListener {

    private final String streamURL = "rtsp://192.168.42.1/live";
    private VideoView videoView;
    private MediaController mediaController;
    private Button next;
    private GameHandler gameHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        videoView = findViewById(R.id.video);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(streamURL);
        videoView.start();
        next = findViewById(R.id.btn_next);
        next.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                videoView.stopPlayback();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.quit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new StreamOffTask().execute();
                                gameHandler = GameHandler.getInstance(CalibrationActivity.this);
                                gameHandler.closeStockfish();
                                Intent intent = new Intent(getApplicationContext(), InitActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("EXIT", true);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Quit");
                alert.show();

        }
        return super.onOptionsItemSelected(item);
    }
}
