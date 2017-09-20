/*
 * Copyright (C) 2011 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.sacids.afyadataV2.android.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.sacids.afyadataV2.android.R;
import org.sacids.afyadataV2.android.app.Preferences;
import org.sacids.afyadataV2.android.app.PrefManager;

import java.util.Locale;


public class SplashScreenActivity extends AppCompatActivity {

    private static String TAG = SplashScreenActivity.class.getSimpleName();

    private Context context = this;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static int SPLASH_TIME_OUT = 1000;

    private SharedPreferences settings;
    private String gcmRegId;
    private PrefManager pref;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

//        settings = getSharedPreferences(Preferences.AFYA_DATA, MODE_PRIVATE);
//        String defaultLocale = settings.getString(Preferences.DEFAULT_LOCALE, "sw");
//
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        android.content.res.Configuration conf = res.getConfiguration();
//        conf.locale = new Locale(defaultLocale);
//        res.updateConfiguration(conf, dm);

        checkPlayServices();

        pref = new PrefManager(this);

        //shared Preference
        settings = getSharedPreferences(Preferences.USER, MODE_PRIVATE);

        //register gcmId to server
        if (settings.getString(Preferences.GCM_REG_ID, "").length() != 0) {
            gcmRegId = settings.getString(Preferences.GCM_REG_ID, "");
            //registerGCMUsers();
        }

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                //check login
                if (pref.isLoggedIn()) {
                    startActivity(new Intent(context, MainActivity.class));
                } else {
                    startActivity(new Intent(context, FrontPageActivity.class));
                }
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    //check play service
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "Device does not support Google Play Service");
                finish();
            }
            return false;
        }
        return true;
    }
}
