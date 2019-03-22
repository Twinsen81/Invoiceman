package com.evartem.invoiceman.util

fun getRandomPeaksForGradientChart(
    peaksNumber: IntRange = 16..16, peaksHeight: IntRange = 5..45,
    startEndPeak: Float = 30f
): Array<Float> {
    val peaks = (1..peaksNumber.random()).map { peaksHeight.random().toFloat() }.toMutableList()
    peaks.add(0, startEndPeak)
    peaks.add(startEndPeak)
    return peaks.toTypedArray()
}
