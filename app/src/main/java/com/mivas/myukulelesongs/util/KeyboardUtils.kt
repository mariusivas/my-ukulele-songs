package com.mivas.myukulelesongs.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object KeyboardUtils {

    fun closeKeyboard(activity: Activity?) {
        if (activity != null) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isAcceptingText && activity.currentFocus != null) {
                imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }
    }

    fun focusEditText(context: Context, editText: EditText) {
        editText.requestFocus(View.FOCUS_RIGHT)
        editText.setSelection(editText.text.length)
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

}
