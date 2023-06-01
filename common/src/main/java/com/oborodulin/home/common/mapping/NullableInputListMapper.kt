package com.oborodulin.home.common.mapping

interface NullableInputListMapper<in I, out O> : Mapper<List<I>?, List<O>>