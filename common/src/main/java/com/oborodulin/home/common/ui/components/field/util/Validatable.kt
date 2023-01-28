package com.oborodulin.home.common.ui.components.field.util

interface Validatable {
    fun errorIdOrNull(input: String): Int?
}