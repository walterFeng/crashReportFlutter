package cc.yourdream.register

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import io.flutter.FlutterInjector
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.android.RenderMode
import io.flutter.embedding.android.TransparencyMode
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
import io.flutter.plugins.GeneratedPluginRegistrant

class DartWidgetsManager {
    lateinit var context: FragmentActivity
    private var widgetContainer: LinearLayout? = null
    private val widgetId = R.id.widgetContainer + 0x16

    ///Display a flutter Widget on the layout of native.
    @SuppressLint("ClickableViewAccessibility")
    fun addDartWidget() {
        dispose()
        widgetContainer?.post {
            val container = FrameLayout(context)
            container.id = widgetId
            widgetContainer?.addView(container, LinearLayout.LayoutParams(493, 500))
            val fragment = FlutterFragment.NewEngineFragmentBuilder(MyFlutterFragment::class.java)
                    .appBundlePath(FlutterInjector.instance().flutterLoader().findAppBundlePath())
                    .dartEntrypoint("printerPage")
                    .transparencyMode(TransparencyMode.transparent)
                    .renderMode(RenderMode.surface)
                    .build<MyFlutterFragment>()

            context.supportFragmentManager.beginTransaction()
                    .add(widgetId, fragment, "dartFragment${widgetId}")
                    .commitAllowingStateLoss()

            fragment.setOnFragmentAttachedListener(object :
                    MyFlutterFragment.OnFragmentAttachedListener {
                override fun onFragmentAttached() {
                    RegisterPrinterPlugin.registerWith(context, fragment.flutterEngine!!)
                    GeneratedPluginRegistrant.registerWith(fragment.flutterEngine!!)
                    container.tag = fragment
                }
            })
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun addDartWidget1() {
        dispose()
        widgetContainer?.post {
            val container = FrameLayout(context)
            container.id = widgetId
            widgetContainer?.addView(container, LinearLayout.LayoutParams(493, 500))

            val app = context.applicationContext as RegisterApplication
            // This has to be lazy to avoid creation before the FlutterEngineGroup.
            val dartEntrypoint =
                    DartExecutor.DartEntrypoint(
                            FlutterInjector.instance().flutterLoader().findAppBundlePath(), "printerPage"
                    )
            val engine = app.engines.createAndRunEngine(context, dartEntrypoint)
            
            GeneratedPluginRegistrant.registerWith(engine)
            RegisterPrinterPlugin.registerWith(context, engine)
            
            FlutterEngineCache.getInstance().put("dartFragment", engine)
            
            val fragment =
                    FlutterFragment.withCachedEngine("dartFragment").build<FlutterFragment>()
            val fragmentManager = context.supportFragmentManager
            fragmentManager.beginTransaction()
                    .add(widgetId, fragment, "dartFragment${widgetId}")
                    .commitAllowingStateLoss()
            container.tag = fragment
        }
    }

    ///Reset the height of the widget.
    fun requestLayout(
            width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
            height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
            listener: DisplayListener? = null
    ) {
        if (widgetContainer!!.childCount > 0 && height > 0) {
            val oldContainer =
                    widgetContainer!!.findViewById<ViewGroup>(widgetId)
            oldContainer?.layoutParams?.width =
                    if (width > 0) width else oldContainer!!.layoutParams!!.width
            oldContainer?.layoutParams?.height =
                    if (height > 0) height else oldContainer!!.layoutParams!!.height
            Log.e(
                    "addDartWidget",
                    "layout${widgetId}"
            )
            oldContainer?.requestLayout()
            if (listener != null && oldContainer?.tag != null) {
                listener.flutterFragment = oldContainer.tag as? FlutterFragment
                (oldContainer.tag as? FlutterFragment)?.flutterEngine?.renderer?.addIsDisplayingFlutterUiListener(
                        listener
                )
                oldContainer.tag = null
            }
            return
        }
    }

    fun container(widgetContainer: LinearLayout?): DartWidgetsManager {
        this.widgetContainer = widgetContainer
        return this
    }

    fun dispose(): Boolean {
        var result = false
        try {
            for (i in 0 until (widgetContainer?.childCount ?: 0)) {
                val v = widgetContainer!!.getChildAt(i)
                val fragment = (v.tag as? FlutterFragment)
                fragment?.let {
                    MainActivity.mainActivity!!.supportFragmentManager.beginTransaction()
                            .remove(v.tag as FlutterFragment).commitAllowingStateLoss()
                }
                result = true
            }
            if (result) {
                widgetContainer?.post { widgetContainer?.removeAllViews() }
            }
        } catch (e: Exception) {
            result = false
            Log.e("walter", e.toString())
        }
        return result
    }

    companion object {

        private val instances = HashMap<String, DartWidgetsManager>()

        fun from(context: FragmentActivity): DartWidgetsManager {
            val key = context::class.java.simpleName
            if (instances.contains(key)) {
                return instances[key]!!
            }
            val manager = DartWidgetsManager()
            manager.context = context
            instances[key] = manager
            return manager
        }
    }

    open class DisplayListener(var flutterFragment: FlutterFragment? = null) :
            FlutterUiDisplayListener {

        override fun onFlutterUiNoLongerDisplayed() {}

        override fun onFlutterUiDisplayed() {}
    }
}
