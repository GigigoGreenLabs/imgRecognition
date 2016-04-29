package com.gigigo.vuforiaimplementation;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gigigo.ggglogger.GGGLogImpl;
import com.gigigo.imagerecognitioninterface.ImageRecognition;
import com.gigigo.imagerecognitioninterface.ImageRecognitionClient;
import com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition.CloudRecognitionActivityLifeCycleCallBack;
import com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition.ICloudRecognitionCommunicator;
import com.vuforia.Trackable;

/**
 * Created by ASV on 20/10/2015.
 */
public class VuforiaActivity extends AppCompatActivity implements ICloudRecognitionCommunicator {

  //for my vuforia Activity implementation
  private static final String TAG = "VuforiaActivity";
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
    //asv llama al init
    initVuforiaKeys();
  }

  //region implements CloudRecoCommunicator ands initializations calls
  public boolean initVuforiaKeys() {
    //Vuforia vuforia = Vuforia.getJsonThemeObject(Preferences.getJsonVuforia(this));
    //String clientAccessKey = vuforia.getClientAccessKey();
    //String clientSecretKey = vuforia.getClientSecretKey();
    //String licenseKey = vuforia.getLicenseKey();

    //TODO VuforiaKeys vuforiaKeys = irClient.getVuforiaKeys();

    //if (clientAccessKey != null && !clientAccessKey.isEmpty() &&
    //        clientSecretKey != null && !clientSecretKey.isEmpty() &&
    //        licenseKey != null && !licenseKey.isEmpty()) {
    //    mCloudRecoCallBack = new CloudRecognitionActivityLifeCycleCallBack((Activity) this, clientAccessKey, clientSecretKey, licenseKey);
    //    return true;
    //} else
    //    return false;
    return false;
  }

  ///**
  // * @Deprecated
  // */
  //private void setThemeColorScheme() {
  //      if (this.mCloudRecoCallBack != null) {
  //          //Set theme
  //          Theme theme = Theme.getJsonThemeObject(Preferences.getJsonTheme(this));
  //          String secundaryColor = theme.getSecundaryColor();
  //          String primaryColor = theme.getPrimaryColor();
  //          Theme.setTheme(secundaryColor, tvTitleVuforia, btnCloseVuforia);
  //          Theme.setTheme(primaryColor, mToolbar);
  //          try {
  //              ActionBar actionBar = getSupportActionBar();
  //
  //              if (actionBar != null) {
  //                  actionBar.hide();
  //              }
  //              //btnCloseVuforia.setImageTintList(new ColorStateList.createFromXml(R.color.close_button,null,null)); MEJORA
  //              this.mCloudRecoCallBack.setUIPointColor(Color.parseColor(secundaryColor));
  //              this.mCloudRecoCallBack.setUIScanLineColor(Color.parseColor(primaryColor));
  //          } catch (IllegalArgumentException e) {
  //              GLog.e(TAG, e.getMessage());
  //          }
  //      }
  //  }

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

    //TODO check colors, default is white, define common resources
    //setThemeColorScheme();

  }

  @Override public void onVuforiaResult(Trackable trackable, String uniqueID) {
    //only for debug info show dialog with image recognition info
    //TODO if (irClient.isDebugging())
    //if (Orchextra.getCore().getDebug())
    //    showCaptureMessageDialog(trackable, UniqueID);
    //else {
    //    sendInfoOrchextraService(UniqueID);
    //    final String sendingText = this.getResources().getString(R.string.sending_to_orchextra_api_orchextra);
    //    Toast.makeText(VuforiaActivity.this, sendingText, Toast.LENGTH_SHORT).show();
    //}

    ImageRecognitionClient irc = ImageRecognitionVuforiaImpl.getImageRecognitionInstance();
    irc.recognizedPattern(uniqueID);

    //TODO irClient.recognizedPattern(uniqueID);
  }
  //endregion

  //region Show Dialog DEBUG INFO
  private void showCaptureMessageDialog(Trackable trackable, String uniqueId) {
    String s = "";
    if (trackable != null && uniqueId != null) {
      if (trackable.getType() != null) s = "Type: " + trackable.getType().toString() + "\n";
      if (trackable.getUserData() != null) {
        s += "UserData: " + trackable.getUserData().toString() + "\n";
      }
      if (trackable.getName() != null) s += "Name: " + trackable.getName().toString() + "\n";

      s += "Id: " + trackable.getId() + "\n";
      s += "Unique Target ID: " + uniqueId + "\n";

      showMessageDialog(uniqueId, s);
    }
  }

  private void showMessageDialog(String uniqueId, String message) {

    //final String sendingText = this.getResources().getString(R.string.sending_to_orchextra_api_orchextra);
    //showMessageDialog(uniqueId, message, "Succesful Scan", "Send", sendingText, "Cancel");
  }

  private void showMessageDialog(final String uniqueId, String preformatmessage, String Title,
      String textOkButton, final String textToastOkButton, String textCancelButton) {
    //region show Message Dialog, info image pattern detected
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    SpannableString spanTex = formatMessageText(preformatmessage);

    builder.setMessage(spanTex)
        .setTitle(Title)
        .setPositiveButton(textOkButton, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            sendInfoOrchextraService(uniqueId);
            Toast.makeText(VuforiaActivity.this, textToastOkButton, Toast.LENGTH_SHORT).show();
          }
        })
        .setNegativeButton(textCancelButton, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();
          }
        })
        .setOnDismissListener(new DialogInterface.OnDismissListener() {
          @Override public void onDismiss(DialogInterface dialog) {
            VuforiaActivity.this.onResume();
          }
        });

    AlertDialog dialog = builder.create();
    dialog.show();
    //endregion
  }

  private SpannableString formatMessageText(String preformatmessage) {
    //region preformat String
    SpannableString spanTex = new SpannableString(preformatmessage);

    int size = spanTex.length();
    int ini = 0;
    int fin = 0;
    char separator1 = '\n';
    char separator2 = ':';

    for (int i = 0; i < size; i++) {
      if (fin == 0) {
        fin = preformatmessage.indexOf(':') + 1;
      } else {
        ini = preformatmessage.indexOf(separator1, fin - 1);
        fin = preformatmessage.indexOf(separator2, ini) + 1;
      }
      if (fin > ini && fin <= size && ini != -1) {
        spanTex.setSpan(new ForegroundColorSpan(Color.parseColor("#748C2A")), ini, fin, 0);
        spanTex.setSpan(new StyleSpan(Typeface.BOLD), ini, fin, 0);
      }
      if (ini < 0) return spanTex;
    }
    //endregion
    return spanTex;
  }
  //endregion

  //region Event  validate to->Orchextra Server

  /**
   * This method sends the information about the recognized image pattern object to orchextra
   * Server
   *
   * @param uniqueId : contains the unique target id about the recognized image pattern object.
   */
  private void sendInfoOrchextraService(String uniqueId) {

    //DeviceAuthentication deviceAuthentication = null;
    //try {
    //    deviceAuthentication = DeviceAuthenticationParse.getAccessToken(new JSONObject(Preferences.getAccessTokenJson(VuforiaActivity.this)));
    //    Intent intent = new Intent(VuforiaActivity.this, ConfigService.class);
    //    Bundle bundle = new Bundle();
    //    bundle.putSerializable(ConfigService.PARAM_DEVICE, deviceAuthentication);
    //    bundle.putSerializable(ConfigService.PARAM_VUFORIA, uniqueId);
    //    intent.putExtras(bundle);
    //    startService(intent);
    //
    //} catch (JSONException e) {
    //    GGGLogImpl.log(TAG, e.getMessage(), LogLevel.ERROR);
    //}
  }
  //endregion
}
