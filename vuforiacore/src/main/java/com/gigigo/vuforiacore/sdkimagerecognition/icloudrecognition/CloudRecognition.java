package com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gigigo.ggglogger.GGGLogImpl;
import com.gigigo.ggglogger.LogLevel;
import com.gigigo.vuforiacore.R;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.ApplicationControl;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.VuforiaException;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.VuforiaSession;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.LoadingDialogHandler;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.Texture;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.VuforiaGLView;
import com.vuforia.CameraDevice;
import com.vuforia.ObjectTracker;
import com.vuforia.State;
import com.vuforia.TargetFinder;
import com.vuforia.TargetSearchResult;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.Vuforia;
import java.util.Vector;

/**
 * Created by Alberto on 31/03/2016.
 */
public class CloudRecognition implements ApplicationControl {

    VuforiaSession vuforiaAppSession;

    // These codes match the ones defined in TargetFinder in Vuforia.jar
    static final int INIT_SUCCESS = 2;
    static final int INIT_ERROR_NO_NETWORK_CONNECTION = -1;
    static final int INIT_ERROR_SERVICE_NOT_AVAILABLE = -2;
    static final int UPDATE_ERROR_AUTHORIZATION_FAILED = -1;
    static final int UPDATE_ERROR_PROJECT_SUSPENDED = -2;
    static final int UPDATE_ERROR_NO_NETWORK_CONNECTION = -3;
    static final int UPDATE_ERROR_SERVICE_NOT_AVAILABLE = -4;
    static final int UPDATE_ERROR_BAD_FRAME_QUALITY = -5;
    static final int UPDATE_ERROR_UPDATE_SDK = -6;
    static final int UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE = -7;
    static final int UPDATE_ERROR_REQUEST_TIMEOUT = -8;

    static final int HIDE_LOADING_DIALOG = 0;
    static final int SHOW_LOADING_DIALOG = 1;

    // Our OpenGL view:
    private VuforiaGLView mGlView;

    // Our renderer:
    private CloudRecognitionRenderer mRenderer;

    private boolean mExtendedTracking = false;
    boolean mFinderStarted = false;
    boolean mStopFinderIfStarted = false;

    private String mAccessKey = "";
    private String mSecretKey = "";
    private String mLicenseKey = "";

    // View overlays to be displayed in the Augmented View
    private RelativeLayout mUILayout;

    // Error message handling:
    private int mlastErrorCode = 0;
    private int mInitErrorCode = 0;
    private boolean mFinishActivityOnError;

    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;

    private GestureDetector mGestureDetector;

    private LoadingDialogHandler loadingDialogHandler;

    private double mLastErrorTime;

    boolean mIsDroidDevice = false;
    private Vector<Texture> mTextures;


    private Activity mActivity;
    private ICloudRecognitionCommunicator mCommunicator;

    public CloudRecognition(Activity activity, ICloudRecognitionCommunicator communicator, String kAccessKey,
                            String kSecretKey,
                            String kLicenseKey) {
        this.mActivity = activity;
        this.mCommunicator = communicator;
        loadingDialogHandler = new LoadingDialogHandler(this.mActivity);

        this.mAccessKey = kAccessKey;
        this.mSecretKey = kSecretKey;
        this.mLicenseKey = kLicenseKey;
/*
        mUILayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Process the Gestures
                if (mSampleAppMenu != null && mSampleAppMenu.processEvent(event))
                    return true;

                return mGestureDetector.onTouchEvent(event);
            }
        });*/
    }


    //region TODO falta ver como cohones asiganr esta vaina loooca que no se puede hacer mediante
    //activityLIfeCycleCallbacks

    //  @Override
    //asv este se ha seteado
    public boolean on_TouchEvent(MotionEvent event) {

        return mGestureDetector.onTouchEvent(event);
    }

    // Callback for configuration changes the activity handles itself
    //@Override
    public void onConfigurationChanged(Configuration config) {
        GGGLogImpl.log("onConfigurationChanged");
        // super.onConfigurationChanged(config);
        vuforiaAppSession.onConfigurationChanged();
    }


