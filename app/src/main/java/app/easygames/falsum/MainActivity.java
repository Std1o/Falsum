package app.easygames.falsum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.stdio.headsortails.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    LinearLayout hud, start_menu, shadow_for_start_menu, input_name, win_dialog, bill_dialog;
    RelativeLayout winLayout, billLayout;
    EditText etName;
    TextView tvName, tvBet, tvBalance, tvCongratulationsDate, tvCongratulationsName, tvName2, tvSum, tvBillDate;
    MaterialButton btnContinue;
    ProgressBar progressBar;
    ImageView ivHeads;
    DBHelper dbHelper;
    SQLiteDatabase database;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy"), sdfTime = new SimpleDateFormat("HH:mm");
    Date currentDateTime = Calendar.getInstance().getTime();
    String name = "";
    int balance = 0, bill = 0, reward = 0;
    Random random = new Random();
    boolean isHeads, userWon;
    MediaPlayer mPlayer;
    String selected, billDate, billTime;
    int id = 0;
    int animPosition = 0;
    int[][] states = new int[][]{
            new int[]{android.R.attr.state_enabled}, // enabled
            new int[]{-android.R.attr.state_enabled}, // disabled
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_pressed}  // pressed
    };
    int[] heads = {R.drawable.i1, R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.i5, R.drawable.i6, R.drawable.i7,
            R.drawable.i8, R.drawable.i1};
    int[] tails = {R.drawable.i1, R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.i5, R.drawable.i6, R.drawable.i7,
            R.drawable.i8, R.drawable.i1, R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.i5};

    int[] colors = new int[]{Color.WHITE, Color.GRAY, Color.WHITE, Color.WHITE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_main);
        initViews();
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        getData();
        setContent();
    }

    private void setContent() {
        tvName.setText(name);
        tvBet.setText("Your BET: " + bill + " FC");
        progressBar.setProgress(bill / 10);
        tvBalance.setText(balance + " FC");
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
        win_dialog = findViewById(R.id.win_dialog);
        bill_dialog = findViewById(R.id.bill_dialog);
        winLayout = findViewById(R.id.winLayout);
        billLayout = findViewById(R.id.billLayout);
        etName = findViewById(R.id.etName);
        tvName = findViewById(R.id.tvName);
        tvBet = findViewById(R.id.tvBet);
        tvCongratulationsDate = findViewById(R.id.tvCongratulationsDate);
        tvCongratulationsName = findViewById(R.id.tvCongratulationsName);
        btnContinue = findViewById(R.id.btnContinue);
        progressBar = findViewById(R.id.progressBar);
        ivHeads = findViewById(R.id.ivHeads);
        tvBalance = findViewById(R.id.tvBalance);
        tvName2 = findViewById(R.id.tvName2);
        tvSum = findViewById(R.id.tvSum);
        tvBillDate = findViewById(R.id.tvBillDate);
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
                setContent();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etName.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                break;
            case R.id.btnContinue:
                hud.setVisibility(View.VISIBLE);
                start_menu.setVisibility(View.GONE);
                shadow_for_start_menu.setVisibility(View.GONE);
                break;
            case R.id.btnDecrease:
                bill = (bill - 100 > 0) ? (bill - 100) : bill;
                database.execSQL("UPDATE configurations SET bill = '" + bill + "' WHERE _id='"
                        + id + "';");
                tvBet.setText("Your BET: " + bill + " FC");
                progressBar.setProgress(bill / 10);
                break;
            case R.id.btnIncrease:
                bill += 100;
                database.execSQL("UPDATE configurations SET bill = '" + bill + "' WHERE _id='"
                        + id + "';");
                tvBet.setText("Your BET: " + bill + " FC");
                progressBar.setProgress(bill / 10);
                break;
            case R.id.ivReverse:
                selected = "tails";
                playSound(R.raw.spin);
                generateResult();
                break;
            case R.id.ivAvers:
                selected = "heads";
                playSound(R.raw.spin);
                generateResult();
                break;
            case R.id.rl_win:
                win_dialog.setVisibility(View.GONE);
                shadow_for_start_menu.setVisibility(View.GONE);
                break;
            case R.id.ivShare:
                layoutToImage(winLayout);
                break;
            case R.id.btnAdd:
                tvName2.setText(name);
                tvBillDate.setText(billDate + " " + billTime);
                tvSum.setText(balance + " FC");
                shadow_for_start_menu.setVisibility(View.VISIBLE);
                bill_dialog.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_bill:
                bill_dialog.setVisibility(View.GONE);
                shadow_for_start_menu.setVisibility(View.GONE);
                break;
            case R.id.btnBetAll:
                bill = balance;
                tvBet.setText(bill + " FC");
                database.execSQL("UPDATE configurations SET bill = '" + bill + "' WHERE _id='"
                        + id + "';");
                progressBar.setProgress(bill/10);
                bill_dialog.setVisibility(View.GONE);
                shadow_for_start_menu.setVisibility(View.GONE);
                break;
            case R.id.btnAddBill:
                balance += 1000;
                tvSum.setText(balance + " FC");
                tvBalance.setText(balance + " FC");
                database.execSQL("UPDATE configurations SET balance = '" + balance + "' WHERE _id='"
                        + id + "';");
                break;
            case R.id.ivShareForBill:
                layoutToImage(billLayout);
                break;
        }
    }

    private void layoutToImage(RelativeLayout relativeLayout) {
        relativeLayout.setDrawingCacheEnabled(true);
        Bitmap bitmap = relativeLayout.getDrawingCache();
        shareImageUri(saveImageExternal(bitmap));
    }

    private Uri saveImageExternal(Bitmap image) {
        //TODO - Should be processed in another thread
        Uri uri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    sdfDate.format(currentDateTime) + "_ " + sdfTime.format(currentDateTime) + ".png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
            uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        } catch (IOException e) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    private void shareImageUri(Uri uri){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }


    private void generateResult() {
        isHeads = random.nextBoolean();
        if (isHeads) {
            showAnim(heads);
            if (selected.equals("heads")) {
                userWon = true;
            }
        } else {
            showAnim(tails);
            if (selected.equals("tails")) {
                userWon = true;
            }
        }
    }

    private void showAnim(int[] images) {
        final Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(animPosition);
                        if (animPosition < images.length) {
                            ivHeads.setImageDrawable(getResources().getDrawable(images[animPosition]));
                        } else {
                            myTimer.cancel();
                            animPosition = 0;
                            if (userWon) {
                                playSound(R.raw.win);
                                balance += bill;
                                int thousand = 1000;
                                if (balance > 10 * thousand && reward < 1) {
                                    reward = 1;
                                    showWinDialog();
                                    database.execSQL("UPDATE configurations SET reward = '" + reward + "' WHERE _id='"
                                            + id + "';");
                                } else if (balance > 100 * thousand && reward < 2) {
                                    reward = 2;
                                    showWinDialog();
                                    database.execSQL("UPDATE configurations SET reward = '" + reward + "' WHERE _id='"
                                            + id + "';");
                                } else if (balance > 1000 * thousand && reward < 3) {
                                    reward = 3;
                                    showWinDialog();
                                    database.execSQL("UPDATE configurations SET reward = '" + reward + "' WHERE _id='"
                                            + id + "';");
                                }
                            } else {
                                playSound(R.raw.lose);
                                balance -= bill;
                            }
                            database.execSQL("UPDATE configurations SET balance = '" + balance + "' WHERE _id='"
                                    + id + "';");
                            tvBalance.setText(balance + " FC");
                            userWon = false;
                        }
                        animPosition++;
                    }
                });
            }

        }, 0, 100);
    }

    private void showWinDialog() {
        win_dialog.setVisibility(View.VISIBLE);
        shadow_for_start_menu.setVisibility(View.VISIBLE);
        tvCongratulationsDate.setText(sdfDate.format(currentDateTime) + " " + sdfTime.format(currentDateTime));
        tvCongratulationsName.setText(name);
    }

    private void playSound(int rawSound) {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
        }
        mPlayer = MediaPlayer.create(this, rawSound);
        mPlayer.start();
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
            int billDateIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_DATE);
            int billTimeIndex = cursor.getColumnIndex(DBHelper.KEY_BILL_TIME);
            int rewardIndex = cursor.getColumnIndex(DBHelper.KEY_REWARD);
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            name = cursor.getString(nameIndex);
            balance = cursor.getInt(balanceIndex);
            bill = cursor.getInt(billIndex);
            billDate = cursor.getString(billDateIndex);
            billTime = cursor.getString(billTimeIndex);
            reward = cursor.getInt(rewardIndex);
            id = cursor.getInt(idIndex);
        } else {
            cursor.close();
            btnContinue.setEnabled(false);
            btnContinue.setStrokeColor(new ColorStateList(states, colors));
            btnContinue.setTextColor(Color.GRAY);
        }
    }
}