package ru.nt202.chessrobotandroidapplication.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ru.nt202.chessrobotandroidapplication.R;
import ru.nt202.chessrobotandroidapplication.logic.Constants;

public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private SeekBar seekBarStrength;
    private TextView textViewStrengthValue;
    private EditText editTextIp;
    private EditText editTextPort;
    private Switch switchDetector;
    private Button buttonSave;
    private Spinner spinnerDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        seekBarStrength = findViewById(R.id.seekBarStrength);
        seekBarStrength.setOnSeekBarChangeListener(this);
        textViewStrengthValue = findViewById(R.id.textViewStrengthValue);

        editTextIp = findViewById(R.id.editTextIP);
        Constants.ip = editTextIp.getText().toString();

        editTextPort = findViewById(R.id.editTextPort);
        Constants.port = editTextPort.getText().toString();

        switchDetector = findViewById(R.id.switchAutoMotionDetector);
        Constants.detection = switchDetector.isChecked();

        spinnerDuration = findViewById(R.id.spinnerDuration);
        ArrayAdapter<?> adapterDuration = ArrayAdapter.createFromResource(this, R.array.durations, android.R.layout.simple_spinner_item);
        adapterDuration.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(adapterDuration);
        spinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] durations = getResources().getStringArray(R.array.durations);
                Constants.duration = Integer.valueOf(durations[position].substring(0, 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Constants.strength = progress * 10;
        textViewStrengthValue.setText(String.valueOf(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSave:
                Constants.ip = editTextIp.getText().toString();
                Constants.port = editTextPort.getText().toString();
                Constants.detection = switchDetector.isChecked();
                Toast.makeText(getApplicationContext(), "Changes saved!", Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
