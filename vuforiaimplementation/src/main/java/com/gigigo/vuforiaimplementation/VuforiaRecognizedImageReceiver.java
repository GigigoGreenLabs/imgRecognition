package com.gigigo.vuforiaimplementation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.gigigo.imagerecognitioninterface.ImageRecognitionClient;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 29/4/16.
 */
public class VuforiaRecognizedImageReceiver extends BroadcastReceiver {

  public static final String RECOGNIZED_IMAGE_INTENT = "com.gigigo.imagerecognition.intent.action.RECOGNIZED_IMAGE";
  public static final String PATTERN_ID = "PATTERN_ID";
  private final ImageRecognitionClient imageRecognitionClient;

  public VuforiaRecognizedImageReceiver(ImageRecognitionClient imageRecognitionClient) {
    this.imageRecognitionClient = imageRecognitionClient;
  }

  @Override public void onReceive(Context context, Intent intent) {
    imageRecognitionClient.recognizedPattern(intent.getStringExtra(PATTERN_ID));
  }
}
