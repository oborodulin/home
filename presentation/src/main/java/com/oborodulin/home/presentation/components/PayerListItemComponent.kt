package com.oborodulin.home.presentation.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oborodulin.home.common.R
import com.oborodulin.home.common.ui.ComponentUiAction
import com.oborodulin.home.common.ui.components.items.ListItemComponent
import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.common.ui.theme.Typography
import com.oborodulin.home.common.util.OnListItemEvent
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "Presentation.Components"
private val EMPTY: OnListItemEvent = {}

@Composable
fun PayerListItemComponent(
    @DrawableRes icon: Int?,
    item: ListItemModel,
    selected: Boolean = false,
    itemActions: List<ComponentUiAction> = emptyList(),
    background: Color = Color.Transparent,
    onFavorite: OnListItemEvent,
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
                        style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
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
                        style = Typography.bodyMedium.copy(
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
                                style = Typography.bodyMedium.copy(fontSize = 12.sp)
                            )
                            item.toDate?.let {
                                Text(
                                    text = " - " + it.format(
                                        DateTimeFormatter.ofLocalizedDate(
                                            FormatStyle.SHORT
                                        ).withLocale(Locale.getDefault())
                                    ),
                                    style = Typography.bodyMedium.copy(fontSize = 12.sp)
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
fun PreviewPayerListItemComponent() {
    val context = LocalContext.current
    PayerListItemComponent(
        icon = R.drawable.outline_photo_24,
        item = ListItemModel.defaultListItemModel(context),
        itemActions = listOf(
            ComponentUiAction.EditListItem { println() },
            ComponentUiAction.DeleteListItem { println() }),
        onFavorite = { println() },
        onClick = { println() }
    )
}
