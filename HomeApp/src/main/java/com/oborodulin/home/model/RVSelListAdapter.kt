package com.oborodulin.home.model

import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import com.oborodulin.home.R
import com.oborodulin.home.controller.ListFragment
import com.oborodulin.home.domain.entity.BaseEntity

private const val TAG = "RVSelListAdapter"

class RVSelListAdapter<T : BaseEntity>(
    private val owner: Fragment,
    val curList: List<T>,
    val listViewModel: ListViewModel<T>?,
    @LayoutRes val layoutId: Int,
    @MenuRes val menuId: Int,
    private val tvListEmptyText: TextView?
) :
    ListAdapter<T, RVSelHolder<T>>(DiffItemCallBack()) {
    val selectList: MutableList<T> = mutableListOf()
    var isEnable = false
    var isSelectAll = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RVSelHolder<T> {
        val view = owner.layoutInflater.inflate(layoutId, parent, false)
        Log.d(TAG, "onCreateViewHolder: Инстанцирование RecyclerViewHolder")
        return (owner as ListFragment<T>).getViewHolder(view)
    }

    override fun getItemCount() = curList.size

    private inner class RecyclerViewActionMode(private val holder: RVSelHolder<T>) :
        ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(menuId, menu)
            Log.d(TAG, "onCreateActionMode")
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            Log.d(TAG, "onPrepareActionMode")
            // when action mode is prepare set isEnable true
            isEnable = true
            clickItem(holder)
            // set observer on getSelectedItemsText method when text change set text on action mode title
            listViewModel?.getSelectedItemsCount()?.observe(
                owner.viewLifecycleOwner
            ) { s ->
                mode.title = String.format("%s Selected", s)
            }
            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            // when click on action mode item get item id
            when (item.itemId) {
                R.id.menu_delete_item -> {
                    Log.d(TAG, "onActionItemClicked menu_delete_item")
                    // remove selected item list
                    selectList.forEach {
                        Log.d(TAG, "remove $it")
                        curList.toMutableList().remove(it)
                        listViewModel?.deleteItem(it)
                    }
                    // notify adapter
                    submitList(curList)
                    // when array list is empty visible text view
                    if (curList.isEmpty()) tvListEmptyText?.visibility = View.VISIBLE
                    // finish action mode
                    mode.finish()
                }
                R.id.menu_select_all_items -> {
                    Log.d(TAG, "onActionItemClicked menu_select_all_items")
                    // when click on select all check condition
                    if (selectList.size == curList.size) {
                        // when all item selected set isSelectAll false
                        isSelectAll = false
                        // create select array list
                        selectList.clear()
                    } else {
                        // when  all item unselected set isSelectAll true
                        isSelectAll = true
                        // clear select array list
                        selectList.clear()
                        // add value in select array list
                        selectList.addAll(curList)
                    }
                    // set text on view model
                    listViewModel?.setSelectedItemsCount(selectList.size)
                    // notify adapter notifyDataSetChanged()
                }
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            Log.d(TAG, "onDestroyActionMode")
            // when action mode is destroy set isEnable false
            isEnable = false
            // set isSelectAll false
            isSelectAll = false
            // clear select array list
            selectList.clear()
            // notify adapter notifyDataSetChanged()
            submitList(curList)
        }
    }

    override fun onBindViewHolder(holder: RVSelHolder<T>, position: Int) {
        Log.d(TAG, "onBindViewHolder")
        val listItem = curList[position]
        holder.bind(listItem)
        holder.itemView.setOnLongClickListener {
            Log.d(TAG, "setOnLongClickListener ${holder.absoluteAdapterPosition}")
            // when action mode is not enable initialize action mode
            // start action mode
            if (!isEnable) owner.activity?.startActionMode(RecyclerViewActionMode(holder))
            else
            // when action mode is already enable call method
                clickItem(holder)
            true
        }
        holder.itemView.setOnClickListener {
            // when action mode is enable call method
            if (isEnable) clickItem(holder)
            else
            // when action mode is not enable display toast
                Toast.makeText(
                    owner.activity, "You Clicked" + curList[holder.absoluteAdapterPosition],
                    Toast.LENGTH_SHORT
                ).show()
        }
        if (isSelectAll) {
            // when value selected visible all check boc image
            holder.checkImageView.visibility = View.VISIBLE
            //set background color
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        } else {
            // when all value unselected hide all check box image
            holder.checkImageView.visibility = View.GONE
            // set background color
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun clickItem(holder: RVSelHolder<T>) {
        Log.d(TAG, "clickItem")
        // get selected item value
        val listItem = curList[holder.absoluteAdapterPosition]
        Log.d(TAG, "clickItem ${holder.absoluteAdapterPosition}")
        // check condition
        if (holder.checkImageView.visibility == View.GONE) {
            // when item not selected visible check box image
            holder.checkImageView.visibility = View.VISIBLE
            // set background color
            holder.itemView.setBackgroundColor(Color.LTGRAY)
            // add value in select array list
            selectList.add(listItem)
        } else {
            // when item selected hide check box image
            holder.checkImageView.visibility = View.GONE
            // set background color
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            // remove value from select list
            selectList.remove(listItem)
        }
        // set text on view model
        listViewModel?.setSelectedItemsCount(selectList.size)
    }
}