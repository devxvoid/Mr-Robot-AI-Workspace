package com.mrrobot.aiworkspace.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compact one-row banner shown at the top of the AI chat screen that surfaces the
 * full "brain" state — active agent, memory count, soul status, and heartbeat —
 * so the user sees everything that's influencing the AI's responses.
 *
 * Tapping any chip jumps to the corresponding management screen.
 */
@Composable
fun BrainStatusBanner(
    activeAgentEmoji: String?,
    activeAgentName: String?,
    memoryCount: Int,
    hasCustomSoul: Boolean,
    heartbeatEnabled: Boolean,
    heartbeatRunning: Boolean,
    onAgentClick: () -> Unit,
    onMemoriesClick: () -> Unit,
    onSoulClick: () -> Unit,
    onHeartbeatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    val anyActive = activeAgentName != null ||
        memoryCount > 0 ||
        hasCustomSoul ||
        heartbeatEnabled

    AnimatedVisibility(
        visible = anyActive,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(14.dp),
            color = scheme.primary.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, scheme.primary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Agent chip
                if (activeAgentName != null) {
                    BrainChip(
                        emoji = activeAgentEmoji ?: "\uD83E\uDD16",
                        label = activeAgentName,
                        tint = scheme.primary,
                        onClick = onAgentClick
                    )
                }

                // Memory chip
                if (memoryCount > 0) {
                    BrainChip(
                        emoji = "\uD83E\uDDE0",
                        label = "$memoryCount mem",
                        tint = scheme.tertiary,
                        onClick = onMemoriesClick
                    )
                }

                // Soul chip
                if (hasCustomSoul) {
                    BrainChip(
                        emoji = "\uD83D\uDC7B",
                        label = "soul",
                        tint = scheme.secondary,
                        onClick = onSoulClick
                    )
                }

                // Heartbeat chip
                if (heartbeatEnabled || heartbeatRunning) {
                    BrainChip(
                        emoji = if (heartbeatRunning) "\u26A1" else "\uD83D\uDC93",
                        label = if (heartbeatRunning) "running" else "heartbeat",
                        tint = scheme.error,
                        onClick = onHeartbeatClick
                    )
                }
            }
        }
    }
}

@Composable
private fun BrainChip(
    emoji: String,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(999.dp),
        color = scheme.surface,
        border = BorderStroke(1.dp, tint.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 13.sp
            )
            Text(
                text = label,
                color = tint,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
