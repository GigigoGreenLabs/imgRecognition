package com.gigigo.vuforiaimplementation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gigigo.ggglogger.GGGLogImpl;
import com.gigigo.ggglogger.LogLevel;
import com.gigigo.imagerecognitioninterface.ImageRecognitionConstants;
import com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition.CloudRecognitionActivityLifeCycleCallBack;
import com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition.ICloudRecognitionCommunicator;
import com.gigigo.vuforiaimplementation.credentials.ParcelableVuforiaCredentials;
import com.vuforia.Trackable;

/**
 * Created by ASV on 20/10/2015.
 */
public class VuforiaActivity extends AppCompatActivity implements ICloudRecognitionCommunicator {

  private static final String RECOGNIZED_IMAGE_INTENT = "com.gigigo.imagerecognition.intent.action.RECOGNIZED_IMAGE";

  //for my vuforia Activity implementation
  private ImageView btnCloseVuforia;
  private TextView tvTitleVuforia;
  private Toolbar mToolbar;
  private String mActivityTitle;

  //basics for any vuforia activity
  private View mView;
  private static CloudRecognitionActivityLifeCycleCallBack mCloudRecoCallBack;

  public VuforiaActivity() {
    this.mActivityTitle = "Image Recognition";
  }

  @Override protected void onCreate(Bundle state) {
    super.onCreate(state);
    GGGLogImpl.log("VuforiaActivity.onCreate");
    initVuforiaKeys(getIntent());
  }

  //region implements CloudRecoCommunicator ands initializations calls
  private void initVuforiaKeys(Intent intent) {
    ParcelableVuforiaCredentials parcelableVuforiaCredentials =
        intent.getParcelableExtra(ImageRecognitionVuforiaImpl.IMAGE_RECOGNITION_CREDENTIALS);

    mCloudRecoCallBack = new CloudRecognitionActivityLifeCycleCallBack(this, this,
        parcelableVuforiaCredentials.getClientAccessKey(),
        parcelableVuforiaCredentials.getClientSecretKey(),
        parcelableVuforiaCredentials.getLicenseKey(), false);

  }

  /**
   * @Deprecated
   */
  private void setThemeColorScheme() {
        if (this.mCloudRecoCallBack != null) {
            try {
                ActionBar actionBar = getSupportActionBar();

                if (actionBar != null) {
                    actionBar.hide();
                }
                //TODO review following Line
                //btnCloseVuforia.setImageTintList(new ColorStateList.createFromXml(R.color.close_button,null,null));
                this.mCloudRecoCallBack.setUIPointColor(ContextCompat.getColor(this, R.color.img_recognition_primary_color));
                this.mCloudRecoCallBack.setUIScanLineColor(ContextCompat.getColor(this, R.color.img_recognition_secondary_color));
            } catch (IllegalArgumentException e) {
                GGGLogImpl.log(e.getMessage(), LogLevel.ERROR);
            }
        }
    }

  @Override public void setContentViewTop() {

    LayoutInflater inflater =
        (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mView = inflater.inflate(R.layout.activity_vuforia, null);
    try {
      addContentView(mView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT));
    } catch (Exception e) {
      e.printStackTrace();
    }

    //region Button Close
    btnCloseVuforia = (ImageView) mView.findViewById(R.id.btnCloseVuforia);

    btnCloseVuforia.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    //endregion

    //region TextView Title
    if (this.mActivityTitle != null) {
      tvTitleVuforia = (TextView) mView.findViewById(R.id.tvTitleVuforia);
      tvTitleVuforia.setText(this.mActivityTitle);
    }
    //endregion

    mToolbar = (Toolbar) mView.findViewById(R.id.toolbar_sdkirorchextra);

    setThemeColorScheme();

  }

  @Override public void onVuforiaResult(Trackable trackable, String uniqueId) {
    sendRecognizedPatternToClient(uniqueId);
  }

  private void sendRecognizedPatternToClient(String uniqueId) {
    Intent i = new Intent();
    i.putExtra(ImageRecognitionConstants.VUFORIA_PATTERN_ID, uniqueId);
    i.setAction(RECOGNIZED_IMAGE_INTENT);
    this.sendBroadcast(i);
  }
}
