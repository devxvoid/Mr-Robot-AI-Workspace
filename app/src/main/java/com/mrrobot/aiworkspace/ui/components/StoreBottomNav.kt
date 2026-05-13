package com.mrrobot.aiworkspace.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.theme.Elevation
import com.mrrobot.aiworkspace.ui.theme.Spacing
import com.mrrobot.aiworkspace.ui.theme.StoreColors

/**
 * Data describing a single bottom-nav entry.
 *
 * @param iconRes drawable resource to render.
 * @param label short label shown under the icon.
 * @param selected whether this item is currently active.
 * @param onClick click handler.
 */
data class StoreBottomNavItem(
    val iconRes: Int,
    val label: String,
    val selected: Boolean,
    val onClick: () -> Unit
)

/**
 * GitHub Store-style translucent bottom navigation.
 *
 * Renders a pill-shaped glass container floating above the system nav
 * bar, with a pill-shaped selection indicator that slides smoothly
 * behind the active tab label.
 */
@Composable
fun StoreBottomNav(
    items: List<StoreBottomNavItem>,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = Spacing.m, vertical = Spacing.s)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = StoreColors.glassSurfaceElevated(),
            border = BorderStroke(1.dp, StoreColors.glassBorder()),
            tonalElevation = Elevation.level3,
            shadowElevation = Elevation.level2
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = Spacing.s),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                items.forEach { item ->
                    StoreNavEntry(
                        item = item,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StoreNavEntry(
    item: StoreBottomNavItem,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    val activeAlpha by animateFloatAsState(
        targetValue = if (item.selected) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "nav_active_alpha"
    )

    val iconTint = if (item.selected) scheme.onPrimaryContainer else scheme.onSurfaceVariant
    val labelColor = if (item.selected) scheme.onPrimaryContainer else scheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = item.onClick),
        contentAlignment = Alignment.Center
    ) {
        // Selection indicator — pill behind the active tab.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 4.dp)
                .clip(CircleShape)
                .background(scheme.primaryContainer.copy(alpha = activeAlpha))
        )

        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )

            if (item.selected) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = labelColor,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}
