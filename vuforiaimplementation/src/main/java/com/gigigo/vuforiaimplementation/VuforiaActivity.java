package com.gigigo.vuforiaimplementation;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import com.gigigo.ggglogger.GGGLogImpl;
import com.gigigo.ggglogger.LogLevel;
import com.gigigo.imagerecognitioninterface.ImageRecognitionConstants;
import com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition.CloudRecognitionActivityLifeCycleCallBack;
import com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition.ICloudRecognitionCommunicator;
import com.gigigo.vuforiaimplementation.credentials.ParcelableVuforiaCredentials;
import com.vuforia.Trackable;

public class VuforiaActivity extends AppCompatActivity implements ICloudRecognitionCommunicator {

  private static final String RECOGNIZED_IMAGE_INTENT = "com.gigigo.imagerecognition.intent.action.RECOGNIZED_IMAGE";

  //basics for any vuforia activity
  //private View mView;
  private static CloudRecognitionActivityLifeCycleCallBack mCloudRecoCallBack;

  @Override protected void onCreate(Bundle state) {
    super.onCreate(state);
    GGGLogImpl.log("VuforiaActivity.onCreate");

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
      hideActionBar();
    }

    initVuforiaKeys(getIntent());
  }

  //region implements CloudRecoCommunicator ands initializations calls
  private void initVuforiaKeys(Intent intent) {
    Bundle b = intent.getBundleExtra(ImageRecognitionVuforiaImpl.IMAGE_RECOGNITION_CREDENTIALS);
    ParcelableVuforiaCredentials parcelableVuforiaCredentials = b.getParcelable(ImageRecognitionVuforiaImpl.IMAGE_RECOGNITION_CREDENTIALS);

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
                this.mCloudRecoCallBack.setUIPointColor(ContextCompat.getColor(this, R.color.ir_scan_point_color));
                this.mCloudRecoCallBack.setUIScanLineColor(ContextCompat.getColor(this, R.color.ir_scan_line_color));
            } catch (IllegalArgumentException e) {
                GGGLogImpl.log(e.getMessage(), LogLevel.ERROR);
            }
        }
    }

  @Override public void setContentViewTop(View vuforiaView) {

    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View view = inflater.inflate(R.layout.activity_vuforia, null);

    RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.layoutContentVuforiaGL);
    relativeLayout.addView(vuforiaView, 0);

    ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT);

    addContentView(view, vlp);

    //region Button Close
    view.findViewById(R.id.btnCloseVuforia).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    //endregion

    setThemeColorScheme();

  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void hideActionBar(){
    ActionBar actionBar = getActionBar();

    if (actionBar != null){
      actionBar.hide();
    }

    android.support.v7.app.ActionBar actionBar1 = getSupportActionBar();

    if (actionBar1 != null){
      actionBar1.hide();
    }

  }

  @Override public void onVuforiaResult(Trackable trackable, String uniqueId) {
    sendRecognizedPatternToClient(uniqueId);
    finish();
  }

  private void sendRecognizedPatternToClient(String uniqueId) {
    Intent i = new Intent();
    i.putExtra(ImageRecognitionConstants.VUFORIA_PATTERN_ID, uniqueId);
    i.setAction(RECOGNIZED_IMAGE_INTENT);
    this.sendBroadcast(i);
  }
}
