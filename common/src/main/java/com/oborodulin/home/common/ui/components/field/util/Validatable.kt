package com.oborodulin.home.common.ui.components.field.util

interface Validatable {
    fun errorIdOrNull(vararg inputs: String): Int?
}