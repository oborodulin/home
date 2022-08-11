package com.oborodulin.home.controller.service

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.oborodulin.home.R
import com.oborodulin.home.controller.Validator
import com.oborodulin.home.domain.entity.Service
import com.oborodulin.home.model.service.ServiceViewModel
import java.util.*

private const val TAG = "ServiceFragment"
private const val ARG_SERVICE_ID = "service_id"

class ServiceFragment : Fragment() {
    private var nextDisplayPos: Int = 0
    private lateinit var service: Service
    private lateinit var etDisplayPos: EditText
    private lateinit var etDisplayName: EditText
    private lateinit var etServiceDescr: EditText
    private lateinit var swIsAllocateRate: Switch
    private lateinit var tilDisplayPos: TextInputLayout
    private lateinit var tilDisplayName: TextInputLayout
    private lateinit var tilServiceDescr: TextInputLayout
    private lateinit var validator: Validator
    private val vmService: ServiceViewModel by lazy {
        ViewModelProvider(this).get(ServiceViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
  /*      vmService.nextDisplayPos().observe(
            viewLifecycleOwner
        ) { nextPos ->
            nextPos?.let {
                this.nextDisplayPos = it
                this.service = Service(it)
            }
        }

   */
        val serviceId: UUID = arguments?.getSerializable(ARG_SERVICE_ID) as UUID
        Log.d(TAG, "args bundle service ID: $serviceId")
       // vmService.loadService(serviceId)
        validator = Validator(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service, container, false)
        etDisplayPos = view.findViewById(R.id.display_pos_input)
        etDisplayName = view.findViewById(R.id.display_name_input)
        etServiceDescr = view.findViewById(R.id.service_descr_input)
        swIsAllocateRate = view.findViewById(R.id.is_allocate_rate_switch)

        tilDisplayPos = view.findViewById(R.id.display_pos_layout)
        tilDisplayName = view.findViewById(R.id.display_name_layout)
        tilServiceDescr = view.findViewById(R.id.service_descr_layout)

        return view
    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {
                R.id.display_pos_input ->
                    if (validator.isNotEmpty(
                            etDisplayPos, s, R.string.display_pos_empty_error
                        ) && validator.isEmptyOrNumber(etDisplayPos, s)
                    ) service.displayPos = s.toString().toInt()
                R.id.display_name_input ->
                    if (validator.isNotEmpty(etDisplayName, s, R.string.display_name_empty_error))
                        service.displayName = s.toString()
                R.id.service_descr_input -> service.serviceDescr = s?.toString()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {
                R.id.display_pos_input -> service.displayPos =
                    s?.toString()?.toInt() ?: nextDisplayPos
                R.id.display_name_input -> service.displayName = s?.toString() ?: ""
                R.id.service_descr_input -> service.serviceDescr = s?.toString()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
  /*      vmService.serviceLiveData.observe(
            viewLifecycleOwner
        ) { service ->
            service?.let {
                this.service = it
                this.nextDisplayPos = service.displayPos
                updateUI()
            }
        }

   */
    }

    override fun onStart() {
        super.onStart()
        etDisplayPos.addTextChangedListener(TextFieldValidation(etDisplayPos))
        etDisplayName.addTextChangedListener(TextFieldValidation(etDisplayName))
        etServiceDescr.addTextChangedListener(TextFieldValidation(etServiceDescr))
    }

    override fun onStop() {
        super.onStop()
        //vmService.saveService(service)
    }

    private fun updateUI() {
        etDisplayPos.setText(service.displayPos)
        etDisplayName.setText(service.displayName)
        etServiceDescr.setText(service.serviceDescr)
        swIsAllocateRate.isChecked = service.isAllocateRate
    }

    companion object {
        fun newInstance(serviceId: UUID): ServiceFragment {
            val args = Bundle().apply {
                putSerializable(ARG_SERVICE_ID, serviceId)
            }
            return ServiceFragment().apply {
                arguments = args
            }
        }
    }
}