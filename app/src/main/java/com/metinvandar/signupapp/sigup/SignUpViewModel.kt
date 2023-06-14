package com.metinvandar.signupapp.sigup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(): ViewModel() {

    private val _formState: MutableStateFlow<FormState> = MutableStateFlow(FormState.Idle)
    val formState: StateFlow<FormState> get() = _formState

    fun validateInputs(firstName: String, email: String, password: String, website: String) {
        val formValidationState = when {
            firstName.isEmpty() -> {
                FormState.Error(FieldError.FIRST_NAME_EMPTY)
            }
            email.isEmpty() -> {
                FormState.Error(FieldError.FIRST_NAME_EMPTY)
            }
            email.matches(Regex(EMAIL_REGEX)).not() -> {
                FormState.Error(FieldError.EMAIL_INVALID)
            }
            website.isEmpty() -> {
                FormState.Error(FieldError.WEB_SITE_EMPTY)
            }
            password.isEmpty() -> {
                FormState.Error(FieldError.PASSWORD_EMPTY)
            } else -> FormState.Success

        }

        _formState.value = formValidationState

    }

    fun resetFormState() {
        _formState.value = FormState.Idle
    }

    companion object {
        private const val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    }
}
