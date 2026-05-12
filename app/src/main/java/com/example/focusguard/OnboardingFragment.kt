package com.example.focusguard

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.focusguard.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FocusViewModel by activityViewModels()

    private var selectedPhotoUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it
            binding.ivAnchorPhoto.setImageURI(it)
            binding.tvUploadHint.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if an anchor is already set. If yes, skip onboarding
        val currentQuote = viewModel.emotionalAnchorQuote.value
        val currentPhoto = viewModel.emotionalAnchorPhotoUri.value
        if (currentQuote != "I build for my family's better tomorrow." || currentPhoto != null) {
            findNavController().navigate(R.id.action_OnboardingFragment_to_FirstFragment)
            return
        }

        binding.btnUploadPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnContinue.setOnClickListener {
            val quote = binding.etAnchorQuote.text.toString().takeIf { it.isNotBlank() }
                ?: "I build for my family's better tomorrow."
            viewModel.saveEmotionalAnchor(quote, selectedPhotoUri?.toString())
            findNavController().navigate(R.id.action_OnboardingFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
