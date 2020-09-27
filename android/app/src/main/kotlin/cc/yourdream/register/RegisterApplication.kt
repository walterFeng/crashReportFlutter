package cc.yourdream.register

import io.flutter.app.FlutterApplication
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint

open class RegisterApplication : FlutterApplication() {

    companion object {
        var flutterEngine: FlutterEngine? = null
    }

    override fun onCreate() {
        super.onCreate()
        // Instantiate your FlutterEngine.
        flutterEngine = FlutterEngine(applicationContext)

        // Pre-warm your FlutterEngine by starting Dart execution.
        flutterEngine?.dartExecutor?.executeDartEntrypoint(DartEntrypoint.createDefault())

        FlutterEngineCache
            .getInstance()
            .put("my_engine_id", flutterEngine)

    }

}
