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
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder


const val text =
    "Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!"
const val text1 = "Hello, world!Hello, world!Hello, world!Hello, world!"

/**
 * 关于空格的解释
 * https://www.jianshu.com/p/bed2607b8c76
 * https://blog.csdn.net/qq_33210042/article/details/105573427
 * https://blog.csdn.net/qq_33210042/article/details/105573427
 */
class MainActivity : AppCompatActivity() {
    private lateinit var parent: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        parent = findViewById(R.id.parent)

        /**
         *
         *  下面这部分是
         */
        val space = "\u0020"
        val tag = "\u3000"
        val spannableString = SpannableString(text1 + space + tag)
        resources.getDrawable(R.drawable.j_wallet_charge_address_copy)?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            //这个地方的对齐模式存在两种
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)
            spannableString.setSpan(
                imageSpan,
                spannableString.length - tag.length,
                spannableString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        addSample {
            val builder =
                TextLayoutBuilder().setText(spannableString)
                    .setTextSize(20f.dp(this))
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)

            if (false) {
                //为什么不动态调整这个参数
                builder.maxLines = 5
            }
            builder
        }
    }

    private fun addSample(block: () -> TextLayoutBuilder) {
        val layout = block() ?: return

        val sampleView = SampleView(this, layout, 3).apply {
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
            // layoutParams = LinearLayout.LayoutParams(200f.dp(this@MainActivity), layout.height)
        }
        parent.addView(sampleView)
        sampleView.setOnClickListener {
            sampleView.showMin = !sampleView.showMin;
            println("=====MainActivity==============setOnClickListener===========${sampleView.showMin}============")
            sampleView.requestLayout()
            // FIXME:  必须加这个，不然不会导致重新绘制，知道哪里出问题吗？

            //修复onMeasure 就好了
            if (false) {
                /**
                 *  这个地方的操作和
                 *  [android.view.View.layout] 里面的触发是一致的
                 *  [android.view.View.setFrame] 里面的触发是一致的
                 *   这样也就证明了为什么  更新了 onMeasure 就可以起到作用，和之前的结论一起对别
                 */
                sampleView.invalidate()
            }
        }
    }

    private fun Float.dp(context: Context): Int {
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics
            )
        )
    }
}
