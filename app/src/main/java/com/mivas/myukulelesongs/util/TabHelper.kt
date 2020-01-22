package com.mivas.myukulelesongs.util

object TabHelper {

    private val allNotes = listOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")
    private val stringStartIndex = listOf(9, 4, 0, 7)


    fun getFormattedTab(text: String, maxCharsPerLine: Int): String {
        var formatted = ""
        var splittedText = text.split("\n").toMutableList()
        while (splittedText.size >= 4) {
            while (splittedText[0].isNotEmpty()) {
                for (i in 0..3) {
                    val charsRemoved = if (splittedText[i].length < maxCharsPerLine) splittedText[i].length else maxCharsPerLine
                    val sub = splittedText[i].substring(0, charsRemoved)
                    formatted += getStringHeader(i) + sub + "\n"
                    splittedText[i] = splittedText[i].drop(charsRemoved)
                }
                formatted += "\n"
            }
            splittedText = splittedText.drop(4).toMutableList()
        }
        return formatted
    }

    private fun getStringHeader(index: Int) = when (index) {
        0 -> "A|"
        1 -> "E|"
        2 -> "C|"
        3 -> "G|"
        else -> "  "
    }

    fun isNumber(char: String) = try {
        char.toInt()
        true
    } catch (e: NumberFormatException) {
        false
    }

    fun getNotesInLine(line: String): List<String> {
        val stringIndex = getStringIndexByHeader(line)
        val notes = mutableListOf<String>()
        val items = line.split("-")
        items.forEach {
            if (isNumber(it)) {
                notes.add(getNoteFromString(it.toInt(), stringIndex))
            }
        }
        return notes
    }

    private fun getNoteFromString(number: Int, string: Int): String {
        val noteIndex = (stringStartIndex[string] + number) % 12
        return allNotes[noteIndex]
    }

    private fun getStringIndexByHeader(line: String) = when {
        line.startsWith("A") -> 0
        line.startsWith("E") -> 1
        line.startsWith("C") -> 2
        line.startsWith("G") -> 3
        else -> 0
    }

}