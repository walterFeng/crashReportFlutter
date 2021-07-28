package cc.yourdream.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.view.FlutterMain


class MainActivity : FragmentActivity() {

    @JvmField
    var widgetContainer: LinearLayout? = null//drat widget container

    @JvmField
    var mainFragment: FlutterFragment? = null //main fragment

    private fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        RegisterPrinterPlugin.registerWith(this, flutterEngine)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this

        FlutterMain.ensureInitializationComplete(applicationContext, null)

        setContentView(R.layout.main_activity)

        val app = applicationContext as RegisterApplication
        // This has to be lazy to avoid creation before the FlutterEngineGroup.
        val dartEntrypoint =
                DartExecutor.DartEntrypoint.createDefault()
        val engine = app.engines.createAndRunEngine(this, dartEntrypoint)
        configureFlutterEngine(engine)
        FlutterEngineCache.getInstance().put("main", engine)
        mainFragment =
                FlutterFragment.withCachedEngine("main").build()
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                .add(R.id.mainContainer, mainFragment!!, "mainFragment")
                .commitAllowingStateLoss()

        widgetContainer = findViewById(R.id.widgetContainer)
    }

    override fun onPostResume() {
        super.onPostResume()
        mainFragment?.onPostResume()
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(@NonNull intent: Intent) {
        mainFragment?.onNewIntent(intent)
    }

    override fun onBackPressed() {
        val manager = DartWidgetsManager.from(this)
        if (manager.dispose()) {
            return
        }
        mainFragment?.onBackPressed()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
    ) {
        mainFragment?.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        )
    }

    override fun onUserLeaveHint() {
        mainFragment?.onUserLeaveHint()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        mainFragment?.onTrimMemory(level)
    }

    override fun onDestroy() {
        mainActivity = null
        DartWidgetsManager.from(this).dispose()
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        var mainActivity: MainActivity? = null
    }
}
