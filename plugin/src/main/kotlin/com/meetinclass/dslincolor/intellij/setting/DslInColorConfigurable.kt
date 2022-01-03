package com.meetinclass.dslincolor.intellij.setting

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class DslInColorConfigurable : SearchableConfigurable {
    private var settingsForm: DslInColorSettingsForm? = null

    override fun createComponent(): JComponent? {
        settingsForm = settingsForm ?: DslInColorSettingsForm()
        return settingsForm?.component()
    }

    override fun isModified(): Boolean {
        return settingsForm?.isModified ?: return false
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val settings = DslInColorSettings.instance
        settings.annotationList = settingsForm?.annotationList() ?: emptySet()
    }

    override fun reset() {
        settingsForm?.loadSettings()
    }

    override fun disposeUIResources() {
        settingsForm = null
    }

    @Nls
    override fun getDisplayName() = "DSL in color"

    override fun getId(): String = ID

    companion object {
        const val ID = "preferences.dsl.in.color"
    }
}