package com.metinvandar.signupapp.sigup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import data.FieldError
import data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(): ViewModel() {

    private val _formState: MutableStateFlow<FormState> = MutableStateFlow(FormState.Idle())
    val formState: StateFlow<FormState> get() = _formState

    val userProfile = UserProfile()

    fun validateInputs(firstName: String, emailAddress: String, password: String, webAddress: String, image: String?) {
        val formValidationState = when {
            firstName.isEmpty() -> {
                FormState.Error(FieldError.FIRST_NAME_EMPTY)
            }
            emailAddress.isEmpty() -> {
                FormState.Error(FieldError.FIRST_NAME_EMPTY)
            }
            emailAddress.matches(Regex(EMAIL_REGEX)).not() -> {
                FormState.Error(FieldError.EMAIL_INVALID)
            }
            password.isEmpty() -> {
                FormState.Error(FieldError.PASSWORD_EMPTY)
            }
            webAddress.isEmpty() -> {
                FormState.Error(FieldError.WEB_SITE_EMPTY)
            }
            image.isNullOrEmpty() -> {
                FormState.Error(FieldError.PROFILE_PHOTO_EMPTY)
            }
            else -> {
                userProfile.run {
                    name = firstName
                    email = emailAddress
                    webSite = webAddress
                    imagePath = image
                }
                FormState.ValidationSuccess
            }

        }

        _formState.value = formValidationState

    }

    fun idleFormState() {
        _formState.value = FormState.Idle(userProfile)
    }

    companion object {
        private const val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    }
}
