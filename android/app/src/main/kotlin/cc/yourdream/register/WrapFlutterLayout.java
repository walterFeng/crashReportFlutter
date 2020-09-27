package cc.yourdream.register;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

//Native shows a flutter widget that doesn't know the height.
// So this control is required to capture the height that the flutter layout completes.
public class WrapFlutterLayout extends FrameLayout implements FlutterPlugin, MethodChannel.MethodCallHandler {

    private static final String TAG = "WrapFlutterLayout";

    double reportedWidth = 0;
    double reportedHeight = 0;

    private MethodChannel channel;

    public WrapFlutterLayout(Context context) {
        this(context, null);
    }

    public WrapFlutterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                resolveSize((int) Math.ceil(reportedWidth), widthMeasureSpec),
                resolveSize((int) Math.ceil(reportedHeight), heightMeasureSpec)
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMethodCall(MethodCall call, @NotNull MethodChannel.Result result) {
        if (call.method.equals("reportSize")) {
            List<Double> args = (List<Double>) call.arguments;
            reportedWidth = args.get(0);
            reportedHeight = args.get(1);
            Log.d(TAG, "width:" + reportedWidth + ",height:" + reportedHeight);
            result.success(true);
            requestLayout();
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), "io.flutter.plugin/wrap_layout");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        channel = null;
    }
}
