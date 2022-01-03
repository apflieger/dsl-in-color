package com.meetinclass.dslincolor.intellij.setting

import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class DslInColorSettingsForm {
    private var panel: JPanel? = null
    private var appearancePanel: JPanel? = null

    private var annotationList: JTextField? = null

    private val settings: DslInColorSettings = DslInColorSettings.instance

    fun component(): JComponent? = panel

    fun annotationList() =
        annotationList?.text?.split(",")?.map { it.trim() }?.filterNot { it.isEmpty() }?.toSet()

    val isModified: Boolean
        get() = (annotationList() != settings.annotationList)

    init {
        loadSettings()
    }

    fun loadSettings() {
        annotationList?.text = settings.annotationList.joinToString(",")
    }
}
