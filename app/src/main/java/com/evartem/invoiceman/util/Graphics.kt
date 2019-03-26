package com.evartem.invoiceman.util

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey

fun getRandomPeaksForGradientChart(
    peaksNumber: IntRange = 16..16, peaksHeight: IntRange = 5..45,
    startEndPeak: Float = 30f
): Array<Float> {
    val peaks = (1..peaksNumber.random()).map { peaksHeight.random().toFloat() }.toMutableList()
    peaks.add(0, startEndPeak)
    peaks.add(startEndPeak)
    return peaks.toTypedArray()
}

@GlideModule
class TheAppGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.apply {
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey(System.currentTimeMillis().toShort()))
        }
    }
}