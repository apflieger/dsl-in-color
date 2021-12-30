package com.meetinclass.dslincolor.intellij.setting


import com.intellij.application.options.colors.*
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorAndFontDescriptorsProvider
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.psi.codeStyle.DisplayPriority
import com.intellij.psi.codeStyle.DisplayPrioritySortable


class ColorPageFactory : ColorAndFontPanelFactory, ColorAndFontDescriptorsProvider, DisplayPrioritySortable {

    override fun getDisplayName(): String = DSL_IN_COLOR_GROUP

    override fun getPanelDisplayName(): String = DSL_IN_COLOR_GROUP

    override fun getPriority(): DisplayPriority = DisplayPriority.COMMON_SETTINGS

    override fun createPanel(options: ColorAndFontOptions): NewColorAndFontPanel {
        val emptyPreview = PreviewPanel.Empty()
        val schemesPanel = SchemesPanel(options)
        val optionsPanel = ColorOptionsPanel(options, schemesPanel, DSL_IN_COLOR_GROUP)

        schemesPanel.addListener(object : ColorAndFontSettingsListener.Abstract() {
            override fun schemeChanged(source: Any) {
                optionsPanel.updateOptionsList()
            }
        })

        return NewColorAndFontPanel(schemesPanel, optionsPanel, emptyPreview, DSL_IN_COLOR_GROUP, null, null)
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = ATTRIBUTE_DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = emptyArray()

    companion object {
        private const val DSL_IN_COLOR_GROUP = "DSL in Color"
        private val ATTRIBUTE_DESCRIPTORS: Array<AttributesDescriptor> by lazy {
            createDescriptors("html-dsl-tag") +
                    createDescriptors("html-dsl-attribute")
        }

        private fun createDescriptors(name: String): Array<AttributesDescriptor> = arrayOf(
            AttributesDescriptor(name, TextAttributesKey.createTextAttributesKey(name))
        )
    }

}