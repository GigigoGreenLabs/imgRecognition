package com.gigigo.vuforiaimplementation;

import android.content.Intent;
import com.gigigo.ggglib.ContextProvider;
import com.gigigo.ggglib.permissions.AndroidPermissionCheckerImpl;
import com.gigigo.ggglib.permissions.Permission;
import com.gigigo.ggglib.permissions.PermissionChecker;
import com.gigigo.ggglib.permissions.UserPermissionRequestResponseListener;
import com.gigigo.imagerecognitioninterface.ImageRecognition;
import com.gigigo.imagerecognitioninterface.ImageRecognitionCredentials;
import com.gigigo.vuforiaimplementation.credentials.ParcelableIrCredentialsAdapter;
import com.gigigo.vuforiaimplementation.credentials.ParcelableVuforiaCredentials;
import com.gigigo.vuforiaimplementation.permissions.CameraPermissionImpl;

/**
 * This is a suitable implementation for image recognition module, in fact this this Vuforia
 * ImageRecognition interface specialization. An instance of this class would call Vuforia SDK
 * when startImageRecognition is called.
 *
 * This class is already managing Camera permissions implementation.
 */
public class ImageRecognitionVuforiaImpl implements ImageRecognition, UserPermissionRequestResponseListener {

  public static final String IMAGE_RECOGNITION_CREDENTIALS = "IMAGE_RECOGNITION_CREDENTIALS";

  private final ContextProvider contextProvider;
  private final PermissionChecker permissionChecker;
  private final Permission cameraPermission;

  private ParcelableVuforiaCredentials credentials;

  public ImageRecognitionVuforiaImpl(ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    this.permissionChecker = new AndroidPermissionCheckerImpl(contextProvider.getApplicationContext(), contextProvider);
    this.cameraPermission = new CameraPermissionImpl();
  }

  /**
   * Checks permissions and starts Image recognitio activity using given credentials. If Permissions
   * were not granted User will be notified. If credentials are not valid you'll have an error log
   * message.
   *
   * @param credentials interface implementation with Vuforia keys
   */
  @Override public void startImageRecognition(ImageRecognitionCredentials credentials) {
    this.credentials = digestCredentials(credentials);

    if (permissionChecker.isGranted(cameraPermission)) {
      startImageRecognitionActivity();
    } else {
      requestPermissions();
    }
  }

  @Override public void onPermissionAllowed(boolean permissionAllowed) {
    if (permissionAllowed) {
      startImageRecognitionActivity();
    }
  }

  private void requestPermissions() {
    if (contextProvider.isActivityContextAvailable()) {
          permissionChecker.askForPermission(cameraPermission, this, contextProvider.getCurrentActivity());
    }
  }

  private ParcelableVuforiaCredentials digestCredentials(
      ImageRecognitionCredentials externalCredentials) {
    ParcelableIrCredentialsAdapter adapter = new ParcelableIrCredentialsAdapter();
    ParcelableVuforiaCredentials credentials = adapter.getParcelableFromCredentialsForVuforia(externalCredentials);
    return credentials;
  }

  private void startImageRecognitionActivity() {
    Intent imageRecognitionIntent = new Intent(contextProvider.getApplicationContext(), VuforiaActivity.class);
    imageRecognitionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    imageRecognitionIntent.putExtra(IMAGE_RECOGNITION_CREDENTIALS, credentials);
    contextProvider.getApplicationContext().startActivity(imageRecognitionIntent);
  }

}
