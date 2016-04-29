package com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition;

import com.vuforia.Trackable;

/**
 * Created by Alberto on 01/04/2016.
 */
public interface ICloudRecognitionCommunicator {
    void setContentViewTop();
    void onVuforiaResult(Trackable trackable, String UniqueID);
}
