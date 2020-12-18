package com.deepmirror.host;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.deepmirror.deepsdk.DeepSDK;
import com.deepmirror.deepsdk.imageProcessor.scan.ScanCallback;
import com.deepmirror.deepsdk.imageProcessor.scan.ScanResult;
import com.deepmirror.deepsdk.view.CameraView;
import com.deepmirror.host.databinding.ActivityCameraBinding;

import java.util.List;
import java.util.Locale;


public class CameraActivity extends AppCompatActivity {
    public static final String TAG = CameraActivity.class.getSimpleName();

    private ActivityCameraBinding binding;

    private boolean mNeedRender = false;
    private CameraView camView;
    private BeepManager mBeepManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        mBeepManager = new BeepManager(getApplicationContext());
    }

    private void initView() {
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        camView = binding.cameraView;
        binding.btnSwitchMode.setOnClickListener(v -> switchMode());
        binding.btnTof.setOnClickListener(v->{
            enableAutoFocusTof();
            binding.btnTof.setText("TOF ENABLED");
        });
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        Log.e(TAG, "dispatchKeyEvent: " + keyCode + "," + action);
        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER:
                    switchMode();
                    break;
                case 29:
                    startCamera();
                    break;
                case 30:
                    stopCamera();
                    break;
                case 31:
                    calibrate();
                    break;
                case 32:
                    startScanBarcode();
                    break;
                case 33:
                    stopScanBarcode();
                    break;
                case 34:
                    startDetectIdCard();
                    break;
                case 35:
                    stopDetectIdCard();
                    break;
                case 36:
                    enableMask();
                    break;
                case 37:
                    disableMask();
                    break;
                case 38:
                    enableLaplacian();
                    break;
                case 39:
                    disableLaplacian();
                    break;
                case 40:
                    setFocus40cm();
                    break;
                case 41:
                    disableAutoFocusTof();
                    break;
                /* Following functions are only for test.*/
                case 55:
                    enableAutoFocus();
                    break;
                case 56:
                    disableAutoFocus();
                    break;
                default:
                    return super.dispatchKeyEvent(event);
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: ");
        super.onResume();
        binding.cameraView.onResume();
        startCamera();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: ");
        super.onPause();
        binding.cameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeepManager.release();
    }

    private void switchMode() {
        mNeedRender = !mNeedRender;
        Log.e(TAG, "switchMode: switch mode");
        if (mNeedRender) {
            Log.e(TAG, "switchMode: enable render");
            camView.enableRender();
        } else {
            Log.e(TAG, "switchMode: disable render");
            camView.disableRender();
        }
    }

    private void startCamera() {
        camView.startCamera();
        camView.startFpsListener((f, fps) -> {
            String fpsStr = String.format(Locale.getDefault(), "FPS:%d,Avg:%f", f, fps);
            Log.e(TAG, fpsStr);
            runOnUiThread(() -> binding.tvFps.setText(fpsStr));
        });
//        camView.enableAutoFocusTof();
        enableLaplacian();
        startScanBarcode();
    }

    private void stopCamera() {
        camView.stop();
    }

    private void startScanBarcode() {
        camView.startScanBarcode(new ScanCallback() {
            @Override
            public void onScanSuccess(List<ScanResult> list) {
                Log.e(TAG, String.format("onScanSuccess:get %d result(s):", list.size()));
                list.forEach(r -> Log.e(TAG, r.getRawValue()));
                mBeepManager.beep();
                //start SecondCameraActivity
//                Intent intent=new Intent(CameraActivity.this,SecondCameraActivity.class);
//                startActivity(intent);
            }

            @Override
            public void onScanFailed() {
                Log.d(TAG, "onScanFailed");
            }

            @Override
            public void onScanError(Exception e) {
                Log.e(TAG, "onScanError: ", e);
            }
        });
    }

    private void stopScanBarcode() {
        camView.stopScanBarcode();
    }

    private void startDetectIdCard() {
        camView.startDetectIdCard(id -> {
            Log.e(TAG, "getId:" + id.getId());
        });
    }

    private void stopDetectIdCard() {
        camView.stopDetectIdCard();
    }

    private void calibrate() {
        camView.startCalibration((dx, dy) -> {
            Toast.makeText(CameraActivity.this, String.format(Locale.getDefault(), "Calibrate success,dx=%d,dy=%d", dx, dy), Toast.LENGTH_SHORT).show();
            startScanBarcode();
        });
    }

    private void enableMask() {
        camView.enableMask();
    }

    private void disableMask() {
        camView.disableMask();
    }

    private void enableAutoFocus() {
        camView.enableAutoFocus();
    }

    private void disableAutoFocus() {
        camView.disableAutoFocus();
    }

    private void enableAutoFocusTof(){
        camView.enableAutoFocusTof();
    }

    private void disableAutoFocusTof(){
        camView.disableAutoFocusTof();
    }


    @SuppressLint("SetTextI18n")
    private void enableLaplacian() {
        camView.enableLaplacian(value -> {
            runOnUiThread(() -> binding.tvLap.setText("LAP:" + value));
            Log.e(TAG, "Laplacian:" + value);
        });
    }

    private void disableLaplacian() {
        camView.disableLaplacian();
    }

    private void setFocus40cm() {
        camView.setFocus(DeepSDK.FOCUS_40CM);
    }

}