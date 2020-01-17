package com.mivas.myukulelesongs.util

import com.mivas.myukulelesongs.model.UCChordData

object ChordHelper {

    private val chords = listOf("Ab", "A", "A#", "Bb", "B", "C", "C#", "Db", "D", "D#", "Eb", "E", "F", "F#", "Gb", "G", "G#")
    private val uniqueChords = listOf("A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#")
    private val variations =
        listOf("m", "7", "m7", "maj7", "aug", "dim", "dim7", "sus2", "sus4", "7sus2", "7sus4", "m7b5", "9", "11", "13", "6", "m6", "add9", "m9", "5", "m13", "mMaj7", "m11", "maj9")

    fun isChordLine(line: String): Boolean {
        val words = line.split(" ").map { it.trim() }
        for (word in words) {
            if (word.isBlank()) continue
            if (word.contains('/')) continue
            var chordDetected = false
            for (chord in chords) {
                if (word.startsWith(chord)) {
                    if (word == chord) {
                        chordDetected = true
                        break
                    } else {
                        for (variation in variations) {
                            if (word == chord + variation) {
                                chordDetected = true
                                break
                            }
                        }
                    }
                }
            }
            if (!chordDetected) return false
        }
        return true
    }

    fun getChordsInLine(line: String): List<String> {
        val chords = mutableListOf<String>()
        val words = line.split(" ").map { it.trim() }
        for (word in words) {
            if (word.isBlank()) continue
            if (word.contains('/')) {
                val invertedChords = word.split("/")
                invertedChords.forEach { getChordsInWord(it, chords) }
            }
            getChordsInWord(word, chords)
        }
        return chords
    }

    private fun getChordsInWord(word: String, chords: MutableList<String>) {
        for (chord in ChordHelper.chords) {
            if (word.startsWith(chord)) {
                if (word == chord) {
                    chords.add(word)
                    break
                } else {
                    for (variation in variations) {
                        if (word == chord + variation) {
                            chords.add(chord + variation)
                            break
                        }
                    }
                }
            }
        }
    }

    fun getUCChordData(chord: String): UCChordData {
        if (chords.contains(chord)) {
            return UCChordData(toFlats(chord), "major")
        } else {
            chords.forEach { preChord ->
                variations.forEach { variation ->
                    if (chord == preChord + variation) {
                        return UCChordData(toFlats(preChord), variation)
                    }
                }
            }
        }
        return UCChordData("", "")
    }

    fun toFlats(preChord: String) = preChord
        .replace("A#", "Bb")
        .replace("C#", "Db")
        .replace("D#", "Eb")
        .replace("F#", "Gb")
        .replace("G#", "Ab")

    fun toSharps(preChord: String) = preChord
        .replace("Bb", "A#")
        .replace("Db", "C#")
        .replace("Eb", "D#")
        .replace("Gb", "F#")
        .replace("Ab", "G#")

    fun getAllChordsVariations(): List<List<String>> {
        val preferSharps = Prefs.getBoolean(Constants.PREF_PREFER_SHARP)
        val all = mutableListOf<List<String>>()
        all.add(uniqueChords.map { if (preferSharps) toSharps(it) else toFlats(it) })
        variations.forEach { variation ->
            all.add(uniqueChords.map { "${if (preferSharps) toSharps(it) else toFlats(it)}$variation" })
        }
        return all
    }

}