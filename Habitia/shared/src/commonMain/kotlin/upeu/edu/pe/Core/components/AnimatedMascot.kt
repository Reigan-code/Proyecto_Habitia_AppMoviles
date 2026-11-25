package upeu.edu.pe.Core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import habitia.shared.generated.resources.Res
import habitia.shared.generated.resources.haabi_duo_hugging
import habitia.shared.generated.resources.haabi_hearts

/**
 * Mascota Haabi (assets). PNG en
 * shared/src/commonMain/composeResources/drawable.
 *
 * Expresiones disponibles: alegría, reposo, abrazo,
 * acompañamiento. No se simulan expresiones tristes
 */
@Composable
fun AnimatedMascot(
    visible: Boolean = true,
    modifier: Modifier = Modifier,
    mascotSize: Dp = 200.dp,
    showHearts: Boolean = true,
) {
    val transition = rememberInfiniteTransition(label = "haabi-infinite")

    val breathing by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haabi-breathing"
    )

    val bobbing by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haabi-bobbing"
    )

    val heartOneY by transition.animateFloat(
        initialValue = 0f, targetValue = -50f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        ),
        label = "heart-one-y"
    )
    val heartOneAlpha by transition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        ),
        label = "heart-one-alpha"
    )
    val heartTwoY by transition.animateFloat(
        initialValue = 0f, targetValue = -50f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1000, StartOffsetType.Delay)
        ),
        label = "heart-two-y"
    )
    val heartTwoAlpha by transition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1000, StartOffsetType.Delay)
        ),
        label = "heart-two-alpha"
    )
    val heartThreeY by transition.animateFloat(
        initialValue = 0f, targetValue = -50f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(2000, StartOffsetType.Delay)
        ),
        label = "heart-three-y"
    )
    val heartThreeAlpha by transition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(2000, StartOffsetType.Delay)
        ),
        label = "heart-three-alpha"
    )

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(450)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(550, easing = FastOutSlowInEasing)
        )
    ) {
        Box(
            modifier = Modifier.size(mascotSize),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.haabi_duo_hugging),
                contentDescription = "Haabi, tu compañero de hábitos",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = breathing
                        scaleY = breathing
                        translationY = bobbing
                    }
            )

            if (showHearts) {
                HeartFloat(y = heartOneY, alpha = heartOneAlpha, modifier = Modifier.align(Alignment.TopStart))
                HeartFloat(y = heartTwoY, alpha = heartTwoAlpha, modifier = Modifier.align(Alignment.TopEnd))
                HeartFloat(y = heartThreeY, alpha = heartThreeAlpha, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
    }
}

@Composable
private fun BoxScope.HeartFloat(
    y: Float,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(Res.drawable.haabi_hearts),
        contentDescription = null,
        modifier = modifier
            .size(38.dp)
            .graphicsLayer {
                translationY = y
                this.alpha = alpha
            }
    )
}
