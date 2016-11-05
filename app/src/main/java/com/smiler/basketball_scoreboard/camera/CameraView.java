package com.smiler.basketball_scoreboard.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    public String TAG = "BS-CameraView";
    private SurfaceHolder holder;
    private Camera camera;

    public CameraView(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        holder = getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraView(Context context) {
        super(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
//        } catch (IOException e) {
        } catch (Exception e) {
              Log.d(TAG, "Error setting camera view: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (holder.getSurface() == null) {
            return;
        }

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size previewSize = parameters.getSupportedPreviewSizes().get(0);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);


//        int degrees = 0;
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                camera.setDisplayOrientation(90);
                parameters.setRotation(90);
//                degrees = 0;
                break;
            case Surface.ROTATION_90:
                camera.setDisplayOrientation(0);
//                degrees = 90;
                break;
            case Surface.ROTATION_180:
                camera.setDisplayOrientation(270);
//                degrees = 180;
                break;
            case Surface.ROTATION_270:
                camera.setDisplayOrientation(180);
//                degrees = 270;
                break;
        }

        //int result = (info.orientation + degrees) % 360;
        //result = (360 - result) % 360; // Compensate the mirror
        //camera.setDisplayOrientation(result);
        //parameters.setRotation(result);
        try {
            camera.setParameters(parameters);
        } catch (RuntimeException e) {
            Log.d(TAG, "Error setParameters for camera: " + e.getMessage());
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error camera surfaceChanged: " + e.getMessage());
        }

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera view after surfaceChanged: " + e.getMessage());
        }
    }

}