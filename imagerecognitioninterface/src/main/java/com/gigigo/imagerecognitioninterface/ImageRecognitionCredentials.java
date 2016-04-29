package com.gigigo.imagerecognitioninterface;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 29/4/16.
 */
public interface ImageRecognitionCredentials {

  String getServerSecretKey();
  String getClientAccessKey();
  String getLicensekey();
  String getServerAccesskey();
  String getClientSecretKey();

}
