package com.mivas.myukulelesongs.util

/**
 * Helper class that handles transpositions.
 */
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

    /**
     * Transposes a song.
     *
     * @param text The song text
     * @param plus True if the transposition is up, else false
     * @param preferSharp True if sharps are preferred, else false
     * @return The transposed song
     */
    fun transposeSong(text: String, plus: Boolean, preferSharp: Boolean): String {
        val lines = text.split("\n")
        var transposed = ""
        lines.forEach { line ->
            if (TabHelper.isTabLine(line)) {
                transposed += transposeTabLine(line, plus) + "\n"
            } else if (ChordHelper.isChordLine(line)) {
                transposed += transposeChordLine(line, plus, preferSharp) + "\n"
            } else {
                transposed += line + "\n"
            }
        }
        if (transposed.isNotEmpty()) transposed = transposed.dropLast(1)
        return transposed
    }

    /**
     * Transposes a line of a tab.
     *
     * @param line The tab line
     * @param plus True if the transposition is up, else false
     */
    private fun transposeTabLine(line: String, plus: Boolean): String {
        var transposedLine = ""
        var numberBuilder = ""
        line.forEach {char ->
            if (char.toString().isNumber()) {
                numberBuilder += char.toString()
            } else {
                if (numberBuilder.isNotEmpty()) {
                    transposedLine = transposeNote(numberBuilder, plus, transposedLine)
                    numberBuilder = ""
                }
                transposedLine += char
            }
        }
        if (numberBuilder.isNotEmpty()) transposedLine = transposeNote(numberBuilder, plus, transposedLine)
        return transposedLine
    }

    /**
     * Transposes a note.
     *
     * @param numberBuilder The builder that builds a note in a line
     * @param plus True if the transposition is up, else false
     * @param transposedLine The current transposed line
     * @return The new transposed line
     */
    private fun transposeNote(numberBuilder: String, plus: Boolean, transposedLine: String): String {
        var newTransposedLine = transposedLine
        var newNumber = numberBuilder.toInt().let { if (plus) it + 1 else it - 1 }
        if (newNumber < 0) newNumber += 12
        if (newNumber > 11) newNumber -= 12
        val newString = newNumber.toString()
        if (numberBuilder.length == newString.length) {
            newTransposedLine += newString
        } else if (numberBuilder.length < newString.length) {
            if (transposedLine.length == 1 || (transposedLine.length >=2 && transposedLine[transposedLine.length - 2] == '-')) {
                newTransposedLine = newTransposedLine.dropLast(1)
                newTransposedLine += newString
            } else {
                newTransposedLine += "X"
            }
        } else if (numberBuilder.length > newString.length) {
            newTransposedLine += "-$newString"
        }
        return newTransposedLine
    }

    /**
     * Transposes a chord line.
     *
     * @param line The line
     * @param plus True if the transposition is up, else false
     * @param preferSharp True if sharps are preferred, else false
     * @return The transposed line
     */
    private fun transposeChordLine(line: String, plus: Boolean, preferSharp: Boolean): String {
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

    /**
     * Transposes a chord.
     *
     * @param chord The chord
     * @param plus True if the transposition is up, else false
     * @param preferSharp True if sharps are preferred, else false
     * @return The transposed chord
     */
    private fun transposeChord(chord: String, plus: Boolean, preferSharp: Boolean): String {
        transpositions.forEach {
            if (chord.contains(it[0])) {
                val pre = when {
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