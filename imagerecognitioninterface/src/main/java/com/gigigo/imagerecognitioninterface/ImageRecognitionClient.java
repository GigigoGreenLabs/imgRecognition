package com.gigigo.imagerecognitioninterface;

import android.content.Context;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 29/4/16.
 */
public interface ImageRecognitionClient {
  ImageRecognitionCredentials obtainImageRecognitionCredentials();
  void recognizedPattern(String patternId);
  //Context getApplicationContext();
}
