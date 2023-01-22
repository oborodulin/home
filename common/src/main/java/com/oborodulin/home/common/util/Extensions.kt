package com.oborodulin.home.common.util

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun Context.toast(messageId: Int) {
    Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

//create an extension function on a date class which returns a string
fun Date.dateToString(format: String): String {
    //simple date formatter
    val dateFormatter = SimpleDateFormat(format, Locale.getDefault())

    //return the formatted date string
    return dateFormatter.format(this)
}