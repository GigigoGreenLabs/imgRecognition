package com.gigigo.vuforiaimplementation;

import android.content.Context;
import android.content.Intent;
import com.gigigo.imagerecognitioninterface.ImageRecognition;
import com.gigigo.imagerecognitioninterface.ImageRecognitionClient;
import com.gigigo.imagerecognitioninterface.ImageRecognitionCredentials;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 29/4/16.
 */
public class ImageRecognitionVuforiaImpl implements ImageRecognition {

  private static final String IMAGE_RECOGNITION_CREDENTIALS = "IMAGE_RECOGNITION_CREDENTIALS";
  private static ImageRecognitionClient imageRecognitionClient;
  private Context context;

  public ImageRecognitionVuforiaImpl(Context context, ImageRecognitionClient imageRecognitionClient) {
    this.context = context.getApplicationContext();
    ImageRecognitionVuforiaImpl.imageRecognitionClient = imageRecognitionClient;
  }

  //@Override public void setImageRecognitionClient(ImageRecognitionClient imageRecognitionClient) {
  //  this.imageRecognitionClient = imageRecognitionClient;
  //}

  static ImageRecognitionClient getImageRecognitionInstance(){
    return imageRecognitionClient;
  }

  @Override public void startImageRecognition() {
    Intent imageRecognitionIntent = new Intent(context, VuforiaActivity.class);
    ImageRecognitionCredentials irc = imageRecognitionClient.obtainImageRecognitionCredentials();
    ParcelableIrCredentialsAdapter parcelableIrCredentialsAdapter = new ParcelableIrCredentialsAdapter();
    ParcelableVuforiaCredentials credentials = parcelableIrCredentialsAdapter.getParcelableFromCredentialsForVuforia(irc);
    imageRecognitionIntent.putExtra(IMAGE_RECOGNITION_CREDENTIALS, credentials);
    context.startActivity(imageRecognitionIntent);
  }



}
