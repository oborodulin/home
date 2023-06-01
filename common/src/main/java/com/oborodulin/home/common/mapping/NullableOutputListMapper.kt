package com.oborodulin.home.common.mapping

interface NullableOutputListMapper<in I, out O> : Mapper<List<I>, List<O>?>