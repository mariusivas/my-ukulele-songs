package com.mivas.myukulelesongs.model

/**
 * Data to be used when retrieving information about a chord from ukulele-chords.com.
 *
 * @param chord The base chord
 * @param chordType The chord type
 */
data class UCChordData(
        val chord: String,
        val chordType: String
)