package com.oborodulin.home.common.mapping

interface Mapper<in I, out O> {
    fun map(input: I): O
}