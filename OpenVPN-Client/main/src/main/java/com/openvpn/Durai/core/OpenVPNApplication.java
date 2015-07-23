package com.openvpn.Durai.core;
import android.app.Application;

import com.openvpn.Durai.BuildConfig;

public class OpenVPNApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PRNGFixes.apply();

        if (BuildConfig.DEBUG) {
            //ACRA.init(this);
        }
    }

}
