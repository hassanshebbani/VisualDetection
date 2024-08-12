package com.example.visualdetection.Core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@Composable
fun MyIconPainter(modifier: Modifier = Modifier, icon: Any?, iconColor: Color? = null, iconSize: Dp = 22.dp, contentDescription: String? = null,
                  contentScale: ContentScale = ContentScale.Fit) {
    Image(
        modifier = modifier.then(if (icon !is ImageVector) Modifier.size(iconSize) else Modifier),
        painter = rememberAsyncImagePainter(
            model = when (icon) {
                is String -> icon
                is Int -> icon
                is ImageVector -> Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = icon, contentDescription = contentDescription, tint = iconColor ?: Color.White,
                )
                else -> null
            }

        ),
        contentScale = contentScale,
        contentDescription = contentDescription,
        colorFilter = if (iconColor != null) ColorFilter.tint(iconColor) else null
    )
}