package com.oborodulin.home.presentation.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oborodulin.home.common.ui.ComponentUiAction
import com.oborodulin.home.common.ui.components.dialog.AlertDialogComponent
import com.oborodulin.home.common.ui.components.items.ListItemComponent
import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.common.ui.theme.Typography
import com.oborodulin.home.common.util.OnListItemEvent
import com.oborodulin.home.presentation.R
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "Presentation.Components"

@Composable
fun ServiceSubtotalListItemComponent(
    @DrawableRes icon: Int?,
    item: ListItemModel,
    selected: Boolean = false,
    itemActions: List<ComponentUiAction> = emptyList(),
    background: Color = Color.Transparent,
    onClick: OnListItemEvent
) {
    ListItemComponent(
        icon = icon,
        item = item,
        selected = selected,
        itemActions = itemActions,
        background = background,
        onClick = onClick,
    ) {
        Row(
            Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
            ) {
                Row {
                    Text(
                        text = item.title,
                        style = Typography.body1.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2
                    )
                }
                item.descr?.let {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = it,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            item.value?.let {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //val nf = NumberFormat.getCurrencyInstance(Locale.getDefault())
                    val nf = NumberFormat.getNumberInstance(Locale.getDefault())
                    nf.roundingMode = RoundingMode.HALF_UP
                    nf.maximumFractionDigits = 0
                    Text(
                        text = nf.format(it),
                        style = Typography.body1.copy(
                            fontWeight = FontWeight.Bold, fontSize = 20.sp
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Divider(thickness = 1.dp)
                    Spacer(Modifier.height(4.dp))
                    item.fromDate?.let {
                        Row {
                            Text(
                                text = it.format(
                                    DateTimeFormatter.ofLocalizedDate(
                                        FormatStyle.SHORT
                                    ).withLocale(Locale.getDefault()),
                                ),
                                style = Typography.body1.copy(fontSize = 12.sp)
                            )
                            item.toDate?.let {
                                Text(
                                    text = " - " + it.format(
                                        DateTimeFormatter.ofLocalizedDate(
                                            FormatStyle.SHORT
                                        ).withLocale(Locale.getDefault())
                                    ),
                                    style = Typography.body1.copy(fontSize = 12.sp)
                                )
                            }
                        }
                        val showDialogState = remember { mutableStateOf(false) }
                        for (action in itemActions.filterIsInstance<ComponentUiAction.PayListItem>()) {
                            AlertDialogComponent(
                                isShow = showDialogState.value,
                                title = { Text(stringResource(com.oborodulin.home.common.R.string.dlg_confirm_title)) },
                                text = { Text(text = action.dialogText) },
                                onDismiss = { showDialogState.value = false },
                                onConfirm = {
                                    showDialogState.value = false
                                    action.event(item)
                                }
                            )
                            Button(
                                onClick = { showDialogState.value = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(0.dp),
                                //.noInteractionClickable(enabled = false) { showDialogState.value = true },
                                shape = RoundedCornerShape(28.dp),
                                contentPadding = PaddingValues(2.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.background,
                                    contentColor = MaterialTheme.colors.primary
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colors.primary)
                            ) {
                                /*Icon(
                                    imageVector = imageVector,
                                    modifier = Modifier.size(iconWidth),
                                    contentDescription = "drawable icons",
                                    tint = Color.Unspecified
                                )*/
                                Image(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(2.dp)),
                                    painter = painterResource(com.oborodulin.home.common.R.drawable.btn_wallet_24),
                                    contentDescription = ""
                                )
                                Text(
                                    text = stringResource(R.string.btn_pay_text),
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    style = Typography.body1.copy(fontSize = 12.sp),
                                    modifier = Modifier
                                        .weight(1f)
                                        //.offset(x = (-12).dp / 2) //default icon width = 24.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewServiceSubtotalListItemComponent() {
    val context = LocalContext.current
    ServiceSubtotalListItemComponent(
        icon = com.oborodulin.home.common.R.drawable.outline_photo_24,
        item = ListItemModel.defaultListItemModel(context),
        itemActions = listOf(
            //ComponentUiAction.EditListItem { println() },
            //ComponentUiAction.DeleteListItem { println() },
            ComponentUiAction.PayListItem { println() }),
        onClick = { println() }
    )
}
