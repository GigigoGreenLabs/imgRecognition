package com.gigigo.imagerecognitioninterface;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 29/4/16.
 */
public interface ImageRecognition {
  <T> void setContextProvider(T contextProvider);
  void startImageRecognition(ImageRecognitionCredentials imageRecognitionCredentials);
}
