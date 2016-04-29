package com.gigigo.vuforiaimplementation.credentials;

import com.gigigo.imagerecognitioninterface.ImageRecognitionCredentials;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 29/4/16.
 */
public class ParcelableIrCredentialsAdapter {

  public ParcelableVuforiaCredentials getParcelableFromCredentialsForVuforia(ImageRecognitionCredentials irc) {

    return new ParcelableVuforiaCredentials(irc.getLicensekey(), irc.getClientAccessKey(),
        irc.getClientSecretKey(), irc.getServerAccesskey(), irc.getServerSecretKey() );
  }
}
