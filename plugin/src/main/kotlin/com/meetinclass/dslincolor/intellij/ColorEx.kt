package com.meetinclass.dslincolor.intellij

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesScheme
import java.awt.Color
import java.awt.Color.WHITE

object ColorEx {

    fun TextAttributesScheme.getColorByAnnotationValue(value: String): Color {
        val stringValue = trimAnnotationValue(value)
        val attributes = getAttributes(TextAttributesKey.createTextAttributesKey(stringValue))
        return if (attributes == null || attributes.foregroundColor == null) {
            when (stringValue) {
                "html-dsl-tag" -> {
                    YELLOW
                }
                "html-dsl-attribute" -> {
                    GREEN
                }
                else -> {
                    WHITE
                }
            }
        } else {
            attributes.foregroundColor
        }
    }

    private fun trimAnnotationValue(value: String): String = value.trimStart('"').trimEnd('"')

    private val YELLOW = hex2Color("#FFC66D")
    private val GREEN = hex2Color("#A5C261")

    private fun hex2Color(colorStr: String): Color {
        return Color(
            Integer.valueOf(colorStr.substring(1, 3), 16),
            Integer.valueOf(colorStr.substring(3, 5), 16),
            Integer.valueOf(colorStr.substring(5, 7), 16)
        )
    }
}