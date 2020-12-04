package com.deepmirror.remote;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.deepmirror.deepsdk.system.AudioManager;
import com.deepmirror.remote.databinding.ActivityMainBinding;

import java.net.DatagramSocket;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        startActivity(binding.btnBluetoothActivity.getId());
    }

    private void initView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBluetoothActivity.setOnClickListener(onClickListener);
        binding.btnNetworkActivity.setOnClickListener(onClickListener);
    }

    private void startActivity(int viewId) {
        if (viewId == binding.btnBluetoothActivity.getId()) {
            startActivity(new Intent(MainActivity.this, BluetoothActivity.class));
        } else if (viewId == binding.btnNetworkActivity.getId()) {
//            startActivity(new Intent(MainActivity.this, NetworkActivity.class));
        }
    }

    private View.OnClickListener onClickListener = v -> startActivity(v.getId());


}
