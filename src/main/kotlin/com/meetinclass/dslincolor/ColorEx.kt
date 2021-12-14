package com.meetinclass.dslincolor

import java.awt.Color

object ColorEx {

    val YELLOW = hex2Color("#FFC66D")
    val GREEN = hex2Color("#A5C261")

    fun hex2Color(colorStr: String): Color {
        return Color(
            Integer.valueOf(colorStr.substring(1, 3), 16),
            Integer.valueOf(colorStr.substring(3, 5), 16),
            Integer.valueOf(colorStr.substring(5, 7), 16)
        )
    }
}