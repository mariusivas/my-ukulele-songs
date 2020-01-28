package com.mivas.myukulelesongs.util

import java.util.*

object UniqueIdGenerator {

    private const val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
    private const val SIZE = 16

    fun generate(): String {
        val random = Random()
        val sb = StringBuilder(SIZE)
        for (i in 0 until SIZE)
            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        return sb.toString()
    }
}