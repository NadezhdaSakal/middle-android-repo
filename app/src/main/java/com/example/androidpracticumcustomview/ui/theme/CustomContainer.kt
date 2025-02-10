package com.example.androidpracticumcustomview.ui.theme

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/*
Задание:
Реализуйте необходимые компоненты;
Создайте проверку что дочерних элементов не более 2-х;
Предусмотрите обработку ошибок рендера дочерних элементов.
Задание по желанию:
Предусмотрите параметризацию длительности анимации.
 */

class CustomContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val maxChildCount = 2


    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val totalWidth = MeasureSpec.getSize(widthMeasureSpec)
        val totalHeight = MeasureSpec.getSize(heightMeasureSpec)

        for (i in 0 until minOf(childCount, maxChildCount)) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
        }

        setMeasuredDimension(totalWidth, totalHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val parentWidth = right - left
        val parentHeight = bottom - top
        val centerX = parentWidth / 2
        val centerY = parentHeight / 2

        for (i in 0 until minOf(childCount, maxChildCount)) {
            val child = getChildAt(i)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            val leftPosition = centerX - childWidth / 2
            val topPosition = centerY - childHeight / 2

            child.layout(
                leftPosition,
                topPosition,
                leftPosition + childWidth,
                topPosition + childHeight
            )

            if (i == 0) {
                child.alpha = 0f
                child.animate().alpha(1f).setDuration(ALPHA_DURATION)
                child.animate().y(top.toFloat()).setDuration(MOVEMENT_DURATION)
            } else {
                child.alpha = 0f
                child.animate().alpha(1f).setDuration(ALPHA_DURATION)
                child.animate().y(bottom.toFloat() - childHeight).setDuration(MOVEMENT_DURATION)
            }
        }
    }

    override fun addView(child: View) {
        if (childCount < maxChildCount) {
            super.addView(child)
        } else {
            throw IllegalStateException(
                "Невозможно добавить более $maxChildCount дочерних элементов в этот FrameLayout"
            )
        }
    }

    companion object {
        const val ALPHA_DURATION = 2000L
        const val MOVEMENT_DURATION = 5000L
    }
}