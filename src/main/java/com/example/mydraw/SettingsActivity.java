package com.example.mydraw;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private CheckBox checkAutoSave;
    private CheckBox checkShowGrid;
    private RadioGroup radioGroupQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("환경설정");
        }

        preferences = getSharedPreferences("DrawingPrefs", MODE_PRIVATE);

        checkAutoSave = findViewById(R.id.checkAutoSave);
        checkShowGrid = findViewById(R.id.checkShowGrid);
        radioGroupQuality = findViewById(R.id.radioGroupQuality);

        loadSettings();
        setupListeners();
    }

    private void loadSettings() {
        checkAutoSave.setChecked(preferences.getBoolean("autoSave", false));
        checkShowGrid.setChecked(preferences.getBoolean("showGrid", false));

        String quality = preferences.getString("quality", "high");
        if (quality.equals("low")) {
            ((RadioButton) findViewById(R.id.radioLow)).setChecked(true);
        } else if (quality.equals("medium")) {
            ((RadioButton) findViewById(R.id.radioMedium)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.radioHigh)).setChecked(true);
        }
    }

    private void setupListeners() {
        checkAutoSave.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference("autoSave", isChecked);
            Toast.makeText(this, "자동 저장: " + (isChecked ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
        });

        checkShowGrid.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference("showGrid", isChecked);
            Toast.makeText(this, "격자 표시: " + (isChecked ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
        });

        radioGroupQuality.setOnCheckedChangeListener((group, checkedId) -> {
            String quality = "high";
            if (checkedId == R.id.radioLow) {
                quality = "low";
            } else if (checkedId == R.id.radioMedium) {
                quality = "medium";
            }

            savePreference("quality", quality);
            Toast.makeText(this, "화질 설정: " + quality, Toast.LENGTH_SHORT).show();
        });
    }

    private void savePreference(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void savePreference(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}