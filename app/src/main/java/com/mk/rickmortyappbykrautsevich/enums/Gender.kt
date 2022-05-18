package com.mk.rickmortyappbykrautsevich.enums

enum class Gender(val value: Int) {
    female(1),
    male(2),
    genderless(3),
    unknown(4);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}