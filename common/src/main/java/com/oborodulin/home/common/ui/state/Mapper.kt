package com.oborodulin.home.common.ui.state

interface Mapper<in I, out O> {
    fun map(input: I): O
}