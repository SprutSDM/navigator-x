package ru.zakoulov.navigatorx.ui

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
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
