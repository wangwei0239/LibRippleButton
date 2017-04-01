package com.jackwang.ripplebutton;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.jackwang.ripplebuttonlib.record_btn_anim.RippleAnimButton;

import java.util.ArrayList;

/**
 * Created by wangwei on 17/3/31.
 */

public class BDRecognitionListener implements RecognitionListener {

    public static String TAG = "RecognitionListener";

    public RippleAnimButton rippleAnimButton;

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(TAG, "onReadyForSpeech");
        rippleAnimButton.setState(RippleAnimButton.State.RIPPLE);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "onBeginningOfSpeech");

    }

    @Override
    public void onRmsChanged(float rmsdB) {
//        Log.i(TAG, "onRmsChanged");
        rippleAnimButton.onRmsChanged(rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(TAG, "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        Log.i(TAG, "onError");
        rippleAnimButton.resetButtonState();
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(TAG, "onResults:"+extractASR(results));
        rippleAnimButton.resetButtonState();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(TAG, "onPartialResults:"+extractASR(partialResults));
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i(TAG, "onEvent");
    }

    public String extractASR(Bundle partialResults){
        ArrayList<String> results = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        StringBuilder sb = new StringBuilder();
        for (String string : results) {
            if (string.trim().length() != 0) {
                sb.append(string);
            }
        }
        return sb.toString();
    }
}
