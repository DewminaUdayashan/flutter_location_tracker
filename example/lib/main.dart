import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_location_tracker/flutter_location_tracker.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final FlutterLocationTracker _locationTracker = FlutterLocationTracker();

  Stream stream;

  void init() async {
    _locationTracker.trackLocation().then((value) {
      setState(() {
        stream = value;
      });
    });
  }

  @override
  void initState() {
    super.initState();
    init();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Location Tracker',
      home: Scaffold(
        body: StreamBuilder<Object>(
            stream: stream,
            builder: (context, snapshot) {
              if (snapshot.hasData)
                return Center(
                  child: Text(snapshot.data.toString()),
                );
              return Center(
                child: CircularProgressIndicator(),
              );
            }),
      ),
    );
  }
}
