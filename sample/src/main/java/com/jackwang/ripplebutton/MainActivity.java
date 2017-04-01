package com.jackwang.ripplebutton;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.baidu.speech.VoiceRecognitionService;
import com.jackwang.ripplebuttonlib.record_btn_anim.RippleAnimButton;

/**
 * Created by wangwei on 17/4/1.
 */

public class MainActivity extends AppCompatActivity {

    private SpeechRecognizer speechRecognizer;
    private BDRecognitionListener bdRecognitionListener;
    private int REQUEST_CODE = 1;
    private RippleAnimButton rippleAnimButton;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        bdRecognitionListener = new BDRecognitionListener(){
            @Override
            public void onResults(Bundle results) {
                super.onResults(results);
                tv.setText(extractASR(results));
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                super.onPartialResults(partialResults);
                tv.setText(extractASR(partialResults));
            }
        };
        // 注册监听器
        speechRecognizer.setRecognitionListener(bdRecognitionListener);
        requestPermission();
        rippleAnimButton = (RippleAnimButton) findViewById(R.id.btn);
        bdRecognitionListener.rippleAnimButton = rippleAnimButton;
        rippleAnimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startASR();
            }
        });
    }

    public void requestPermission(){
        //申请WRITE_EXTERNAL_STORAGE权限
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);

    }

    public void startASR() {
        Intent intent = new Intent();
        intent.putExtra("sample", 16000); // 离线仅支持16000采样率
        intent.putExtra("language", "cmn-Hans-CN"); // 离线仅支持中文普通话
        speechRecognizer.startListening(intent);
    }

}
