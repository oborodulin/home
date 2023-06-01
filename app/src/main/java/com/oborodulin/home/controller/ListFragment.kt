package com.oborodulin.home.controller

import android.view.View
import com.oborodulin.home.common.data.entities.BaseEntity
import com.oborodulin.home.model.RVSelHolder

interface ListFragment<T : BaseEntity> {
    fun getViewHolder(view: View): RVSelHolder<T>
}