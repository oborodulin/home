package com.oborodulin.home.servicing.ui.service.list

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.state.UiState
import com.oborodulin.home.data.R
import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.data.util.ServiceType
import com.oborodulin.home.presentation.navigation.NavRoutes
import com.oborodulin.home.presentation.navigation.ServiceInput
import com.oborodulin.home.servicing.domain.usecases.DeleteServiceUseCase
import com.oborodulin.home.servicing.domain.usecases.GetServicesUseCase
import com.oborodulin.home.servicing.domain.usecases.ServiceUseCases
import com.oborodulin.home.servicing.ui.model.ServiceListItem
import com.oborodulin.home.servicing.ui.model.converters.ServicesListConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

private const val TAG = "Servicing.ui.ServicesListViewModel"

@HiltViewModel
class ServicesListViewModelImpl @Inject constructor(
    private val state: SavedStateHandle,
    private val payerUseCases: ServiceUseCases,
    private val payersListConverter: ServicesListConverter
) : ServicesListViewModel,
    MviViewModel<List<ServiceListItem>, UiState<List<ServiceListItem>>, ServicesListUiAction, ServicesListUiSingleEvent>(
        state = state
    ) {

    override fun initState() = UiState.Loading

    override suspend fun handleAction(action: ServicesListUiAction): Job {
        Timber.tag(TAG)
            .d("handleAction(ServicesListUiAction) called: %s", action.javaClass.name)
        val job = when (action) {
            is ServicesListUiAction.Load -> {
                loadServices()
            }

            is ServicesListUiAction.EditService -> {
                submitSingleEvent(
                    ServicesListUiSingleEvent.OpenServiceScreen(
                        NavRoutes.Service.routeForService(
                            ServiceInput(action.serviceId)
                        )
                    )
                )
            }

            is ServicesListUiAction.DeleteService -> {
                deleteService(action.serviceId)
            }
            /*is PostListUiAction.UserClick -> {
                updateInteraction(action.interaction)
                submitSingleEvent(
                    PostListUiSingleEvent.OpenUserScreen(
                        NavRoutes.User.routeForUser(
                            UserInput(action.userId)
                        )
                    )
                )
            }*/
        }
        return job
    }

    private fun loadServices(): Job {
        Timber.tag(TAG).d("loadServices() called")
        val job = viewModelScope.launch(errorHandler) {
            payerUseCases.getServicesUseCase.execute(GetServicesUseCase.Request).map {
                payersListConverter.convert(it)
            }.collect {
                submitState(it)
            }
        }
        return job
    }

    private fun deleteService(serviceId: UUID): Job {
        Timber.tag(TAG)
            .d("deleteService() called: serviceId = %s", serviceId.toString())
        val job = viewModelScope.launch(errorHandler) {
            payerUseCases.deleteServiceUseCase.execute(
                DeleteServiceUseCase.Request(serviceId)
            ).collect {}
        }
        return job
    }

    override fun initFieldStatesByUiModel(uiModel: Any): Job? = null

    companion object {
        fun previewModel(ctx: Context) =
            object : ServicesListViewModel {
                override var primaryObjectData: StateFlow<ArrayList<String>> =
                    MutableStateFlow(arrayListOf())
                override val uiStateFlow = MutableStateFlow(UiState.Success(previewList(ctx)))
                override val singleEventFlow = Channel<ServicesListUiSingleEvent>().receiveAsFlow()
                override val actionsJobFlow: SharedFlow<Job?> = MutableSharedFlow()

                //fun viewModelScope(): CoroutineScope = CoroutineScope(Dispatchers.Main)
                override fun handleActionJob(action: () -> Unit, afterAction: () -> Unit) {}
                override fun submitAction(action: ServicesListUiAction): Job? = null
                override fun setPrimaryObjectData(value: ArrayList<String>) {}
            }

        fun previewList(ctx: Context) = listOf(
            ServiceListItem(
                id = UUID.randomUUID(),
                servicePos = 1, serviceType = ServiceType.RENT,
                serviceName = ctx.resources.getString(R.string.service_rent)
            ),
            ServiceListItem(
                id = UUID.randomUUID(),
                servicePos = 2,
                serviceType = ServiceType.ELECTRICITY,
                serviceMeterType = MeterType.ELECTRICITY,
                serviceName = ctx.resources.getString(R.string.service_electricity),
                serviceMeasureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.kWh_unit),
            ),
            ServiceListItem(
                id = UUID.randomUUID(),
                servicePos = 3,
                serviceType = ServiceType.GAS,
                serviceMeterType = MeterType.GAS,
                serviceName = ctx.resources.getString(R.string.service_gas),
                serviceMeasureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
            ),
            ServiceListItem(
                id = UUID.randomUUID(),
                servicePos = 4,
                serviceType = ServiceType.COLD_WATER,
                serviceMeterType = MeterType.COLD_WATER,
                serviceName = ctx.resources.getString(R.string.service_cold_water),
                serviceMeasureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
            ),
            ServiceListItem(
                id = UUID.randomUUID(),
                servicePos = 5,
                serviceType = ServiceType.WASTE,
                serviceMeterType = MeterType.HOT_WATER,
                serviceName = ctx.resources.getString(R.string.service_waste),
                serviceMeasureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.m3_unit),
            ),
            ServiceListItem(
                id = UUID.randomUUID(),
                servicePos = 6,
                serviceType = ServiceType.HEATING,
                serviceMeterType = MeterType.HEATING,
                serviceName = ctx.resources.getString(R.string.service_heating),
                serviceMeasureUnit = ctx.resources.getString(com.oborodulin.home.common.R.string.Gcal_unit),
            )
        )
    }
}