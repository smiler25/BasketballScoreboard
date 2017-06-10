package com.smiler.basketball_scoreboard.camera;

import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.layout.BaseLayout;
import com.smiler.basketball_scoreboard.layout.CameraLayout;
import com.smiler.basketball_scoreboard.game.Game;
import com.smiler.basketball_scoreboard.preferences.Preferences;

public class CameraFragment extends Fragment implements
        CameraLayout.ClickListener, CameraLayout.LongClickListener {

    public static String TAG = "BS-CameraFragment";
    public static final String FRAGMENT_TAG = "CameraFragment";
    private Preferences preferences;
    private Camera camera;
    private Game game;
    private BaseLayout layout;
    private CameraFragmentListener listener;

    public interface CameraFragmentListener {
        void onViewCreated(BaseLayout layout);
        void onCameraPause();
    }

    public static CameraFragment newInstance() {
        return new CameraFragment();
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
        camera = CameraUtils.getCameraInstance();
        if (camera == null) {
            CameraUtils.requestCameraPermission(getActivity());
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_camera_fail), Toast.LENGTH_LONG).show();
            finish();
            return null;
        }

        CameraView cameraView = new CameraView(getActivity(), camera);
        layout = new CameraLayout(getActivity(), preferences, cameraView, this, this);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (listener != null) {
            listener.onViewCreated(layout);
        }
        CameraUtils.requestStoragePermission(getActivity());
    }

    private Camera.PictureCallback picture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (CameraUtils.canSave) {
                try {
                    new CameraUtils.SaveImageTask(getActivity(), layout, game).execute(data);
                } catch (RuntimeException e){
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_storage_save_fail), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error saving image");
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_storage_permission_fail), Toast.LENGTH_LONG).show();
                CameraUtils.requestStoragePermission(getActivity());
            }
            camera.startPreview();
        }
    };

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