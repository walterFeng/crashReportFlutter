import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'page/print_page.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  static const MethodChannel _channel =
      const MethodChannel('cc.yourdream.register/printer');

  @override
  Widget build(BuildContext context) {
    SystemChrome.setEnabledSystemUIOverlays([SystemUiOverlay.bottom]);
    return MaterialApp(
      theme: ThemeData(
        brightness: Brightness.light,
        primaryColorBrightness: Brightness.light,
        highlightColor: Color(0xffD7D7D7),
      ),
      initialRoute: "/",
      debugShowCheckedModeBanner: false,
      routes: <String, WidgetBuilder>{
        "/": (BuildContext context) {
          return Scaffold(
            body: Container(
                width: double.infinity,
                height: double.infinity,
                alignment: Alignment.center,
                padding: EdgeInsets.all(25),
                color: Color(0xff999999),
                child: Column(
                  children: [
                    GestureDetector(
                      onTap: () {
                        _channel.invokeMethod(
                            PrintPage.NATIVE_PRINT_METHOD, new HashMap());
                      },
                      child: Container(
                          color: Colors.deepOrangeAccent,
                          width: 120,
                          height: 60,
                          alignment: Alignment.center,
                          child: Text(
                            "show fragment",
                          )),
                    ),
                    SizedBox(height: 20),
                    GestureDetector(
                      onTap: () {
                        _channel.invokeMethod(
                            "printReceiptMultipleFlutters", new HashMap());
                      },
                      child: Container(
                          color: Colors.deepOrangeAccent,
                          width: 120,
                          height: 60,
                          alignment: Alignment.center,
                          child: Text(
                            "show fragment use Multiple-Flutters",
                          )),
                    ),
                    SizedBox(height: 100),
                    Text(
                        "The fragment will be removed after a few seconds. Click another button to see the display status.")
                  ],
                )),
          );
        }
      },
    );
  }
}

@pragma('vm:entry-point')
void printerPage() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(MaterialApp(
      home: Scaffold(body: PrintPage()), debugShowCheckedModeBanner: false));
}
