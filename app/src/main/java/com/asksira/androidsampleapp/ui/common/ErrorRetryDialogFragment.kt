package com.asksira.androidsampleapp.ui.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.asksira.androidsampleapp.R

class ErrorRetryDialogFragment: DialogFragment() {

    companion object {
        val TAG = this::class.simpleName!!
    }

    interface OnRetryListener {
        fun onErrorRetry()
    }

    private var onRetryListener: OnRetryListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRetryListener) {
            onRetryListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent = parentFragment
        if (parent is OnRetryListener) {
            onRetryListener = parent
        }
        val view = inflater.inflate(R.layout.fragment_error_retry_dialog, container, false)
        view.findViewById<TextView>(R.id.tvErrorMessage).text = arguments?.getString("message")
        view.findViewById<TextView>(R.id.tvOK).setOnClickListener {
            dismiss()
        }
        view.findViewById<TextView>(R.id.tvRetry).setOnClickListener {
            onRetryListener?.onErrorRetry()
            dismiss()
        }
        return view
    }

}