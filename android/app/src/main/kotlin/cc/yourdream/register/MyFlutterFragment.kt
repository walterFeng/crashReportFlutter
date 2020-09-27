package cc.yourdream.register

import android.content.Context
import io.flutter.embedding.android.FlutterFragment

class MyFlutterFragment : FlutterFragment() {

    private var mListener: OnFragmentAttachedListener? = null

    fun setOnFragmentAttachedListener(listener: OnFragmentAttachedListener) {
        mListener = listener
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener?.onFragmentAttached()
    }

    interface OnFragmentAttachedListener {
        fun onFragmentAttached()
    }
}
