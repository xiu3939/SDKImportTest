package com.deepmirror.remote;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.deepmirror.deepsdk.camera.CropParam;
import com.deepmirror.deepsdk.camera.DefaultParam;
import com.deepmirror.deepsdk.io.bluetooth.BluetoothManager;
import com.deepmirror.deepsdk.io.wireless.NetworkManager;
import com.deepmirror.deepsdk.protocol.exception.ProtocolException;
import com.deepmirror.deepsdk.protocol.pack.CameraOffset;
import com.deepmirror.deepsdk.protocol.pack.CameraViewPos;
import com.deepmirror.remote.databinding.ActivityBluetoothBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BluetoothActivity extends AppCompatActivity {
    public static final String TAG = BluetoothActivity.class.getSimpleName();
    private ActivityBluetoothBinding binding;
    private DeviceAdapter mDeviceAdapter;
    private List<BluetoothDevice> deviceList;
    private final int requestCode = 100;

    private static final float RATIO = 4f / 3;
    private int camX, camY, camW, camH;
    private int cropDx, cropDy;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    Log.e(TAG, "find new device:" + device.getAddress());
                    deviceList.add(device);
                    mDeviceAdapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                stopListenForScanResult();
                Log.e(TAG, "finish discovery");
                runOnUiThread(() -> Toast.makeText(context, "finish discovery", Toast.LENGTH_SHORT).show());
                if (deviceList.size() == 0) {
                    Log.e(TAG, "no device");
                }

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestPermission();
        initView();
    }

    private void initView() {
        binding = ActivityBluetoothBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnEnableBt.setOnClickListener(onClickListener);
        binding.btnDisableBt.setOnClickListener(onClickListener);
        binding.btnScan.setOnClickListener(onClickListener);

        binding.btnEnableWifi.setOnClickListener(onClickListener);
        binding.btnConnectWifi.setOnClickListener(onClickListener);

        binding.sbX.setMax(Resources.getSystem().getDisplayMetrics().widthPixels);
        binding.sbX.setOnSeekBarChangeListener(seekBarChangeListener);

        binding.sbY.setMax(Resources.getSystem().getDisplayMetrics().heightPixels);
        binding.sbY.setOnSeekBarChangeListener(seekBarChangeListener);

        binding.sbW.setMax(CropParam.getDefaultCropParam().getW());
        binding.sbW.setOnSeekBarChangeListener(seekBarChangeListener);

        binding.sbH.setMax(CropParam.getDefaultCropParam().getH());
        binding.sbH.setOnSeekBarChangeListener(seekBarChangeListener);

        binding.sbDx.setMax(DefaultParam.CAM_WIDTH - CropParam.getDefaultCropParam().getW());
        binding.sbDx.setOnSeekBarChangeListener(seekBarChangeListener);

        binding.sbDy.setMax(DefaultParam.CAM_HEIGHT - CropParam.getDefaultCropParam().getH());
        binding.sbDy.setOnSeekBarChangeListener(seekBarChangeListener);

        binding.btnGetViewPos.setOnClickListener(onClickListener);
        binding.btnGetCamOffset.setOnClickListener(onClickListener);

        binding.btnShowParams.setOnClickListener(onClickListener);
    }

    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_ADMIN}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == this.requestCode) {
            for (int res : grantResults) {
                if (res != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "permission not allowed");
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BluetoothManager.getInstance().setBluetoothListener(bluetoothListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BluetoothManager.getInstance().setBluetoothListener(null);
    }

    private void showInputPasswordDialog() {
        final EditText editText = new EditText(BluetoothActivity.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(BluetoothActivity.this);
        inputDialog.setTitle("Input password").setView(editText);
        inputDialog.setPositiveButton("confirm", (dialog, which) -> {
            try {
                BluetoothManager.getInstance().connectWifi(NetworkManager.getInstance().getCurrentSSID(), editText.getText().toString());
            } catch (ProtocolException e) {
                runOnUiThread(() -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).show();
    }


    private BluetoothManager.BluetoothListener bluetoothListener = new BluetoothManager.BluetoothListener() {
        @Override
        public void onStateChanged(int i) {
            switch (i) {
                case BluetoothManager.STATE_NONE:
                    runOnUiThread(() -> binding.tvStatus.setText("NONE"));

                    break;
                case BluetoothManager.STATE_LISTEN:
                    runOnUiThread(() -> binding.tvStatus.setText("LISTEN"));

                    break;
                case BluetoothManager.STATE_CONNECTING:
                    runOnUiThread(() -> binding.tvStatus.setText("CONNECTING"));
                    break;
                case BluetoothManager.STATE_CONNECTED:
                    runOnUiThread(() -> binding.tvStatus.setText("CONNECTED"));
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

//    private StatusCallback versionCallback = new StatusCallback() {
//        @Override
//        protected void onValue(Object o) {
//            Log.e(TAG, "versionCallback$onValue: " + String.valueOf((byte) o));
//        }
//    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                if (view.getId() == binding.btnEnableBt.getId()) {
                    BluetoothManager.getInstance().enableBluetooth();
                } else if (view.getId() == binding.btnDisableBt.getId()) {
                    BluetoothManager.getInstance().disableBluetooth();
                } else if (view.getId() == binding.btnEnableWifi.getId()) {
                    BluetoothManager.getInstance().enableWifi();
                } else if (view.getId() == binding.btnConnectWifi.getId()) {
                    showInputPasswordDialog();
                } else if (view.getId() == binding.btnScan.getId()) {
                    if (!BluetoothManager.getInstance().isDiscovering()) {
                        startListenForScanResult();
                        BluetoothManager.getInstance().startDiscovery();

                        deviceList = new ArrayList<>();
                        mDeviceAdapter = new DeviceAdapter(deviceList, getLayoutInflater());
                        binding.lvScan.setAdapter(mDeviceAdapter);
                        binding.lvScan.setOnItemClickListener((adapterView, view1, position, id) -> {
                            Log.e(TAG, "onItemClick: try to connect...");
                            BluetoothManager.getInstance().connect(deviceList.get(position));
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(BluetoothActivity.this, "discovering", Toast.LENGTH_SHORT).show());
                    }
                } else if (view.getId() == binding.btnGetViewPos.getId()) {
                    CameraViewPos pos = BluetoothManager.getInstance().getCameraViewPos();
                    runOnUiThread(() -> {
                        Toast.makeText(BluetoothActivity.this,
                                String.format(Locale.getDefault(), "get cameraView: x=%d,y=%d,w=%d,h=%d", pos.x, pos.y, pos.w, pos.h), Toast.LENGTH_LONG).show();
                        binding.sbX.setProgress(pos.x);
                        binding.sbY.setProgress(pos.y);
                        binding.sbW.setProgress(pos.w);
                        binding.sbH.setProgress(pos.h);
                        camX = pos.x;
                        camY = pos.y;
                        camW = pos.w;
                        camH = pos.h;
                    });
                } else if (view.getId() == binding.btnGetCamOffset.getId()) {
                    CameraOffset offset = BluetoothManager.getInstance().getCameraOffset();
                    runOnUiThread(() -> {
                        Toast.makeText(BluetoothActivity.this, String.format(Locale.getDefault(), "get cameraOffset: dx=%d,dy=%d", offset.dx, offset.dy), Toast.LENGTH_LONG).show();
                        binding.sbDx.setProgress(offset.dx);
                        binding.sbDy.setProgress(offset.dy);
                        cropDx = offset.dx;
                        cropDy = offset.dy;
                    });
                } else if (view.getId() == binding.btnShowParams.getId()) {
                    runOnUiThread(() -> Toast.makeText(BluetoothActivity.this,
                            String.format(Locale.getDefault(), "x=%d,y=%d,w=%d,h=%d,dx=%d,dy=%d", camX, camY, camW, camH, cropDx, cropDy), Toast.LENGTH_LONG).show());
                }
            } catch (ProtocolException e) {
                runOnUiThread(() -> Toast.makeText(BluetoothActivity.this, "get error:" + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            try {
                int i = seekBar.getProgress();
                if (seekBar.getId() == binding.sbDx.getId()) {
                    cropDx = i;
                    BluetoothManager.getInstance().setCameraOffset(cropDx, cropDy);
                } else if (seekBar.getId() == binding.sbDy.getId()) {
                    cropDy = i;
                    BluetoothManager.getInstance().setCameraOffset(cropDx, cropDy);
                } else {
                    if (seekBar.getId() == binding.sbW.getId()) {
                        camW = i;
                        camH = (int) (i / RATIO);
                        binding.sbH.setProgress(camH);
                    } else if (seekBar.getId() == binding.sbH.getId()) {
                        camH = i;
                        camW = (int) (i * RATIO);
                        binding.sbW.setProgress(camW);
                    } else if (seekBar.getId() == binding.sbX.getId()) {
                        camX = i;
                    } else if (seekBar.getId() == binding.sbY.getId()) {
                        camY = i;
                    }
                    BluetoothManager.getInstance().setCameraViewPos(camX, camY, camW, camH);
                }
            } catch (ProtocolException e) {
                runOnUiThread(() -> Toast.makeText(BluetoothActivity.this, "get error:" + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    };


    private void startListenForScanResult() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    private void stopListenForScanResult() {
        unregisterReceiver(mReceiver);
    }
}
