package com.oborodulin.home.common.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FabComponent(
    imageVector: ImageVector = Icons.Default.Add,
    text: String = "",
    contentDescription: String = "",
    onClick: () -> Unit = {}
) {
    ExtendedFloatingActionButton(
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier
                    .padding(end = 8.dp)
            )
        },
        text = { Text(text) },
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.elevation(8.dp)
    )
}

@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Day Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PreviewFABComponent() {
    FabComponent(text = "Demo", onClick = {})
}
