package com.mivas.myukulelesongs.util

import org.jetbrains.anko.collections.forEachReversedByIndex

object KeyHelper {

    private val keyChords = listOf(
        listOf("C", "Dm", "Em", "F", "G", "Am", "Bdim"),
        listOf("G", "Am", "Bm", "C", "D", "Em", "F#dim"),
        listOf("G", "Am", "Bm", "C", "D", "Em", "Gbdim"),
        listOf("D", "Em", "F#m", "G", "A", "Bm", "C#dim"),
        listOf("D", "Em", "Gbm", "G", "A", "Bm", "Dbdim"),
        listOf("A", "Bm", "C#m", "D", "E", "F#m", "G#dim"),
        listOf("A", "Bm", "Dbm", "D", "E", "Gbm", "Abdim"),
        listOf("E", "F#m", "G#m", "A", "B", "C#m", "D#dim"),
        listOf("E", "Gbm", "Abm", "A", "B", "Dbm", "Ebdim"),
        listOf("B", "C#m", "D#m", "E", "F#", "G#m", "A#dim"),
        listOf("B", "Dbm", "Ebm", "E", "Gb", "Abm", "Bbdim"),
        listOf("F", "Gm", "Am", "A#", "C", "Dm", "Edim"),
        listOf("F", "Gm", "Am", "Bb", "C", "Dm", "Edim"),
        listOf("A#", "Cm", "Dm", "D#", "F", "Gm", "Adim"),
        listOf("Bb", "Cm", "Dm", "Eb", "F", "Gm", "Adim"),
        listOf("D#", "Fm", "Gm", "G#", "A#", "Cm", "Ddim"),
        listOf("Eb", "Fm", "Gm", "Ab", "Bb", "Cm", "Ddim"),
        listOf("G#", "A#m", "Cm", "C#", "D#", "Fm", "Gdim"),
        listOf("Ab", "Bbm", "Cm", "Db", "Eb", "Fm", "Gdim"),
        listOf("C#", "D#m", "Fm", "F#", "G#", "A#m", "Cdim"),
        listOf("Db", "Ebm", "Fm", "Gb", "Ab", "Bbm", "Cdim"),
        listOf("F#", "G#m", "A#m", "B", "C#", "D#m", "Fdim"),
        listOf("Gb", "Abm", "Bbm", "B", "Db", "Ebm", "Fdim")
    )

    private val keyNotes = listOf(
        listOf("C", "D", "E", "F", "G", "A", "B"),
        listOf("G", "A", "B", "C", "D", "E", "Gb"),
        listOf("D", "E", "Gb", "G", "A", "B", "Db"),
        listOf("A", "B", "Db", "D", "E", "Gb", "Ab"),
        listOf("E", "Gb", "Ab", "A", "B", "Db", "Eb"),
        listOf("B", "Db", "Eb", "E", "Gb", "Ab", "Bb"),
        listOf("Gb", "Ab", "Bb", "B", "Db", "Eb", "F"),
        listOf("Db", "Eb", "F", "Gb", "Ab", "Bb", "C"),
        listOf("Ab", "Bb", "C", "Db", "Eb", "F", "G"),
        listOf("Eb", "F", "G", "Ab", "Bb", "C", "D"),
        listOf("Bb", "C", "D", "Eb", "F", "G", "A"),
        listOf("F", "G", "A", "Bb", "C", "D", "E")
    )
    
    private val allKeys = listOf("Ab", "A", "A#", "Bb", "B", "C", "C#", "Db", "D", "D#", "Eb", "E", "F", "F#", "Gb", "G", "G#", "Abm", "Am", "A#m", "Bbm", "Bm", "Cm", "C#m", "Dbm", "Dm", "D#m", "Ebm", "Em", "Fm", "F#m", "Gbm", "Gm", "G#m")

    fun findKeyFromChords(chords: List<String>): String {
        var bestMatches = 0
        val bestKeys = mutableListOf<List<String>>()
        keyChords.forEach { keySet ->
            val matches = chords.count { keySet.contains(it) }
            if (matches > bestMatches) {
                bestMatches = matches
                bestKeys.clear()
                bestKeys.add(keySet)
            } else if (matches > 0 && matches == bestMatches) {
                bestKeys.add(keySet)
            }
        }
        return when {
            bestKeys.size == 1 -> bestKeys[0][0].run { if (Prefs.getBoolean(Constants.PREF_PREFER_SHARP)) toSharps() else toFlats() }
            bestKeys.size > 1 -> {
                var bestRelativeMinorMatches = 0
                var bestOverallKey = ""
                bestKeys.forEachReversedByIndex { bestKey ->
                    val matches = chords.count { it == bestKey[5] }
                    if (matches >= bestRelativeMinorMatches) {
                        bestRelativeMinorMatches = matches
                        bestOverallKey = bestKey[0]
                    }
                }
                bestOverallKey.run { if (Prefs.getBoolean(Constants.PREF_PREFER_SHARP)) toSharps() else toFlats() }
            }
            else -> ""
        }
    }

    fun findKeyFromTab(notes: List<String>): String {
        var bestMatches = 0
        var bestKey = ""
        keyNotes.forEach { keySet ->
            val matches = notes.count { keySet.contains(it) }
            if (matches > bestMatches) {
                bestMatches = matches
                bestKey = keySet[0]
            }
        }
        return bestKey.run { if (Prefs.getBoolean(Constants.PREF_PREFER_SHARP)) toSharps() else toFlats() }
    }

    fun getAllKeys() = allKeys

}