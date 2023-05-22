package com.oborodulin.home.common.ui.components.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.oborodulin.home.common.ui.theme.Typography

/**
 * Created by tfakioglu on 12.December.2021
 */

@Composable
fun GridItem(posterPath: String, title: String, desc: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(color = MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ImageContainer(posterPath = posterPath)

            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                maxLines = 2
            )
            if (desc.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    text = desc,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = Typography.bodySmall.copy(color = Color.White)
                )
            }
            Spacer(Modifier.padding(8.dp))
        }
    }
}

@Composable
fun ImageContainer(posterPath: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = TopCenter
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(8.dp),
            painter = rememberAsyncImagePainter(
                model = posterPath
            ),
            contentScale = ContentScale.Fit,
            contentDescription = ""
        )
    }
}