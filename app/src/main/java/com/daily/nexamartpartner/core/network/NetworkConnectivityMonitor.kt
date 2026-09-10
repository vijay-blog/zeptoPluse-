package com.daily.nexamartpartner.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Process-wide connectivity signal. This describes device network capability,
 * not whether the NexaMart backend is healthy or reachable.
 */
class NetworkConnectivityMonitor(context: Context) {
    enum class State { ONLINE, OFFLINE }

    private val connectivityManager =
        context.applicationContext.getSystemService(ConnectivityManager::class.java)

    private val _state = MutableStateFlow(currentState())
    val state: StateFlow<State> = _state.asStateFlow()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = publish()
        override fun onLost(network: Network) = publish()
        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) = publish()
    }

    init {
        runCatching {
            connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build(),
                callback
            )
        }
    }

    fun refresh() = publish()

    private fun publish() {
        _state.value = currentState()
    }

    private fun currentState(): State {
        val active = connectivityManager.activeNetwork ?: return State.OFFLINE
        val capabilities = connectivityManager.getNetworkCapabilities(active) ?: return State.OFFLINE
        return if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            State.ONLINE
        } else {
            State.OFFLINE
        }
    }
}
