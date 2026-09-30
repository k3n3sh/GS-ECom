package com.k3n3sh.gsecom.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ConnectivityChecker {
    fun isOnline(): Boolean
}

class AndroidConnectivityChecker @Inject constructor(
    @ApplicationContext private val context: Context,
) : ConnectivityChecker {

    // VALIDATED = real internet, not just Wi-Fi
    override fun isOnline(): Boolean {
        val manager = context.getSystemService(ConnectivityManager::class.java) ?: return true
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