    // Process Single Tap event to trigger autofocus
    private class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler();


        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Generates a Handler to trigger autofocus
            // after 1 second
            autofocusHandler.postDelayed(new Runnable() {
                public void run() {
                    boolean result = CameraDevice.getInstance().setFocusMode(
                            CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);

                    if (!result)
                        Log.e("SingleTapUp", "Unable to trigger focus");
                }
            }, 1000L);

            return true;
        }
    }
    //endregion


    // Called when the activity first starts or needs to be recreated after
    // resuming the application or a configuration change.
    //region overrides ACTIVITY
    public void on_Create() {
        GGGLogImpl.log("on_Create");
        // super.onCreate(savedInstanceState);
        if (this.mLicenseKey == "" || this.mAccessKey == "" || this.mSecretKey == "") {
            Log.e(this.mActivity.getResources().getString(R.string.orchextra_auth_error_tag), this.mActivity.getResources().getString(R.string.orchextra_auth_error_text));
            this.mActivity.finish();
        } else {
            vuforiaAppSession = new VuforiaSession(this, this.mLicenseKey);
            startLoadingAnimation();
            vuforiaAppSession.initAR(this.mActivity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // Creates the GestureDetector listener for processing double tap
            mGestureDetector = new GestureDetector(this.mActivity, new GestureListener());
            mTextures = new Vector<Texture>();
            //asv quitado loadTextures();
            mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith("droid");
        }
    }

    // Called when the activity will start interacting with the user.
    protected void on_Resume() {
        GGGLogImpl.log("on_Resume");
        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice) {
            this.mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            this.mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        try {
            if (vuforiaAppSession != null)
                vuforiaAppSession.resumeAR();
        } catch (VuforiaException e) {
            GGGLogImpl.log(e.getString(), LogLevel.ERROR);
        }

        // Resume the GL view:
        if (mGlView != null) {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }

    }

    // Called when the system is about to start resuming a previous activity.
    protected void on_Pause() {
        GGGLogImpl.log("on_Pause");
        // super.onPause();
        //asv aqui no hacen gestion del flash de la camara ;(
        try {
            vuforiaAppSession.pauseAR();
        } catch (VuforiaException e) {
            GGGLogImpl.log(e.getMessage(), LogLevel.ERROR);
        } catch (Throwable tr) {
            GGGLogImpl.log(tr.getMessage(), LogLevel.ERROR);
        }

        // Pauses the OpenGLView
        if (mGlView != null) {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }
    }

    // The final call you receive before your activity is destroyed.
    protected void on_Destroy() {
        GGGLogImpl.log("on_Destroy");
        //super.onDestroy();
        try {
            if (vuforiaAppSession != null)
                vuforiaAppSession.stopAR();
        } catch (VuforiaException e) {
            GGGLogImpl.log(e.getMessage(), LogLevel.ERROR);
        }
        System.gc();
    }
    //endregion

    protected void deinitCloudReco() {
        // Get the object tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
                .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null) {
            GGGLogImpl.log("Failed to destroy the tracking data set because the ObjectTracker has not"
                + " been initialized.", LogLevel.ERROR);
            return;
        }

        // Deinitialize target finder:
        TargetFinder finder = objectTracker.getTargetFinder();
        finder.deinit();
    }


    public void startLoadingAnimation() {
        // Inflates the Overlay Layout to be displayed above the Camera View
        LayoutInflater inflater = LayoutInflater.from(this.mActivity);
        mUILayout = (RelativeLayout) inflater.inflate(R.layout.camera_overlay, null, false);

        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);

        // By default
        loadingDialogHandler.mLoadingDialogContainer = mUILayout
                .findViewById(R.id.loading_indicator);
        loadingDialogHandler.mLoadingDialogContainer
                .setVisibility(View.VISIBLE);

        this.mActivity.addContentView(mUILayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

    }


    // Initializes AR application components.
    private void initApplicationAR() {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();

        // Initialize the GLView with proper flags
        mGlView = new VuforiaGLView(this.mActivity);
        mGlView.init(translucent, depthSize, stencilSize);

        // Setups the Renderer of the GLView
        mRenderer = new CloudRecognitionRenderer(vuforiaAppSession, this);
        //asv fuera mRenderer.setTextures(mTextures);
        mGlView.setRenderer(mRenderer);

    }

    //region retrieve Error Message
    // Returns the error message for each error code
    private String getStatusDescString(int code) {
        if (code == UPDATE_ERROR_AUTHORIZATION_FAILED)
            return this.mActivity.getString(R.string.UPDATE_ERROR_AUTHORIZATION_FAILED_DESC);
        if (code == UPDATE_ERROR_PROJECT_SUSPENDED)
            return this.mActivity.getString(R.string.UPDATE_ERROR_PROJECT_SUSPENDED_DESC);
        if (code == UPDATE_ERROR_NO_NETWORK_CONNECTION)
            return this.mActivity.getString(R.string.UPDATE_ERROR_NO_NETWORK_CONNECTION_DESC);
        if (code == UPDATE_ERROR_SERVICE_NOT_AVAILABLE)
            return this.mActivity.getString(R.string.UPDATE_ERROR_SERVICE_NOT_AVAILABLE_DESC);
        if (code == UPDATE_ERROR_UPDATE_SDK)
            return this.mActivity.getString(R.string.UPDATE_ERROR_UPDATE_SDK_DESC);
        if (code == UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE)
            return this.mActivity.getString(R.string.UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE_DESC);
        if (code == UPDATE_ERROR_REQUEST_TIMEOUT)
            return this.mActivity.getString(R.string.UPDATE_ERROR_REQUEST_TIMEOUT_DESC);
        if (code == UPDATE_ERROR_BAD_FRAME_QUALITY)
            return this.mActivity.getString(R.string.UPDATE_ERROR_BAD_FRAME_QUALITY_DESC);
        else {
            return this.mActivity.getString(R.string.UPDATE_ERROR_UNKNOWN_DESC);
        }
    }


    // Returns the error message for each error code
    private String getStatusTitleString(int code) {
        if (code == UPDATE_ERROR_AUTHORIZATION_FAILED)
            return this.mActivity.getString(R.string.UPDATE_ERROR_AUTHORIZATION_FAILED_TITLE);
        if (code == UPDATE_ERROR_PROJECT_SUSPENDED)
            return this.mActivity.getString(R.string.UPDATE_ERROR_PROJECT_SUSPENDED_TITLE);
        if (code == UPDATE_ERROR_NO_NETWORK_CONNECTION)
            return this.mActivity.getString(R.string.UPDATE_ERROR_NO_NETWORK_CONNECTION_TITLE);
        if (code == UPDATE_ERROR_SERVICE_NOT_AVAILABLE)
            return this.mActivity.getString(R.string.UPDATE_ERROR_SERVICE_NOT_AVAILABLE_TITLE);
        if (code == UPDATE_ERROR_UPDATE_SDK)
            return this.mActivity.getString(R.string.UPDATE_ERROR_UPDATE_SDK_TITLE);
        if (code == UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE)
            return this.mActivity.getString(R.string.UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE_TITLE);
        if (code == UPDATE_ERROR_REQUEST_TIMEOUT)
            return this.mActivity.getString(R.string.UPDATE_ERROR_REQUEST_TIMEOUT_TITLE);
        if (code == UPDATE_ERROR_BAD_FRAME_QUALITY)
            return this.mActivity.getString(R.string.UPDATE_ERROR_BAD_FRAME_QUALITY_TITLE);
        else {
            return this.mActivity.getString(R.string.UPDATE_ERROR_UNKNOWN_TITLE);
        }
    }

    //endregion

    // Shows error messages as System dialogs
    public void showErrorMessage(int errorCode, double errorTime, boolean finishActivityOnError) {
        if (errorTime < (mLastErrorTime + 5.0) || errorCode == mlastErrorCode)
            return;

        mlastErrorCode = errorCode;
        mFinishActivityOnError = finishActivityOnError;


        final Activity activity = this.mActivity;


        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (mErrorDialog != null) {
                    mErrorDialog.dismiss();
                }

                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        activity);
                builder
                        .setMessage(
                                getStatusDescString(CloudRecognition.this.mlastErrorCode))
                        .setTitle(
                                getStatusTitleString(CloudRecognition.this.mlastErrorCode))
                        .setCancelable(false)
                        .setIcon(0)
                        .setPositiveButton(activity.getString(R.string.button_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mFinishActivityOnError) {
                                            activity.finish();
                                        } else {
                                            dialog.dismiss();
                                        }
                                    }
                                });

                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }


    /* Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message) {
        final String errorMessage = message;
        final Activity activity = this.mActivity;




        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (mErrorDialog != null) {
                    mErrorDialog.dismiss();
                }

                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        activity);
                builder
                        .setMessage(errorMessage)
                        .setTitle(activity.getString(R.string.INIT_ERROR))
                        .setCancelable(false)
                        .setIcon(0)
                        .setPositiveButton(activity.getString(R.string.button_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        activity.finish();
                                    }
                                });

                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }
*/

    public void startFinderIfStopped() {
        if (!mFinderStarted) {
            mFinderStarted = true;

            // Get the object tracker:
            TrackerManager trackerManager = TrackerManager.getInstance();
            ObjectTracker objectTracker = (ObjectTracker) trackerManager
                    .getTracker(ObjectTracker.getClassType());

            // Initialize target finder:
            TargetFinder targetFinder = objectTracker.getTargetFinder();

            targetFinder.clearTrackables();
            targetFinder.startRecognition();
        }
    }


    public void stopFinderIfStarted() {
        if (mFinderStarted) {
            mFinderStarted = false;

            // Get the object tracker:
            TrackerManager trackerManager = TrackerManager.getInstance();
            ObjectTracker objectTracker = (ObjectTracker) trackerManager
                    .getTracker(ObjectTracker.getClassType());

            // Initialize target finder:
            TargetFinder targetFinder = objectTracker.getTargetFinder();

            targetFinder.stop();
        }
    }

    //region asv new ini
    int mScanLineColor = 0;
    int mPointColor = 0;

    public void setUIScanLineColor(int color) {
        this.mScanLineColor = color;
        float r = Color.red(mScanLineColor);
        float g = Color.green(mScanLineColor);
        float b = Color.blue(mScanLineColor);

        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
                .getTracker(ObjectTracker.getClassType());

        // Initialize target finder:
        TargetFinder targetFinder = objectTracker.getTargetFinder();

        // mTargetFinder.setUIScanlineColor(r, g, b);
        targetFinder.setUIScanlineColor(16, 22, 79);
        //mTargetFinder.setUIScanlineColor(55, 42, 202);

    }

    public void setUIPointColor(int color) {
        this.mPointColor = color;
        float r = Color.red(mPointColor);
        float g = Color.green(mPointColor);
        float b = Color.blue(mPointColor);
        // mTargetFinder.setUIPointColor(r, g, b);

        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
                .getTracker(ObjectTracker.getClassType());

        // Initialize target finder:
        TargetFinder targetFinder = objectTracker.getTargetFinder();

        // mTargetFinder.setUIScanlineColor(r, g, b);
        targetFinder.setUIPointColor(77, 29, 87);

    }
    //endregion

    //region implements -->ApplicationControl
    //1º
    @Override
    public boolean doInitTrackers() {
        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;

        // Indicate if the trackers were initialized correctly
        boolean result = true;

        tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null) {
            GGGLogImpl.log("Tracker not initialized. Tracker already initialized or the camera is already started", LogLevel.ERROR);
            result = false;
        } else {
            GGGLogImpl.log("Tracker successfully initialized");
        }

        return result;
    }

    //2º
    @Override
    public boolean doLoadTrackersData() {
        GGGLogImpl.log("doLoadTrackersData");

        // Get the object tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
                .getTracker(ObjectTracker.getClassType());

        // Initialize target finder:
        TargetFinder targetFinder = objectTracker.getTargetFinder();

        // Start initialization:
        if (targetFinder.startInit(this.mAccessKey, this.mSecretKey)) {
            targetFinder.waitUntilInitFinished();
        }

        int resultCode = targetFinder.getInitState();
        if (resultCode != TargetFinder.INIT_SUCCESS) {
            if (resultCode == TargetFinder.INIT_ERROR_NO_NETWORK_CONNECTION) {
                mInitErrorCode = UPDATE_ERROR_NO_NETWORK_CONNECTION;
            } else {
                mInitErrorCode = UPDATE_ERROR_SERVICE_NOT_AVAILABLE;
            }

            GGGLogImpl.log("Failed to initialize target finder.", LogLevel.ERROR);
            return false;
        }

        // Use the following calls if you would like to customize the color of
        // the UI
        // targetFinder->setUIScanlineColor(1.0, 0.0, 0.0);
        // targetFinder->setUIPointColor(0.0, 0.0, 1.0);

        return true;
    }

    //3º
    @Override
    public boolean doStartTrackers() {
        // Indicate if the trackers were started correctly
        boolean result = true;

        // Start the tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
                .getTracker(ObjectTracker.getClassType());
        objectTracker.start();

        // Start cloud based recognition if we are in scanning mode:
        TargetFinder targetFinder = objectTracker.getTargetFinder();
        targetFinder.startRecognition();
        mFinderStarted = true;

        return result;
    }

    //4º
    @Override
    public boolean doStopTrackers() {
        // Indicate if the trackers were stopped correctly
        boolean result = true;

        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
                .getTracker(ObjectTracker.getClassType());

        if (objectTracker != null) {
            objectTracker.stop();

            // Stop cloud based recognition:
            TargetFinder targetFinder = objectTracker.getTargetFinder();
            targetFinder.stop();
            mFinderStarted = false;

            // Clears the trackables
            targetFinder.clearTrackables();
        } else {
            result = false;
        }

        return result;
    }

    //5º
    @Override
    public boolean doUnloadTrackersData() {
        return true;
    }

    //6º
    @Override
    public boolean doDeinitTrackers() {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());

        return result;
    }

    //7º
    @Override
    public void onInitARDone(VuforiaException exception) {
        if (exception == null) {
            initApplicationAR();

            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            this.mActivity.addContentView(mGlView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            // Start the camera:
            try {
                vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
            } catch (VuforiaException e) {
                GGGLogImpl.log(e.getMessage(), LogLevel.ERROR);
            }

            boolean result = CameraDevice.getInstance().setFocusMode(
                    CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

            if (!result)
                GGGLogImpl.log("Unable to enable continuous autofocus", LogLevel.ERROR);

            mUILayout.bringToFront();

            // Hides the Loading Dialog
            loadingDialogHandler.sendEmptyMessage(HIDE_LOADING_DIALOG);

            mUILayout.setBackgroundColor(Color.TRANSPARENT);
            this.mCommunicator.setContentViewTop();

        } else {
            GGGLogImpl.log(exception.getString(), LogLevel.ERROR);
            if (mInitErrorCode != 0) {

                showErrorMessage(mInitErrorCode, 10, true);
            } else {
                //asv esto podria sustituirse por un finish(); y a jugar a la palita
                this.mActivity.finish();
                GGGLogImpl.log(exception.getMessage(), LogLevel.ERROR);
                //showInitializationErrorMessage(exception.getString());
            }
        }
    }

    //8º
    @Override
    public void onVuforiaUpdate(State state) {

        // Get the tracker manager:
        TrackerManager trackerManager = TrackerManager.getInstance();

        // Get the object tracker:
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
                .getTracker(ObjectTracker.getClassType());

        // Get the target finder:
        TargetFinder finder = objectTracker.getTargetFinder();

        // Check if there are new results available:
        final int statusCode = finder.updateSearchResults();

        // Show a message if we encountered an error:
        if (statusCode < 0) {

            boolean closeAppAfterError = (
                    statusCode == UPDATE_ERROR_NO_NETWORK_CONNECTION ||
                            statusCode == UPDATE_ERROR_SERVICE_NOT_AVAILABLE);

            showErrorMessage(statusCode, state.getFrame().getTimeStamp(), closeAppAfterError);

        } else if (statusCode == TargetFinder.UPDATE_RESULTS_AVAILABLE) {
            // Process new search results
            if (finder.getResultCount() > 0) {
                TargetSearchResult result = finder.getResult(0);

                // Check if this target is suitable for tracking:
                if (result.getTrackingRating() > 0) {
                    Trackable trackable = finder.enableTracking(result);

                    if (mExtendedTracking)
                        trackable.startExtendedTracking();

                    //asvnew para transmitir resultado al vuforiaactivity mediante el pipe/communicator
                    this.mCommunicator.onVuforiaResult(trackable, result.getUniqueTargetId());
                }
            }
        }
    }
    //endregion
}
