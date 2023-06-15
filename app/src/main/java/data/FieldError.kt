package data

import androidx.annotation.StringRes
import com.metinvandar.signupapp.R

enum class FieldError(@StringRes val errorMessageId: Int) {
    FIRST_NAME_EMPTY(R.string.first_name_empty_error),
    EMAIL_INVALID(R.string.email_invalid),
    PASSWORD_INVALID(R.string.invalid_password_message),
    WEB_SITE_EMPTY(R.string.website_empty_error),
    PROFILE_PHOTO_EMPTY(R.string.profile_photo_empty_error),
}

