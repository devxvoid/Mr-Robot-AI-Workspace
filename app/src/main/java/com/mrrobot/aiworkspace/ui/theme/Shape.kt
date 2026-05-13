package com.mrrobot.aiworkspace.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material 3 shape scale.
 *
 * Components should consume these via MaterialTheme.shapes.* rather than
 * hardcoding RoundedCornerShape in screens:
 *
 *   extraSmall (4dp)  - tag/chip edges, TextField, SearchBar corners
 *   small      (8dp)  - FilledTonalButton, IconButton, small surfaces
 *   medium     (12dp) - Cards (ElevatedCard, OutlinedCard), dialogs
 *   large      (16dp) - Bottom sheets, large cards, top app bar bottoms
 *   extraLarge (28dp) - FAB, Hero elements, large modal sheets
 */
val AppShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * The Hacker theme keeps corners sharp to feel like a terminal UI,
 * while still respecting the M3 shape scale (just with tighter radii).
 */
val HackerShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(10.dp),
    extraLarge = RoundedCornerShape(16.dp)
)
