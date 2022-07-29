package com.oborodulin.home.controller.payer

import android.os.Bundle
import com.oborodulin.home.controller.Validator
import android.text.*
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.oborodulin.home.R
import com.oborodulin.home.domain.payer.Payer
import com.oborodulin.home.model.payer.PayerViewModel
import java.math.BigDecimal
import java.util.*


private const val ARG_PAYER_ID = "payer_id"

class PayerFragment : Fragment() {
    private val LOGTAG = this::class.toString()
    private lateinit var payer: Payer
    private lateinit var etErcCode: EditText
    private lateinit var etFullName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etTotalArea: EditText
    private lateinit var etLivingSpace: EditText
    private lateinit var etHeatedVolume: EditText
    private lateinit var actvPaymentDay: AutoCompleteTextView
    private lateinit var etPersonsNum: EditText
    private lateinit var tilErcCode: TextInputLayout
    private lateinit var tilFullName: TextInputLayout
    private lateinit var tilAddress: TextInputLayout
    private lateinit var tilTotalArea: TextInputLayout
    private lateinit var tilLivingSpace: TextInputLayout
    private lateinit var tilHeatedVolume: TextInputLayout
    private lateinit var tilPaymentDay: TextInputLayout
    private lateinit var tilPersonsNum: TextInputLayout
    private lateinit var validator: Validator
    private val vmPayer: PayerViewModel by lazy {
        ViewModelProvider(this).get(PayerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        payer = Payer()
        val payerId: UUID = arguments?.getSerializable(ARG_PAYER_ID) as UUID
        Log.d(LOGTAG, "args bundle payer ID: $payerId")
        vmPayer.loadPayer(payerId)
        validator = Validator(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payer, container, false)
        etErcCode = view.findViewById(R.id.erc_code_input)
        etFullName = view.findViewById(R.id.full_name_input)
        etAddress = view.findViewById(R.id.address_input)
        etTotalArea = view.findViewById(R.id.total_area_input)
        etLivingSpace = view.findViewById(R.id.living_space_input)
        etHeatedVolume = view.findViewById(R.id.heated_volume_input)
        etPersonsNum = view.findViewById(R.id.persons_num_input)

        actvPaymentDay = view.findViewById(R.id.payment_day_dropdown)
        actvPaymentDay.setAdapter(
            ArrayAdapter(
                requireActivity(),
                R.layout.dropdown_menu_popup_item,
                //android.R.layout.simple_dropdown_item_1line,
                resources.getStringArray(R.array.days)
            )
        )
        tilErcCode = view.findViewById(R.id.erc_code_layout)
        tilFullName = view.findViewById(R.id.full_name_layout)
        tilAddress = view.findViewById(R.id.address_layout)
        tilTotalArea = view.findViewById(R.id.total_area_layout)
        tilLivingSpace = view.findViewById(R.id.living_space_layout)
        tilHeatedVolume = view.findViewById(R.id.heated_volume_layout)
        tilPaymentDay = view.findViewById(R.id.payment_day_layout)
        tilPersonsNum = view.findViewById(R.id.persons_num_layout)

        tilTotalArea.suffixText = superscriptText(R.string.m2_unit, "2")
        tilLivingSpace.suffixText = superscriptText(R.string.m2_unit, "2")
        tilHeatedVolume.suffixText = superscriptText(R.string.m3_unit, "3")
        return view
    }

    private fun superscriptText(@StringRes resId: Int, s: String): CharSequence {
        val resString = resources.getString(resId)
        val strSpan = SpannableStringBuilder(resString)

        strSpan.setSpan(
            SuperscriptSpan(), resString.indexOf(s),
            resString.indexOf(s) + s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        strSpan.setSpan(
            RelativeSizeSpan(0.5f), resString.indexOf(s),
            resString.indexOf(s) + s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return strSpan
    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {
                R.id.erc_code_input ->
                    if (validator.isNotEmpty(etErcCode, s, R.string.erc_code_empty_error))
                        payer.ercCode = s.toString()
                R.id.full_name_input ->
                    if (validator.isNotEmpty(etFullName, s, R.string.full_name_empty_error))
                        payer.fullName = s.toString()
                R.id.address_input ->
                    if (validator.isNotEmpty(etAddress, s, R.string.address_empty_error))
                        payer.address = s.toString()
                R.id.total_area_input -> if (validator.isEmptyOrDecimal(etTotalArea, s))
                    payer.totalArea = BigDecimal(s.toString())
                else payer.totalArea = null
                R.id.living_space_input -> if (validator.isEmptyOrDecimal(etLivingSpace, s))
                    payer.livingSpace = BigDecimal(s.toString())
                else payer.livingSpace = null
                R.id.heated_volume_input -> if (validator.isEmptyOrDecimal(etHeatedVolume, s))
                    payer.heatedVolume = BigDecimal(s.toString())
                else payer.heatedVolume = null
                R.id.persons_num_input ->
                    if (validator.isNotEmpty(etPersonsNum, s, R.string.persons_num_empty_error) &&
                        validator.isEmptyOrNumber(etPersonsNum, s)
                    )
                        payer.personsNum = s.toString().toInt()
                R.id.payment_day_dropdown ->
                    if (validator.isNotEmpty(actvPaymentDay, s, R.string.payment_day_empty_error))
                        payer.paymentDay = s.toString().toInt()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {
                R.id.erc_code_input -> payer.ercCode = s?.toString() ?: ""
                R.id.full_name_input -> payer.fullName = s?.toString() ?: ""
                R.id.address_input -> payer.address = s?.toString() ?: ""
                R.id.total_area_input -> payer.totalArea = s?.toString()?.toBigDecimalOrNull()
                R.id.living_space_input -> payer.livingSpace = s?.toString()?.toBigDecimalOrNull()
                R.id.heated_volume_input -> payer.heatedVolume = s?.toString()?.toBigDecimalOrNull()
                R.id.payment_day_dropdown -> payer.paymentDay = s?.toString()?.toIntOrNull()
                R.id.persons_num_input -> payer.personsNum = s?.toString()?.toIntOrNull()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vmPayer.payerLiveData.observe(
            viewLifecycleOwner
        ) { payer ->
            payer?.let {
                this.payer = it
                updateUI()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        etErcCode.addTextChangedListener(TextFieldValidation(etErcCode))
        etFullName.addTextChangedListener(TextFieldValidation(etFullName))
        etAddress.addTextChangedListener(TextFieldValidation(etAddress))
        etTotalArea.addTextChangedListener(TextFieldValidation(etTotalArea))
        etLivingSpace.addTextChangedListener(TextFieldValidation(etLivingSpace))
        etHeatedVolume.addTextChangedListener(TextFieldValidation(etHeatedVolume))
        etPersonsNum.addTextChangedListener(TextFieldValidation(etPersonsNum))
        actvPaymentDay.addTextChangedListener(TextFieldValidation(actvPaymentDay))
    }

    override fun onStop() {
        super.onStop()
        vmPayer.savePayer(payer)
    }

    private fun updateUI() {
        etErcCode.setText(payer.ercCode)
        etFullName.setText(payer.fullName)
        etAddress.setText(payer.address)
        etTotalArea.setText(payer.totalArea?.toString())
        etLivingSpace.setText(payer.livingSpace?.toString())
        etHeatedVolume.setText(payer.heatedVolume?.toString())
        etPersonsNum.setText(payer.personsNum?.toString())
        actvPaymentDay.setText(payer.paymentDay?.toString())
    }

    companion object {
        fun newInstance(payerId: UUID): PayerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_PAYER_ID, payerId)
            }
            return PayerFragment().apply {
                arguments = args
            }
        }
    }
}