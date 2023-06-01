package com.oborodulin.home.common.mapping

open class ListMapperImpl<in I, out O>(
    private val mapper: Mapper<I, O>
) : ListMapper<I, O> {
    override fun map(input: List<I>): List<O> =
        input.map(mapper::map)
}