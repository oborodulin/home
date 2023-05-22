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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oborodulin.home.common.R
import com.oborodulin.home.common.ui.ComponentUiAction
import com.oborodulin.home.common.ui.components.dialog.AlertDialogComponent
import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.common.ui.theme.Typography
import com.oborodulin.home.common.util.OnListItemEvent
import timber.log.Timber

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
    onClick: OnListItemEvent = EMPTY,
    content: @Composable () -> Unit
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
        //.background(color = MaterialTheme.colorScheme.background)
        //.clickable {}
        elevation = CardDefaults.cardElevation(8.dp)
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
                content()
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
                for (action in itemActions.filter { it.isMenuButton }) {
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
    ListItemComponent(
        icon = R.drawable.outline_photo_24,
        item = ListItemModel.defaultListItemModel(LocalContext.current),
        itemActions = listOf(
            ComponentUiAction.EditListItem { println() },
            ComponentUiAction.DeleteListItem { println() }),
        onClick = { println() }
    ) {
        Text(
            text = stringResource(R.string.preview_list_item_text),
            style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}
