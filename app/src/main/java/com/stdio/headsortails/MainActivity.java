package com.stdio.headsortails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    LinearLayout hud, start_menu, shadow_for_start_menu, input_name;
    EditText etName;
    TextView tvName;
    DBHelper dbHelper;
    SQLiteDatabase database;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    Date currentDateTime = Calendar.getInstance().getTime();
    String name = "";
    int balance = 0;

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
                break;
        }
    }

    private void saveInitialConfigurations() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_NAME, etName.getText().toString());
        contentValues.put(DBHelper.KEY_DATE, sdfDate.format(currentDateTime));
        contentValues.put(DBHelper.KEY_TIME, sdfTime.format(currentDateTime));
        contentValues.put(DBHelper.KEY_BALANCE, 1000);
        contentValues.put(DBHelper.KEY_BILL, 0);
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
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            name = cursor.getString(nameIndex);
            balance = cursor.getInt(balanceIndex);
        } else {
            cursor.close();
        }
    }
}