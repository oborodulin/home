package com.oborodulin.home.common.ui.components.items

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oborodulin.home.common.R
import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.common.ui.theme.Typography
import timber.log.Timber

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "Common.UI"
private val EMPTY: (ListItemModel) -> Unit = {}

@Composable
fun ListItemComponent(
    icon: Int?,
    item: ListItemModel,
    selected: Boolean = false,
    background: Color = Color.Transparent,
    onClick: (ListItemModel) -> Unit = EMPTY,
    onEdit: (ListItemModel) -> Unit = EMPTY,
    onDelete: (ListItemModel) -> Unit = EMPTY
) {
    Timber.tag(TAG)
        .d("ListItemComponent(...) called: {\"listItem\": {\"icon\": $icon, \"title\": \"${item.title}\", \"desc\": \"${item.descr}\"}}")
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .height(88.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .selectable(selected = selected, onClick = { if (onClick !== EMPTY) onClick(item) }),
        //.background(color = MaterialTheme.colors.background)
        //.clickable {}
        elevation = 10.dp
    ) {
        Row(
            Modifier
                .padding(all = 4.dp)
                .fillMaxSize()
        ) {
            icon?.let {
                Image(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    painter = painterResource(icon),
                    contentScale = ContentScale.Crop,
                    contentDescription = ""
                )
            }
            Column(
                verticalArrangement = Top,
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .fillMaxHeight()
            ) {
                Text(
                    text = item.title,
                    style = Typography.body1.copy(fontWeight = FontWeight.Bold)
                )
                item.descr?.let {
                    Text(
                        text = item.descr,
                        modifier = Modifier.padding(vertical = 8.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Column(
                verticalArrangement = Top,
                modifier = Modifier
                    .padding(all = 4.dp)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (onEdit !== EMPTY) {
                        Image(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onEdit(item) },
                            painter = painterResource(R.drawable.outline_mode_edit_black_24),
                            contentDescription = ""
                        )
                    }
                    if (onDelete !== EMPTY) {
                        Image(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onDelete(item) },
                            painter = painterResource(R.drawable.outline_delete_black_24),
                            contentDescription = ""
                        )
                    }
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
    ListItemComponent(
        icon = R.drawable.outline_photo_24,
        item = ListItemModel(
            title = context.resources.getString(R.string.preview_blank_title),
            descr = context.resources.getString(R.string.preview_blank_descr),
        ),
        onClick = { println() }, onEdit = { println() }, onDelete = { println() }
    )
}
