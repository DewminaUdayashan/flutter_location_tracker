package com.location.flutter_location_tracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;


public class FlutterLocationTrackerPlugin extends AppCompatActivity implements FlutterPlugin, MethodCallHandler, ActivityAware {

    private MethodChannel channel;
    private Activity activity;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

   public static LocationService locationService = new LocationService();
public static BinaryMessenger binaryMessenger;


    private void startLocationServices() {
        Intent intent = new Intent(activity.getApplicationContext(), locationService.getClass());
        intent.setAction(Constant.ACTION_START_LOCATION_SERVICE);
        try {
            activity.getApplicationContext().startService(intent);

        } catch (Exception e) {
            Log.d("sads", "startLocationServices: " + e.getMessage());
        }
    }



    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
      binaryMessenger = flutterPluginBinding.getBinaryMessenger();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_location_tracker");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "track":
                try {
                    startLocationServices();
                    result.success(String.valueOf(locationService.latitude));
                } catch (Exception e) {
                    Log.d("TAG", "onMethodCall: Error " + e.getMessage());
                }
                break;
            case "CHECK_PERMISSION":
                result.success(isPermissionGranted());
                break;
            case "REQUEST_PERMISSION":
                requestPermission();
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        Log.d("TAGa", "onAttachedToActivity: attached");
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }


    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "onRequestPermissionsResult: PERMISSION_GRANTED");
            } else {
                Log.d("TAG", "onRequestPermissionsResult: PERMISSION_DENIDED");
            }
        }
    }
}