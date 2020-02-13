package com.mivas.myukulelesongs.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Util class that help with opening and closing the keyboard.
 */
object KeyboardUtils {

    /**
     * Closes the keyboard.
     *
     * @param activity The activity
     */
    fun closeKeyboard(activity: Activity?) {
        if (activity != null) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isAcceptingText && activity.currentFocus != null) {
                imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }
    }

    /**
     * Focuses an EditText and opens the keyboard.
     *
     * @param context The context
     * @param editText The EditText to focus
     */
    fun focusEditText(context: Context, editText: EditText) {
        editText.requestFocus(View.FOCUS_RIGHT)
        editText.setSelection(editText.text.length)
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

}
