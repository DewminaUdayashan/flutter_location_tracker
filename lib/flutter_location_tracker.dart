import 'dart:async';

import 'package:flutter/services.dart';

class FlutterLocationTracker {
  static const MethodChannel _channel =
      const MethodChannel('flutter_location_tracker');

  static const EventChannel stream = const EventChannel('stream');

  Future<Stream<dynamic>> trackLocation() async {
    final String version = await _channel.invokeMethod('track');
    return  stream.receiveBroadcastStream();
  }

  Future<bool> checkPermission() async {
    return await _channel.invokeMethod('CHECK_PERMISSION');
  }

  void requestPermission() async =>
      await _channel.invokeMethod('REQUEST_PERMISSION');
}
