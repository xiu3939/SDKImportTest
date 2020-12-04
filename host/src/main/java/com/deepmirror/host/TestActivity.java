package com.deepmirror.host;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.deepmirror.deepsdk.DeepSDK;
import com.deepmirror.deepsdk.common.Utils;
import com.deepmirror.deepsdk.io.socket.UdpManager;
import com.deepmirror.deepsdk.protocol.ProtocolManager;
import com.deepmirror.deepsdk.protocol.pack.CameraOffset;
import com.deepmirror.deepsdk.protocol.pack.CameraViewPos;
import com.deepmirror.host.databinding.ActivityTestBinding;

public class TestActivity extends AppCompatActivity {
    public static final String TAG = TestActivity.class.getSimpleName();
    private ActivityTestBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
    }

    private void initView() {
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        binding.btnGetFrame.setOnClickListener(v -> getFrame());
        binding.btnGetFrame.setOnClickListener(v -> scanID());
    }

    @Override
    protected void onResume() {
        super.onResume();

        UdpManager.getInstance().start();
        startCamera();
        binding.cameraView.onResume();
        binding.cameraView.enableRender();
        DeepSDK.getInstance().enableProtocol(protocolTask);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DeepSDK.getInstance().disableProtocol();
//        UdpManager.getInstance().stop();
        binding.cameraView.onPause();
    }

    ProtocolManager.ProtocolTask protocolTask = new ProtocolManager.ProtocolTask() {
        @Override
        public CameraViewPos getCameraViewPos() {
            Log.e(TAG, "getCameraViewPos ");
            return new CameraViewPos((int) binding.cameraView.getX(), (int) binding.cameraView.getY(), binding.cameraView.getWidth(), binding.cameraView.getHeight());
        }

        @Override
        public void setCameraViewPos(CameraViewPos cameraViewPos) {
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
            Log.e(TAG, "getCameraOffset");
            return new CameraOffset(binding.cameraView.getCameraCropDx(), binding.cameraView.getCameraCropDy());
        }

        @Override
        public void setCameraOffset(CameraOffset cameraOffset) {
            Log.e(TAG, "setCameraOffset");
            binding.cameraView.updateCameraCropOffset(cameraOffset.dx, cameraOffset.dy);
        }
    };

    private void startCamera() {
        binding.cameraView.startCamera();
        binding.cameraView.updateCameraCropOffset(1180, 800);
    }

    private void getFrame() {
        binding.cameraView.getFrameOnce(Utils::saveBinary);
    }

    private void scanID() {
        binding.cameraView.startDetectIdCard(info -> Log.e(TAG, "scanID: " + info.getId()));
    }
}
