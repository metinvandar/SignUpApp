package com.metinvandar.signupapp.sigup

import data.FieldError
import data.UserProfile

sealed class FormState {
    data class Idle(var imagePath: String? = null, var userProfile: UserProfile? = null) :
        FormState()

    object ValidationSuccess : FormState()
    class Error(val fieldError: FieldError) : FormState()
}
