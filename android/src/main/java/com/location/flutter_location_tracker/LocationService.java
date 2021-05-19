package com.location.flutter_location_tracker;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;


import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.stream.Stream;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;


public class LocationService extends Service implements EventChannel.StreamHandler {
    EventChannel.EventSink eventSink;

    int i = 0;
    double latitude;
    double longitude;


    final public LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            latitude = locationResult.getLastLocation().getLatitude();
            longitude = locationResult.getLastLocation().getLongitude();

            // Newly build EventChannel, CHANNEL The role of constants MethodChannel Same
            // Processor Setting Stream(StreamHandler)
            if (eventSink != null)
                eventSink.success(latitude);
            else Log.d("TAG", "EVENT SINK NULL");
            Log.d("onLocationResult: ", latitude + " " + longitude);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    private void startLocationService() {
        Log.d("22TAG", "startLocationService: started");
        String channelId = "location_notification_channel";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0, resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), channelId
        );
//        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Services Enabled..");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running...!");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel =
                        new NotificationChannel(
                                channelId,
                                "Location Service",
                                NotificationManager.IMPORTANCE_HIGH
                        );
                notificationChannel.setDescription("This Channel Used By Location Service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Constant.LOCATION_SERVICE_ID, builder.build());

    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }


    /////

    private static final String CHANNEL = "stream";
    EventChannel eventChannel;
    ;
/////

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (FlutterLocationTrackerPlugin
                .binaryMessenger != null) {
            eventChannel = new EventChannel(FlutterLocationTrackerPlugin.binaryMessenger, CHANNEL);
            eventChannel.setStreamHandler(this);
        } else Log.d("TAG", "onStartCommand: Binary Messange Null");

        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constant.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constant.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        Log.d("TAG", "onListen: Sink");
        this.eventSink = eventSink;
    }


    @Override
    public void onCancel(Object o) {
        Log.d("TAG", "onCancel: ");
        eventSink = null;
    }
}
