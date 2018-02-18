package ru.nt202.chessrobotandroidapplication.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import ru.nt202.chessrobotandroidapplication.R;
import ru.nt202.chessrobotandroidapplication.cameratasks.StreamOffTask;
import ru.nt202.chessrobotandroidapplication.cameratasks.StreamOnTask;
import ru.nt202.chessrobotandroidapplication.logic.GameHandler;

public class InitActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private GameHandler gameHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)){
            finish();
        }
        setContentView(R.layout.activity_init);
        button = findViewById(R.id.btn_connect_next);
        button.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (button.getText().equals("Connect")) {
            try {
                StreamOnTask streamOnTask = new StreamOnTask();
                streamOnTask.execute();
                if (streamOnTask.get()) {
                    Toast.makeText(getApplicationContext(), "Connection succeeded!", Toast.LENGTH_SHORT).show();
                    button.setText("Next");
                } else {
                    Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_SHORT).show();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_SHORT).show();
            }
        } else {
            startActivity(new Intent(this, CalibrationActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new StreamOffTask().execute();
        gameHandler = GameHandler.getInstance(InitActivity.this);
        gameHandler.closeStockfish();
        finish();
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
                                gameHandler = GameHandler.getInstance(InitActivity.this);
                                gameHandler.closeStockfish();
                                finish();
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
