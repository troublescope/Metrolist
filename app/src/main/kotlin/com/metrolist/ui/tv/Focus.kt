// Focus.kt
package com.metrolist.ui.tv

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme

/**
 * A reusable modifier for creating focusable items in a TV UI.
 *
 * This modifier applies focusability, click handling, and visual feedback for focus state.
 * It includes a Spotify-style outline and a subtle zoom effect.
 *
 * @param shape The shape of the item, used for clipping and the border.
 * @param onClick The lambda to be executed when the item is clicked.
 * @param onLongClick The lambda to be executed when the item is long-clicked.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.focusableItem(
    shape: Shape,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        label = "scale"
    )

    val borderColor = if (isFocused) {
        MaterialTheme.colorScheme.onSurface
    } else {
        Color.Transparent
    }

    this
        .scale(scale)
        .border(
            width = 2.dp,
            color = borderColor,
            shape = shape
        )
        .focusable(interactionSource = interactionSource)
        .clip(shape)
        .combinedClickable(
            interactionSource = interactionSource,
            indication = null, // Disable default ripple, visual feedback is handled by border and scale
            onClick = onClick,
            onLongClick = {
                if (onLongClick != null) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                }
            }
        )
}


/**
 * A modifier to request initial focus for a composable.
 *
 * @param focusRequester The [FocusRequester] to be used for requesting focus.
 */
fun Modifier.initialFocus(focusRequester: FocusRequester): Modifier {
    return this.focusRequester(focusRequester)
}

@Composable
fun FocusableIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    onLongClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .focusableItem(
                shape = shape,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .alpha(if (enabled) 1f else 0.5f),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
