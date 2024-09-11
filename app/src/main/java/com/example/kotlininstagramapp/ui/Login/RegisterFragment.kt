package com.example.kotlininstagramapp.ui.Login

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.databinding.FragmentRegisterBinding
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import org.greenrobot.eventbus.EventBus

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleEmailTelClick()
        handleIleriButtonClick()
        binding.tvLogin.setOnClickListener {
            // Navigate to LoginFragment or LoginActivity if needed
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
      //  _binding = null
    }

    private fun handleIleriButtonClick() {
        binding.btnIleri.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_registerNextFragment)

            val action = RegisterFragmentDirections.actionRegisterFragmentToRegisterNextFragment(
                binding.etRegisterpage.text.toString(),
                binding.etRegisterpage.hint.toString()
            )
            findNavController().navigate(action)

           /* if (binding.etRegisterpage.hint == "Phone") {
                binding.registerRoot.visibility = View.GONE
                binding.flRegisterpage.visibility = View.VISIBLE
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_registerpage, RegisterNextFragment())
                    .addToBackStack("RegisterFragment")
                    .commit()

                EventBus.getDefault().postSticky(EventBusDataEvents.KayitBilgileriGonder(binding.etRegisterpage.text.toString(), null))
            } else {
                binding.registerRoot.visibility = View.GONE
                binding.flRegisterpage.visibility = View.VISIBLE
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_registerpage, RegisterNextFragment())
                    .addToBackStack("RegisterFragment")
                    .commit()

                EventBus.getDefault().postSticky(EventBusDataEvents.KayitBilgileriGonder(null, binding.etRegisterpage.text.toString()))
            }*/
        }
    }

    private fun handleEmailTelClick() {
        binding.tvTel.setOnClickListener {
            binding.viewTel.setBackgroundColor(Color.BLACK)
            binding.viewMail.setBackgroundColor(Color.parseColor("#DFDFDF"))
            binding.etRegisterpage.apply {
                inputType = InputType.TYPE_CLASS_PHONE
                hint = "Phone"
            }
        }

        binding.tvEposta.setOnClickListener {
            binding.viewTel.setBackgroundColor(Color.parseColor("#DFDFDF"))
            binding.viewMail.setBackgroundColor(Color.BLACK)
            binding.etRegisterpage.apply {
                inputType = InputType.TYPE_CLASS_TEXT
                hint = "Email"
            }
        }
    }
}
