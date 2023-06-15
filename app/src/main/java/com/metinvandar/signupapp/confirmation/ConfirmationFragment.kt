package com.metinvandar.signupapp.confirmation

import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.metinvandar.signupapp.R
import com.metinvandar.signupapp.databinding.FragmentConfirmationBinding
import com.metinvandar.signupapp.sigup.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmationFragment : Fragment() {

    private var _binding: FragmentConfirmationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel.userProfile) {
            binding.greeting.text = getString(R.string.greeting_with_name, name ?: "")
            binding.name.text = name

            val spannableString = SpannableString(webSite)
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val x = 0
                }
            }, 0, webSite!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(
                ForegroundColorSpan(Color.BLUE),
                0,
                webSite!!.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.website.movementMethod = LinkMovementMethod.getInstance()
            binding.website.text = spannableString
            binding.website.paintFlags = binding.website.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            binding.imageViewAvatar.setImageURI(Uri.parse(imagePath))
            binding.email.text = email
        }
    }
}
