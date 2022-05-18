package com.mk.rickmortyappbykrautsevich.presentation.fragments.enums

enum class Status(val value: Int) {
    alive(1),
    dead(2),
    unknown(3);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}