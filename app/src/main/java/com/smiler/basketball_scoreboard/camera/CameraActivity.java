package com.smiler.basketball_scoreboard.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.layout.BaseLayout;
import com.smiler.basketball_scoreboard.layout.CameraLayout;
import com.smiler.basketball_scoreboard.models.Game;
import com.smiler.basketball_scoreboard.panels.SidePanelFragment;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;
import com.smiler.basketball_scoreboard.preferences.Preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TreeSet;

import static com.smiler.basketball_scoreboard.Constants.GUEST;
import static com.smiler.basketball_scoreboard.Constants.HOME;

public class CameraActivity extends AppCompatActivity implements
        Game.GameListener,
        CameraLayout.ClickListener,
        CameraLayout.LongClickListener,
        SidePanelFragment.SidePanelListener
{

    public static String TAG = "BS-CameraActivity";
    private Camera camera;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        camera = getCameraInstance();
        if (camera == null) {
            Toast.makeText(this, getResources().getString(R.string.toast_camera_fail), Toast.LENGTH_LONG).show();
            finish();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        game = Game.getInstance(this);
        game.setListener(this);
        Preferences preferences = Preferences.getInstance(getApplicationContext());
        preferences.read();
        CameraView cameraView = new CameraView(this, camera);
        BaseLayout layout = new CameraLayout(this, preferences, cameraView, this, this);
        setContentView(layout);
        game.setLayout(layout);
        game.setCurrentState();
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
        releaseCamera();
    }

    @Override
    public void finish() {
        releaseCamera();
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

    @Override
    public void onSidePanelClose(boolean left) {

    }

    @Override
    public void onSidePanelActiveSelected(TreeSet<SidePanelRow> rows, boolean left) {

    }

    @Override
    public void onSidePanelNoActive(boolean left) {

    }

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {
        int degree, overlayW, overlayBottomMargin;
        ViewGroup view;
        Bitmap overlayBitmap;
        File path;

        SaveImageTask(int orientation) {
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
            String filename = String.format("%s-%s (%s)", game.getName(HOME), game.getName(GUEST),
                    dateFormat.format(System.currentTimeMillis()));
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
    public void onPlayHorn() {

    }

    @Override
    public void onNewGame() {

    }

    @Override
    public BaseLayout onInitLayout() {
        return null;
    }

    @Override
    public void onConfirmDialog(String type) {

    }

    @Override
    public void onWinDialog(String type, String team, int winScore, int loseScore) {

    }

    @Override
    public void onShowTimeout(long seconds, String team) {

    }

    @Override
    public void onSwitchSides(boolean show) {

    }

    @Override
    public void onChangeScoreClick(int team) {
        game.changeScore(team, 1);
    }

    @Override
    public void onMainTimeClick() {
        game.mainTimeClick();
    }

    @Override
    public void onPeriodClick() {
        game.newPeriod(true);
    }

    @Override
    public void onShotTimeClick() {
        game.shotTimeClick();
    }

    @Override
    public void onTakePictureClick() {
        camera.takePicture(null, null, picture);
    }

    @Override
    public void onPlayerButtonClick(boolean left, SidePanelRow player) {
        game.playerAction(left, player);
    }

    @Override
    public boolean onScoreLongClick(int team) {
        game.nullScore(team);
        return true;
    }

    @Override
    public boolean onMainTimeLongClick() {
        return true;
    }

    @Override
    public boolean onPeriodLongClick() {
        game.newPeriod(false);
        return true;
    }

    @Override
    public boolean onShotTimeLongClick() {
        return true;
    }

    public boolean onPlayerButtonLongClick(boolean left) {
//        showListDialog(left);
        return true;
    }

    @Override
    public void onShowToast(int resId, int len) {
        Toast.makeText(this, getResources().getString(resId), len).show();
    }
}
