package com.oborodulin.home.controller.service

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.oborodulin.home.R
import com.oborodulin.home.controller.ListFragment
import com.oborodulin.home.domain.entity.BaseEntity
import com.oborodulin.home.domain.entity.Service
import com.oborodulin.home.model.RVSelHolder
import com.oborodulin.home.model.RVSelListAdapter
import com.oborodulin.home.model.service.ServiceListViewModel
import java.util.*

private const val TAG = "ServiceListFragment"

class ServiceListFragment : Fragment(), ListFragment<Service> {
    /**
     * Интерфейс обратных вызовов
     */
    interface Callbacks {
        fun onServiceEditClick(serviceId: UUID)
        fun onServiceSelected(serviceId: UUID)
    }

    private var callbacks: Callbacks? = null
    private lateinit var serviceRecyclerView: RecyclerView
    private lateinit var serviceListEmptyText: TextView
    private lateinit var serviceListFab: FloatingActionButton

    private var adapter: RVSelListAdapter<Service>? =
        RVSelListAdapter(
            this,
            emptyList(),
            null,
            R.layout.list_item_service,
            R.menu.list_items,
            null
        )

    private val serviceListViewModel: ServiceListViewModel by lazy {
        ViewModelProvider(this).get(ServiceListViewModel::class.java)
    }

    private inner class ServiceHolder<T : BaseEntity>(view: View) :
        RVSelHolder<T>(view, R.id.iv_item_service_select) {
        private lateinit var service: T
        private val displayNameTextView: TextView =
            itemView.findViewById(R.id.tv_item_service_display_name)
        private val serviceDescrTextView: TextView =
            itemView.findViewById(R.id.tv_item_service_description)
        private val editImageView: ImageView = itemView.findViewById(R.id.iv_service_edit)

        init {
            itemView.setOnClickListener(this)
            editImageView.setOnClickListener(this)
        }

        override fun bind(entity: T) {
            service = entity
            displayNameTextView.text = (service as Service).displayName
            serviceDescrTextView.text = (service as Service).serviceDescr
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.iv_service_edit -> callbacks?.onServiceEditClick(service.id)
                else -> callbacks?.onServiceSelected(service.id)
            }
        }
    }

    override fun getViewHolder(view: View): RVSelHolder<Service> {
        return ServiceHolder(view)
    }

    companion object {
        fun newInstance(): ServiceListFragment {
            return ServiceListFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_list, container, false)
        serviceRecyclerView = view.findViewById(R.id.rv_service) as RecyclerView
        serviceListEmptyText = view.findViewById(R.id.tv_service_list_empty) as TextView
        serviceListFab = view.findViewById(R.id.fab_service_list) as FloatingActionButton
        serviceListFab.setOnClickListener { newService() }

        serviceRecyclerView.layoutManager = LinearLayoutManager(context)
        updateUI(emptyList())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
  /*      serviceListViewModel.servicesLiveData.observe(
            viewLifecycleOwner
        ) { services ->
            services?.let {
                Log.i(TAG, "Got services ${it.size}")
                updateUI(it)
            }
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(services: List<Service>) {
        adapter = RVSelListAdapter(
            this,
            services,
            serviceListViewModel,
            R.layout.list_item_service,
            R.menu.list_items,
            serviceListEmptyText
        )
        adapter?.submitList(services)
        serviceRecyclerView.adapter = adapter
    }

    private fun newService() {
   /*     serviceListViewModel.nextDisplayPos().observe(
            viewLifecycleOwner
        ) { nextPos ->
            nextPos?.let {
                val service = Service(it)
                serviceListViewModel.addService(service)
                callbacks?.onServiceEditClick(service.id)
            }
        }

    */
    }
}