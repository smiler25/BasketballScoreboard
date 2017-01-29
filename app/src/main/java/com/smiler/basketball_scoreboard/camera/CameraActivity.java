package com.smiler.basketball_scoreboard.camera;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.Constants;
import com.smiler.basketball_scoreboard.CountDownTimer;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.preferences.PrefActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    public static String TAG = "BS-CameraActivity";

    private Camera camera;
    private TextView hScoreView, gScoreView, periodView, mainTimeView, shotTimeView, userTextView;
    private short hScore, gScore, period = 1, numRegular;
    private long mainTime, shotTime, mainTimePref, shotTimePref, shortShotTimePref, overTimePref;
    private long startTime, totalTime;
    private int layoutType;
    private boolean useDirectTimer, directTimerStopped, enableShotTime;
    private boolean mainTimerOn, shotTimerOn;
    private boolean created;
    private Intent intent;
    private String hName = "Home", gName = "Guest";
//    private Game game;

    private Handler customHandler = new Handler();
    private CountDownTimer mainTimer, shotTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        game = Game.getInstance(this, this);
        camera = getCameraInstance();
        if (camera == null) {
            finish();
        }

        //sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //getSettings();
        //if (sharedPref.getInt("app_version", 1) < BuildConfig.VERSION_CODE) {
        //    // showUpdatesWindow();
        //    SharedPreferences.Editor editor = sharedPref.edit();
        //    editor.putInt("app_version", BuildConfig.VERSION_CODE);
        //    editor.apply();
        //}

        if (!created) {
            intent = getIntent();
        }
        layoutType = intent.getIntExtra("layoutType", Constants.LAYOUT_FULL);
        setContentView(layoutType == Constants.LAYOUT_FULL ? R.layout.camera_layout_full : R.layout.camera_layout_simple);
        CameraView cameraView = new CameraView(this, camera);
        FrameLayout view = (FrameLayout) findViewById(R.id.camera_preview);
        view.addView(cameraView);

        hScoreView = (TextView) findViewById(R.id.camera_home_score);
        gScoreView = (TextView) findViewById(R.id.camera_guest_score);
        mainTimeView = (TextView) findViewById(R.id.camera_time);
        ImageView takePictureBu = (ImageView) findViewById(R.id.camera_take_picture);

        TextView hNameView = (TextView) findViewById(R.id.camera_home_name);
        TextView gNameView = (TextView) findViewById(R.id.camera_guest_name);

        hName = intent.getStringExtra("hName");
        gName = intent.getStringExtra("gName");
        hScore = intent.getShortExtra("hScore", hScore);
        gScore = intent.getShortExtra("gScore", gScore);
        getSettings();

        hScoreView.setText(String.format(Constants.FORMAT_TWO_DIGITS, hScore));
        gScoreView.setText(String.format(Constants.FORMAT_TWO_DIGITS, gScore));

        mainTime = intent.getLongExtra("mainTime", 0);
        setMainTimeText(mainTime);
        hNameView.setText(hName);
        gNameView.setText(gName);

        if (layoutType == Constants.LAYOUT_FULL) {
            periodView = (TextView) findViewById(R.id.camera_period);
            shotTimeView = (TextView) findViewById(R.id.camera_shot_clock);
            period = intent.getShortExtra("period", period);
            shotTime = intent.getLongExtra("shotTime", 0);
            setPeriod(period);
            setShotTimeText(shotTime);
            periodView.setOnClickListener(this);
            periodView.setOnLongClickListener(this);
            shotTimeView.setOnClickListener(this);
            shotTimeView.setOnLongClickListener(this);
        } else {
            enableShotTime = false;
        }

        hScoreView.setOnClickListener(this);
        gScoreView.setOnClickListener(this);
        mainTimeView.setOnClickListener(this);
        hNameView.setOnClickListener(this);
        gNameView.setOnClickListener(this);
        takePictureBu.setOnClickListener(this);

        hScoreView.setOnLongClickListener(this);
        gScoreView.setOnLongClickListener(this);
        hNameView.setOnLongClickListener(this);
        gNameView.setOnLongClickListener(this);
        mainTimeView.setOnLongClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera == null) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendDataIntent();
        releaseCamera();
    }

    private void sendDataIntent() {
        int resCode = RESULT_OK;
        if (intent == null) {
            intent = getIntent();
            if (intent == null) {
                resCode = RESULT_CANCELED;
            }
        }
        if (resCode == RESULT_OK) {
            intent.putExtra("hScore", hScore);
            intent.putExtra("gScore", gScore);
            intent.putExtra("mainTime", mainTime);
            if (layoutType == Constants.LAYOUT_FULL){
                intent.putExtra("shotTime", shotTime);
                intent.putExtra("period", period);
            }
        }
        setResult(resCode, intent);
    }

    @Override
    public void finish() {
        releaseCamera();
        sendDataIntent();
        super.finish();
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            Log.d(TAG, "getCameraInstance error" + e);
        }
        return c;
    }

    private void releaseCamera(){
        if (camera != null){
            camera.release();
            camera = null;

        }
    }

    private Camera.PictureCallback picture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
