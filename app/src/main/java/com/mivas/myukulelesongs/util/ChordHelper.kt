package com.mivas.myukulelesongs.util

import com.mivas.myukulelesongs.model.UCChordData

object ChordHelper {

    private val chords = listOf("Ab", "A", "A#", "Bb", "B", "C", "C#", "Db", "D", "D#", "Eb", "E", "F", "F#", "Gb", "G", "G#")
    private val variations =
        listOf("m", "aug", "dim", "7", "m7", "maj7", "m7b5", "sus2", "sus4", "7sus4", "9", "11", "13", "6", "m6", "add9", "m9", "5", "dim7", "m13", "7sus2", "mMaj7", "m11", "maj9")

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
            return UCChordData(fixSharps(chord), "major")
        } else {
            chords.forEach { preChord ->
                variations.forEach { variation ->
                    if (chord == preChord + variation) {
                        return UCChordData(fixSharps(preChord), variation)
                    }
                }
            }
        }
        return UCChordData("", "")
    }

    private fun fixSharps(preChord: String) = preChord
        .replace("A#", "Bb")
        .replace("C#", "Db")
        .replace("D#", "Eb")
        .replace("F#", "Gb")
        .replace("G#", "Ab")

}