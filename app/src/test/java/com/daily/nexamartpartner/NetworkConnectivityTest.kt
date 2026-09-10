package com.daily.nexamartpartner

import com.daily.nexamartpartner.core.network.NetworkConnectivityMonitor
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkConnectivityTest {
    @Test
    fun states_are_explicit() {
        assertEquals(NetworkConnectivityMonitor.State.ONLINE.name, "ONLINE")
        assertEquals(NetworkConnectivityMonitor.State.OFFLINE.name, "OFFLINE")
    }
}
