package com.gigigo.vuforiaimplementation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.gigigo.imagerecognitioninterface.ImageRecognitionConstants;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 29/4/16.
 */
public abstract class VuforiaRecognizedImageReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {
    vuforiaPatternRecognized(intent.getStringExtra(ImageRecognitionConstants.VUFORIA_PATTERN_ID));
  }

  public abstract void vuforiaPatternRecognized(String stringExtra);

}
