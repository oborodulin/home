package com.oborodulin.home.metering.ui.model.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.views.MeterValuePrevPeriodView
import com.oborodulin.home.metering.ui.model.MeterValueListItem

class PrevMetersValuesViewToMeterValueListItemListMapper(mapper: PrevMetersValuesViewToMeterValueListItemMapper) :
    ListMapperImp<MeterValuePrevPeriodView, MeterValueListItem>(mapper)