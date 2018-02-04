package com.kmema.android.mappy;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmema on 2/1/2018.
 */

public class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    protected int MY_PERMISSION_REQUEST_CODE_FINE_AND_COARSE_LOCATION = 20;
    protected final String[] LIST_REQUESTING_LOCATION_PERMISSION = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    private String[] deniedPermissionLocationTagging;

    protected void requestingRuntimePermission(final Activity activity, final String[] permissions, final int customPermissionConstant) {

        if (permissions.length > 1 && customPermissionConstant == MY_PERMISSION_REQUEST_CODE_FINE_AND_COARSE_LOCATION) {
            if (getDeniedPermissionAmongTheLocationPermissions().length == 1) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, deniedPermissionLocationTagging[0])) {
                    Snackbar.make(findViewById(android.R.id.content), "App Needs permission to work", Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(activity, deniedPermissionLocationTagging, customPermissionConstant);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(activity, deniedPermissionLocationTagging, customPermissionConstant);
                }
            } else if (getDeniedPermissionAmongTheLocationPermissions().length > 1) {
                if (isFirstTimeAskForLocationPermission()) {
                    ActivityCompat.requestPermissions(activity, deniedPermissionLocationTagging, customPermissionConstant);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Needs Multiple Apps Permission", Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(activity, deniedPermissionLocationTagging, customPermissionConstant);
                                }
                            }).show();
                }
            }
        }
    }

    private boolean isFirstTimeAskForLocationPermission() {
        SharedPreferences sharedPreferences = getSharedPreferences("permissionasls",MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("PHOTO_FIRST_PERMISSION", true);
        if(isFirstTime){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("PHOTO_FIRST_PERMISSION", false);
            editor.commit();
        }
    return isFirstTime;
    }


    protected String[] getDeniedPermissionAmongTheLocationPermissions() {
        final List<String> deniedPermissions = new ArrayList<String>();
        for(String permission: LIST_REQUESTING_LOCATION_PERMISSION){
            if(ActivityCompat.checkSelfPermission(this, permission)==PackageManager.PERMISSION_DENIED){
                deniedPermissions.add(permission);
            }
        }
        this.deniedPermissionLocationTagging = deniedPermissions.toArray(new String[deniedPermissions.size()]);
        return deniedPermissionLocationTagging;
    }

    protected boolean checkWhetherAllPermissionArePresent() {
        for (String permission : LIST_REQUESTING_LOCATION_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    protected void startAppPermissionSetting(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}