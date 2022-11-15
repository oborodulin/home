package com.oborodulin.home.controller.payer

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.oborodulin.home.R
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.controller.ListFragment
import com.oborodulin.home.data.local.db.entities.BaseEntity
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.model.RVSelHolder
import com.oborodulin.home.model.RVSelListAdapter
import java.util.*

private const val TAG = "PayerListFragment"

class PayerListFragment : Fragment(), ListFragment<PayerEntity> {
    /**
     * Интерфейс обратных вызовов
     */
    interface Callbacks {
        fun onPayerEditClick(payerId: UUID)
        fun onPayerListClick(payerId: UUID)
    }

    private var callbacks: Callbacks? = null
    private lateinit var rvPayer: RecyclerView
    private lateinit var tvPayerListEmpty: TextView
    private lateinit var fabPayerList: FloatingActionButton

    private var adapter: RVSelListAdapter<PayerEntity>? =
        RVSelListAdapter(
            this,
            emptyList(),
            null,
            R.layout.list_item_payer,
            R.menu.list_items,
            null
        )

    //private lateinit var newPayerButton: Button
/*    private val lvmPayer: PayerListViewModel by lazy {
        ViewModelProvider(this).get(PayerListViewModel::class.java)
    }
*/
    private inner class PayerHolder<T : BaseEntity>(view: View) :
        RVSelHolder<T>(view, R.id.iv_item_payer_select) {
        private lateinit var payer: T
        private val tvFullName: TextView = itemView.findViewById(R.id.tv_item_payer_full_name)
        private val tvAddress: TextView = itemView.findViewById(R.id.tv_item_payer_address)
        private val ivEdit: ImageView = itemView.findViewById(R.id.iv_payer_edit)

        init {
            itemView.setOnClickListener(this)
            ivEdit.setOnClickListener(this)
        }

        override fun bind(entity: T) {
            payer = entity
            tvFullName.text = (payer as Payer).fullName
            tvAddress.text = (payer as Payer).address
        }

        override fun onClick(v: View) {
            //Toast.makeText(context, "ImageView: ${payer.address} pressed!", Toast.LENGTH_SHORT).show()
            when (v.id) {
                R.id.iv_payer_edit -> callbacks?.onPayerEditClick(payer.id)
                else -> callbacks?.onPayerListClick(payer.id)
            }
        }
    }

    override fun getViewHolder(view: View): RVSelHolder<PayerEntity> {
        return PayerHolder(view)
    }

    companion object {
        fun newInstance(): PayerListFragment {
            return PayerListFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payer_list, container, false)
        rvPayer = view.findViewById(R.id.rv_payer)
        tvPayerListEmpty = view.findViewById(R.id.tv_payer_list_empty)
        fabPayerList = view.findViewById(R.id.fab_payer_list)
        fabPayerList.setOnClickListener { newPayer() }

        /*
        newPayerButton = view.findViewById(R.id.new_payer_button) as Button
        newPayerButton.setOnClickListener(View.OnClickListener {
            newPayer()
        })
         */
        rvPayer.layoutManager = LinearLayoutManager(context)
        updateUI(emptyList())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*lvmPayer.payersLiveData.observe(
            viewLifecycleOwner
        ) { payers ->
            payers?.let {
                Log.i(TAG, "Got payers ${it.size}")
                updateUI(it)
            }
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.payer_list_fragment, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add_payer -> {
                newPayer()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(payers: List<PayerEntity>) {
        adapter = RVSelListAdapter(
            this,
            payers,
            null,
//            lvmPayer,
            R.layout.list_item_payer,
            R.menu.list_items,
            tvPayerListEmpty
        )
        adapter?.submitList(payers)
        rvPayer.adapter = adapter
        /*
        newPayerButton.visibility = when (adapter?.itemCount?.compareTo(0) == 0) {
            true -> View.VISIBLE
            false -> View.GONE
        }
        payerRecyclerView.visibility = when (adapter?.itemCount?.compareTo(0) == 0) {
            true -> View.GONE
            false -> View.VISIBLE
        }
         */
    }

    private fun newPayer() {
        val payer = Payer()
        //lvmPayer.addPayer(payer)
        callbacks?.onPayerEditClick(payer.id)
    }
}