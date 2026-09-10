package com.daily.nexamartpartner.features.admin.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.core.format.ValueFormatter
import com.daily.nexamartpartner.core.widgets.UiFeedback
import com.daily.nexamartpartner.databinding.FragmentAdminProductDetailsBinding
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.admin.domain.model.ProductAdminAction
import com.daily.nexamartpartner.features.admin.domain.model.ProductDetails
import com.daily.nexamartpartner.features.admin.presentation.state.ProductDetailsUiState
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.ProductDetailsViewModel
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.ProductDetailsViewModelFactory
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.ProductEvent
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModel
import com.daily.nexamartpartner.features.auth.presentation.viewmodel.AuthCoordinatorViewModelFactory
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ProductDetailsScreen : Fragment(R.layout.fragment_admin_product_details) {
    private var _binding: FragmentAdminProductDetailsBinding? = null
    private val binding: FragmentAdminProductDetailsBinding
        get() = requireNotNull(_binding)

    private val productId by lazy { requireArguments().getString(ARG_PRODUCT_ID).orEmpty() }

    private val authCoordinatorViewModel: AuthCoordinatorViewModel by activityViewModels {
        AuthCoordinatorViewModelFactory(
            restoreSessionUseCase = requireContext().appContainer.restoreSessionUseCase,
            logoutUseCase = requireContext().appContainer.logoutUseCase,
            authStateStore = requireContext().appContainer.authStateStore
        )
    }

    private val viewModel: ProductDetailsViewModel by viewModels {
        ProductDetailsViewModelFactory(
            productId = productId,
            getProductDetails = requireContext().appContainer.provideGetProductDetailsUseCase(),
            performProductAdminAction = requireContext().appContainer.providePerformProductAdminActionUseCase()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminProductDetailsBinding.bind(view)
        binding.productDetailsBackButton.setOnClickListener { findNavController().navigateUp() }
        binding.productDetailsRefreshButton.setOnClickListener { viewModel.refresh() }
        binding.productDetailsRetryButton.setOnClickListener { viewModel.retry() }
        binding.productDetailsSwipeRefresh.setOnRefreshListener { viewModel.refresh() }
        collectUi()
        collectEvents()
    }

    private fun collectUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun collectEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is ProductEvent.SessionExpired -> authCoordinatorViewModel.onSessionExpired()
                        is ProductEvent.Message -> UiFeedback.showSnackbar(binding.root, event.text)
                        is ProductEvent.ActionSucceeded -> {
                            UiFeedback.showSnackbar(
                                binding.root,
                                getString(R.string.admin_product_action_success)
                            )
                            if (event.action == ProductAdminAction.DELETE) {
                                findNavController().navigateUp()
                            }
                        }

                        is ProductEvent.SavedSuccessfully -> Unit
                    }
                }
            }
        }
    }

    private fun render(state: ProductDetailsUiState) {
        binding.productDetailsSwipeRefresh.isRefreshing = state.isRefreshing
        when (val content = state.content) {
            is ProductDetailsUiState.Content.Loading -> {
                binding.productDetailsLoading.isVisible = true
                binding.productDetailsStateSection.isVisible = false
                binding.productDetailsContentSection.isVisible = false
            }

            is ProductDetailsUiState.Content.Success -> {
                binding.productDetailsLoading.isVisible = false
                binding.productDetailsStateSection.isVisible = false
                binding.productDetailsContentSection.isVisible = true
                renderProduct(content.product, state.actionInProgress)
            }

            is ProductDetailsUiState.Content.Error -> {
                binding.productDetailsLoading.isVisible = false
                binding.productDetailsStateSection.isVisible = true
                binding.productDetailsContentSection.isVisible = false
                binding.productDetailsStateTitleText.text = getString(R.string.admin_products_error_title)
                binding.productDetailsStateMessageText.text = content.message
            }

            is ProductDetailsUiState.Content.Unavailable -> {
                binding.productDetailsLoading.isVisible = false
                binding.productDetailsStateSection.isVisible = true
                binding.productDetailsContentSection.isVisible = false
                binding.productDetailsStateTitleText.text = getString(R.string.admin_products_unavailable_title)
                binding.productDetailsStateMessageText.text = content.message
            }
        }
    }

    private fun renderProduct(product: ProductDetails, actionInProgress: ProductAdminAction?) {
        val unavailable = getString(R.string.admin_product_details_unavailable_value)
        binding.productNameText.text = product.name.ifBlank { unavailable }
        binding.productDescriptionText.text = product.description ?: unavailable
        binding.productCategoryText.text = getString(
            R.string.admin_product_details_category_template,
            product.categoryName ?: unavailable
        )

        val priceText = formatPrice(product.price, product.currencyCode, unavailable)
        val discountedText = product.discountedPrice?.let {
            formatPrice(it, product.currencyCode, unavailable)
        }
        binding.productPriceText.text = buildString {
            append(getString(R.string.admin_product_details_price_template, priceText))
            discountedText?.let {
                append('\n')
                append(getString(R.string.admin_product_details_discounted_price_template, it))
            }
            product.discountPercent?.let {
                append('\n')
                append(getString(R.string.admin_product_details_discount_template, it.toPlainString()))
            }
        }

        binding.productStockText.text = getString(
            R.string.admin_product_details_stock_template,
            product.stock?.toString() ?: unavailable,
            product.unit ?: unavailable
        )
        binding.productSkuText.text = getString(
            R.string.admin_product_details_sku_template,
            product.sku ?: unavailable
        )
        binding.productStatusText.text = getString(
            R.string.admin_product_details_status_template,
            product.status.backendValue,
            product.availability.backendValue
        )
        binding.productTimestampsText.text = getString(
            R.string.admin_product_details_timestamps_template,
            product.createdAt ?: unavailable,
            product.updatedAt ?: unavailable
        )

        renderActions(product, actionInProgress)
    }

    private fun formatPrice(
        amount: java.math.BigDecimal?,
        currencyCode: String?,
        unavailable: String
    ): String = when {
        amount == null -> unavailable
        currencyCode.isNullOrBlank() -> amount.toPlainString()
        else -> ValueFormatter.formatCurrency(amount, currencyCode)
    }

    private fun renderActions(product: ProductDetails, actionInProgress: ProductAdminAction?) {
        binding.productActionsContainer.removeAllViews()
        if (product.allowedActions.isEmpty()) {
            binding.productActionsContainer.addView(
                android.widget.TextView(requireContext()).apply {
                    text = getString(R.string.admin_product_no_actions)
                }
            )
            return
        }
        product.allowedActions.forEach { action ->
            binding.productActionsContainer.addView(
                MaterialButton(requireContext()).apply {
                    text = action.backendValue
                    isEnabled = actionInProgress == null
                    setOnClickListener { handleAction(product, action) }
                }
            )
        }
    }

    private fun handleAction(product: ProductDetails, action: ProductAdminAction) {
        if (action == ProductAdminAction.EDIT) {
            findNavController().navigate(
                R.id.adminProductFormFragment,
                bundleOf("mode" to "EDIT", "productId" to product.productId)
            )
            return
        }
        val message = if (action == ProductAdminAction.DELETE) {
            getString(R.string.admin_product_delete_confirmation, product.name)
        } else {
            getString(R.string.admin_product_action_confirmation, product.name, action.backendValue)
        }
        UiFeedback.showConfirmationDialog(
            anchor = binding.root,
            title = action.backendValue,
            message = message,
            positiveActionText = getString(R.string.common_confirm),
            negativeActionText = getString(R.string.common_cancel),
            onConfirmed = { viewModel.performAction(action) }
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_PRODUCT_ID = "productId"
    }
}
