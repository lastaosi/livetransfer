package com.jh.livetransfer.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionManager {
    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    fun hasPermissions(context: Context): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
