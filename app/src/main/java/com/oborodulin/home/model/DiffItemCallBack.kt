package com.oborodulin.home.model

import androidx.recyclerview.widget.DiffUtil
import com.oborodulin.home.data.local.db.entities.BaseEntity

class DiffItemCallBack<T : BaseEntity> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem == newItem
}
