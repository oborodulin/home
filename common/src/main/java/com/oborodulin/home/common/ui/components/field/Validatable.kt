package com.oborodulin.home.common.ui.components.field

interface Validatable {
    fun errorIdOrNull(input: String): Int?
}