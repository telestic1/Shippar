package com.example.shippar.Utils

import android.content.Context
import android.content.Intent

object Utils {
    fun intentWithClear(fromActivity: Context, toActivity: Class<*>) {
        val i = Intent(fromActivity, toActivity)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        fromActivity.startActivity(i)
    }
}