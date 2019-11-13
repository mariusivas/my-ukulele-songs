package com.mivas.myukulelesongs.util

object TransposeHelper {

    private val transpositions = listOf(
        listOf("Ab", "A", "G", "A", "G"),
        listOf("A#", "B", "A", "B", "A"),
        listOf("Bb", "B", "A", "B", "A"),
        listOf("C#", "D", "C", "D", "C"),
        listOf("Db", "D", "C", "D", "C"),
        listOf("D#", "E", "D", "E", "D"),
        listOf("Eb", "E", "D", "E", "D"),
        listOf("F#", "G", "F", "G", "F"),
        listOf("Gb", "G", "F", "G", "F"),
        listOf("G#", "A", "G", "A", "G"),
        listOf("A", "A#", "G#", "Bb", "Ab"),
        listOf("B", "C", "A#", "C", "Bb"),
        listOf("C", "C#", "B", "Db", "B"),
        listOf("D", "D#", "C#", "Eb", "Db"),
        listOf("E", "F", "D#", "F", "Eb"),
        listOf("F", "F#", "E", "Gb", "E"),
        listOf("G", "G#", "F#", "Ab", "Gb")
    )

    fun transposeSong(text:String, plus: Boolean, preferSharp: Boolean): String {
        val lines = text.split("\n")
        var transposed = ""
        lines.forEach { line ->
            transposed += if (ChordHelper.isChordLine(line)) {
                transposeChordLine(line, plus, preferSharp) + "\n"
            } else {
                line + "\n"
            }
        }
        return transposed
    }

    private fun transposeChordLine(line:String, plus: Boolean, preferSharp: Boolean): String {
        var transposedLine = ""
        var index = 0
        var chord = ""
        var skipNext = false
        while (index < line.length) {
            val char = line[index]
            if (char == ' ') {
                if (skipNext) {
                    skipNext = false
                    if (index + 1 < line.length && line[index + 1] != ' ') {
                        transposedLine += char
                    }
                } else {
                    transposedLine += char
                }
            } else {
                chord += char
                if ((index + 1 < line.length && line[index + 1] == ' ') || index + 1 >= line.length) {
                    //process word
                    val transposedChord = transposeChord(chord, plus, preferSharp)
                    when {
                        transposedChord.length > chord.length -> {
                            transposedLine += transposedChord
                            skipNext = true
                        }
                        transposedChord.length < chord.length -> transposedLine += "$transposedChord "
                        else -> transposedLine += transposedChord
                    }
                    chord = ""
                }
            }
            index++
        }
        return transposedLine
    }

    private fun transposeChord(chord:String, plus: Boolean, preferSharp: Boolean): String {
        transpositions.forEach {
            if (chord.contains(it[0])) {
                val pre =  when {
                    plus && preferSharp -> it[1]
                    !plus && preferSharp -> it[2]
                    plus && !preferSharp -> it[3]
                    !plus && !preferSharp -> it[4]
                    else -> it[0]
                }
                return chord.replace(it[0], pre)
            }
        }
        return chord
    }
}