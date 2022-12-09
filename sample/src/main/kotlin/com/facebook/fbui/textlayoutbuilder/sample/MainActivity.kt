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
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var parent: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        parent = findViewById(R.id.parent)

        /**
         * 这个一块的宽度还是需要获取的
         * 先获取一下父的宽度，根据宽度和参数进行计算
         *
         * 那我们自己的也可以，只不过需要了解一下系统的兼容性
         */
        addSample {
            val builder =
                TextLayoutBuilder().setText("Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!Hello, world!")
                    .setTextSize(20f.dp(this))

            if (false) {
                //为什么不动态调整这个参数
                builder.maxLines = 5
            }
            builder
        }
    }

    private fun addSample(block: () -> TextLayoutBuilder) {
        val layout = block() ?: return
        parent.addView(
            SampleView(this, layout).apply {
//                layoutParams = LinearLayout.LayoutParams(200f.dp(this@MainActivity), layout.height)
            })
    }

    private fun Float.dp(context: Context): Int {
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics
            )
        )
    }
}
