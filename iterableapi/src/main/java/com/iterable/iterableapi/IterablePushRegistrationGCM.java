package com.iterable.iterableapi;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by David Truong dt@iterable.com
 */
class IterablePushRegistrationGCM extends AsyncTask<IterableGCMRegistrationData, Void, String> {
    static final String TAG = "IterableGCM";

    private IterableGCMRegistrationData iterableGCMRegistrationData;
    private boolean disableAfterRegistration;

    /**
     * Generates a deviceRegistrationToken from GCM
     * @param params
     * @return registration token
     */
    protected String doInBackground(IterableGCMRegistrationData... params) {
        String registrationToken = "";

        try {
            iterableGCMRegistrationData = params[0];

            if (iterableGCMRegistrationData.iterableAppId != null) {
                Class instanceIdClass = Class.forName(IterableConstants.INSTANCE_ID_CLASS);
                if (instanceIdClass != null) {
                    Context mainContext = IterableApi.sharedInstance.getMainActivityContext();
                    if (mainContext != null) {
                        InstanceID instanceID = InstanceID.getInstance(mainContext);

                        String idInstance = instanceID.getId();
                        registrationToken = instanceID.getToken(iterableGCMRegistrationData.projectNumber,
                                GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                        if (!registrationToken.isEmpty()) {
                            IterableApi.sharedInstance.registerDeviceToken(iterableGCMRegistrationData.iterableAppId, registrationToken);
                        }
                    } else {
                        IterableLogger.e(TAG, "MainActivity Context is null");
                    }
                }
            } else {
                IterableLogger.e("IterableGCM", "The IterableAppId has not been added to the AndroidManifest");
            }
        } catch (ClassNotFoundException e) {
            IterableLogger.e(TAG, "ClassNotFoundException: Check that play-services-gcm is added " +
                    "to the build dependencies", e);
        } catch (IOException e) {
            IterableLogger.e(TAG, "Invalid projectNumber", e);
        }
        return registrationToken;
    }

    /**
     * Executes the disable
     * @param registrationToken
     */
    @Override
    protected void onPostExecute(String registrationToken) {
        super.onPostExecute(registrationToken);
        if (iterableGCMRegistrationData.disableAfterRegistration) {
            disableOnRegistrationComplete(registrationToken);
        }
    }

    /**
     * Disables a device after
     * @param registrationToken
     */
    protected void disableOnRegistrationComplete(String registrationToken) {
        IterableApi.sharedInstance.disablePush(registrationToken);
    }
}

class IterableGCMRegistrationData {
    String iterableAppId = "";
    String projectNumber = "";
    boolean disableAfterRegistration = false;
    public IterableGCMRegistrationData(String iterableAppId, String projectNumber, boolean disableAfterRegistration){
        this.iterableAppId = iterableAppId;
        this.projectNumber = projectNumber;
        this.disableAfterRegistration = disableAfterRegistration;
    }
}


