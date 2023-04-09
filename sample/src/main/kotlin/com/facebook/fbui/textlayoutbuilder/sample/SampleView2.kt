package com.facebook.fbui.textlayoutbuilder.sample

import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.view.View

class SampleView2(context: Context, private val layout: Layout) : View(context) {
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        layout.draw(canvas)
    }
}