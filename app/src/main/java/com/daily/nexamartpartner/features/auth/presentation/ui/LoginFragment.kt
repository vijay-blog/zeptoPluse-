package com.daily.nexamartpartner.features.auth.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.FragmentLoginBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.LoginViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.LoginViewModelFactory
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = requireNotNull(_binding)

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            loginUseCase = requireContext().appContainer.loginUseCase,
            authStateStore = requireContext().appContainer.authStateStore
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        bindListeners()
        collectState()
    }

    private fun bindListeners() {
        binding.identifierInputEditText.doAfterTextChanged {
            loginViewModel.onIdentifierChanged(it?.toString().orEmpty())
        }
        binding.passwordInputEditText.doAfterTextChanged {
            loginViewModel.onPasswordChanged(it?.toString().orEmpty())
        }
        binding.loginButton.setOnClickListener { loginViewModel.submitLogin() }
        binding.createAccountButton.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiState.collect { state ->
                    binding.identifierInputLayout.error = state.identifierError
                    binding.passwordInputLayout.error = state.passwordError

                    binding.loginButton.isEnabled = !state.isSubmitting
                    binding.loginButton.text =
                        if (state.isSubmitting) "Please wait..." else getString(R.string.login_button)

                    if (state.formError.isNullOrBlank()) {
                        binding.loginErrorText.visibility = View.GONE
                    } else {
                        binding.loginErrorText.visibility = View.VISIBLE
                        binding.loginErrorText.text = state.formError
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
