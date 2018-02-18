package ru.nt202.chessrobotandroidapplication.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

import ru.nt202.chessrobotandroidapplication.R;
import ru.nt202.chessrobotandroidapplication.cameratasks.StreamOffTask;
import ru.nt202.chessrobotandroidapplication.logic.GameHandler;

import static ru.nt202.chessrobotandroidapplication.logic.Constants.pieceNames;
import static ru.nt202.chessrobotandroidapplication.logic.Constants.squareNames;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    // В течении 5-20 секунд переставить, затем отслеживание. задается в настройках.

    private GameHandler gameHandler;
    private Map<String, ImageButton> pieceButtons;
    private Map<String, Bitmap> pieceBitmaps;
    private ProgressBar progressBar;

    private ImageButton moveBtn;

    private Button recognizeBtn, playBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        gameHandler = GameHandler.getInstance(this);
        gameHandler.startStockfish(); // start engine
        pieceButtons = new HashMap<>(64);
        pieceBitmaps = new HashMap<>(13);
        recognizeBtn = findViewById(R.id.recognize_btn);
        recognizeBtn.setOnClickListener(this);
        playBtn = findViewById(R.id.play_btn);
        playBtn.setOnClickListener(this);
        initPieceButtons();
        initPieceBitmaps();
        lockPieceButtons();
        playBtn.setEnabled(false); // first start

        moveBtn = findViewById(R.id.move);
        moveBtn.setOnLongClickListener(this);
        moveBtn.setEnabled(false);
        moveBtn.setVisibility(View.INVISIBLE);
    }

    private void initPieceButtons() {
        Resources resources = this.getResources();
        int resourceId;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                resourceId = resources.getIdentifier(squareNames[i][j] + "_image_btn", "id", this.getPackageName());
                pieceButtons.put(squareNames[i][j], (ImageButton) findViewById(resourceId));
                pieceButtons.get(squareNames[i][j]).setScaleType(ImageView.ScaleType.FIT_XY);
                pieceButtons.get(squareNames[i][j]).setOnClickListener(this);
            }
        }
    }

    private void initPieceBitmaps() {
        Resources resources = this.getResources();
        int resourceId;
        for (int i = 0; i < 13; i++) {
            resourceId = resources.getIdentifier(pieceNames[i], "drawable", this.getPackageName());
            pieceBitmaps.put(pieceNames[i], BitmapFactory.decodeResource(resources, resourceId));
        }
    }

    @SuppressLint("SetTextI18n")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recognize_btn:
                gameHandler.recognize();
                progressBar.setVisibility(View.VISIBLE);
                recognizeBtn.setEnabled(false);
                lockPieceButtons();
                moveBtn.setVisibility(View.INVISIBLE);
                moveBtn.setEnabled(false);
                playBtn.setEnabled(false);
                return;
            case R.id.play_btn:
                if (playBtn.getText().equals("Start")) {
                    playBtn.setText("Stop");
                    lockPieceButtons();
                    gameHandler.play();
                    moveBtn.setVisibility(View.VISIBLE);
                    moveBtn.setEnabled(true);

                } else {
                    playBtn.setText("Start");
                    clearBoard();
                    recognizeBtn.setEnabled(true);
                    playBtn.setEnabled(false);
                    moveBtn.setVisibility(View.INVISIBLE);
                    moveBtn.setEnabled(false);
                }
                // TODO: something cool
                return;
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ImageButton button = pieceButtons.get(squareNames[i][j]);
                if (v.getId() == button.getId()) {
                    gameHandler.refreshSquare(squareNames[i][j]);
                }
            }
        }
    }

    public void visualisePieces(Map<String, String> board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    String pieceName = board.get(squareNames[i][j]);
                    Bitmap bitmap = pieceBitmaps.get(pieceName);
                    pieceButtons.get(squareNames[i][j]).setImageBitmap(bitmap);
                } catch (Exception e) {
                    // NOPE
                }
            }
        }
    }

    public void visualisePiece(String squareName, String pieceName) {
        Bitmap bitmap = pieceBitmaps.get(pieceName);
        pieceButtons.get(squareName).setImageBitmap(bitmap);
    }

    private void clearBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Bitmap bitmap = pieceBitmaps.get("em");
                pieceButtons.get(squareNames[i][j]).setImageBitmap(bitmap);
            }
        }
    }

    private void lockPieceButtons() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieceButtons.get(squareNames[i][j]).setEnabled(false);
            }
        }
    }

    public void lockMoveBtn() {
        moveBtn.setEnabled(false);
    }

    public void unlockPieceButtons() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieceButtons.get(squareNames[i][j]).setEnabled(true);
            }
        }
    }

    public void unlockPlayButton() {
        playBtn.setEnabled(true);
    }

    public void unlockMoveButton() {
        moveBtn.setEnabled(true);
    }

    public void stopProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onLongClick(View v) {
        gameHandler.move();
        moveBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        return false;
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
                                gameHandler = GameHandler.getInstance(MainActivity.this);
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