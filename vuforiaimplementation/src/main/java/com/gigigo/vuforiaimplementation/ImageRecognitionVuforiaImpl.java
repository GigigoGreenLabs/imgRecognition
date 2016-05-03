package com.gigigo.vuforiaimplementation;

import android.content.Context;
import android.content.Intent;
import com.gigigo.imagerecognitioninterface.ImageRecognition;
import com.gigigo.imagerecognitioninterface.ImageRecognitionCredentials;
import com.gigigo.vuforiaimplementation.credentials.ParcelableIrCredentialsAdapter;
import com.gigigo.vuforiaimplementation.credentials.ParcelableVuforiaCredentials;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 29/4/16.
 */
public class ImageRecognitionVuforiaImpl implements ImageRecognition {

  public static final String IMAGE_RECOGNITION_CREDENTIALS = "IMAGE_RECOGNITION_CREDENTIALS";
  private Context context;

  public ImageRecognitionVuforiaImpl(Context context) {
    this.context = context.getApplicationContext();
  }

  @Override public void startImageRecognition(ImageRecognitionCredentials imageRecognitionCredentials) {
    Intent imageRecognitionIntent = new Intent(context, VuforiaActivity.class);
    ParcelableIrCredentialsAdapter adapter = new ParcelableIrCredentialsAdapter();
    ParcelableVuforiaCredentials credentials = adapter.getParcelableFromCredentialsForVuforia(imageRecognitionCredentials);
    imageRecognitionIntent.putExtra(IMAGE_RECOGNITION_CREDENTIALS, credentials);
    context.startActivity(imageRecognitionIntent);
  }


}
