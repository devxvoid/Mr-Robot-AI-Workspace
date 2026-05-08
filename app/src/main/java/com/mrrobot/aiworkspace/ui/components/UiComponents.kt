package com.mrrobot.aiworkspace.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val NeonGreen = Color(0xFF00FF88)
val NeonCyan = Color(0xFF00D4FF)
val NeonPurple = Color(0xFF8B5CF6)
val Danger = Color(0xFFFF3366)
val Amber = Color(0xFFFFB800)

@Composable
fun ScreenShell(content: @Composable ColumnScope.() -> Unit) {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        scheme.background,
                        scheme.surface,
                        scheme.surfaceVariant.copy(alpha = 0.92f)
                    )
                )
            )
    ) {
        AmbientGlow()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            content = content
        )
    }
}

@Composable
fun AmbientGlow() {
    val infinite = rememberInfiniteTransition(label = "ambient")
    val alpha by infinite.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.24f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = (-110).dp, y = 40.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(NeonGreen.copy(alpha = .32f), Color.Transparent)
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 130.dp, y = 80.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(NeonCyan.copy(alpha = .24f), Color.Transparent)
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

    Card(
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
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = scheme.surface.copy(alpha = .92f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun Title(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 24.sp,
        lineHeight = 29.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PageTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 30.sp,
        lineHeight = 35.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
fun Subtitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
}

@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OutlineAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun StatusPill(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        color = color.copy(alpha = .12f),
        border = BorderStroke(1.dp, color.copy(alpha = .45f)),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun PremiumMetric(
    label: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            maxLines = 2
        )
    }
}
