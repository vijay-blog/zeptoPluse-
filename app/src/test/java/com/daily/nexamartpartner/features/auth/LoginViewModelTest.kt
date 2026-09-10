package com.daily.nexamartpartner.features.auth

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.auth.domain.model.AuthState
import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import com.daily.nexamartpartner.features.auth.domain.repository.AuthRepository
import com.daily.nexamartpartner.features.auth.domain.usecase.LoginUseCase
import com.daily.nexamartpartner.features.auth.presentation.state.AuthStateStore
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.LoginViewModel
import com.daily.nexamartpartner.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state is empty`() {
        val viewModel = buildViewModel(AppResult.Success(adminSession()))
        assertEquals("", viewModel.uiState.value.identifier)
        assertEquals("", viewModel.uiState.value.password)
    }

    @Test
    fun `validation failure sets field errors`() = runTest {
        val viewModel = buildViewModel(AppResult.Success(adminSession()))

        viewModel.submitLogin()
        advanceUntilIdle()

        assertEquals("Please enter your phone or email.", viewModel.uiState.value.identifierError)
        assertEquals("Please enter your password.", viewModel.uiState.value.passwordError)
    }

    @Test
    fun `login success updates auth state`() = runTest {
        val authStateStore = AuthStateStore()
        val viewModel = buildViewModel(AppResult.Success(adminSession()), authStateStore)
        viewModel.onIdentifierChanged("admin@nexa.com")
        viewModel.onPasswordChanged("strong_password")

        viewModel.submitLogin()
        advanceUntilIdle()

        assertTrue(authStateStore.authState.value is AuthState.AuthenticatedAdmin)
    }

    @Test
    fun `login failure exposes error`() = runTest {
        val viewModel = buildViewModel(
            AppResult.Failure(
                AppFailure("Invalid login details. Please try again.", 401, FailureType.UNAUTHORIZED)
            )
        )
        viewModel.onIdentifierChanged("admin@nexa.com")
        viewModel.onPasswordChanged("wrong")

        viewModel.submitLogin()
        advanceUntilIdle()

        assertEquals("Invalid login details. Please try again.", viewModel.uiState.value.formError)
    }

    private fun buildViewModel(
        result: AppResult<UserSession>,
        authStateStore: AuthStateStore = AuthStateStore()
    ): LoginViewModel {
        val repository = object : AuthRepository {
            override suspend fun login(credentials: LoginCredentials): AppResult<UserSession> = result
            override suspend fun register(credentials: com.daily.nexamartpartner.features.auth.domain.model.RegistrationCredentials): AppResult<UserSession> = result
            override suspend fun restoreSession(): AppResult<UserSession?> = AppResult.Success(null)
            override suspend fun logout(): AppResult<Unit> = AppResult.Success(Unit)
            override suspend fun refreshToken(): AppResult<UserSession> = result
        }
        return LoginViewModel(LoginUseCase(repository), authStateStore)
    }

    private fun adminSession(): UserSession {
        return UserSession(
            accessToken = "access",
            refreshToken = "refresh",
            userId = 1L,
            name = "Admin",
            contact = "9999999999",
            role = UserRole.ADMIN
        )
    }
}
