package cc.yourdream.register

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import androidx.fragment.app.FragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler


class RegisterPrinterPlugin constructor(private val activity: Activity) : FlutterPlugin,
    MethodCallHandler {

    var channel: MethodChannel? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(binding.binaryMessenger, CHANNEL)
        channel?.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
        channel = null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "printReceipt" -> {
                val requestLayout = call.argument<Int>("requestLayout") ?: 0
                val width = call.argument<Int>("width") ?: 0
                val height = call.argument<Int>("height") ?: 0

                if (requestLayout == 0) {
                    //init flutter view:
                    DartWidgetsManager
                        .from(activity as FragmentActivity)
                        .container(MainActivity.mainActivity?.widgetContainer)
                        .addDartWidget()
                    result.success(true)
                    return
                }
                DartWidgetsManager
                    .from(activity as FragmentActivity)
                    .container(MainActivity.mainActivity?.widgetContainer)
                    .requestLayout(width,
                        height, object : DartWidgetsManager.DisplayListener() {
                            var isFirst = true
                            override fun onFlutterUiDisplayed() {
                                if (!isFirst) {
                                    return
                                }
                                isFirst = false
                                super.onFlutterUiDisplayed()

                                Handler().postDelayed(
                                    { printCached(flutterFragment?.flutterEngine) },
                                    800
                                )
                            }
                        })
                result.success(true)
            }
            else -> result.notImplemented()
        }
    }

    private fun printCached(flutterEngine: FlutterEngine?) {
        val bitmap: Bitmap =
            flutterEngine?.renderer?.bitmap ?: return
        try {
            //TODO use this bitmap
            Handler().postDelayed(
                {
                    if (!bitmap.isRecycled) bitmap.recycle()
                    MainActivity.mainActivity?.let {
                        DartWidgetsManager
                            .from(it)
                            .dispose()
                    }
                },
                4000 ///TODO test again
            )
        } catch (e: Exception) {
            Log.e("walter printCached", e.toString())
        }
    }

    companion object {

        private const val CHANNEL = "cc.yourdream.register/printer"

        fun registerWith(context: Activity, flutterEngine: FlutterEngine) {
            val instance = RegisterPrinterPlugin(context)
            flutterEngine.plugins.add(instance)
            instance.channel = MethodChannel(flutterEngine.dartExecutor, CHANNEL)
            instance.channel?.setMethodCallHandler(instance)
        }
    }
}
