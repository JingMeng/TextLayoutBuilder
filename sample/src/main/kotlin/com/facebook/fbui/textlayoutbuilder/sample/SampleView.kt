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
 *
 *
 * 之前不希望 onMeasure 来创建 staticLayout
 *
 * 我们可以借鉴TextView的方案
 *
 * [android.widget.TextView]
 * [android.widget.TextView.onMeasure]
 *
 * https://juejin.cn/post/6934593494182952974
 *
 * https://jaeger.itscoder.com/android/2016/08/05/staticlayout-source-analyse.html
 *  其直接子类有 StaticLayout、DynamicLayout、BoringLayout，
 *
 *  在官方的文档中提到，如果文本内容会被编辑，应该使用 DynamicLayout，
 *  如果文本显示之后不会发生改变，应该使用 StaticLayout，
 *  而 BoringLayout 则使用场景极为有限：当你确保你的文本只有一行，且所有的字符均是从左到右显示的（某些语言的文字是从右到左显示的），你才可以使用 BoringLayout。
 *
 *
 *  // 在 onMeasure 以及很多地方都调用这个方法，人家判null了 ，也就是进行了复用
 *   if (mLayout == null) {
 *       makeNewLayout(want, hintWant, boring, hintBoring,
 *       width - getCompoundPaddingLeft() - getCompoundPaddingRight(), false);
 *   }
 *
 *   其次那个宽度也限制了
 *   1. 如果固定，你写多少是多少
 *   2. 如果atmost的话， 那就计算宽度，计算算了在和给予的进行比较
 *
 *
 *  如果没有北京因素影响的话，当前屏幕宽度就是最好的了
 *
 *  getDesiredWidthWithLimit 这个是一个静态的方法 所有的子类相关调用都是公用的，但是我们并没有使用
 *
 *   但是官方给与了 TextLayoutBuilder.MEASURE_MODE_AT_MOST 应对情形，也不需要我们考虑了，给一个最大的就结束了
 *   builder.setWidth(measuredWidth, TextLayoutBuilder.MEASURE_MODE_AT_MOST)
 *
 *  这个方法也是调用的 getDesiredWidthWithLimit
 *
 *  https://www.jianshu.com/p/52791b3de34a
 *
 *  =============
 *
 *  想问个问题，为什么BoringLayou测绘出来的Text的大小，和Paint.getTextBound()出来的不一样，我测试了一下，如果一个TextView的大小是wrap，我给30像素的文字，结果TextView的高度竟然是35
 *
 * fixme
 *  测量还是有差距的，现在测量应该换一下测量方式，不应该使用pant
 *  https://juejin.cn/post/6930503954971394062
 *
 *
 *  还有是自定义ViewGroup的时候还需要重写一个方法，这个方法目前你也忘记了
 *
 **/
class SampleView(
    context: Context,
    private var builder: TextLayoutBuilder,
    val maxLine: Int = 3
) : View(context) {


    var showMin: Boolean = false;
    var mFirst: Boolean = true;
    var originText: CharSequence = "";

    var size = 0

    init {
        originText = builder.text as CharSequence;
    }

    /**
     * 在这边创建了两个layout，用切换切换
     *
     */
    var layout: Layout? = null;
    var layoutMin: Layout? = null;
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (!mFirst) {
            if (showMin && (layout?.lineCount ?: 0) > maxLine) {
                setMeasuredDimension(layoutMin?.width ?: 0, layoutMin?.height ?: 0)
            } else {
                setMeasuredDimension(layout?.width ?: 0, layout?.height ?: 0)
            }
            return;
        }
        mFirst = false;

        builder.text = originText
        builder = builder.setWidth(measuredWidth, TextLayoutBuilder.MEASURE_MODE_AT_MOST)
        layout = builder.build();

        /**
         * 修改宽度的问题，是为了自适应的时候使用
         */
        setMeasuredDimension(layout?.width ?: 0, layout?.height ?: 0)
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
            setMeasuredDimension(layoutMin?.width ?: 0, layoutMin?.height ?: 0)
        }
    }

    /**
     * 是使用这个，还是使用上面的那个
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }


    /**
     *  [android.view.ViewGroup]
     *  ViewGroup 也没有重写这个方法
     *  [android.view.ViewGroup.onDraw]
     *  这个方法也没有重写
     *  [android.view.ViewGroup.draw]
     *
     *   final boolean dirtyOpaque = (privateFlags & PFLAG_DIRTY_MASK) == PFLAG_DIRTY_OPAQUE && (mAttachInfo == null || !mAttachInfo.mIgnoreDirtyState);
     *
     *   是这个标志位，但是我记忆中有一个标志位的清除操作
     *
     *  这个竟然也没有重写
     *  [android.view.ViewGroup.onMeasure]
     *  [android.view.ViewGroup.measure]
     *
     *  还是回到了最原始的那个问题
     *
     *  requestLayout 会导致 onDraw 执行吗
     *
     * [android.view.ViewRootImpl]
     *   performTraversals();
     *   performMeasure
     *    这个的调用条件是什么，好几次都分析到了
     *    performDraw()
     *
     * 可以是记忆便宜，measure 会导致layout是否是强制执行
     *
     * https://juejin.cn/post/6856291250081906696
     * 由于在 measure 过程中设置了PFLAG_LAYOUT_REQUIRED标记，那么就会调用onLayout来进行view的布局过程，这个过程完成后，清理PFLAG_LAYOUT_REQUIRED和PFLAG_FORCE_LAYOUT标记表示布局过程完成了。这里需要注意的是view在测量后大小可能发生变化，这时候通过setFrame设置其边框时会调用invalidate的调用，因此可能会导致onDraw的调用。
     *
     * [android.view.ViewGroup.onLayout]
     * [android.view.View.layout]
     *
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        size++
        if (false && size > 2) {
            throw  RuntimeException("=======查看一下调用源头==============");
        }
        val count = layout?.lineCount ?: 0
        println("============$showMin=====${count > maxLine}=====")
        if (showMin && count > maxLine) {
            layoutMin?.draw(canvas)
        } else {
            layout?.draw(canvas)
        }

    }
}
