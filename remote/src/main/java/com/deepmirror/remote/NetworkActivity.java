//package com.deepmirror.remote;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.inputmethod.EditorInfo;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.deepmirror.deepsdk.io.socket.UdpManager;
//import com.deepmirror.deepsdk.protocol.exception.ProtocolException;
//import com.deepmirror.deepsdk.protocol.pack.VolumeTAS;
//import com.deepmirror.remote.databinding.ActivityNetworkBinding;
//
//public class NetworkActivity extends AppCompatActivity {
//    public static final String TAG = NetworkActivity.class.getSimpleName();
//    private ActivityNetworkBinding binding;
//
//    private int sbBrightCurrentMin = 0X1C;
//    private int sbBrightPWMMin = 0x100;
//    private int sbBrightPWMMulti = 10;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        initView();
//
//    }
//
//    private void initView() {
//        binding = ActivityNetworkBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        binding.btnStart.setOnClickListener(onClickListener);
//
//        binding.btnGetVolume.setOnClickListener(onClickListener);
//        binding.btnGetMax.setOnClickListener(onClickListener);
//        binding.btnVolRaise.setOnClickListener(onClickListener);
//        binding.btnVolLower.setOnClickListener(onClickListener);
//        binding.btnMute.setOnClickListener(onClickListener);
//        binding.btnMuteMax.setOnClickListener(onClickListener);
//        binding.btnGetBrightCurrent.setOnClickListener(onClickListener);
//        binding.btnGetBrightPwm.setOnClickListener(onClickListener);
//
//        binding.sbVol.setMax(15);
//        binding.sbVol.setOnSeekBarChangeListener(onSeekBarChangeListener);
//
//        binding.sbVolMax.setMax(5);
//        binding.sbVolMax.setOnSeekBarChangeListener(onSeekBarChangeListener);
//
//        binding.sbVolAttn.setMax(127);
//        binding.sbVolAttn.setOnSeekBarChangeListener(onSeekBarChangeListener);
//
//
//        binding.sbBrightCurrent.setMax(0x80 - sbBrightCurrentMin);
//        binding.sbBrightCurrent.setOnSeekBarChangeListener(onSeekBarChangeListener);
//
//        binding.sbBrightPwm.setMax((0x6D8 - sbBrightPWMMin) / sbBrightPWMMulti);
//        binding.sbBrightPwm.setOnSeekBarChangeListener(onSeekBarChangeListener);
//
//        binding.etVol.setOnEditorActionListener(onEditorActionListener);
//
//        binding.btnGetFocus.setOnClickListener(onClickListener);
//        binding.btnSetFocus.setOnClickListener(onClickListener);
//        binding.btnGetFocusP0.setOnClickListener(onClickListener);
//        binding.btnSetFocusP0.setOnClickListener(onClickListener);
//        binding.btnGetFocusP1.setOnClickListener(onClickListener);
//        binding.btnSetFocusP1.setOnClickListener(onClickListener);
////        binding.etFocus.setOnEditorActionListener(onEditorActionListener);
////        binding.etFocusP0.setOnEditorActionListener(onEditorActionListener);
////        binding.etFocusP1.setOnEditorActionListener(onEditorActionListener);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        UdpManager.getInstance().start();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        UdpManager.getInstance().stop();
//    }
//
//    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
//        @Override
//        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//            try {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    if (v.getId() == binding.etVol.getId()) {
//                        byte value = Byte.parseByte(binding.etVol.getText().toString());
//                        UdpManager.getInstance().setVolume(value);
//                        runOnUiThread(() -> binding.sbVol.setProgress(value));
//
//                    } else if (v.getId() == binding.etVolMax.getId() || v.getId() == binding.etVolAttn.getId()) {
//                        byte base = Byte.parseByte(binding.etVolMax.getText().toString());
//                        byte attn = Byte.parseByte(binding.etVolAttn.getText().toString());
//                        UdpManager.getInstance().setVolumeTas(base, attn);
//                        runOnUiThread(() -> {
//                            binding.sbVolMax.setProgress(base);
//                            binding.sbVolAttn.setProgress(attn);
//                        });
//                    }
//                }
//            } catch (ProtocolException e) {
//                showError(e);
//            }
//            return false;
//        }
//    };
//    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if (!fromUser) return;
//            if (seekBar.getId() == binding.sbVol.getId()) {
//                runOnUiThread(() -> binding.etVol.setText(String.valueOf(progress)));
//            } else if (seekBar.getId() == binding.sbVolMax.getId()) {
//                runOnUiThread(() -> binding.etVolMax.setText(String.valueOf(progress)));
//            } else if (seekBar.getId() == binding.sbVolAttn.getId()) {
//                runOnUiThread(() -> binding.etVolAttn.setText(String.valueOf(progress)));
//            } else if (seekBar.getId() == binding.sbBrightCurrent.getId()) {
//                runOnUiThread(() -> binding.etBrightCurrentValue.setText(String.valueOf(progress + sbBrightCurrentMin)));
//            } else if (seekBar.getId() == binding.sbBrightPwm.getId()) {
//                runOnUiThread(() -> binding.etBrightPwmValue.setText(String.valueOf(progress * sbBrightPWMMulti + sbBrightPWMMin)));
//            }
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            try {
//                if (seekBar.getId() == binding.sbVol.getId()) {
//                    UdpManager.getInstance().setVolume((byte) seekBar.getProgress());
//                } else if ((seekBar.getId() == binding.sbVolMax.getId()) || (seekBar.getId() == binding.sbVolAttn.getId())) {
//                    UdpManager.getInstance().setVolumeTas((byte) binding.sbVolMax.getProgress(), (byte) binding.sbVolAttn.getProgress());
//                } else if (seekBar.getId() == binding.sbBrightCurrent.getId()) {
//                    UdpManager.getInstance().setOptoBrightnessCurrent(seekBar.getProgress() + sbBrightCurrentMin);
//                } else if (seekBar.getId() == binding.sbBrightPwm.getId()) {
//                    UdpManager.getInstance().setOptoBrightnessPWM(seekBar.getProgress() * sbBrightPWMMulti + sbBrightPWMMin);
//                }
//            } catch (ProtocolException e) {
//                showError(e);
//            }
//        }
//    };
//
//    private View.OnClickListener onClickListener = v -> {
//        try {
//            if (v.getId() == binding.btnStart.getId()) {
//                UdpManager.getInstance().registerIp();
//                runOnUiThread(() -> Toast.makeText(this, "start success", Toast.LENGTH_SHORT).show());
//
//            } else if (v.getId() == binding.btnGetVolume.getId()) {
//                int volume = UdpManager.getInstance().getVolume();
//                runOnUiThread(() -> {
//                    binding.sbVol.setProgress(volume);
//                    binding.etVol.setText(String.valueOf(volume));
//                });
//            } else if (v.getId() == binding.btnGetMax.getId()) {
//                VolumeTAS volumeTAS = UdpManager.getInstance().getVolumeTas();
//                runOnUiThread(() -> {
//                    Log.e(TAG, String.format("vol base:%d,attn:%d", volumeTAS.base, volumeTAS.attn));
//                    binding.sbVolMax.setProgress(volumeTAS.base);
//                    binding.sbVolAttn.setProgress(volumeTAS.attn);
//                    binding.etVolMax.setText(String.valueOf(volumeTAS.base));
//                    binding.etVolAttn.setText(String.valueOf(volumeTAS.attn));
//                });
//            } else if (v.getId() == binding.btnMute.getId()) {
//                UdpManager.getInstance().mute();
//                runOnUiThread(() -> {
//                    binding.sbVol.setProgress(0);
//                    binding.etVol.setText(String.valueOf(0));
//                });
//            } else if (v.getId() == binding.btnMuteMax.getId()) {
//                UdpManager.getInstance().setVolumeTas((byte) 0, (byte) 0);
//                runOnUiThread(() -> {
//                    binding.sbVolMax.setProgress(0);
//                    binding.etVolMax.setText(String.valueOf(0));
//                    binding.sbVolAttn.setProgress(0);
//                    binding.etVolAttn.setText(String.valueOf(0));
//                });
//
//            } else if (v.getId() == binding.btnGetBrightCurrent.getId()) {
//                int value = UdpManager.getInstance().getOptoBrightnessCurrent();
//                runOnUiThread(() -> {
//                    int t = value - sbBrightCurrentMin;
//                    binding.sbBrightCurrent.setProgress(t);
//                    binding.etBrightCurrentValue.setText(String.valueOf(value));
//                });
//            } else if (v.getId() == binding.btnGetBrightPwm.getId()) {
//                int value = UdpManager.getInstance().getOptoBrightnessPWM();
//                runOnUiThread(() -> {
//                    int t = (value - sbBrightPWMMin) / sbBrightPWMMulti;
//                    binding.sbBrightPwm.setProgress(t);
//                    binding.etBrightPwmValue.setText(String.valueOf(value));
//                });
//            } else if (v.getId() == binding.btnGetFocus.getId()) {
//                int focus = UdpManager.getInstance().getFocusLength();
//                runOnUiThread(() -> binding.etFocus.setText(String.valueOf(focus)));
//            } else if (v.getId() == binding.btnSetFocus.getId()) {
//                int focus = Integer.parseInt(binding.etFocus.getText().toString());
//                UdpManager.getInstance().setFocusLength(focus);
//            } else if (v.getId() == binding.btnGetFocusP0.getId()) {
//                getFocusParam(0);
//            } else if (v.getId() == binding.btnGetFocusP1.getId()) {
//                getFocusParam(1);
//            } else if (v.getId() == binding.btnSetFocusP0.getId()) {
//                setFocusParam(0);
//            } else if (v.getId() == binding.btnSetFocusP1.getId()) {
//                setFocusParam(1);
//            }
//        } catch (ProtocolException e) {
//            showError(e);
//        }
//    };
//
//    private void getFocusParam(int index) {
//        try {
//            int focus = UdpManager.getInstance().getParam(index);
//            runOnUiThread(() -> {
//                switch (index) {
//                    case 0:
//                        binding.etFocusP0.setText(String.valueOf(focus));
//                        break;
//                    case 1:
//                        binding.etFocusP1.setText(String.valueOf(focus));
//                        break;
//                }
//            });
//        } catch (ProtocolException e) {
//            showError(e);
//        }
//
//    }
//
//    private void setFocusParam(int index) {
//        int value = 0;
//        switch (index) {
//            case 0:
//                value = Integer.parseInt(binding.etFocusP0.getText().toString());
//                break;
//            case 1:
//                value = Integer.parseInt(binding.etFocusP1.getText().toString());
//                break;
//        }
//        try {
//            UdpManager.getInstance().saveParam(index, value);
//        } catch (ProtocolException e) {
//            showError(e);
//        }
//    }
//
//    private void showError(ProtocolException e) {
//        runOnUiThread(() -> Toast.makeText(this, "get error:" + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//}
