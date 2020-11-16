package com.stdio.headsortails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    LinearLayout hud, start_menu, shadow_for_start_menu, input_name;
    EditText etName;
    TextView tvName, tvBet;
    MaterialButton btnContinue;
    ProgressBar progressBar;
    DBHelper dbHelper;
    SQLiteDatabase database;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    Date currentDateTime = Calendar.getInstance().getTime();
    String name = "";
    int balance = 0, bill = 0;
    int id = 0;
    int[][] states = new int[][] {
            new int[] { android.R.attr.state_enabled}, // enabled
            new int[] {-android.R.attr.state_enabled}, // disabled
            new int[] {-android.R.attr.state_checked}, // unchecked
            new int[] { android.R.attr.state_pressed}  // pressed
    };

    int[] colors = new int[] {
            Color.WHITE,
            Color.GRAY,
            Color.WHITE,
            Color.WHITE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_main);
        initViews();
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        getData();
        tvName.setText(name);
        tvBet.setText("Your BET: " + bill + " FC");
        progressBar.setProgress(bill/10);
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initViews() {
        hud = findViewById(R.id.hud);
        start_menu = findViewById(R.id.start_menu);
        shadow_for_start_menu = findViewById(R.id.shadow_for_start_menu);
        input_name = findViewById(R.id.input_name);
        etName = findViewById(R.id.etName);
        tvName = findViewById(R.id.tvName);
        tvBet = findViewById(R.id.tvBet);
        btnContinue = findViewById(R.id.btnContinue);
        progressBar = findViewById(R.id.progressBar);
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
                saveInitialConfigurations();
                getData();
                tvName.setText(name);
                break;
            case R.id.btnContinue:
                hud.setVisibility(View.VISIBLE);
                start_menu.setVisibility(View.GONE);
                shadow_for_start_menu.setVisibility(View.GONE);
                break;
            case R.id.btnDecrease:
                bill = (bill - 100 > 0) ? (bill-100) : bill;
                database.execSQL("UPDATE configurations SET bill = '" + bill +  "' WHERE _id='"
                        + id + "';");
                tvBet.setText("Your BET: " + bill + " FC");
                progressBar.setProgress(bill/10);
                break;
            case R.id.btnIncrease:
                bill += 100;
                database.execSQL("UPDATE configurations SET bill = '" + bill +  "' WHERE _id='"
                        + id + "';");
                tvBet.setText("Your BET: " + bill + " FC");
                progressBar.setProgress(bill/10);
                break;
        }
    }

    private void saveInitialConfigurations() {
        database.delete(DBHelper.TABLE_CONFIGURATIONS, null, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_NAME, etName.getText().toString());
        contentValues.put(DBHelper.KEY_DATE, sdfDate.format(currentDateTime));
        contentValues.put(DBHelper.KEY_TIME, sdfTime.format(currentDateTime));
        contentValues.put(DBHelper.KEY_BALANCE, 1000);
        contentValues.put(DBHelper.KEY_BILL, 100);
        contentValues.put(DBHelper.KEY_BILL_DATE, "-");
        contentValues.put(DBHelper.KEY_BILL_TIME, "-");
        contentValues.put(DBHelper.KEY_REWARD, 0);
        database.insert(DBHelper.TABLE_CONFIGURATIONS, null, contentValues);
    }

    private void getData() {
        Cursor cursor = database.query(DBHelper.TABLE_CONFIGURATIONS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int balanceIndex = cursor.getColumnIndex(DBHelper.KEY_BALANCE);
            int billIndex = cursor.getColumnIndex(DBHelper.KEY_BILL);
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            name = cursor.getString(nameIndex);
            balance = cursor.getInt(balanceIndex);
            bill = cursor.getInt(billIndex);
            id = cursor.getInt(idIndex);
        } else {
            cursor.close();
            btnContinue.setEnabled(false);
            btnContinue.setStrokeColor(new ColorStateList(states, colors));
            btnContinue.setTextColor(Color.GRAY);
        }
    }
}