package com.metinvandar.signupapp.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.metinvandar.signupapp.R

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.showError(
    errorMessage: String,
    onClose: (() -> Unit)? = null
) {
    requireView().snackBar(
        message = errorMessage,
        action = onClose,
        onDismissed = onClose
    )
}

fun View.snackBar(
    message: String,
    action: (() -> Unit)? = null,
    onDismissed: (() -> Unit)? = null,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    val snackBar = Snackbar.make(this, message, duration)
    val okay = context.getString(R.string.ok)
    snackBar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
    onDismissed?.let { dismissCallback ->
        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                dismissCallback.invoke()
            }
        })
    }
    snackBar.setAction(okay) {
        action?.invoke()
        snackBar.dismiss()
    }
    snackBar.show()
}

fun ScrollView.scrollToView(view: View) {
    this.post {
        scrollTo(0, view.bottom)
        view.requestFocus()
    }
}