//            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            new SaveImageTask(getWindowManager().getDefaultDisplay().getRotation()).execute(data);
            camera.startPreview();
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {
        int degree, overlayW, overlayBottomMargin;
        ViewGroup view;
        Bitmap overlayBitmap;
        File path;

        public SaveImageTask(int orientation) {
            switch (orientation) {
                case 0:
                    degree = 90;
                    break;
                case 1:
                    degree = 0;
                    break;
                case 2:
                    degree = 270;
                    break;
                case 3:
                    degree = 180;
                    break;
            }
        }

        @Override
        protected void onPreExecute() {
            String res_type = getResources().getString(R.string.res_type);
            view = (ViewGroup) findViewById(R.id.camera_line);
            overlayW = view.getWidth();
            int h = view.getHeight();
            if (res_type.equals("port") && (degree == 90 || degree == 270)) {
                overlayBottomMargin = h / 2;
            } else if (res_type.equals("sw600-port")){
                overlayBottomMargin = 2 * h;
            } else {
                overlayBottomMargin = h;
            }
            view.setDrawingCacheEnabled(true);
            overlayBitmap = view.getDrawingCache();
        }

        @Override
        protected Void doInBackground(byte[]... data) {
            Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data[0], 0, data[0].length);
            Matrix mtx = new Matrix();
            mtx.setRotate(degree);

            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            int h = size.x > size.y ? size.y : size.x;
            int w = (int)((float) cameraBitmap.getWidth() / cameraBitmap.getHeight() * h);

            Bitmap cameraScaledBitmap = Bitmap.createScaledBitmap(cameraBitmap, w, h, true);
            cameraScaledBitmap = Bitmap.createBitmap(cameraScaledBitmap, 0, 0, cameraScaledBitmap.getWidth(), cameraScaledBitmap.getHeight(), mtx, true);
            cameraBitmap.recycle();
            w = cameraScaledBitmap.getWidth();
            h = cameraScaledBitmap.getHeight();

            Bitmap finalImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(finalImage);
            canvas.drawBitmap(cameraScaledBitmap, 0, 0, null);
            canvas.drawBitmap(overlayBitmap, (w - overlayW) / 2, h - overlayBottomMargin, null);

            path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Basketball scoreboard");
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    Log.d(TAG, "Path doesn't exist. Failed to create directory");
                    return null;
                }
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
            String filename = String.format("%s-%s (%s)", hName, gName, dateFormat.format(System.currentTimeMillis()));
            File image = new File(path, filename + ".jpg");

            try {
                FileOutputStream fos = new FileOutputStream(image);
                finalImage.compress(Bitmap.CompressFormat.JPEG, 95, fos);
                fos.close();
            } catch (IOException e) {
                Toast.makeText(getApplication(), "Can't save picture", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Error while saving picture: " + e.getMessage());
                return null;
            }
//            finalImage.recycle();
            cameraScaledBitmap.recycle();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplication(), "Saved to " + path, Toast.LENGTH_LONG).show();
