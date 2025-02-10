package com.example.androidpracticumcustomview.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.layout.Layout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.launch

/*
Задание:
Реализуйте необходимые компоненты;
Создайте проверку что дочерних элементов не более 2-х;
Предусмотрите обработку ошибок рендера дочерних элементов.
Задание по желанию:
Предусмотрите параметризацию длительности анимации.
 */

const val ALPHA_DURATION = 2000
const val MOVEMENT_DURATION = 5000

@Composable
fun CustomContainerCompose(
    firstChild: @Composable (() -> Unit),
    secondChild: @Composable (() -> Unit)
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val screenHeight = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(scope) {
        launch {
            offsetY.animateTo(
                targetValue = screenHeight.floatValue / 2,
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

    Layout(
        modifier = Modifier.alpha(alpha.value),
        content = {
            firstChild()
            secondChild()
        }
    ) { measurableList, constraints ->
        val placeableList = measurableList.map { measurable ->
            measurable.measure(constraints)
        }

        if (placeableList.size > 2) {
            throw IllegalStateException(
                "Невозможно добавить более 2 дочерних элементов в этот контейнер"
            )
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeableList.forEachIndexed { index, placeable ->
                screenHeight.floatValue = constraints.maxHeight.toFloat() - 100
                val x = constraints.maxWidth / 2 - placeable.width / 2
                val y = constraints.maxHeight / 2
                if (index == 0) {
                    placeable.placeRelative(x = x, y = (y - offsetY.value.toInt()))
                } else {
                    placeable.placeRelative(x = x, y = (offsetY.value.toInt() + y))
                }
            }
        }
    }
}
