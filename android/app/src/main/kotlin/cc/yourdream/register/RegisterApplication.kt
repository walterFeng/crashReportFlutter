package cc.yourdream.register

import io.flutter.app.FlutterApplication
import io.flutter.embedding.engine.FlutterEngineGroup

open class RegisterApplication : FlutterApplication() {

    lateinit var engines: FlutterEngineGroup

    override fun onCreate() {
        super.onCreate()
        engines = FlutterEngineGroup(this)
    }
}
