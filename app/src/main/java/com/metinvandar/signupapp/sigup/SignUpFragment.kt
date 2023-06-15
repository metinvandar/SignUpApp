package com.metinvandar.signupapp.sigup

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.metinvandar.signupapp.R
import com.metinvandar.signupapp.databinding.FragmentSignUpBinding
import com.metinvandar.signupapp.extensions.hideKeyboard
import com.metinvandar.signupapp.extensions.scrollToView
import com.metinvandar.signupapp.extensions.showError
import dagger.hilt.android.AndroidEntryPoint
import data.FieldError
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by activityViewModels()

    private var imageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showError(errorMessage = getString(R.string.camera_permission_error))
            } else {
                dispatchTakePictureIntent()
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri?.let {
                    binding.addAvatar.visibility = View.GONE
                    binding.imageViewAvatar.visibility = View.VISIBLE
                    binding.imageViewAvatar.setImageURI(it)
                    viewModel.userProfile.imagePath = it.toString()
                }
            }
        }

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
        binding.avatarContainer.setOnClickListener { requestCameraPermission() }
        binding.submitButton.setOnClickListener {
            viewModel.validateInputs(
                emailAddress = binding.emailEditText.text.toString(),
                password = binding.passwordEditText.text.toString(),
                firstName = binding.firstNameEditText.text.toString(),
                webAddress = binding.websiteEditText.text.toString(),
                image = imageUri?.toString()
            )
        }
    }

    private fun collectFormState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.formState.collect { formState ->
                    when (formState) {
                        is FormState.ValidationSuccess -> {
                            hideKeyboard()
                            viewModel.idleFormState()
                            findNavController().navigate(SignUpFragmentDirections.actionSignUpToConfirmation())

                        }
                        is FormState.Error -> {
                            handleFormValidationError(formState.fieldError)
                        }
                        is FormState.Idle -> {
                            formState.userProfile?.imagePath?.let {
                                val uri = Uri.parse(it)
                                binding.run {
                                    addAvatar.visibility = View.GONE
                                    imageViewAvatar.visibility = View.VISIBLE
                                    imageViewAvatar.setImageURI(uri)

                                    firstNameTextInputLayout.error = null
                                    emailTextInputLayout.error = null
                                    websiteTextInputLayout.error = null
                                    passwordTextInputLayout.error = null
                                }
                            }


                        }
                    }
                }
            }
        }
    }

    private fun handleFormValidationError(fieldError: FieldError) {
        when (fieldError.name) {
            FieldError.EMAIL_INVALID.name -> {
                binding.emailTextInputLayout.error = getString(fieldError.errorMessageId)
                binding.scrollView.scrollToView(binding.emailTextInputLayout)
            }
            FieldError.FIRST_NAME_EMPTY.name -> {
                binding.firstNameTextInputLayout.error = getString(fieldError.errorMessageId)
                binding.scrollView.scrollToView(binding.firstNameTextInputLayout)
            }
            FieldError.PASSWORD_INVALID.name -> {
                binding.passwordTextInputLayout.error = getString(fieldError.errorMessageId)
                binding.passwordEditText.text?.clear()
                binding.scrollView.scrollToView(binding.passwordTextInputLayout)
            }
            FieldError.WEB_SITE_EMPTY.name -> {
                binding.websiteTextInputLayout.error = getString(fieldError.errorMessageId)
                binding.scrollView.scrollToView(binding.websiteTextInputLayout)
            }
            FieldError.PROFILE_PHOTO_EMPTY.name -> {
                showError(
                    errorMessage = getString(fieldError.errorMessageId),
                    onClose = {
                        viewModel.idleFormState()
                    })
            }
        }
    }

    private fun requestCameraPermission() {
        val permissionsGranted = PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireContext(), it
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (permissionsGranted) {
            dispatchTakePictureIntent()
        } else {
            requestPermissionLauncher.launch(PERMISSIONS)
        }
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoUri: Uri? = context?.contentResolver?.let { resolver ->
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val randomString = UUID.randomUUID().toString().substring(0, 6)
            val imageTitle = "IMG_$timeStamp$randomString.jpg"

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, imageTitle)
            }
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            imageUri
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        takePictureLauncher.launch(intent)
    }

    companion object {
        private val PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}