package com.daily.nexamartpartner.routing

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import com.daily.nexamartpartner.features.auth.presentation.state.AuthStateStore

class ProtectedNavigator(
    private val navController: NavController,
    private val authStateStore: AuthStateStore
) {
    fun navigate(destinationId: Int) {
        navigate(destinationId) { }
    }

    fun navigate(destinationId: Int, options: NavOptionsBuilder.() -> Unit) {
        val authState = authStateStore.authState.value
        val authorizedDestination = NavigationGuard.resolveAuthorizedDestination(authState, destinationId)
        val currentId = navController.currentDestination?.id
        if (currentId == authorizedDestination) return
        navController.navigate(authorizedDestination, null, navOptions(options))
    }
}
