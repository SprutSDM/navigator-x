package ru.zakoulov.navigatorx.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() {
    val activity = activity ?: return
    val view = activity.currentFocus
    view?.let { v ->
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun Fragment.showKeyboardFor(view: View? = null) {
    val activity = activity ?: return
    val focusedView = activity.currentFocus ?: view?.apply { requestFocus() }
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.showSoftInput(focusedView, InputMethodManager.SHOW_IMPLICIT)
}

fun ImageView.setTintColor(@ColorRes colorRes: Int) {
    setColorFilter(ContextCompat.getColor(context, colorRes))
}

fun View.setBackgroundShapeColor(@ColorRes colorRes: Int) {
    val shapeDrawable = background as? GradientDrawable ?: return
    shapeDrawable.setColor(ContextCompat.getColor(context, colorRes))
}

fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    setTextColor(ContextCompat.getColor(context, colorRes))
}
