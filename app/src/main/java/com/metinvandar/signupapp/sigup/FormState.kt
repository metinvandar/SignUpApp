package com.metinvandar.signupapp.sigup

sealed class FormState {
    object Idle: FormState()
    object Success: FormState()
    data class Error(val fieldError: FieldError): FormState()
}
