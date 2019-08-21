package com.mivas.myukulelesongs.util

import com.mivas.myukulelesongs.model.ChordLineData

object ChordDetector {

    private val chords = listOf("Ab", "A", "A#", "Bb", "B", "C", "C#", "Db", "D", "D#", "Eb", "E", "F", "F#", "Gb", "G", "G#")
    private val variations = listOf("m", "aug", "dim", "7", "m7", "maj7", "aug7", "dim7", "m7b5", "add9", "madd9", "6", "m6", "5", "9", "m9", "11", "13", "sus2", "sus4", "7sus2", "7sus4")

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
        for (chord in ChordDetector.chords) {
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
}