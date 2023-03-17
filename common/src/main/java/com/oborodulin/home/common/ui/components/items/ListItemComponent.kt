package com.oborodulin.home.common.ui.components.items

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oborodulin.home.common.R
import com.oborodulin.home.common.ui.ComponentUiAction
import com.oborodulin.home.common.ui.components.dialog.AlertDialogComponent
import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.common.ui.theme.Typography
import com.oborodulin.home.common.util.OnListItemEvent
import com.oborodulin.home.common.util.Utils
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "Common.UI"
private val EMPTY: OnListItemEvent = {}

@Composable
fun ListItemComponent(
    @DrawableRes icon: Int?,
    item: ListItemModel,
    selected: Boolean = false,
    itemActions: List<ComponentUiAction> = emptyList(),
    background: Color = Color.Transparent,
    onFavorite: OnListItemEvent = EMPTY,
    onClick: OnListItemEvent = EMPTY
) {
    Timber.tag(TAG)
        .d(
            "ListItemComponent(...) called: {\"listItem\": {\"icon\": %s, \"itemId\": \"%s\", \"title\": \"%s\", \"desc\": \"%s\", \"value\": \"%s\", \"isFavorite\": \"%s\"}}",
            icon,
            item.itemId,
            item.title,
            item.descr,
            item.value,
            item.isFavoriteMark
        )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .selectable(selected = selected, onClick = { if (onClick !== EMPTY) onClick(item) })
            .padding(horizontal = 4.dp, vertical = 4.dp),
        //.background(color = MaterialTheme.colors.background)
        //.clickable {}
        elevation = 10.dp
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(all = 4.dp)
        ) {
            Column(
                Modifier
                    .weight(0.8f)
                    .width(80.dp)
            ) {
                icon?.let {
                    Image(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        painter = painterResource(icon),
                        contentScale = ContentScale.Crop,
                        contentDescription = ""
                    )
                }
            }
            Column(
                verticalArrangement = Top,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2.9f)
                //.padding(horizontal = 8.dp)
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
                            if (onFavorite !== EMPTY) {
                                //val isFavorite = remember { mutableStateOf(item.isFavoriteMark) }
                                Image(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .padding(4.dp)
                                        .clickable {
//                                    if (!isFavorite.value) {
                                            if (!item.isFavoriteMark) {
                                                onFavorite(item)
                                                //isFavorite.value = true
                                            }
                                        },
                                    painter = when (item.isFavoriteMark) {//isFavorite.value
                                        true -> painterResource(R.drawable.outline_favorite_black_20)
                                        false -> painterResource(R.drawable.outline_favorite_border_black_20)
                                    },
                                    contentDescription = ""
                                )
                            }
                            Text(
                                text = item.title,
                                style = Typography.body1.copy(fontWeight = FontWeight.Bold),
                                maxLines = 2
                            )
                        }
                        item.descr?.let {
                            Text(
                                modifier = Modifier.padding(vertical = 4.dp),
                                text = item.descr,
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
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.3f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val showDialogState = remember { mutableStateOf(false) }
                val spaceVal = 18
                var itemIndex = 0
                for (action in itemActions) {
                    when (action) {
                        is ComponentUiAction.EditListItem -> {
                            Image(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { action.event(item) },
                                painter = painterResource(R.drawable.outline_mode_edit_black_24),
                                contentDescription = ""
                            )
                        }
                        is ComponentUiAction.DeleteListItem -> {
                            AlertDialogComponent(
                                isShow = showDialogState.value,
                                title = { Text(stringResource(R.string.dlg_confirm_title)) },
                                text = { Text(text = action.dialogText) },
                                onDismiss = { showDialogState.value = false },
                                onConfirm = {
                                    showDialogState.value = false
                                    action.event(item)
                                }
                            )
                            Image(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showDialogState.value = true },
                                painter = painterResource(R.drawable.outline_delete_black_24),
                                contentDescription = ""
                            )
                        }
                        is ComponentUiAction.PayListItem -> {
                            AlertDialogComponent(
                                isShow = showDialogState.value,
                                title = { Text(stringResource(R.string.dlg_confirm_title)) },
                                text = { Text(text = action.dialogText) },
                                onDismiss = { showDialogState.value = false },
                                onConfirm = {
                                    showDialogState.value = false
                                    action.event(item)
                                }
                            )
                            Image(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showDialogState.value = true },
                                painter = painterResource(R.drawable.btn_wallet_24),
                                contentDescription = ""
                            )
                        }
                    }
                    itemIndex++
                    if (itemIndex < itemActions.size) Spacer(Modifier.height(spaceVal.dp))
                }
            }
        }
    }
}


@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewListItemComponent() {
    val context = LocalContext.current
    val listItem = ListItemModel(
        itemId = UUID.randomUUID(),
        title = context.resources.getString(R.string.preview_blank_title),
        descr = context.resources.getString(R.string.preview_blank_descr),
        value = BigDecimal("123456.54"),
        fromDate = Utils.toOffsetDateTime("2022-08-01T14:29:10.212+03:00"),
        toDate = Utils.toOffsetDateTime("2022-09-01T14:29:10.212+03:00")
    )
    ListItemComponent(
        icon = R.drawable.outline_photo_24,
        item = listItem,
        itemActions = listOf(
            ComponentUiAction.EditListItem { println() },
            ComponentUiAction.DeleteListItem { println() }),
        onFavorite = { println() },
        onClick = { println() },
    )
}
