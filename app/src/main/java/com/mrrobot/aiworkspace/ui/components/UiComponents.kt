package com.mrrobot.aiworkspace.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val NeonCyan = Color(0xFF00D4FF)
val NeonPurple = Color(0xFF8B5CF6)
val NeonGreen = Color(0xFF00FF88)
val SoftTextColor = Color(0xFFCBD5E1)

@Composable
fun ScreenShell(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF020617),
                        Color(0xFF071226),
                        Color(0xFF020617)
                    )
                )
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xAA0B1020),
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                BorderStroke(
                    1.dp,
                    NeonCyan.copy(alpha = .25f)
                ),
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

@Composable
fun Title(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    )
}

@Composable
fun PageTitle(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp,
        lineHeight = 38.sp
    )
}

@Composable
fun Subtitle(text: String) {
    Text(
        text = text,
        color = Color(0xFF94A3B8),
        fontSize = 15.sp,
        lineHeight = 22.sp
    )
}

@Composable
fun SoftText(
    text: String
) {
    Text(
        text = text,
        color = SoftTextColor,
        fontSize = 14.sp,
        lineHeight = 21.sp
    )
}

@Composable
fun StatusPill(
    text: String,
    color: Color = NeonCyan
) {
    Surface(
        color = color.copy(alpha = .15f),
        border = BorderStroke(
            1.dp,
            color.copy(alpha = .45f)
        ),
        shape = RoundedCornerShape(100)
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            )
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonCyan,
            contentColor = Color.Black
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = .18f)
        )
    ) {
        Text(
            text = text,
            color = NeonCyan,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Panel(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    GlassCard(modifier = modifier) {

        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        subtitle?.let {
            Subtitle(it)
        }

        content()
    }
}
