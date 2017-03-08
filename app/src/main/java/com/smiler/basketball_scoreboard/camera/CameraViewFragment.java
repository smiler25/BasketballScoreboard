package com.smiler.basketball_scoreboard.camera;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.layout.BaseLayout;
import com.smiler.basketball_scoreboard.layout.CameraLayout;
import com.smiler.basketball_scoreboard.models.Game;
import com.smiler.basketball_scoreboard.preferences.Preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static com.smiler.basketball_scoreboard.Constants.GUEST;
import static com.smiler.basketball_scoreboard.Constants.HOME;

public class CameraViewFragment extends Fragment implements
        CameraLayout.ClickListener, CameraLayout.LongClickListener {

    public static String TAG = "BS-CameraViewFragment";
    public static final String FRAGMENT_TAG = "CameraFragment";
    private Preferences preferences;
    private CameraView cameraView;
    private Camera camera;
    private Game game;
    private BaseLayout layout;
    private CameraFragmentListener listener;

    public interface CameraFragmentListener {
        void onViewCreated(BaseLayout layout);
        void onCameraPause();
    }

    public static CameraViewFragment newInstance() {
        return new CameraViewFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    private void finish() {
        releaseCamera();
        if (listener != null) {
            listener.onCameraPause();
        }
    }
    private void releaseCamera(){
        if (camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        camera = getCameraInstance();
        if (camera == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_camera_fail), Toast.LENGTH_LONG).show();
            finish();
            return null;
        }

        cameraView = new CameraView(getActivity(), camera);
        layout = new CameraLayout(getActivity(), preferences, cameraView, this, this);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (listener != null) {
            listener.onViewCreated(layout);
        }
    }

    public static Camera getCameraInstance() {
        try {
            return Camera.open();
        } catch (Exception e) {
            Log.d(TAG, "getCameraInstance error: " + e);
        }
        return null;
    }

    private Camera.PictureCallback picture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
//            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            new SaveImageTask(getActivity().getWindowManager().getDefaultDisplay().getRotation()).execute(data);
            camera.startPreview();
        }
    };

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
            view = (ViewGroup) layout.findViewById(R.id.camera_line);
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
            getActivity().getWindowManager().getDefaultDisplay().getSize(size);
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
                Toast.makeText(getActivity().getApplication(), "Can't save picture", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Error while saving picture: " + e.getMessage());
                return null;
            }
//            finalImage.recycle();
            cameraScaledBitmap.recycle();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getActivity(), "Saved to " + path, Toast.LENGTH_LONG).show();
//            camera.startPreview();
        }
    }


    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
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

    public void setGame(Game game) {
        this.game = game;
    }

    public void setListener(CameraFragmentListener listener) {
        this.listener = listener;
    }
}