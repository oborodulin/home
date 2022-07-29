package com.oborodulin.home.model

import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.oborodulin.home.domain.BaseEntity

open class RVSelHolder<T : BaseEntity>(view: View, @IdRes val checkBoxId: Int) :
    RecyclerView.ViewHolder(view),
    View.OnClickListener {
    val checkImageView: ImageView = itemView.findViewById(checkBoxId)

    open fun bind(entity: T) {}
    override fun onClick(v: View) = Unit
}