package com.malinowski

import kotlinx.serialization.Serializable

@Serializable
data class LetterWord(
    val letter: String,
    val words: List<String>
)