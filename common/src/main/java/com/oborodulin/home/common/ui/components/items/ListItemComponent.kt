package com.oborodulin.home.common.ui.components.items

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oborodulin.home.common.ui.model.ListItemModel
import com.oborodulin.home.common.ui.theme.Typography
import timber.log.Timber

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "Common.UI"

@Composable
fun ListItemComponent(icon: Int?, item: ListItemModel, onClick: (ListItemModel) -> Unit) {
    Timber.tag(TAG)
        .d("ListItemComponent(...) called: {\"listItem\": {\"icon\": $icon, \"title\": \"${item.title}\", \"desc\": \"${item.descr}\"}}")
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colors.background)
    ) {
        Row(
            Modifier
                .padding(all = 4.dp)
                .fillMaxSize()
                .clickable {
                    onClick(item)
                }
        ) {
/*            Image(
                modifier = Modifier
                    .padding(4.dp)
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                painter = rememberImagePainter(
                    data = posterPath
                ),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
*/
            Column(
                verticalArrangement = Top,
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 12.dp)
                    .fillMaxHeight()
            ) {
                Text(
                    text = item.title,
                    style = Typography.body1.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = item.descr ?: "",
                    modifier = Modifier
                        .padding(vertical = 8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = Typography.body2.copy(
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewListItemComponent() {
    ListItemComponent(
        icon = null,
        item = ListItemModel(title = "Title", descr = "Description"),
        onClick = {})
}
