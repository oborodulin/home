package com.oborodulin.home.metering.ui.model.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.views.PrevMetersValuesView
import com.oborodulin.home.metering.ui.model.MeterValueListItemModel

class PrevMetersValuesViewToMeterValueListItemModelListMapper(mapper: PrevMetersValuesViewToMeterValueModelMapper) :
    ListMapperImp<PrevMetersValuesView, MeterValueListItemModel>(mapper)