package com.metinvandar.signupapp.sigup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.metinvandar.signupapp.SignUpFragmentDirections
import com.metinvandar.signupapp.databinding.FragmentSignUpBinding
import com.metinvandar.signupapp.extensions.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFormState()
        binding.submitButton.setOnClickListener {
            viewModel.validateInputs(
                email = binding.emailEditText.text.toString(),
                password = binding.passwordEditText.text.toString(),
                firstName = binding.firstNameEditText.text.toString(),
                website = binding.websiteEditText.text.toString()
            )
        }
    }

    private fun collectFormState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.formState.collect { formState ->
                    when (formState) {
                        is FormState.Success -> {
                            binding.run {
                                passwordTextInputLayout.error = null
                                emailTextInputLayout.error = null
                                firstNameTextInputLayout.error = null
                            }
                            hideKeyboard()
                            viewModel.resetFormState()
                            findNavController().navigate(SignUpFragmentDirections.actionSignUpToCamera())
                        }
                        is FormState.Error -> {
                            handleFormValidationError(formState.fieldError)
                        }
                        is FormState.Idle -> {
                            // Initial state. Do nothing
                        }
                    }
                }
            }
        }
    }

    private fun handleFormValidationError(fieldError: FieldError) {
        when (fieldError.name) {
            FieldError.EMAIL_EMPTY.name -> {
                binding.emailTextInputLayout.error = getString(fieldError.errorMessageId)
            }
            FieldError.EMAIL_INVALID.name -> {
                binding.emailTextInputLayout.error = getString(fieldError.errorMessageId)
            }
            FieldError.FIRST_NAME_EMPTY.name -> {
                binding.firstNameTextInputLayout.error = getString(fieldError.errorMessageId)
            }
            FieldError.PASSWORD_EMPTY.name -> {
                binding.passwordTextInputLayout.error = getString(fieldError.errorMessageId)
            }
            FieldError.WEB_SITE_EMPTY.name -> {
                binding.websiteTextInputLayout.error = getString(fieldError.errorMessageId)
            }
        }
    }
}