package com.deepmirror.host;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.deepmirror.host.databinding.ActivityMainBinding;


/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        startActivity(binding.btnCameraActivity.getId());
    }

    private void initView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBluetoothActivity.setOnClickListener(onClickListener);
        binding.btnCameraActivity.setOnClickListener(onClickListener);
        binding.btnTestActivity.setOnClickListener(onClickListener);
    }

    private void startActivity(int viewId) {
        if (viewId == binding.btnBluetoothActivity.getId()) {
            startActivity(new Intent(MainActivity.this, BluetoothActivity.class));
        } else if (viewId == binding.btnCameraActivity.getId()) {
            startActivity(new Intent(MainActivity.this, CameraActivity.class));
        } else if (viewId == binding.btnTestActivity.getId()) {
            startActivity(new Intent(MainActivity.this, TestActivity.class));
        }
    }

    private View.OnClickListener onClickListener = view -> startActivity(view.getId());

}