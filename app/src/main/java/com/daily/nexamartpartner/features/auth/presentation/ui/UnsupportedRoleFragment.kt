package com.daily.nexamartpartner.features.auth.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.FragmentUnsupportedRoleBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModelFactory

class UnsupportedRoleFragment : Fragment(R.layout.fragment_unsupported_role) {
    private var _binding: FragmentUnsupportedRoleBinding? = null
    private val binding: FragmentUnsupportedRoleBinding
        get() = requireNotNull(_binding)

    private val authCoordinatorViewModel: AuthCoordinatorViewModel by activityViewModels {
        AuthCoordinatorViewModelFactory(
            restoreSessionUseCase = requireContext().appContainer.restoreSessionUseCase,
            logoutUseCase = requireContext().appContainer.logoutUseCase,
            authStateStore = requireContext().appContainer.authStateStore
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUnsupportedRoleBinding.bind(view)
        binding.backToLoginButton.setOnClickListener {
            authCoordinatorViewModel.returnToLoginFromUnsupportedRole()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
