package com.smiler.basketball_scoreboard.camera;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.layout.BaseLayout;
import com.smiler.basketball_scoreboard.models.Game;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.smiler.basketball_scoreboard.Constants.GUEST;
import static com.smiler.basketball_scoreboard.Constants.HOME;
import static com.smiler.basketball_scoreboard.Constants.MEDIA_FOLDER;
import static com.smiler.basketball_scoreboard.Constants.PERMISSION_CODE_CAMERA;
import static com.smiler.basketball_scoreboard.Constants.PERMISSION_CODE_STORAGE;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT_PHOTO;


public class CameraUtils {
    public static String TAG = "BS-CameraUtils";
    static boolean canSave = false;

    public static void enableSaving() {
        canSave = true;
    }

    static Camera getCameraInstance() {
        try {
            return Camera.open();
        } catch (Exception e) {
            Log.d(TAG, "getCameraInstance error: " + e);
        }
        return null;
    }

    static void requestCameraPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CODE_CAMERA);
        }
    }

    static void requestStoragePermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE_STORAGE);
        } else {
            CameraUtils.canSave = true;
        }
    }

    static class SaveImageTask extends AsyncTask<byte[], Void, Void> {
        int degree, overlayW, overlayBottomMargin;
        ViewGroup view;
        Bitmap overlayBitmap;
        File path;
        BaseLayout layout;
        Activity activity;
        Game game;

        SaveImageTask(Activity activity, BaseLayout layout, Game game) {
            this.layout = layout;
            this.activity = activity;
            this.game = game;
            int orientation = activity.getWindowManager().getDefaultDisplay().getRotation();
            switch (orientation) {
                case Surface.ROTATION_0:
                    degree = 90;
                    break;
                case Surface.ROTATION_90:
                    degree = 0;
                    break;
                case Surface.ROTATION_180:
                    degree = 270;
                    break;
                case Surface.ROTATION_270:
                    degree = 180;
                    break;
            }
        }

        @Override
        protected void onPreExecute() {
            String res_type = activity.getResources().getString(R.string.res_type);
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
            activity.getWindowManager().getDefaultDisplay().getSize(size);
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

            path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), MEDIA_FOLDER);
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    Toast.makeText(activity, String.format(activity.getResources().getString(R.string.toast_storage_folder_fail), path), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Save image: path doesn't exist, failed to create directory");
                    return null;
                }
            }
            String filename = String.format("%s-%s (%s)", game.getName(HOME), game.getName(GUEST),
                    TIME_FORMAT_PHOTO.format(System.currentTimeMillis()));
            File image = new File(path, filename + ".jpg");

            try {
                FileOutputStream fos = new FileOutputStream(image);
                finalImage.compress(Bitmap.CompressFormat.JPEG, 95, fos);
                fos.close();
            } catch (IOException e) {
                Toast.makeText(activity, "Can't save picture", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Error while saving picture: " + e.getMessage());
                return null;
            }
//            finalImage.recycle();
            cameraScaledBitmap.recycle();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(activity, String.format(activity.getResources().getString(R.string.toast_storage_save_ok), path), Toast.LENGTH_LONG).show();
//            camera.startPreview();
        }
    }
}
