package net.maiatoday.hellolittleprinter.util

import android.content.Context
import android.widget.Toast

/**
 * Created by maia on 2017/09/05.
 */
fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


fun Context.toastSlow(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
