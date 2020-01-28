package com.mivas.myukulelesongs.util

fun String.toSharps() = replace("Bb", "A#")
    .replace("Db", "C#")
    .replace("Eb", "D#")
    .replace("Gb", "F#")
    .replace("Ab", "G#")

fun String.toFlats() = replace("A#", "Bb")
    .replace("C#", "Db")
    .replace("D#", "Eb")
    .replace("F#", "Gb")
    .replace("G#", "Ab")

fun String.isNumber() = try {
    toInt()
    true
} catch (e: NumberFormatException) {
    false
}