package com.mivas.myukulelesongs.util

/**
 * Helper class that formats the tab
 */
object TabHelper {

    private val allNotes = listOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")
    private val stringStartIndex = listOf(9, 4, 0, 7)

    val headerChars = listOf('G', 'C', 'E', 'A', '|')

    /**
     * Formats the tab so that it goes on the next line when it reaches the end of a line.
     *
     * @param text The tab
     * @param maxCharsPerLine THe maximum number of characters that fits in a line
     * @return The formatted tab
     */
    fun getAlignedTab(text: String, maxCharsPerLine: Int): String {
        var formatted = ""
        var lines = text.split("\n").toMutableList()
        while (lines.isNotEmpty()) {
            if (validTabLines(lines)) {
                for (i in 0..3) {
                    val charsRemoved = if (lines[i].length < maxCharsPerLine) lines[i].length else maxCharsPerLine
                    val sub = lines[i].substring(0, charsRemoved)
                    formatted += getStringHeader(i) + sub + "\n"
                    lines[i] = lines[i].drop(charsRemoved)
                }
                if (lines[0].isEmpty()) lines = lines.drop(4).toMutableList()
                if (lines.isNotEmpty() && isTabLine(lines[0])) formatted += "\n"
            } else {
                formatted += lines[0] + "\n"
                lines = lines.drop(1).toMutableList()
            }
        }
        return formatted
    }

    /**
     * Checks if a line is part of a tab.
     *
     * @param line The line
     * @return True if is a tab line, else false
     */
    fun isTabLine(line: String) = line.isNotEmpty()
            && line.contains('-')
            && line.all { it == '-' || it.toString().isNumber() || it == 'A' || it == 'E' || it == 'C' || it == 'G' || it == '|' || it == 'X' }
            && line.none { it == ' ' }

    /**
     * Gets all the notes(numbers) in a line
     *
     * @param line The line
     * @return a list of notes
     */
    fun getNotesInLine(line: String): List<String> {
        val stringIndex = getStringIndexByHeader(line)
        val notes = mutableListOf<String>()
        val items = line.split("-")
        items.forEach {
            if (it.isNumber()) {
                notes.add(getNoteFromString(it.toInt(), stringIndex))
            }
        }
        return notes
    }

    /**
     * Checks if the lines form a valid tab.
     *
     * @param lines The lines
     * @return True if the lines for a tab, else false
     */
    private fun validTabLines(lines: List<String>) = lines.size >= 4 && isTabLine(lines[0]) && isTabLine(lines[1]) && isTabLine(lines[2]) && isTabLine(lines[3])

    /**
     * Returns the header of a tab based on the string
     *
     * @param index The string index
     * @return The tab header
     */
    private fun getStringHeader(index: Int) = when (index) {
        0 -> "A|"
        1 -> "E|"
        2 -> "C|"
        3 -> "G|"
        else -> "  "
    }

    /**
     * Returns the note based on the number in the tab and the string.
     *
     * @param number The number in the tab line
     * @param string The string the number is on
     * @return The matching note
     */
    private fun getNoteFromString(number: Int, string: Int): String {
        val noteIndex = (stringStartIndex[string] + number) % 12
        return allNotes[noteIndex]
    }

    /**
     * Returns the index of a string from a tab line.
     *
     * @param line The tab line
     * @return The string index
     */
    private fun getStringIndexByHeader(line: String) = when {
        line.startsWith("A") -> 0
        line.startsWith("E") -> 1
        line.startsWith("C") -> 2
        line.startsWith("G") -> 3
        else -> 0
    }

}