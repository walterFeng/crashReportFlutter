import 'dart:async';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter/services.dart';

class PrintPage extends StatefulWidget {
  final pageWidth;
  static const String NATIVE_PRINT_METHOD = "printReceipt";

  const PrintPage({Key key, this.pageWidth: 375 - 46.0}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return _PrintPageState();
  }
}

class _PrintPageState extends State<PrintPage> {
  GlobalKey rootKey = new GlobalKey();
  static const MethodChannel _channel =
      const MethodChannel('cc.yourdream.register/printer');
  List<String> list = new List();

  double currentHeight = 0;
  int currentState = 0;

  @override
  void initState() {
    super.initState();
    requestData();
  }

  void requestData() {
    postDelay(() {
      setState(() {
        for (int i = 0; i < 10; i++) {
          list.add("thanks");
          list.add("for");
          list.add("your");
          list.add("handling");
        }
      });
    }, delay: 1500);
  }

  @override
  Widget build(BuildContext context) {
    try {
      return LayoutBuilder(
        builder: (BuildContext context, BoxConstraints constraints) {
          setLayoutMeasureListener();
          return Directionality(
            key: rootKey,
            textDirection: TextDirection.ltr,
            child: SizedBox(width: widget.pageWidth, child: bodyContent()),
          );
        },
      );
    } catch (e) {
      print(e.toString());
    }
    return Text('');
  }

  Widget bodyContent() {
    List<Widget> contents = [];
    for (int i = 0; i < list.length; i++) {
      contents.add(Container(
          width: double.infinity,
          height: 45,
          padding: EdgeInsets.only(left: 20),
          margin: EdgeInsets.all(2),
          decoration: BoxDecoration(
              color: Colors.white,
              shape: BoxShape.rectangle,
              borderRadius: BorderRadius.circular(4),
              boxShadow: [
                BoxShadow(
                    color: Color(0x1A000000),
                    offset: Offset(0, 5),
                    blurRadius: 17,
                    spreadRadius: 0)
              ]),
          child: Text(list[i],
              style: TextStyle(
                  color: Colors.black,
                  fontSize: 20,
                  fontFamily: "PingFang-Medium"))));
    }
    try {
      return SingleChildScrollView(
          child: Column(
        children: [...contents],
      ));
    } catch (ignored) {}
    return Text('');
  }

  Timer postDelay(Function callback, {delay: 10}) {
    startTimeOut() {
      var duration = Duration(milliseconds: delay);
      return Timer(duration, callback);
    }

    return startTimeOut();
  }

  void setLayoutMeasureListener() {
    SchedulerBinding.instance.addPostFrameCallback((Duration timeStamp) async {
      if (rootKey.currentContext == null || (currentState > 0)) {
        return;
      }
      RenderBox box = rootKey.currentContext.findRenderObject();
      double width =
          box.getMaxIntrinsicWidth(double.infinity) * window.devicePixelRatio;
      double height =
          box.getMaxIntrinsicHeight(width) * window.devicePixelRatio;
      debugPrint("walter:" +
          "${box.getMaxIntrinsicWidth(double.infinity)}," +
          width.toString() +
          "," +
          height.toString());
      debugPrint("walter: FrameCallback:" + currentState.toString());
      double newHeight = height;
      if (newHeight == currentHeight) {
        return;
      }

      try {
        debugPrint("walter:" + "printReceipt");
        setState(() {
          currentHeight = newHeight;
          currentState =
              (currentState == 0) ? (currentState + 1) : currentState;
        });
        dynamic params = {
          "debug": true,
          "requestLayout": currentState,
          "width": (width + 0.99).toInt(),
          "height": (height + 0.99).toInt(),
        };
        bool result =
            await _channel.invokeMethod(PrintPage.NATIVE_PRINT_METHOD, params);
        debugPrint("result:" + result?.toString());
      } catch (ignored) {
        debugPrint(ignored.toString());
      }
    });
  }
}
