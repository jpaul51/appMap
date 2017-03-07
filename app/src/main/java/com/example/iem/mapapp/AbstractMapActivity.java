package com.example.iem.mapapp;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by iem on 02/11/2016.
 */


public class AbstractMapActivity extends AppCompatActivity {
    static final String TAG_ERROR_DIALOG_FRAGMENT="errorDialog";

    private  Context context;




    protected boolean readyToGo() {
        GoogleApiAvailability checker=
                GoogleApiAvailability.getInstance();

        int status=checker.isGooglePlayServicesAvailable(this);

        if (status == ConnectionResult.SUCCESS) {
            if (getVersionFromPackageManager(this)>=2) {
                return(true);
            }
            else {

                finish();
            }
        }
        else if (checker.isUserResolvableError(status)) {

        }
        else {
            Toast.makeText(this, "no maps", Toast.LENGTH_LONG).show();
            finish();
        }

        return(false);
    }





    private static int getVersionFromPackageManager(Context context) {
        PackageManager packageManager=context.getPackageManager();
        FeatureInfo[] featureInfos=
                packageManager.getSystemAvailableFeatures();
        if (featureInfos != null && featureInfos.length > 0) {
            for (FeatureInfo featureInfo : featureInfos) {
                // Null feature name means this feature is the open
                // gl es version feature.
                if (featureInfo.name == null) {
                    if (featureInfo.reqGlEsVersion != FeatureInfo.GL_ES_VERSION_UNDEFINED) {
                        return getMajorVersion(featureInfo.reqGlEsVersion);
                    }
                    else {
                        return 1; // Lack of property means OpenGL ES
                        // version 1
                    }
                }
            }
        }
        return 1;
    }

    /** @see FeatureInfo#getGlEsVersion() */
    private static int getMajorVersion(int glEsVersion) {
        return((glEsVersion & 0xffff0000) >> 16);
    }
}