package com.gigigo.imagerecognitioninterface;


/**
 * Created by Sergio Martinez Rodriguez
 * Date 29/4/16.
 */
public interface ImageRecognitionClient {

  ImageRecognitionCredentials obtainImageRecognitionCredentials();
  void recognizedPattern(String patternId);

}
