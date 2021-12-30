package com.meetinclass.dslincolor.intellij

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesScheme
import java.awt.Color

object ColorEx {

    fun TextAttributesScheme.dslTagColor(): Color {
        val attributes = getAttributes(TAG_ATTRIBUTES_KEY)
        return if (attributes == null || attributes.foregroundColor == null) {
            YELLOW
        } else {
            attributes.foregroundColor
        }
    }

    fun TextAttributesScheme.dslAttrColor(): Color {
        val attributes = getAttributes(ATTR_ATTRIBUTES_KEY)
        return if (attributes == null || attributes.foregroundColor == null) {
            GREEN
        } else {
            attributes.foregroundColor
        }
    }

    private val YELLOW = hex2Color("#FFC66D")
    private val GREEN = hex2Color("#A5C261")

    private val TAG_ATTRIBUTES_KEY = TextAttributesKey.createTextAttributesKey("html-dsl-tag")
    private val ATTR_ATTRIBUTES_KEY = TextAttributesKey.createTextAttributesKey("html-dsl-attribute")

    private fun hex2Color(colorStr: String): Color {
        return Color(
            Integer.valueOf(colorStr.substring(1, 3), 16),
            Integer.valueOf(colorStr.substring(3, 5), 16),
            Integer.valueOf(colorStr.substring(5, 7), 16)
        )
    }
}