//            camera.startPreview();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_take_picture:
                camera.takePicture(null, null, picture);
                break;
            case R.id.camera_home_score:
            case R.id.camera_home_name:
                changeHomeScore(1);
                break;
            case R.id.camera_guest_score:
            case R.id.camera_guest_name:
                changeGuestScore(1);
                break;
            case R.id.camera_period:
                setPeriod(period++);
                break;
            case R.id.camera_time:
                mainTimeClick();
                break;
            case R.id.camera_shot_clock:
                if (mainTimerOn && shotTimer != null) {
                    shotTimer.cancel();
                    startShotCountDownTimer(shotTimePref);
                } else {
                    if (shotTime == shotTimePref){
                        shotTime = shortShotTimePref;
                    } else {
                        shotTime = shotTimePref;
                    }
                    setShotTimeText(shotTime);
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.camera_home_score:
            case R.id.camera_home_name:
                changeHomeScore(-1);
                return true;
            case R.id.camera_guest_score:
            case R.id.camera_guest_name:
                changeGuestScore(-1);
                return true;
            case R.id.camera_period:
                period = 1;
                setPeriod(period);
                return true;
        }
        return false;
    }

    private void getSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        useDirectTimer = sharedPref.getBoolean(PrefActivity.PREF_DIRECT_TIMER, false);
        enableShotTime = sharedPref.getBoolean(PrefActivity.PREF_ENABLE_SHOT_TIME, true);
        shotTimePref = sharedPref.getInt(PrefActivity.PREF_SHOT_TIME, 24) * 1000;
        boolean enableShortShotTime = sharedPref.getBoolean(PrefActivity.PREF_ENABLE_SHORT_SHOT_TIME, true);
        shortShotTimePref = enableShortShotTime ? sharedPref.getInt(PrefActivity.PREF_SHORT_SHOT_TIME, 14) * 1000 : shotTimePref;
        mainTimePref = sharedPref.getInt(PrefActivity.PREF_REGULAR_TIME, 10) * Constants.SECONDS_60;
        overTimePref = sharedPref.getInt(PrefActivity.PREF_OVERTIME, 5) * Constants.SECONDS_60;
        numRegular = (short) sharedPref.getInt(PrefActivity.PREF_NUM_REGULAR, 4);
    }

    private void changeGuestScore(int value) {
        gScore += value;
        gScoreView.setText(String.format(Constants.FORMAT_TWO_DIGITS, gScore));
    }

    private void changeHomeScore(int value) {
        hScore += value;
        hScoreView.setText(String.format(Constants.FORMAT_TWO_DIGITS, hScore));
    }

    private void setMainTimeText(long millis) {
        mainTimeView.setText(Constants.TIME_FORMAT.format(millis));
    }

    private void setShotTimeText(long millis) {
        if (millis >= 5000) {
            shotTimeView.setText(String.format(Constants.FORMAT_TWO_DIGITS, (short) Math.ceil(millis / 1000.0)));
        } else {
            shotTimeView.setText(String.format(Constants.TIME_FORMAT_SHORT, millis / 1000, millis % 1000 / 100));
        }
    }

    private void setPeriod(short period) {
        if (period <= numRegular) {
            mainTime = totalTime = mainTimePref;
            periodView.setText(String.format("Q%d", period));
        } else {
            mainTime = totalTime = overTimePref;
            periodView.setText(String.format("OT%d", period - numRegular));
        }
        if (useDirectTimer) {
            mainTime = 0;
        }
        setMainTimeText(mainTime);
    }

    public void mainTimeClick() {
        if (!mainTimerOn) {
            if (useDirectTimer) {
                startDirectTimer();
            } else {
                startMainCountDownTimer();
            }
        } else {
            pauseGame();
        }
    }

    private void pauseGame() {
        if (useDirectTimer) {
            pauseDirectTimer();
        } else if (mainTimerOn) {
            mainTimer.cancel();
        }
        if (shotTimer != null && enableShotTime && shotTimerOn) { shotTimer.cancel(); }
        mainTimerOn = shotTimerOn = false;
    }

    private void startMainCountDownTimer() {
        mainTimer = new CountDownTimer(mainTime, Constants.SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                mainTime = millisUntilFinished;
                setMainTimeText(mainTime);
                if (enableShotTime && mainTime < shotTime && shotTimerOn) {
                    shotTimer.cancel();
                }
            }
            @Override
            public void onFinish() {
                mainTimerOn = false;
                setMainTimeText(0);
                if (enableShotTime && shotTimerOn) {
                    shotTimer.cancel();
                    setShotTimeText(0);
                }
            }
        }.start();
        mainTimerOn = true;
        if (enableShotTime && mainTime > shotTime) {
            startShotCountDownTimer();
        }
    }

    private void startShotCountDownTimer(long startValue) {
        if (shotTimerOn){
            shotTimer.cancel();
        }
        shotTime = startValue;
        startShotCountDownTimer();
    }

    private void startShotCountDownTimer() {
        shotTimer = new CountDownTimer(shotTime, Constants.SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                shotTime = millisUntilFinished;
                setShotTimeText(shotTime);
            }
            @Override
            public void onFinish() {
                pauseGame();
                setShotTimeText(0);
                shotTime = shotTimePref;
            }
        }.start();
        shotTimerOn = true;
    }

    private void startDirectTimer() {
        startTime = SystemClock.uptimeMillis() - mainTime;
        if (directTimerStopped) {
            stopDirectTimer();
        }
        mainTimerOn = true;
        customHandler.postDelayed(directTimerThread, 0);
        if (enableShotTime) {
            startShotCountDownTimer();
        }
    }

    private void stopDirectTimer() {
        startTime = SystemClock.uptimeMillis();
        mainTime = 0;
        directTimerStopped = true;
        customHandler.removeCallbacks(directTimerThread);
        if (shotTimer != null && enableShotTime && shotTimerOn) { shotTimer.cancel(); }
        mainTimerOn = shotTimerOn = false;
    }

    private void pauseDirectTimer() {
        customHandler.removeCallbacks(directTimerThread);
        if (shotTimer != null && enableShotTime && shotTimerOn) { shotTimer.cancel(); }
        mainTimerOn = directTimerStopped = shotTimerOn = false;
    }

    private Runnable directTimerThread = new Runnable() {
        @Override
        public void run() {
            mainTime = SystemClock.uptimeMillis() - startTime;
            setMainTimeText(mainTime);
            if (mainTime >= totalTime) {
                stopDirectTimer();
                return;
            }
            customHandler.postDelayed(this, Constants.SECOND);
        }
    };
}
