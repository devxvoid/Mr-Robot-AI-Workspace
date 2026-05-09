package com.mrrobot.aiworkspace.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val NeonGreen = Color(0xFF00FF88)
val NeonCyan = Color(0xFF00D4FF)
val NeonPurple = Color(0xFF8B5CF6)
val Danger = Color(0xFFFF3366)
val Amber = Color(0xFFFFB800)

@Composable
fun ScreenShell(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        scheme.background,
                        scheme.surface,
                        scheme.surfaceVariant.copy(alpha = 0.94f)
                    )
                )
            )
    ) {
        AmbientGlow()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun AmbientGlow() {
    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-100).dp, y = 40.dp)
                .alpha(0.16f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(NeonGreen.copy(alpha = 0.34f), Color.Transparent)
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 120.dp, y = 80.dp)
                .alpha(0.14f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(NeonCyan.copy(alpha = 0.30f), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        scheme.primary.copy(alpha = .42f),
                        scheme.secondary.copy(alpha = .28f),
                        NeonPurple.copy(alpha = .20f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            ),
        shape = RoundedCornerShape(22.dp),
        color = scheme.surface.copy(alpha = .92f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }
}

@Composable
fun PageTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
fun Title(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 23.sp,
        lineHeight = 29.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun Subtitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        lineHeight = 21.sp
    )
}

@Composable
fun SoftText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        lineHeight = 21.sp
    )
}

@Composable
fun StatusPill(
    text: String,
    color: Color? = null,
    modifier: Modifier = Modifier
) {
    val resolved = color ?: MaterialTheme.colorScheme.primary

    Surface(
        modifier = modifier,
        color = resolved.copy(alpha = .13f),
        border = BorderStroke(1.dp, resolved.copy(alpha = .42f)),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = resolved,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = .35f)
        )
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
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
        Title(title)
        subtitle?.let { Subtitle(it) }
        content()
    }
}
