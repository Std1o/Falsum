package com.stdio.headsortails;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    LinearLayout hud, start_menu, shadow_for_start_menu, input_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        hud = findViewById(R.id.hud);
        start_menu = findViewById(R.id.start_menu);
        shadow_for_start_menu = findViewById(R.id.shadow_for_start_menu);
        input_name = findViewById(R.id.input_name);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                input_name.setVisibility(View.VISIBLE);
                start_menu.setVisibility(View.GONE);
                break;
            case R.id.btnInputName:
                hud.setVisibility(View.VISIBLE);
                input_name.setVisibility(View.GONE);
                shadow_for_start_menu.setVisibility(View.GONE);
                break;
        }
    }
}