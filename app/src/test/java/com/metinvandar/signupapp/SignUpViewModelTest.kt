package com.metinvandar.signupapp

import com.metinvandar.signupapp.sigup.FormState
import com.metinvandar.signupapp.sigup.SignUpViewModel
import data.FieldError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    @get:Rule
    internal val coroutineScopeRule = MainCoroutineRule()

    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setUp() {
        viewModel = SignUpViewModel()
    }


    @Test
    fun `validateInputs - invalid email`() = runTest {
        // Given
        val email = "metinvandar"
        val name = "metin"
        val password = "12345"
        val image = "image"
        val website = "website.com"

        // When
        viewModel.validateInputs(
            firstName = name,
            emailAddress = email,
            password = password,
            image = image,
            webAddress = website
        )

        // Then
        val formState = viewModel.formState.first()
        assert(formState is FormState.Error)
        val errorState = formState as FormState.Error
        assert(errorState.fieldError == FieldError.EMAIL_INVALID)
    }

    @Test
    fun `validateInputs - empty name`() = runTest {
        // Given
        val email = "metinvandar@gmail.com"
        val name = ""
        val password = "12345"
        val image = "image"
        val website = "website.com"

        // When
        viewModel.validateInputs(
            firstName = name,
            emailAddress = email,
            password = password,
            image = image,
            webAddress = website
        )

        // Then
        val formState = viewModel.formState.first()
        assert(formState is FormState.Error)
        val errorState = formState as FormState.Error
        assert(errorState.fieldError == FieldError.FIRST_NAME_EMPTY)
    }

    @Test
    fun `validateInputs - website empty`() = runTest {
        // Given
        val email = "metinvandar@gmail.com"
        val name = "metin"
        val password = "123metinvandar"
        val image = "image"
        val website = ""

        // When
        viewModel.validateInputs(
            firstName = name,
            emailAddress = email,
            password = password,
            image = image,
            webAddress = website
        )

        // Then
        val formState = viewModel.formState.first()
        assert(formState is FormState.Error)
        val errorState = formState as FormState.Error
        assert(errorState.fieldError == FieldError.WEB_SITE_EMPTY)
    }

    @Test
    fun `validateInputs - password invalid`() = runTest {
        // Given
        val email = "metinvandar@gmail.com"
        val name = "metin"
        val password = "12345"
        val image = "image"
        val website = ""

        // When
        viewModel.validateInputs(
            firstName = name,
            emailAddress = email,
            password = password,
            image = image,
            webAddress = website
        )

        // Then
        val formState = viewModel.formState.first()
        assert(formState is FormState.Error)
        val errorState = formState as FormState.Error
        assert(errorState.fieldError == FieldError.PASSWORD_INVALID)
    }

    @Test
    fun `validateInputs - all valid`() = runTest {
        // Given
        val email = "metinvandar@gmail.com"
        val name = "metin"
        val password = "12345Metin"
        val image = "image"
        val website = "website"

        // When
        viewModel.validateInputs(
            firstName = name,
            emailAddress = email,
            password = password,
            image = image,
            webAddress = website
        )

        // Then
        val formState = viewModel.formState.first()
        assert(formState is FormState.ValidationSuccess)
    }
}