package ru.zakoulov.navigatorx.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.otaliastudios.zoom.ZoomLayout

class ZoomViewMap @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ZoomLayout(context, attrs, defStyleAttr) {

    init {

    }

    val mapView: ImageView? = null

    val markersView: RelativeLayout? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Measure ourselves as MATCH_PARENT
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.UNSPECIFIED) {
            throw RuntimeException("$TAG must be used with fixed dimensions (e.g. match_parent)")
        }
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)

        // Measure our child as unspecified.
        val spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        mapView?.let {
            measureChild(it, spec, spec)
        }
    }

    companion object {
        private const val TAG = "ZoomViewMap"
    }
}
