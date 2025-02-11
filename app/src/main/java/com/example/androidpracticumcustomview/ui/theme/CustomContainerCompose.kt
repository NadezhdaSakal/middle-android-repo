package com.example.androidpracticumcustomview.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.Layout
import kotlinx.coroutines.launch

private const val ALPHA_DURATION = 2000
private const val MOVEMENT_DURATION = 5000
private const val MAX_CHILD_COUNT = 2

@Composable
fun CustomContainerCompose(
    firstChild: @Composable (() -> Unit)? = null,
    secondChild: @Composable (() -> Unit)? = null
) {
    require(listOfNotNull(firstChild, secondChild).size <= MAX_CHILD_COUNT) {
        "Невозможно добавить более $MAX_CHILD_COUNT дочерних элементов в этот контейнер"
    }

    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    var screenHeight by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        launch {
            offsetY.animateTo(
                targetValue = screenHeight / 2,
                animationSpec = tween(MOVEMENT_DURATION)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(ALPHA_DURATION)
            )
        }
    }

    Box(modifier = Modifier.alpha(alpha.value)) {
        Layout(
            content = {
                firstChild?.let { Box(Modifier) { it() } }
                secondChild?.let { Box(Modifier) { it() } }
            }
        ) { measurableList, constraints ->
            screenHeight = constraints.maxHeight.toFloat() - 100

            val placeableList = measurableList.map { measurable ->
                measurable.measure(constraints)
            }

            layout(constraints.maxWidth, constraints.maxHeight) {
                placeableList.forEachIndexed { index, placeable ->
                    val x = constraints.maxWidth / 2 - placeable.width / 2
                    val y = constraints.maxHeight / 2
                    val yOffset = if (index == 0) -offsetY.value.toInt() else offsetY.value.toInt()

                    placeable.placeRelative(x = x, y = y + yOffset)
                }
            }
        }
    }
}
