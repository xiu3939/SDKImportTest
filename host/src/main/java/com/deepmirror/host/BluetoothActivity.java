package com.deepmirror.host;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.deepmirror.deepsdk.DeepSDK;
import com.deepmirror.deepsdk.io.bluetooth.BluetoothManager;
import com.deepmirror.deepsdk.protocol.ProtocolManager;
import com.deepmirror.deepsdk.protocol.pack.CameraOffset;
import com.deepmirror.deepsdk.protocol.pack.CameraViewPos;
import com.deepmirror.host.databinding.ActivityBluetoothBinding;


public class BluetoothActivity extends AppCompatActivity {
    public static final String TAG = BluetoothActivity.class.getSimpleName();
    private ActivityBluetoothBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
    }

    private void initView() {
        binding = ActivityBluetoothBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnEnableBt.setOnClickListener(onClickListener);
        binding.btnDisableBt.setOnClickListener(onClickListener);
        binding.btnServerStart.setOnClickListener(onClickListener);
        binding.btnServerStop.setOnClickListener(onClickListener);
        binding.btnGetMode.setOnClickListener(onClickListener);
        binding.btnSetMode.setOnClickListener(onClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.cameraView.onResume();
        binding.cameraView.enableRender();
        startCamera();
        BluetoothManager.getInstance().setBluetoothListener(bluetoothListener);
        if (BluetoothManager.getInstance().isEnabled()) {
            BluetoothManager.getInstance().start();
        }
        DeepSDK.getInstance().enableProtocol(protocolTask);
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.cameraView.onPause();
        BluetoothManager.getInstance().setBluetoothListener(null);
        BluetoothManager.getInstance().stop();
        DeepSDK.getInstance().disableProtocol();
    }

    private void startCamera() {
        binding.cameraView.startCamera();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == binding.btnEnableBt.getId()) {
                BluetoothManager.getInstance().enableBluetooth();
            } else if (v.getId() == binding.btnDisableBt.getId()) {
                BluetoothManager.getInstance().disableBluetooth();
            } else if (v.getId() == binding.btnServerStart.getId()) {
                BluetoothManager.getInstance().start();
            } else if (v.getId() == binding.btnServerStop.getId()) {
                BluetoothManager.getInstance().stop();
            } else if (v.getId() == binding.btnGetMode.getId()) {
                runOnUiThread(() ->{
                    if (BluetoothManager.getInstance().getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                        Toast.makeText(BluetoothActivity.this, "not discoverable", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(BluetoothActivity.this, "discoverable", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (v.getId() == binding.btnSetMode.getId()) {
                //enable discoverable
                Log.e(TAG, "set discoverable:" + BluetoothManager.getInstance().setDiscoverable());
            }
        }
    };

    private BluetoothManager.BluetoothListener bluetoothListener = new BluetoothManager.BluetoothListener() {
        @Override
        public void onStateChanged(int i) {
            switch (i) {
                case BluetoothManager.STATE_NONE:
                    binding.tvStatus.setText("NONE");
                    break;
                case BluetoothManager.STATE_LISTEN:
                    binding.tvStatus.setText("LISTEN");
                    break;
                case BluetoothManager.STATE_CONNECTING:
                    binding.tvStatus.setText("CONNECTING");
                    break;
                case BluetoothManager.STATE_CONNECTED:
                    binding.tvStatus.setText("CONNECTED");
                    break;
            }
        }

        @Override
        public void onConnectionLost() {
            runOnUiThread(() -> binding.tvConnected.setText(""));
        }

        @Override
        public void onConnectionFailed() {

        }

        @Override
        public void onConnected(String s, String s1) {
            runOnUiThread(() -> binding.tvConnected.setText(String.format("name=%s,mac=%s", s, s1)));
        }

    };


    ProtocolManager.ProtocolTask protocolTask = new ProtocolManager.ProtocolTask() {
        @Override
        public CameraViewPos getCameraViewPos() {
            return new CameraViewPos((int) binding.cameraView.getX(), (int) binding.cameraView.getY(), binding.cameraView.getWidth(), binding.cameraView.getHeight());
        }

        @Override
        public void setCameraViewPos(CameraViewPos cameraViewPos) {
            Log.e(TAG, String.format("setCameraViewPos:x=%d,y=%d,w=%d,h=%d ",cameraViewPos.x,cameraViewPos.y,cameraViewPos.w,cameraViewPos.h ));
            binding.cameraView.setX(cameraViewPos.x);
            binding.cameraView.setY(cameraViewPos.y);
            runOnUiThread(() -> {
                ViewGroup.LayoutParams params = binding.cameraView.getLayoutParams();
                params.width = cameraViewPos.w;
                params.height = cameraViewPos.h;
                binding.cameraView.setLayoutParams(params);
            });
        }

        @Override
        public CameraOffset getCameraOffset() {
            return new CameraOffset(binding.cameraView.getCameraCropDx(), binding.cameraView.getCameraCropDy());
        }

        @Override
        public void setCameraOffset(CameraOffset cameraOffset) {
            binding.cameraView.updateCameraCropOffset(cameraOffset.dx, cameraOffset.dy);
        }
    };


}
