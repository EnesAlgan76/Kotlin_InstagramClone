package com.example.kotlininstagramapp.Login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kotlininstagramapp.R
import com.example.kotlininstagramapp.utils.EventBusDataEvents
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MailFragment : Fragment() {
    var text: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mail, container, false)
        var button = view.findViewById<Button>(R.id.btn_mail_frg)
        var textView = view.findViewById<TextView>(R.id.tv_mail_frg)
        textView.text = text

        Log.e("Mail Fragment Çalıştı","---")

        button.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_registerpage, RegisterFragment())
                .addToBackStack("RegisterFragmentMail")
                .commit()

        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }

    @Subscribe(sticky = true)
    fun onTelefonGonderReceived(event: EventBusDataEvents.KayitBilgileriGonder) {
        text = event.mail.toString()
    }
}
