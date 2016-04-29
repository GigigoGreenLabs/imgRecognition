package com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.gigigo.ggglogger.GGGLogImpl;
import com.gigigo.ggglogger.LogLevel;

/**
 * Created by Alberto on 31/03/2016.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CloudRecognitionActivityLifeCycleCallBack implements Application.ActivityLifecycleCallbacks {

    // public AppCompatDelegate delegate;
    private static Activity mActivity;
    public static CloudRecognition mCloudReco;

    public CloudRecognitionActivityLifeCycleCallBack(Activity activity,ICloudRecognitionCommunicator icloud, String kAccessKey,
                                                     String kSecretKey, String kLicenseKey) {
        GGGLogImpl.log("CloudRecognitionActivityLifeCycleCallBack.constructor");
        mActivity = activity;

        this.mCloudReco = new CloudRecognition(activity,  icloud, kAccessKey, kSecretKey, kLicenseKey);
        mActivity.getApplication().registerActivityLifecycleCallbacks(this);
    }

    public CloudRecognitionActivityLifeCycleCallBack(Activity activity, String kAccessKey,
                                                     String kSecretKey,
                                                     String kLicenseKey) {

       this(activity, (ICloudRecognitionCommunicator) activity, kAccessKey, kSecretKey, kLicenseKey);
       /* GGGLogImpl.log("CloudRecognitionActivityLifeCycleCallBack.constructor");
        mActivity = activity;

        this.mCloudReco = new CloudRecognition(activity, (ICloudRecognitionCommunicator) activity, kAccessKey, kSecretKey, kLicenseKey);
        mActivity.getApplication().registerActivityLifecycleCallbacks(this);*/
    }

    //region Bridge CloudReco
    public void initUIRecognizer() {
        try {
            if (this.mCloudReco != null)
                this.mCloudReco.on_Create();
        } catch (Exception ex) {
            GGGLogImpl.log(ex.getMessage(), LogLevel.ERROR);
        }
    }

    public void setUIScanLineColor(int color) {
        try {
            if (this.mCloudReco != null)
                this.mCloudReco.setUIScanLineColor(color);
        } catch (Exception ex) {
            GGGLogImpl.log(ex.getMessage(), LogLevel.ERROR);
        }
    }

    public void setUIPointColor(int color) {
        try {
            if (this.mCloudReco != null)
                this.mCloudReco.setUIPointColor(color);
        } catch (Exception ex) {
            GGGLogImpl.log(ex.getMessage(), LogLevel.ERROR);
        }
    }
    //endregion

    //region android.app.Application.ActivityLifecycleCallbacks
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        GGGLogImpl.log("CloudRecognitionActivityLifeCycleCallBack.onActivityCreated");
        if (mActivity.equals(activity))
            this.mCloudReco.on_Create(); //asv este no chuta bien
    }

    @Override
    public void onActivityStarted(Activity activity) {
        GGGLogImpl.log("CloudRecognitionActivityLifeCycleCallBack.onActivityStarted,donothing");
        if (mActivity.equals(activity))
            this.mCloudReco.on_Create();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        GGGLogImpl.log("CloudRecognitionActivityLifeCycleCallBack.onActivityResumed");
        if (mActivity.equals(activity))
            this.mCloudReco.on_Resume();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        GGGLogImpl.log("CloudRecognitionActivityLifeCycleCallBack.onActivityPaused");
        if (mActivity.equals(activity))
            this.mCloudReco.on_Pause();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        GGGLogImpl.log("CloudRecognitionActivityLifeCycleCallBack.onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        GGGLogImpl.log("CloudRecognitionActivityLifeCycleCallBack.onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        GGGLogImpl.log("CloudRecognitionActivityLifeCycleCallBack.onActivityDestroyed");
        if (mActivity.equals(activity))
            this.mCloudReco.on_Destroy();
        activity.getApplication().unregisterActivityLifecycleCallbacks(this);

    }
    //endregion
}
