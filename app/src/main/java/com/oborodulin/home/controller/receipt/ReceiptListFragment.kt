package com.oborodulin.home.controller.receipt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.oborodulin.home.R
import com.oborodulin.home.model.receipt.ReceiptListViewModel

private const val TAG = "ReceiptListFragment"

class ReceiptListFragment : Fragment() {
    private val receiptListViewModel: ReceiptListViewModel by lazy {
        ViewModelProvider(this).get(ReceiptListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total receipts: ${receiptListViewModel.receiptEntities.size}")
    }

    companion object {
        fun newInstance(): ReceiptListFragment {
            return ReceiptListFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payer, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
    }
}