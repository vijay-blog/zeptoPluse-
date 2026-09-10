package com.daily.nexamartpartner.features.shared.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.databinding.FragmentFeaturePlaceholderBinding

class FeaturePlaceholderFragment : Fragment(R.layout.fragment_feature_placeholder) {
    private var _binding: FragmentFeaturePlaceholderBinding? = null
    private val binding: FragmentFeaturePlaceholderBinding
        get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFeaturePlaceholderBinding.bind(view)

        binding.featureTitleText.text = requireArguments().getString(ARG_FEATURE_TITLE).orEmpty()
        binding.featureSubtitleText.text = requireArguments().getString(ARG_FEATURE_SUBTITLE).orEmpty()
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_FEATURE_TITLE = "featureTitle"
        const val ARG_FEATURE_SUBTITLE = "featureSubtitle"
    }
}
