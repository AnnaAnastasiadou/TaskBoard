package com.example.taskboard.presentation.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class NetworkMonitor @Inject constructor(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // A callbackFlow is a specialized way to turn a callback-based API (like Android's listener)
    // into a Kotlin Flow (a stream of data).
    val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // push ture into the stream whenever the network comes online
                trySend(true)
            }

            override fun onLost(network: Network) {
                // push false into the stream whenever the network comes offline
                trySend(false)
            }
        }

        val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
        connectivityManager.registerNetworkCallback(request, callback)

        // set initial state
        trySend(checkCurrentNetwork())

        // if the fragment or viewModel is destroyed, the app will still listen
        // to the network in the background if not unregistered
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    // sometimes the system might trigger onAvailable twice in a row (eg. switching from Wi-Fi to a faster Wi-Fi)
    // distinctUntilChanged() ensures that if the flow received the same value twice, it's ignored
    }.distinctUntilChanged()

    private fun checkCurrentNetwork(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}