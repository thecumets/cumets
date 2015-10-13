/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dciets.cumets.service;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dciets.cumets.MainActivity;
import com.dciets.cumets.R;
import com.dciets.cumets.network.Server;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
    }

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        if(Looper.myLooper() == null) {
            Looper.prepare();
        }
        List<String> providers = locationManager.getProviders(true);
        if (providers.size() > 0) {
            if(Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "start Location request Marshmallow");
                    for(String provider : providers) {
                        locationManager.requestLocationUpdates(
                                provider, 30000, 0, locationListener);
                        Location l = locationManager.getLastKnownLocation(provider);
                        if (l != null) {
                            Server.pushLocation(getApplicationContext(), String.valueOf(l.getLongitude()), String.valueOf(l.getLatitude()));
                        }
                    }
                } else {
                    Server.pushLocation(this, null, null);
                }
            } else {
                Log.i(TAG, "start Location request normal");
                for(String provider : providers) {
                    locationManager.requestLocationUpdates(
                            provider, 30000, 0, locationListener);
                    Location l = locationManager.getLastKnownLocation(provider);
                    if (l != null) {
                        Server.pushLocation(getApplicationContext(), String.valueOf(l.getLongitude()), String.valueOf(l.getLatitude()));
                    }
                }
            }
        } else {
            Server.pushLocation(this, null, null);
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            String longitude = "Longitude: " + loc.getLongitude();
            Log.i(TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.i(TAG, latitude);
            Server.pushLocation(getApplicationContext(), longitude, latitude);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

}
