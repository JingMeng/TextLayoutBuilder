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
import android.view.View
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder

class SampleView(context: Context, private val builder: TextLayoutBuilder) : View(context) {

    /**
     * 在这边创建了两个layout，用切换切换
     */
    var layout: Layout? = null;
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        layout = builder.setWidth(measuredWidth, TextLayoutBuilder.MEASURE_MODE_AT_MOST).build();
        // 这个里就拿到了数据
        val lineCount = layout?.lineCount
    }

    /**
     * 是使用这个，还是使用上面的那个
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        layout?.draw(canvas)
    }
}
