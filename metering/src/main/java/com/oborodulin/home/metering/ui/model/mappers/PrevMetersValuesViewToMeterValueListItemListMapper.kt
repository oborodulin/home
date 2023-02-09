package com.oborodulin.home.metering.ui.model.mappers

import com.oborodulin.home.common.mapping.ListMapperImp
import com.oborodulin.home.data.local.db.views.MeterValuePrevPeriodsView
import com.oborodulin.home.metering.ui.model.MeterValueListItem

class PrevMetersValuesViewToMeterValueListItemListMapper(mapper: PrevMetersValuesViewToMeterValueModelMapper) :
    ListMapperImp<MeterValuePrevPeriodsView, MeterValueListItem>(mapper)