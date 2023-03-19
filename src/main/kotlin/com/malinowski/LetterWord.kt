package com.malinowski

import kotlinx.serialization.Serializable

@Serializable
data class LetterWord(
    val letter: Char,
    val words: List<String>
)