package com.gigigo.imagerecognition;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.gigigo.ggglib.ContextProvider;
import com.gigigo.imagerecognitioninterface.ImageRecognitionCredentials;
import com.gigigo.vuforiaimplementation.ImageRecognitionVuforiaImpl;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startVuforia();
      }
    });
  }

  private void startVuforia() {
    ImageRecognitionCredentials imageRecognitionCredentials = new ImageRecognitionCredentials() {
      @Override public String getClientAccessKey() {
        return BuildConfig.VUFORIA_KEY;
      }

      @Override public String getClientSecretKey() {
        return BuildConfig.VUFORIA_SECRET;
      }

      @Override public String getLicensekey() {
        return BuildConfig.VUFORIA_LICENSE;
      }
    };

    ImageRecognitionVuforiaImpl imageRecognitionVuforia = new ImageRecognitionVuforiaImpl();
    imageRecognitionVuforia.setContextProvider(createContextProvider());
    imageRecognitionVuforia.startImageRecognition(imageRecognitionCredentials);

  }

  private ContextProvider createContextProvider() {
    return new ContextProvider() {
      @Override public Activity getCurrentActivity() {
        return MainActivity.this;
      }

      @Override public boolean isActivityContextAvailable() {
        return true;
      }

      @Override public Context getApplicationContext() {
        return MainActivity.this.getApplication().getApplicationContext();
      }

      @Override public boolean isApplicationContextAvailable() {
        return true;
      }
    };
  }
}
