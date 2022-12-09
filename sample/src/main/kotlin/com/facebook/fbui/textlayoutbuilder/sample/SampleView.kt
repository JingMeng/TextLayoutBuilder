/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.fbui.textlayoutbuilder.sample

import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.text.TextUtils
import android.view.View
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder

/**
 * https://github.com/JingMeng/better/blob/c89628495cd0b15a96e082209f850ebded3d9d6a/app/src/main/java/me/fenfei/ui/view/text/view/bug/ExpandTextView2.java
 */
class SampleView(
    context: Context,
    private var builder: TextLayoutBuilder,
    val maxLine: Int = 3
) : View(context) {

    var showMin: Boolean = false;
    var mFirst: Boolean = true;
    var originText: String = "";

    init {
        originText = builder.text as String;
    }

    /**
     * 在这边创建了两个layout，用切换切换
     */
    var layout: Layout? = null;
    var layoutMin: Layout? = null;
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (!mFirst) {
            if (showMin && (layout?.lineCount ?: 0) > maxLine) {
                setMeasuredDimension(measuredWidth, layoutMin?.height ?: 0)
            } else {
                setMeasuredDimension(measuredWidth, layout?.height ?: 0)
            }
            return;
        }
        mFirst = false;

        builder.text = originText
        builder = builder.setWidth(measuredWidth, TextLayoutBuilder.MEASURE_MODE_AT_MOST)
        layout = builder.build();

        setMeasuredDimension(measuredWidth, layout?.height ?: 0)
        // 这个里就拿到了数据
        val layout1 = layout
        //从这个 lineCount 的关系就可以推断出 不为 null---加上 ?:就不行了
        val lineCount = layout1?.lineCount
        if (lineCount != null && lineCount > maxLine && !showMin) {
            val stringCount = builder.text
            val paint = layout1.paint
            val start: Int = layout1.getLineStart(maxLine - 1)
            val end: Int = layout1.getLineEnd(maxLine - 1)
            //修改这一行的文字
            var lineText: String = stringCount.substring(start, end)

            if (TextUtils.isEmpty(lineText)) {
                lineText = ""
            }
            val ellipsizeText = "展开"
            // 省略文字的宽度
            val dotWidth: Float = paint.measureText(ellipsizeText)

            // 将第 showLineCount 行最后的文字替换为 ellipsizeText
            // 将第 showLineCount 行最后的文字替换为 ellipsizeText
            var endIndex = 0
            for (i in lineText.length - 1 downTo 0) {
                val str = lineText.substring(i, lineText.length)
                // 找出文字宽度大于 ellipsizeText 的字符
                if (paint.measureText(str) >= dotWidth) {
                    endIndex = i
                    break
                }
            }

            // 新的第 showLineCount 的文字
            val newEndLineText = lineText.substring(0, endIndex) + ellipsizeText
            //重新拼接文字
            layoutMin = builder.setText(stringCount.substring(0, start) + newEndLineText).build()
            showMin = true;
            setMeasuredDimension(measuredWidth, layoutMin?.height ?: 0)
        }
    }

    /**
     * 是使用这个，还是使用上面的那个
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val count = layout?.lineCount ?: 0
        println("============$showMin=====${count > maxLine}=====")
        if (showMin && count > maxLine) {
            layoutMin?.draw(canvas)
        } else {
            layout?.draw(canvas)
        }

    }
}
